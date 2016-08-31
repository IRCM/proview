package ca.qc.ircm.proview.sample;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of {@link EluateSample submitted eluate sample} services.
 */
@Service
@Transactional
public class EluateSampleServiceImpl implements EluateSampleService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected EluateSampleServiceImpl() {
  }

  protected EluateSampleServiceImpl(EntityManager entityManager,
      SampleActivityService sampleActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public EluateSample get(Long id) {
    if (id == null) {
      return null;
    }

    EluateSample sample = entityManager.find(EluateSample.class, id);
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  @Override
  public void update(EluateSample sample, String justification) {
    authorizationService.checkAdminRole();

    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(sample, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(sample);
  }
}
