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
 * Service for transfer.
 */
@Service
@Transactional
public class TransferService extends BaseTreatmentService {
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

  protected TransferService() {
  }

  protected TransferService(EntityManager entityManager, JPAQueryFactory queryFactory,
      TransferActivityService transferActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.transferActivityService = transferActivityService;
    this.activityService = activityService;
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
   * Returns all transfers involving sample.
   *
   * @param sample
   *          sample
   * @return all transfers involving sample
   */
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

  /**
   * Insert transfer into database.
   *
   * @param transfer
   *          transfer
   */
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

  /**
   * Undo erroneous transfer that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * transfer are not the right ones. So, in practice, the transfer never actually occurred.
   *
   * @param transfer
   *          erroneous transfer to undo
   * @param justification
   *          explanation of what was incorrect with the transfer
   * @throws DestinationUsedInTreatmentException
   *           destination container(s) is used in another treatment and sample cannot be remove
   */
  public void undoErroneous(Transfer transfer, String justification)
      throws DestinationUsedInTreatmentException {
    authorizationService.checkAdminRole();

    transfer.setDeleted(true);
    transfer.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    transfer.setDeletionJustification(justification);

    // Remove sample from destinations.
    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<>();
    Collection<SampleContainer> removeFailed = new LinkedHashSet<>();
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
    transfer.setDeletionJustification(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
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
