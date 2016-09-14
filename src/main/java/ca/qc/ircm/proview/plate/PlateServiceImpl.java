package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.plate.QPlateSpot.plateSpot;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DATA_ANALYSIS;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.PlateSpotService.SpotLocation;
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
 * Service class for plates.
 */
@Service
@Transactional
public class PlateServiceImpl implements PlateService {
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

  protected PlateServiceImpl() {
  }

  protected PlateServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      PlateActivityService plateActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.plateActivityService = plateActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Plate get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Plate.class, id);
  }

  @Override
  public Plate getWithSpots(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Plate.class, id);
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
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

  /**
   * Initialize all spots of a plate.
   *
   * @param plate
   *          plate
   */
  private void initPlateSpotList(Plate plate) {
    List<PlateSpot> spots = new ArrayList<PlateSpot>();
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

  @Override
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

  @Override
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
