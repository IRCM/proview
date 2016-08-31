package ca.qc.ircm.proview.digestion;

import static ca.qc.ircm.proview.digestion.QDigestedSample.digestedSample;
import static ca.qc.ircm.proview.digestion.QDigestion.digestion;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of digestion services.
 */
@Service
@Transactional
public class DigestionServiceImpl extends BaseTreatmentService implements DigestionService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private DigestionActivityService digestionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DigestionServiceImpl() {
  }

  protected DigestionServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      DigestionActivityService digestionActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.digestionActivityService = digestionActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Digestion get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Digestion.class, id);
  }

  @Override
  public List<Digestion> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Digestion> query = queryFactory.select(digestion);
    query.from(digestion, digestedSample);
    query.where(digestedSample._super.in(digestion.treatmentSamples));
    query.where(digestedSample.sample.eq(sample));
    query.where(digestion.deleted.eq(false));
    return query.distinct().fetch();
  }

  @Override
  public void insert(Digestion digestion) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    digestion.setUser(user);
    digestion.setInsertTime(new Date());

    entityManager.persist(digestion);

    // Log insertion of digestion.
    entityManager.flush();
    Activity activity = digestionActivityService.insert(digestion);
    activityService.insert(activity);
  }

  @Override
  public void undoErroneous(Digestion digestion, String justification) {
    authorizationService.checkAdminRole();

    digestion.setDeleted(true);
    digestion.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    digestion.setDeletionJustification(justification);

    // Log changes.
    Activity activity = digestionActivityService.undoErroneous(digestion, justification);
    activityService.insert(activity);

    entityManager.merge(digestion);
  }

  @Override
  public void undoFailed(Digestion digestion, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    digestion.setDeleted(true);
    digestion.setDeletionType(Treatment.DeletionType.FAILED);
    digestion.setDeletionJustification(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during digestion.
      for (DigestedSample digestedSample : digestion.getTreatmentSamples()) {
        SampleContainer container = digestedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers where sample were transfered after digestion.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        digestionActivityService.undoFailed(digestion, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(digestion);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
