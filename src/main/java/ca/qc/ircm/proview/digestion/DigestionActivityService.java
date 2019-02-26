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

package ca.qc.ircm.proview.digestion;

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.SampleStatusUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerRepository;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates activities about {@link Digestion} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DigestionActivityService {
  private static final QSubmission qsubmission = QSubmission.submission;
  @Inject
  private DigestionRepository digestionRepository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private SampleContainerRepository sampleContainerRepository;
  @Inject
  private AuthorizationService authorizationService;

  protected DigestionActivityService() {
  }

  /**
   * Creates an activity about insertion of digestion.
   *
   * @param digestion
   *          inserted digestion
   * @return activity about insertion of digestion
   */
  @CheckReturnValue
  public Activity insert(final Digestion digestion) {
    final User user = authorizationService.getCurrentUser();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    Set<Long> submissionIds = new HashSet<>();
    for (TreatedSample ts : digestion.getTreatedSamples()) {
      Sample newSample = ts.getSample();
      if (newSample instanceof SubmissionSample) {
        Sample oldSample = sampleRepository.findOne(ts.getSample().getId());
        SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
        SubmissionSample oldSubmissionSample = (SubmissionSample) oldSample;
        updateBuilders.add(new SampleStatusUpdateActivityBuilder().oldSample(oldSubmissionSample)
            .newSample(newSubmissionSample));
        Submission newSubmission = newSubmissionSample.getSubmission();
        Submission oldSubmission = oldSubmissionSample.getSubmission();
        if (submissionIds.add(newSubmission.getId())) {
          updateBuilders.add(submissionUpdate(newSubmission)
              .column(qname(qsubmission.digestionDate)).oldValue(oldSubmission.getDigestionDate())
              .newValue(newSubmission.getDigestionDate()));
        }
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
    activity.setRecordId(digestion.getId());
    activity.setUser(user);
    activity.setTableName(Treatment.TABLE_NAME);
    activity.setExplanation(null);
    activity.setUpdates(updates);
    return activity;
  }

  private UpdateActivityBuilder submissionUpdate(Submission submission) {
    return new UpdateActivityBuilder().tableName(Submission.TABLE_NAME)
        .actionType(ActionType.UPDATE).recordId(submission.getId());
  }

  /**
   * Creates an activity about update of digestion.
   *
   * @param digestion
   *          digestion containing new properties/values
   * @param explanation
   *          explanation
   * @return activity about update of digestion
   */
  @CheckReturnValue
  public Optional<Activity> update(Digestion digestion, String explanation) {
    User user = authorizationService.getCurrentUser();
    final Digestion oldDigestion = digestionRepository.findOne(digestion.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    updateBuilders.add(digestionUpdate(digestion).column("protocol")
        .oldValue(oldDigestion.getProtocol().getId()).newValue(digestion.getProtocol().getId()));
    Map<Long, TreatedSample> oldTreatedSampleIds = oldDigestion.getTreatedSamples().stream()
        .collect(Collectors.toMap(ts -> ts.getId(), ts -> ts));
    digestion.getTreatedSamples().stream()
        .filter(ts -> !oldTreatedSampleIds.containsKey(ts.getId()))
        .forEach(ts -> updateBuilders.add(treatedSampleAction(ts, ActionType.INSERT)));
    digestion.getTreatedSamples().stream().filter(ts -> oldTreatedSampleIds.containsKey(ts.getId()))
        .forEach(ts -> {
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("sampleId")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getSample().getId())
              .newValue(ts.getSample().getId()));
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("containerId")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getContainer().getId())
              .newValue(ts.getContainer().getId()));
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("comment")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getComment())
              .newValue(ts.getComment()));
        });

    List<UpdateActivity> updates = updateBuilders.stream().filter(builder -> builder.isChanged())
        .map(builder -> builder.build()).collect(Collectors.toList());

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(digestion.getId());
      activity.setUser(user);
      activity.setTableName("treatment");
      activity.setExplanation(explanation);
      activity.setUpdates(updates);
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  private UpdateActivityBuilder digestionUpdate(Digestion digestion) {
    return new UpdateActivityBuilder().tableName("treatment").recordId(digestion.getId())
        .actionType(ActionType.UPDATE);
  }

  private UpdateActivityBuilder treatedSampleAction(TreatedSample ts, ActionType actionType) {
    return new UpdateActivityBuilder().tableName("treatedsample").recordId(ts.getId())
        .actionType(actionType);
  }

  /**
   * Creates an activity about digestion being marked as erroneous.
   *
   * @param digestion
   *          erroneous digestion that was undone
   * @param explanation
   *          explanation of what was incorrect with the digestion
   * @return activity about digestion being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(final Digestion digestion, final String explanation) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(digestion.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about digestion being marked as failed.
   *
   * @param digestion
   *          failed digestion that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about digestion being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(final Digestion digestion, final String failedDescription,
      final Collection<SampleContainer> bannedContainers) {
    final User user = authorizationService.getCurrentUser();

    // Log update for banned containers.
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    if (bannedContainers != null) {
      for (SampleContainer container : bannedContainers) {
        SampleContainer oldContainer = sampleContainerRepository.findOne(container.getId());
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

    final String explanation = failedDescription;
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(digestion.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(updates);
    return activity;
  }
}
