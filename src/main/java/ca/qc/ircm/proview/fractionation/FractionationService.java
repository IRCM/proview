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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for fractionation.
 */
@Service
@Transactional
public class FractionationService extends BaseTreatmentService {
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

  protected FractionationService() {
  }

  protected FractionationService(EntityManager entityManager, JPAQueryFactory queryFactory,
      FractionationActivityService fractionationActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.fractionationActivityService = fractionationActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects fractionation from database.
   *
   * @param id
   *          database identifier of fractionation
   * @return fractionation
   */
  public Fractionation get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Fractionation.class, id);
  }

  /**
   * Selects fractionated sample corresponding to specified container. Null is returned if container
   * is not linked to a fractionated sample. If fractionated sample was transfered, search will find
   * it in any destination.
   *
   * @param container
   *          sample's container
   * @return fractionated sample corresponding to specified container
   */
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

  /**
   * Selects all fractionations involving sample.
   *
   * @param sample
   *          sample
   * @return all fractionations involving sample
   */
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

  /**
   * Add fractionation to database.
   *
   * @param fractionation
   *          fractionation
   */
  public void insert(Fractionation fractionation) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();
    validateSpotDestination(fractionation);

    fractionation.setInsertTime(Instant.now());
    fractionation.setUser(user);

    // Reassign samples inside spots.
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      detail.getDestinationContainer().setSample(detail.getSample());
      detail.getDestinationContainer().setTreatmentSample(detail);
    }

    // Insert destination tubes.
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      if (detail.getDestinationContainer() instanceof Tube) {
        detail.getDestinationContainer().setTimestamp(Instant.now());
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
      detail.getDestinationContainer().setTimestamp(Instant.now());
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

  /**
   * Undo erroneous fractionation that never actually occurred. This method is usually called
   * shortly after action was inserted into the database. The user realises that the samples checked
   * for fractionation are not the right ones. So, in practice, the fractionation never actually
   * occurred.
   *
   * @param fractionation
   *          erroneous fractionation to undo
   * @param justification
   *          explanation of what was incorrect with the fractionation
   * @throws DestinationUsedInTreatmentException
   *           destination container(s) is used in another treatment and sample cannot be remove
   */
  public void undoErroneous(Fractionation fractionation, String justification)
      throws DestinationUsedInTreatmentException {
    authorizationService.checkAdminRole();

    fractionation.setDeleted(true);
    fractionation.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    fractionation.setDeletionJustification(justification);
    // Remove sample from destinations.
    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<>();
    Collection<SampleContainer> removeFailed = new LinkedHashSet<>();
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

  /**
   * Report that a problem occurred during fractionation causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the fractionation was done but the incorrect
   * fractionation could only be detected later in the sample processing. Thus the fractionation is
   * not undone but flagged as having failed.
   *
   * @param fractionation
   *          fractionation to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in fractionation should be banned, this will also ban any
   *          container were samples were transfered / fractionated after fractionation
   */
  public void undoFailed(Fractionation fractionation, String failedDescription,
      boolean banContainers) {
    authorizationService.checkAdminRole();

    fractionation.setDeleted(true);
    fractionation.setDeletionType(Treatment.DeletionType.FAILED);
    fractionation.setDeletionJustification(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
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
