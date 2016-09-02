package ca.qc.ircm.proview.transfer;

import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;
import static ca.qc.ircm.proview.transfer.QTransfer.transfer;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.tube.Tube;
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
 * Default implementation of transfer services.
 */
@Service
@Transactional
public class TransferServiceImpl extends BaseTreatmentService implements TransferService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private TransferActivityService transferActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected TransferServiceImpl() {
  }

  protected TransferServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      TransferActivityService transferActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.transferActivityService = transferActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Transfer get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Transfer.class, id);
  }

  @Override
  public List<Transfer> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Transfer> query = queryFactory.select(transfer);
    query.from(transfer, sampleTransfer);
    query.where(sampleTransfer._super.in(transfer.treatmentSamples));
    query.where(sampleTransfer.sample.eq(sample));
    query.where(transfer.deleted.eq(false));
    return query.distinct().fetch();
  }

  @Override
  public void insert(Transfer transfer) {
    authorizationService.checkAdminRole();
    final User user = authorizationService.getCurrentUser();

    // Reassign samples inside destinations.
    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      sampleTransfer.getDestinationContainer().setSample(sampleTransfer.getSample());
      sampleTransfer.getDestinationContainer().setTreatmentSample(sampleTransfer);
    }

    // Insert destination tubes.
    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      if (sampleTransfer.getDestinationContainer() instanceof Tube) {
        sampleTransfer.getDestinationContainer().setTimestamp(Instant.now());
        entityManager.persist(sampleTransfer.getDestinationContainer());
      }
    }

    // Insert transfer.
    transfer.setInsertTime(Instant.now());
    transfer.setUser(user);

    entityManager.persist(transfer);
    // Link container to sample and treatment sample.
    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      sampleTransfer.getDestinationContainer().setTimestamp(Instant.now());
    }

    // Log insertion of transfer.
    entityManager.flush();
    Activity activity = transferActivityService.insert(transfer);
    activityService.insert(activity);

    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      entityManager.merge(sampleTransfer.getDestinationContainer());
    }
  }

  @Override
  public void undoErroneous(Transfer transfer, String justification)
      throws DestinationUsedInTreatmentException {
    authorizationService.checkAdminRole();

    transfer.setDeleted(true);
    transfer.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    transfer.setDeletionJustification(justification);

    // Remove sample from destinations.
    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<SampleContainer>();
    Collection<SampleContainer> removeFailed = new LinkedHashSet<SampleContainer>();
    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      SampleContainer destination = sampleTransfer.getDestinationContainer();
      if (containerUsedByTreatmentOrAnalysis(destination)) {
        removeFailed.add(destination);
      }
    }
    if (!removeFailed.isEmpty()) {
      throw new DestinationUsedInTreatmentException("Cannot remove sample from all destinations",
          removeFailed);
    }
    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      SampleContainer destination = sampleTransfer.getDestinationContainer();
      destination.setSample(null);
      destination.setTreatmentSample(null);
      samplesRemoved.add(destination);
    }

    // Log changes.
    Activity activity =
        transferActivityService.undoErroneous(transfer, justification, samplesRemoved);
    activityService.insert(activity);

    entityManager.merge(transfer);
    for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
      entityManager.merge(sampleTransfer.getDestinationContainer());
    }
  }

  @Override
  public void undoFailed(Transfer transfer, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    transfer.setDeleted(true);
    transfer.setDeletionType(Treatment.DeletionType.FAILED);
    transfer.setDeletionJustification(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during transfer.
      for (SampleTransfer sampleTransfer : transfer.getTreatmentSamples()) {
        SampleContainer container = sampleTransfer.getDestinationContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after transfer.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        transferActivityService.undoFailed(transfer, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(transfer);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
