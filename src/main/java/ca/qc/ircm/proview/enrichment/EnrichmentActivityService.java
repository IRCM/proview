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

package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.DatabaseLogUtil;
import ca.qc.ircm.proview.history.SampleStatusUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link Enrichment} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class EnrichmentActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected EnrichmentActivityService() {
  }

  protected EnrichmentActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of enrichment.
   *
   * @param enrichment
   *          inserted enrichment
   * @return activity about insertion of enrichment
   */
  @CheckReturnValue
  public Activity insert(final Enrichment enrichment) {
    final User user = authorizationService.getCurrentUser();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    for (EnrichedSample ts : enrichment.getTreatmentSamples()) {
      Sample newSample = ts.getSample();
      Sample oldSample = entityManager.find(Sample.class, ts.getSample().getId());
      if (newSample instanceof SubmissionSample) {
        SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
        SubmissionSample oldSubmissionSample = (SubmissionSample) oldSample;
        updateBuilders.add(new SampleStatusUpdateActivityBuilder().oldSample(oldSubmissionSample)
            .newSample(newSubmissionSample));
      }
    }

    // Keep updates that changed.
    final List<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(enrichment.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(null);
    activity.setUpdates(updates);
    return activity;
  }

  /**
   * Creates an activity about update of enrichment.
   *
   * @param enrichment
   *          enrichment containing new properties/values
   * @param explanation
   *          explanation
   * @return activity about update of enrichment
   */
  @CheckReturnValue
  public Optional<Activity> update(Enrichment enrichment, String explanation) {
    User user = authorizationService.getCurrentUser();
    final Enrichment oldEnrichment = entityManager.find(Enrichment.class, enrichment.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    updateBuilders.add(enrichmentUpdate(enrichment).column("protocol")
        .oldValue(oldEnrichment.getProtocol().getId()).newValue(enrichment.getProtocol().getId()));
    Map<Long, EnrichedSample> oldEnrichedSampleIds = oldEnrichment.getTreatmentSamples().stream()
        .collect(Collectors.toMap(ts -> ts.getId(), ts -> ts));
    enrichment.getTreatmentSamples().stream()
        .filter(ts -> !oldEnrichedSampleIds.containsKey(ts.getId()))
        .forEach(ts -> updateBuilders.add(enrichedSampleAction(ts, ActionType.INSERT)));
    enrichment.getTreatmentSamples().stream()
        .filter(ts -> oldEnrichedSampleIds.containsKey(ts.getId())).forEach(ts -> {
          updateBuilders.add(enrichedSampleAction(ts, ActionType.UPDATE).column("sampleId")
              .oldValue(oldEnrichedSampleIds.get(ts.getId()).getSample().getId())
              .newValue(ts.getSample().getId()));
          updateBuilders.add(enrichedSampleAction(ts, ActionType.UPDATE).column("containerId")
              .oldValue(oldEnrichedSampleIds.get(ts.getId()).getContainer().getId())
              .newValue(ts.getContainer().getId()));
          updateBuilders.add(enrichedSampleAction(ts, ActionType.UPDATE).column("comment")
              .oldValue(oldEnrichedSampleIds.get(ts.getId()).getComment())
              .newValue(ts.getComment()));
        });

    List<UpdateActivity> updates = updateBuilders.stream().filter(builder -> builder.isChanged())
        .map(builder -> builder.build()).collect(Collectors.toList());

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(enrichment.getId());
      activity.setUser(user);
      activity.setTableName("treatment");
      activity.setExplanation(DatabaseLogUtil.reduceLength(explanation, 255));
      activity.setUpdates(updates);
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  private UpdateActivityBuilder enrichmentUpdate(Enrichment enrichment) {
    return new UpdateActivityBuilder().tableName("treatment").recordId(enrichment.getId())
        .actionType(ActionType.UPDATE);
  }

  private UpdateActivityBuilder enrichedSampleAction(EnrichedSample ts, ActionType actionType) {
    return new UpdateActivityBuilder().tableName("treatmentsample").recordId(ts.getId())
        .actionType(actionType);
  }

  /**
   * Creates an activity about enrichment being marked as erroneous.
   *
   * @param enrichment
   *          erroneous enrichment that was undone
   * @param explanation
   *          explanation of what was incorrect with the enrichment
   * @return activity about enrichment being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(final Enrichment enrichment, final String explanation) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(enrichment.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about enrichment being marked as failed.
   *
   * @param enrichment
   *          failed enrichment that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about enrichment being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(final Enrichment enrichment, String failedDescription,
      final Collection<SampleContainer> bannedContainers) {
    final User user = authorizationService.getCurrentUser();

    // Log update for banned containers.
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    if (bannedContainers != null) {
      for (SampleContainer container : bannedContainers) {
        SampleContainer oldContainer = entityManager.find(SampleContainer.class, container.getId());
        updateBuilders
            .add(new BanSampleContainerUpdateActivityBuilder().oldContainer(oldContainer));
      }
    }

    // Keep updates that changed.
    final List<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    final String explanation = DatabaseLogUtil.reduceLength(failedDescription, 255);
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(enrichment.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(updates);
    return activity;
  }
}
