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
 * Default implementation of submitted molecule sample services.
 */
@Service
@Transactional
public class MoleculeSampleServiceImpl implements MoleculeSampleService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected MoleculeSampleServiceImpl() {
  }

  protected MoleculeSampleServiceImpl(EntityManager entityManager,
      SampleActivityService sampleActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public MoleculeSample get(Long id) {
    if (id == null) {
      return null;
    }

    MoleculeSample sample = entityManager.find(MoleculeSample.class, id);
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  @Override
  public void update(MoleculeSample sample, String justification) throws SaveStructureException {
    authorizationService.checkAdminRole();

    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(sample, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(sample);
  }
}
