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
 * Default implementation of plate's spots services.
 */
@Service
@Transactional
public class PlateSpotServiceImpl implements PlateSpotService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected PlateSpotServiceImpl() {
  }

  protected PlateSpotServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  @Override
  public PlateSpot get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(PlateSpot.class, id);
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
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
