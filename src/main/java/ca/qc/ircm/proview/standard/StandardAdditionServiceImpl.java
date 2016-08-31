package ca.qc.ircm.proview.standard;

import static ca.qc.ircm.proview.standard.QAddedStandard.addedStandard;
import static ca.qc.ircm.proview.standard.QStandardAddition.standardAddition;

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
 * MyBatis implementation of addition of standards services.
 */
@Service
@Transactional
public class StandardAdditionServiceImpl extends BaseTreatmentService
    implements StandardAdditionService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private StandardAdditionActivityService standardAdditionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected StandardAdditionServiceImpl() {
  }

  protected StandardAdditionServiceImpl(EntityManager entityManager,
      JPAQueryFactory queryFactory, StandardAdditionActivityService standardAdditionActivityService,
      ActivityService activityService, AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.standardAdditionActivityService = standardAdditionActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public StandardAddition get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(StandardAddition.class, id);
  }

  @Override
  public List<StandardAddition> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<StandardAddition> query = queryFactory.select(standardAddition);
    query.from(standardAddition, addedStandard);
    query.where(addedStandard._super.in(standardAddition.treatmentSamples));
    query.where(addedStandard.sample.eq(sample));
    query.where(standardAddition.deleted.eq(false));
    return query.distinct().fetch();
  }

  @Override
  public void insert(StandardAddition standardAddition) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    standardAddition.setInsertTime(new Date());
    standardAddition.setUser(user);

    // Insert standard addition.
    entityManager.persist(standardAddition);

    // Log insertion of addition of standards.
    entityManager.flush();
    Activity activity = standardAdditionActivityService.insert(standardAddition);
    activityService.insert(activity);
  }

  @Override
  public void undoErroneous(StandardAddition standardAddition, String justification) {
    authorizationService.checkAdminRole();

    standardAddition.setDeleted(true);
    standardAddition.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    standardAddition.setDeletionJustification(justification);

    // Log changes.
    Activity activity =
        standardAdditionActivityService.undoErroneous(standardAddition, justification);
    activityService.insert(activity);

    entityManager.merge(standardAddition);
  }

  @Override
  public void undoFailed(StandardAddition standardAddition, String failedDescription,
      boolean banContainers) {
    authorizationService.checkAdminRole();

    standardAddition.setDeleted(true);
    standardAddition.setDeletionType(Treatment.DeletionType.FAILED);
    standardAddition.setDeletionJustification(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during standardAddition.
      for (AddedStandard addedStandard : standardAddition.getTreatmentSamples()) {
        SampleContainer container = addedStandard.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after
        // standardAddition.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity = standardAdditionActivityService.undoFailed(standardAddition,
        failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(standardAddition);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
