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

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.TreatmentSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
   * Insert transfer into database.
   *
   * @param transfer
   *          transfer
   */
  public void insert(Transfer transfer) {
    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(transfer);
    final User user = authorizationService.getCurrentUser();
    Instant now = Instant.now();

    // Link container to sample.
    for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
      treatmentSample.getDestinationContainer().setSample(treatmentSample.getSample());
      treatmentSample.getDestinationContainer().setTimestamp(now);
    }

    // Insert destination tubes.
    for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
      if (treatmentSample.getDestinationContainer() instanceof Tube) {
        entityManager.persist(treatmentSample.getDestinationContainer());
      }
    }
    // Insert destination plates.
    for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
      Set<Plate> insertedPlates = new HashSet<>();
      if (treatmentSample.getDestinationContainer() instanceof Well) {
        Well destinationWell = (Well) treatmentSample.getDestinationContainer();
        if (destinationWell.getId() == null
            && !insertedPlates.contains(destinationWell.getPlate())) {
          plateService.insert(destinationWell.getPlate());
          insertedPlates.add(destinationWell.getPlate());
        }
      }
    }
    for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
      entityManager.merge(treatmentSample.getDestinationContainer());
    }

    // Insert transfer.
    transfer.setInsertTime(now);
    transfer.setUser(user);

    entityManager.persist(transfer);

    // Log insertion of transfer.
    entityManager.flush();
    Activity activity = transferActivityService.insert(transfer);
    activityService.insert(activity);
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
   * @param removeSamplesFromDestinations
   *          true if samples should be removed from destination containers
   * @param banContainers
   *          true if containers used in transfer should be banned, this will also ban any container
   *          were samples were transfered / fractionated after transfer
   */
  public void undo(Transfer transfer, String failedDescription,
      boolean removeSamplesFromDestinations, boolean banContainers) {
    if (removeSamplesFromDestinations && banContainers) {
      throw new IllegalArgumentException(
          "removeSamplesFromDestinations and banContainers cannot be both true");
    }
    authorizationService.checkAdminRole();

    transfer.setDeleted(true);
    transfer.setDeletionExplanation(failedDescription);

    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<>();
    if (removeSamplesFromDestinations) {
      // Remove sample from destinations.
      Collection<SampleContainer> removeFailed = new LinkedHashSet<>();
      for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
        SampleContainer destination = treatmentSample.getDestinationContainer();
        if (containerUsedByTreatmentOrAnalysis(destination)) {
          removeFailed.add(destination);
        }
      }
      if (!removeFailed.isEmpty()) {
        throw new IllegalArgumentException("Cannot remove sample from all destinations");
      }
      for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
        SampleContainer destination = treatmentSample.getDestinationContainer();
        destination.setSample(null);
        samplesRemoved.add(destination);
      }
    }

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during transfer.
      for (TreatmentSample treatmentSample : transfer.getTreatmentSamples()) {
        SampleContainer container = treatmentSample.getDestinationContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after transfer.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        transferActivityService.undo(transfer, failedDescription, samplesRemoved, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(transfer);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
