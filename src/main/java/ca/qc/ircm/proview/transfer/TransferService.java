/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.transfer;

import static ca.qc.ircm.proview.transfer.QTransfer.transfer;
import static ca.qc.ircm.proview.transfer.QTransferedSample.transferedSample;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for transfer.
 */
@Service
@Transactional
public class TransferService extends BaseTreatmentService {
  @SuppressWarnings("unused")
  private static final Logger loggger = LoggerFactory.getLogger(TransferService.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private TransferActivityService transferActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private PlateService plateService;
  @Inject
  private AuthorizationService authorizationService;

  protected TransferService() {
  }

  protected TransferService(EntityManager entityManager, JPAQueryFactory queryFactory,
      TransferActivityService transferActivityService, ActivityService activityService,
      PlateService plateService, AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.transferActivityService = transferActivityService;
    this.activityService = activityService;
    this.plateService = plateService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects transfer from database.
   *
   * @param id
   *          database identifier of transfer
   * @return transfer
   */
  public Transfer get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Transfer.class, id);
  }

  /**
   * Returns all transfers involving one of submission's samples.
   *
   * @param submission
   *          submission
   * @return all transfers involving one of submission's samples
   */
  public List<Transfer> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Transfer> query = queryFactory.select(transfer);
    query.from(transfer, transferedSample);
    query.where(transferedSample._super.in(transfer.treatmentSamples));
    query.where(transferedSample.sample.in(submission.getSamples()));
    query.where(transfer.deleted.eq(false));
    return query.distinct().fetch();
  }

  /**
   * Insert transfer into database.
   *
   * @param transfer
   *          transfer
   */
  public void insert(Transfer transfer) {
    authorizationService.checkAdminRole();
    final User user = authorizationService.getCurrentUser();
    Instant now = Instant.now();

    // Insert destination tubes.
    for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
      if (transferedSample.getDestinationContainer() instanceof Tube) {
        transferedSample.getDestinationContainer().setTimestamp(Instant.now());
        entityManager.persist(transferedSample.getDestinationContainer());
      }
    }
    // Insert destination plates.
    for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
      Set<Plate> insertedPlates = new HashSet<>();
      if (transferedSample.getDestinationContainer() instanceof Well) {
        Well destinationWell = (Well) transferedSample.getDestinationContainer();
        if (destinationWell.getId() == null
            && !insertedPlates.contains(destinationWell.getPlate())) {
          plateService.insert(destinationWell.getPlate());
          insertedPlates.add(destinationWell.getPlate());
        }
      }
    }

    // Insert transfer.
    transfer.setInsertTime(now);
    transfer.setUser(user);

    entityManager.persist(transfer);
    // Link container to sample and treatment sample.
    for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
      transferedSample.getDestinationContainer().setSample(transferedSample.getSample());
      transferedSample.getDestinationContainer().setTimestamp(now);
    }

    // Log insertion of transfer.
    entityManager.flush();
    Activity activity = transferActivityService.insert(transfer);
    activityService.insert(activity);
  }

  /**
   * Undo erroneous transfer that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * transfer are not the right ones. So, in practice, the transfer never actually occurred.
   *
   * @param transfer
   *          erroneous transfer to undo
   * @param explanation
   *          explanation of what was incorrect with the transfer
   * @throws DestinationUsedInTreatmentException
   *           destination container(s) is used in another treatment and sample cannot be remove
   */
  public void undoErroneous(Transfer transfer, String explanation)
      throws DestinationUsedInTreatmentException {
    authorizationService.checkAdminRole();

    transfer.setDeleted(true);
    transfer.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    transfer.setDeletionExplanation(explanation);

    // Remove sample from destinations.
    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<>();
    Collection<SampleContainer> removeFailed = new LinkedHashSet<>();
    for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
      SampleContainer destination = transferedSample.getDestinationContainer();
      if (containerUsedByTreatmentOrAnalysis(destination)) {
        removeFailed.add(destination);
      }
    }
    if (!removeFailed.isEmpty()) {
      throw new DestinationUsedInTreatmentException("Cannot remove sample from all destinations",
          removeFailed);
    }
    for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
      SampleContainer destination = transferedSample.getDestinationContainer();
      destination.setSample(null);
      samplesRemoved.add(destination);
    }

    // Log changes.
    Activity activity =
        transferActivityService.undoErroneous(transfer, explanation, samplesRemoved);
    activityService.insert(activity);

    entityManager.merge(transfer);
    for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
      entityManager.merge(transferedSample.getDestinationContainer());
    }
  }

  /**
   * Report that a problem occurred during transfer causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the transfer was done but the incorrect
   * transfer could only be detected later in the sample processing. Thus the transfer is not undone
   * but flagged as having failed.
   *
   * @param transfer
   *          transfer to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in transfer should be banned, this will also ban any container
   *          were samples were transfered / fractionated after transfer
   */
  public void undoFailed(Transfer transfer, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    transfer.setDeleted(true);
    transfer.setDeletionType(Treatment.DeletionType.FAILED);
    transfer.setDeletionExplanation(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during transfer.
      for (TransferedSample transferedSample : transfer.getTreatmentSamples()) {
        SampleContainer container = transferedSample.getDestinationContainer();
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
