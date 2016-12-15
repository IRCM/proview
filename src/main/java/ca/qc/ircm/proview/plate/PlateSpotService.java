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

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for plate's spots.
 */
@Service
@Transactional
public class PlateSpotService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected PlateSpotService() {
  }

  protected PlateSpotService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects spot from database.
   *
   * @param id
   *          database identifier of spot
   * @return spot
   */
  public PlateSpot get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(PlateSpot.class, id);
  }

  /**
   * Returns PlateSpot on plate at specified location.
   *
   * @param plateParam
   *          spot's plate
   * @param location
   *          spot's location on plate
   * @return plateSpot on plate at specified location
   */
  public PlateSpot get(Plate plateParam, SpotLocation location) {
    if (plateParam == null || location == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    JPAQuery<PlateSpot> query = queryFactory.select(plateSpot);
    query.from(plateSpot);
    query.join(plateSpot.plate, plate);
    query.where(plate.eq(plateParam));
    query.where(plateSpot.row.eq(location.getRow()));
    query.where(plateSpot.column.eq(location.getColumn()));
    return query.fetchOne();
  }

  /**
   * Selects most recent spot where sample was put.
   *
   * @param sample
   *          sample
   * @return most recent spot where sample was put
   */
  public PlateSpot last(Sample sample) {
    if (sample == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    JPAQuery<PlateSpot> query = queryFactory.select(plateSpot);
    query.from(plateSpot);
    query.where(plateSpot.sample.eq(sample));
    query.orderBy(plateSpot.timestamp.desc());
    query.limit(1);
    return query.fetchOne();
  }

  /**
   * Selects all plate's spots.
   *
   * @param plate
   *          plate
   * @return all plate's spots
   */
  public List<PlateSpot> all(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<PlateSpot> query = queryFactory.select(plateSpot);
    query.from(plateSpot);
    query.where(plateSpot.plate.eq(plate));
    return query.fetch();
  }

  /**
   * Selects all spots where sample is located.
   *
   * @param sample
   *          sample
   * @return all spots where sample is located
   */
  public List<PlateSpot> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<PlateSpot> query = queryFactory.select(plateSpot);
    query.from(plateSpot);
    query.where(plateSpot.sample.eq(sample));
    return query.fetch();
  }

  /**
   * Returns spots where sample is located on plate.
   *
   * @param sample
   *          sample
   * @param plate
   *          plate
   * @return Spots where sample is located.
   */
  public List<PlateSpot> location(final Sample sample, final Plate plate) {
    if (sample == null || plate == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<PlateSpot> query = queryFactory.select(plateSpot);
    query.from(plateSpot);
    query.where(plateSpot.plate.eq(plate));
    query.where(plateSpot.sample.eq(sample));
    return query.fetch();
  }
}
