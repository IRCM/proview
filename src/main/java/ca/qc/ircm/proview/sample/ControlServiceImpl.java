package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.QControl.control;
import static ca.qc.ircm.proview.sample.QSample.sample;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of control services.
 */
@Service
@Transactional
public class ControlServiceImpl implements ControlService {
  private static final String CONTROL_BASE_LIMS = "CONTROL.";
  private static final Pattern CONTROL_NUMBER_PATTERN =
      Pattern.compile(Pattern.quote(CONTROL_BASE_LIMS) + "(\\d+)");
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private TubeService tubeService;
  @Inject
  private AuthorizationService authorizationService;

  protected ControlServiceImpl() {
  }

  protected ControlServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      SampleActivityService sampleActivityService, ActivityService activityService,
      TubeService tubeService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.tubeService = tubeService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Control get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Control.class, id);
  }

  @Override
  public void insert(Control control) {
    authorizationService.checkAdminRole();

    generateLims(control);
    entityManager.persist(control);

    // Insert tube.
    Tube tube = new Tube();
    tube.setSample(control);
    tube.setName(tubeService.generateTubeName(control, new HashSet<String>()));
    entityManager.persist(tube);
    control.setOriginalContainer(tube);

    entityManager.flush();
    // Log insertion to database.
    Activity activity = sampleActivityService.insertControl(control);
    activityService.insert(activity);
  }

  private String lastLims() {
    JPAQuery<String> query = queryFactory.select(control.lims);
    query.from(control);
    query.orderBy(control.id.desc());
    query.limit(1);
    return query.fetchOne();
  }

  private boolean limsExists(String lims) {
    JPAQuery<Long> query = queryFactory.select(sample.id);
    query.from(sample);
    query.where(sample.lims.eq(lims));
    return query.fetchCount() > 0;
  }

  private void generateLims(Control sample) {
    String base = CONTROL_BASE_LIMS;
    String lastLims = lastLims();
    int lastValue;
    if (lastLims != null) {
      Matcher matcher = CONTROL_NUMBER_PATTERN.matcher(lastLims);
      matcher.matches();
      lastValue = Integer.parseInt(matcher.group(1));
    } else {
      lastValue = 0;
    }
    String lims = base + (lastValue + 1);
    if (limsExists(lims)) {
      lastValue++;
      lims = base + (lastValue + 1);
    }
    sample.setLims(lims);
  }

  @Override
  public void update(Control control, String justification) {
    authorizationService.checkAdminRole();

    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(control, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(control);
  }
}
