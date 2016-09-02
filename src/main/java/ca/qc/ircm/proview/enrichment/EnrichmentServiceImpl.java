package ca.qc.ircm.proview.enrichment;

import static ca.qc.ircm.proview.enrichment.QEnrichedSample.enrichedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichment.enrichment;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of enrichment services.
 */
@Service
@Transactional
public class EnrichmentServiceImpl extends BaseTreatmentService implements EnrichmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private EnrichmentActivityService enrichmentActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected EnrichmentServiceImpl() {
  }

  protected EnrichmentServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      EnrichmentActivityService enrichmentActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.enrichmentActivityService = enrichmentActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Enrichment get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Enrichment.class, id);
  }

  @Override
  public List<Enrichment> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Enrichment> query = queryFactory.select(enrichment);
    query.from(enrichment, enrichedSample);
    query.where(enrichedSample._super.in(enrichment.treatmentSamples));
    query.where(enrichedSample.sample.eq(sample));
    query.where(enrichment.deleted.eq(false));
    return query.distinct().fetch();
  }

  @Override
  public void insert(Enrichment enrichment) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    enrichment.setInsertTime(Instant.now());
    enrichment.setUser(user);

    entityManager.persist(enrichment);

    // Log insertion of enrichment.
    entityManager.flush();
    Activity activity = enrichmentActivityService.insert(enrichment);
    activityService.insert(activity);
  }

  @Override
  public void undoErroneous(Enrichment enrichment, String justification) {
    authorizationService.checkAdminRole();

    enrichment.setDeleted(true);
    enrichment.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    enrichment.setDeletionJustification(justification);

    // Log changes.
    Activity activity = enrichmentActivityService.undoErroneous(enrichment, justification);
    activityService.insert(activity);

    entityManager.merge(enrichment);
  }

  @Override
  public void undoFailed(Enrichment enrichment, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    enrichment.setDeleted(true);
    enrichment.setDeletionType(Treatment.DeletionType.FAILED);
    enrichment.setDeletionJustification(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during enrichment.
      for (EnrichedSample enrichedSample : enrichment.getTreatmentSamples()) {
        SampleContainer container = enrichedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after enrichment.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        enrichmentActivityService.undoFailed(enrichment, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(enrichment);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
