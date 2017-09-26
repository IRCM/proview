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
import static ca.qc.ircm.proview.plate.QWell.well;
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
   * Returns plate with specified id.
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
   * Returns plate with specified name.
   *
   * @param name
   *          plate's name
   * @return plate
   */
  public Plate get(String name) {
    if (name == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    JPAQuery<Plate> query = queryFactory.select(plate);
    query.from(plate);
    query.where(plate.name.eq(name));
    return query.fetchOne();
  }

  /**
   * Selects all plates passing filter.
   *
   * @param filter
   *          filters plates
   * @return all plates passing filter
   */
  public List<Plate> all(PlateFilter filter) {
    authorizationService.checkAdminRole();

    if (filter == null) {
      filter = new PlateFilterBuilder().build();
    }
    JPAQuery<Plate> query = queryFactory.select(plate);
    query.from(plate);
    if (filter.type() != null) {
      query.where(plate.type.eq(filter.type()));
    }
    if (filter.containsAnySamples() != null) {
      query.from(plate.wells, well);
      query.where(well.sample.in(filter.containsAnySamples()));
    }
    return query.distinct().fetch();
  }

  /**
   * Selects all plates of specified type.
   *
   * @param type
   *          plate's type
   * @return all plates of specified type
   */
  public List<Plate> choices(PlateType type) {
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
    query.join(plate.wells, well);
    query.from(submissionSample);
    query.where(submissionSample.eq(well.sample));
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
   * Insert plate and it's wells into database.
   *
   * @param plate
   *          plate to insert
   */
  public void insert(Plate plate) {
    authorizationService.checkAdminRole();

    plate.setInsertTime(Instant.now());
    initWellList(plate);
    entityManager.persist(plate);

    entityManager.flush();
    // Log insertion of plate.
    Activity activity = plateActivityService.insert(plate);
    activityService.insert(activity);
  }

  private void initWellList(Plate plate) {
    plate.initWells();
    plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
  }

  /**
   * Bans multiple wells to prevent them from being used. Wells that will be banned are wells that
   * are located from <code>from parameter</code> up to <code>to parameter</code>. If a well was
   * already banned, no change is made to that well.
   *
   * @param plate
   *          plate were wells are located
   * @param from
   *          first well to ban
   * @param to
   *          last well to ban
   * @param explanation
   *          explanation for banning wells
   */
  public void ban(Plate plate, WellLocation from, WellLocation to, String explanation) {
    authorizationService.checkAdminRole();

    Collection<Well> wells = plate.wells(from, to);
    for (Well well : wells) {
      well.setBanned(true);
    }

    // Log change.
    Activity activity = plateActivityService.ban(wells, explanation);
    activityService.insert(activity);

    for (Well well : wells) {
      entityManager.merge(well);
    }
  }

  /**
   * Reactivates multiple wells that were banned. Wells that will be reactivated are wells that are
   * located from <code>from parameter</code> up to <code>to parameter</code>. If a well was not
   * banned, no change is made to that well.
   *
   * @param plate
   *          plate were wells are located
   * @param from
   *          first well to reactivate
   * @param to
   *          last well to reactivate
   * @param explanation
   *          explanation for reactivating wells
   */
  public void activate(Plate plate, WellLocation from, WellLocation to, String explanation) {
    authorizationService.checkAdminRole();

    Collection<Well> wells = plate.wells(from, to);
    for (Well well : wells) {
      well.setBanned(false);
    }

    // Log change.
    Activity activity = plateActivityService.activate(wells, explanation);
    activityService.insert(activity);

    for (Well well : wells) {
      entityManager.merge(well);
    }
  }
}
