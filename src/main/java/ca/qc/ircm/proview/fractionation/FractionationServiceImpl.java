package ca.qc.ircm.proview.fractionation;

import static ca.qc.ircm.proview.fractionation.QFractionation.fractionation;
import static ca.qc.ircm.proview.fractionation.QFractionationDetail.fractionationDetail;
import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.transfer.DestinationUsedInTreatmentException;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of fractionation services.
 */
@Service
@Transactional
public class FractionationServiceImpl extends BaseTreatmentService
    implements FractionationService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private FractionationActivityService fractionationActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected FractionationServiceImpl() {
  }

  protected FractionationServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      FractionationActivityService fractionationActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.fractionationActivityService = fractionationActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Fractionation get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Fractionation.class, id);
  }

  @Override
  public FractionationDetail find(SampleContainer container) {
    if (container == null) {
      return null;
    }
    authorizationService.checkSampleReadPermission(container.getSample());

    return findFration(container);
  }

  private FractionationDetail findFration(SampleContainer container) {
    FractionationDetail fd = null;
    {
      JPAQuery<FractionationDetail> query = queryFactory.select(fractionationDetail);
      query.from(fractionationDetail);
      query.where(fractionationDetail.destinationContainer.eq(container));
      fd = query.fetchOne();
    }
    if (fd != null) {
      return fd;
    } else {
      JPAQuery<SampleTransfer> query = queryFactory.select(sampleTransfer);
      query.from(sampleTransfer);
      query.where(sampleTransfer.destinationContainer.eq(container));
      SampleTransfer st = query.fetchOne();
      if (st != null) {
        return findFration(st.getContainer());
      } else {
        return null;
      }
    }
  }

  @Override
  public List<Fractionation> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Fractionation> query = queryFactory.select(fractionation);
    query.from(fractionation, fractionationDetail);
    query.where(fractionationDetail._super.in(fractionation.treatmentSamples));
    query.where(fractionationDetail.sample.eq(sample));
    query.where(fractionation.deleted.eq(false));
    return query.distinct().fetch();
  }

  private void validateSpotDestination(Fractionation fractionation) {
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      if (detail.getDestinationContainer() instanceof Tube) {
        throw new IllegalArgumentException("Fractions cannot be placed in tubes");
      }
    }
  }

  @Override
  public void insert(Fractionation fractionation) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();
    validateSpotDestination(fractionation);

    fractionation.setInsertTime(new Date());
    fractionation.setUser(user);

    // Reassign samples inside spots.
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      detail.getDestinationContainer().setSample(detail.getSample());
      detail.getDestinationContainer().setTreatmentSample(detail);
    }

    // Insert destination tubes.
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      if (detail.getDestinationContainer() instanceof Tube) {
        detail.getDestinationContainer().setTimestamp(new Date());
        entityManager.persist(detail.getDestinationContainer());
      }
    }

    // Insert fractionation.
    Map<Sample, Integer> positions = new HashMap<>();
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      Sample sample = detail.getSample();
      Integer lastPosition = lastPosition(sample);
      if (lastPosition == null) {
        lastPosition = 0;
      }
      positions.put(sample, lastPosition + 1);
    }
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      Sample sample = detail.getSample();
      Integer position = positions.get(sample);
      detail.setPosition(position);
      positions.put(sample, position + 1);
    }
    entityManager.persist(fractionation);
    // Link container to sample and treatment sample.
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      detail.getDestinationContainer().setTimestamp(new Date());
    }

    // Log insertion of fractionation.
    entityManager.flush();
    Activity activity = fractionationActivityService.insert(fractionation);
    activityService.insert(activity);

    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      entityManager.merge(detail.getDestinationContainer());
    }
  }

  private Integer lastPosition(Sample sample) {
    JPAQuery<Integer> query = queryFactory.select(fractionationDetail.position.max());
    query.from(fractionationDetail);
    query.where(fractionationDetail.sample.eq(sample));
    return query.fetchOne();
  }

  @Override
  public void undoErroneous(Fractionation fractionation, String justification)
      throws DestinationUsedInTreatmentException {
    authorizationService.checkAdminRole();

    fractionation.setDeleted(true);
    fractionation.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    fractionation.setDeletionJustification(justification);
    // Remove sample from destinations.
    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<SampleContainer>();
    Collection<SampleContainer> removeFailed = new LinkedHashSet<SampleContainer>();
    for (FractionationDetail fractionationDetail : fractionation.getTreatmentSamples()) {
      SampleContainer destination = fractionationDetail.getDestinationContainer();
      if (containerUsedByTreatmentOrAnalysis(destination)) {
        removeFailed.add(destination);
      }
    }
    if (!removeFailed.isEmpty()) {
      throw new DestinationUsedInTreatmentException("Cannot remove sample from all destinations",
          removeFailed);
    }
    for (FractionationDetail fractionationDetail : fractionation.getTreatmentSamples()) {
      SampleContainer destination = fractionationDetail.getDestinationContainer();
      destination.setSample(null);
      destination.setTreatmentSample(null);
      samplesRemoved.add(destination);
    }

    // Log changes.
    Activity activity =
        fractionationActivityService.undoErroneous(fractionation, justification, samplesRemoved);
    activityService.insert(activity);

    entityManager.merge(fractionation);
    for (FractionationDetail fractionationDetail : fractionation.getTreatmentSamples()) {
      entityManager.merge(fractionationDetail.getDestinationContainer());
    }
  }

  @Override
  public void undoFailed(Fractionation fractionation, String failedDescription,
      boolean banContainers) {
    authorizationService.checkAdminRole();

    fractionation.setDeleted(true);
    fractionation.setDeletionType(Treatment.DeletionType.FAILED);
    fractionation.setDeletionJustification(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during transfer.
      for (FractionationDetail fractionationDetail : fractionation.getTreatmentSamples()) {
        SampleContainer destination = fractionationDetail.getDestinationContainer();
        destination.setBanned(true);
        bannedContainers.add(destination);

        // Ban containers were sample were transfered after transfer.
        this.banDestinations(destination, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        fractionationActivityService.undoFailed(fractionation, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(fractionation);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
