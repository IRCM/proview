/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.plate.QPlateSpot.plateSpot;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DATA_ANALYSIS;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for plates.
 */
@Service
@Transactional
public class PlateService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private PlateActivityService plateActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected PlateService() {
  }

  protected PlateService(EntityManager entityManager, JPAQueryFactory queryFactory,
      PlateActivityService plateActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.plateActivityService = plateActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Finds plate in database.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  public Plate get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Plate.class, id);
  }

  /**
   * Finds plate in database.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  public Plate getWithSpots(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Plate.class, id);
  }

  /**
   * Selects all plates of specified type.
   *
   * @param type
   *          plate's type
   * @return all plates of specified type
   */
  public List<Plate> choices(Plate.Type type) {
    if (type == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Plate> query = queryFactory.select(plate);
    query.from(plate);
    query.where(plate.type.eq(type));
    return query.fetch();
  }

  /**
   * Returns true if plate is available, false otherwise. A plate is considered available when all
   * it's samples are analyzed.
   *
   * @param plateParam
   *          plate
   * @return true if plate is available, false otherwise
   */
  public boolean available(Plate plateParam) {
    if (plateParam == null) {
      return false;
    }
    authorizationService.checkAdminRole();

    JPAQuery<Long> query = queryFactory.select(plate.id);
    query.from(plate);
    query.join(plate.spots, plateSpot);
    query.from(submissionSample);
    query.where(submissionSample.eq(plateSpot.sample));
    query.where(plate.eq(plateParam));
    query.where(submissionSample.status.notIn(Arrays.asList(DATA_ANALYSIS, ANALYSED, CANCELLED)));
    return query.fetchCount() == 0;
  }

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          plate's name
   * @return true if name is available in database, false otherwise
   */
  public boolean nameAvailable(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkAdminRole();

    JPAQuery<Long> query = queryFactory.select(plate.id);
    query.from(plate);
    query.where(plate.name.eq(name));
    return query.fetchCount() == 0;
  }

  /**
   * Insert plate and it's spots into database.
   *
   * @param plate
   *          plate to insert
   */
  public void insert(Plate plate) {
    authorizationService.checkAdminRole();

    plate.setInsertTime(Instant.now());
    initPlateSpotList(plate);
    entityManager.persist(plate);

    entityManager.flush();
    // Log insertion of plate.
    Activity activity = plateActivityService.insert(plate);
    activityService.insert(activity);
  }

  private void initPlateSpotList(Plate plate) {
    List<PlateSpot> spots = new ArrayList<>();
    for (int row = 0; row < plate.getRowCount(); row++) {
      for (int column = 0; column < plate.getColumnCount(); column++) {
        PlateSpot plateSpot = new PlateSpot(row, column);
        plateSpot.setTimestamp(Instant.now());
        plateSpot.setPlate(plate);
        spots.add(plateSpot);
      }
    }
    plate.setSpots(spots);
  }

  /**
   * Bans multiple spots to prevent them from being used. Spots that will be banned are spots that
   * are located from <code>from parameter</code> up to <code>to parameter</code>. If a spot was
   * already banned, no change is made to that spot.
   *
   * @param plate
   *          plate were spots are located
   * @param from
   *          first spot to ban
   * @param to
   *          last spot to ban
   * @param justification
   *          justification for banning spots
   */
  public void ban(Plate plate, SpotLocation from, SpotLocation to, String justification) {
    authorizationService.checkAdminRole();

    Collection<PlateSpot> spots = plate.spots(from, to);
    for (PlateSpot spot : spots) {
      spot.setBanned(true);
    }

    // Log change.
    Activity activity = plateActivityService.ban(spots, justification);
    activityService.insert(activity);

    for (PlateSpot spot : spots) {
      entityManager.merge(spot);
    }
  }

  /**
   * Reactivates multiple spots that were banned. Spots that will be reactivated are spots that are
   * located from <code>from parameter</code> up to <code>to parameter</code>. If a spot was not
   * banned, no change is made to that spot.
   *
   * @param plate
   *          plate were spots are located
   * @param from
   *          first spot to reactivate
   * @param to
   *          last spot to reactivate
   * @param justification
   *          justification for reactivating spots
   */
  public void activate(Plate plate, SpotLocation from, SpotLocation to, String justification) {
    authorizationService.checkAdminRole();

    Collection<PlateSpot> spots = plate.spots(from, to);
    for (PlateSpot spot : spots) {
      spot.setBanned(false);
    }

    // Log change.
    Activity activity = plateActivityService.activate(spots, justification);
    activityService.insert(activity);

    for (PlateSpot spot : spots) {
      entityManager.merge(spot);
    }
  }
}
