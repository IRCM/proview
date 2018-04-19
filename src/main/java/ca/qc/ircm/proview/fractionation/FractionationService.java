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

import static ca.qc.ircm.proview.treatment.QTreatmentSample.treatmentSample;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.TreatmentSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
  public TreatmentSample search(SampleContainer container) {
    if (container == null) {
      return null;
    }
    authorizationService.checkSampleReadPermission(container.getSample());

    return searchFration(container);
  }

  private TreatmentSample searchFration(SampleContainer container) {
    TreatmentSample fd = null;
    {
      JPAQuery<TreatmentSample> query = queryFactory.select(treatmentSample);
      query.from(treatmentSample);
      query.where(treatmentSample.destinationContainer.eq(container));
      query.where(treatmentSample.treatment.instanceOf(Fractionation.class));
      fd = query.fetchOne();
    }
    if (fd != null) {
      return fd;
    } else {
      JPAQuery<TreatmentSample> query = queryFactory.select(treatmentSample);
      query.from(treatmentSample);
      query.where(treatmentSample.destinationContainer.eq(container));
      query.where(treatmentSample.treatment.instanceOf(Transfer.class));
      TreatmentSample st = query.fetchOne();
      if (st != null) {
        return searchFration(st.getContainer());
      } else {
        return null;
      }
    }
  }

  private void validateWellDestination(Fractionation fractionation) {
    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
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
    chechSameUserForAllSamples(fractionation);
    User user = authorizationService.getCurrentUser();
    validateWellDestination(fractionation);

    fractionation.setInsertTime(Instant.now());
    fractionation.setUser(user);

    // Reassign samples inside wells.
    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
      detail.getDestinationContainer().setSample(detail.getSample());
    }

    // Insert destination tubes.
    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
      if (detail.getDestinationContainer() instanceof Tube) {
        detail.getDestinationContainer().setTimestamp(Instant.now());
        entityManager.persist(detail.getDestinationContainer());
      }
    }

    // Insert fractionation.
    Map<Sample, Integer> positions = new HashMap<>();
    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
      Sample sample = detail.getSample();
      Integer lastPosition = lastPosition(sample);
      if (lastPosition == null) {
        lastPosition = 0;
      }
      positions.put(sample, lastPosition + 1);
    }
    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
      Sample sample = detail.getSample();
      Integer position = positions.get(sample);
      detail.setPosition(position);
      positions.put(sample, position + 1);
    }
    entityManager.persist(fractionation);
    // Link container to sample and treatment sample.
    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
      detail.getDestinationContainer().setTimestamp(Instant.now());
    }

    // Log insertion of fractionation.
    entityManager.flush();
    Activity activity = fractionationActivityService.insert(fractionation);
    activityService.insert(activity);

    for (TreatmentSample detail : fractionation.getTreatmentSamples()) {
      entityManager.merge(detail.getDestinationContainer());
    }
  }

  private Integer lastPosition(Sample sample) {
    JPAQuery<Integer> query = queryFactory.select(treatmentSample.position.max());
    query.from(treatmentSample);
    query.where(treatmentSample.sample.eq(sample));
    return query.fetchOne();
  }

  /**
   * Undo fractionation.
   *
   * @param fractionation
   *          fractionation to undo
   * @param explanation
   *          description of the problem that occurred
   * @param removeSamplesFromDestinations
   *          true if samples should be removed from destination containers
   * @param banContainers
   *          true if containers used in fractionation should be banned, this will also ban any
   *          container were samples were transfered / fractionated after fractionation
   */
  public void undo(Fractionation fractionation, String explanation,
      boolean removeSamplesFromDestinations, boolean banContainers) {
    if (removeSamplesFromDestinations && banContainers) {
      throw new IllegalArgumentException(
          "removeSamplesFromDestinations and banContainers cannot be both true");
    }
    authorizationService.checkAdminRole();

    fractionation.setDeleted(true);
    fractionation.setDeletionExplanation(explanation);

    // Remove sample from destinations.
    Collection<SampleContainer> samplesRemoved = new LinkedHashSet<>();
    if (removeSamplesFromDestinations) {
      Collection<SampleContainer> removeFailed = new LinkedHashSet<>();
      for (TreatmentSample treatmentSample : fractionation.getTreatmentSamples()) {
        SampleContainer destination = treatmentSample.getDestinationContainer();
        if (containerUsedByTreatmentOrAnalysis(destination)) {
          removeFailed.add(destination);
        }
      }
      if (!removeFailed.isEmpty()) {
        throw new IllegalArgumentException("Cannot remove sample from all destinations");
      }
      for (TreatmentSample treatmentSample : fractionation.getTreatmentSamples()) {
        SampleContainer destination = treatmentSample.getDestinationContainer();
        destination.setSample(null);
        samplesRemoved.add(destination);
      }
    }

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during transfer.
      for (TreatmentSample treatmentSample : fractionation.getTreatmentSamples()) {
        SampleContainer destination = treatmentSample.getDestinationContainer();
        destination.setBanned(true);
        bannedContainers.add(destination);

        // Ban containers were sample were transfered after transfer.
        this.banDestinations(destination, bannedContainers);
      }
    }

    // Log changes.
    Activity activity = fractionationActivityService.undo(fractionation, explanation,
        samplesRemoved, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(fractionation);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
