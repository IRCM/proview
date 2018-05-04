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

package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.SampleStatusUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link MsAnalysis} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class MsAnalysisActivityService {
  private static final QSubmission qsubmission = QSubmission.submission;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected MsAnalysisActivityService() {
  }

  protected MsAnalysisActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of MS analysis.
   *
   * @param msAnalysis
   *          MS analysis
   * @return activity about insertion of MS analysis
   */
  @CheckReturnValue
  public Activity insert(final MsAnalysis msAnalysis) {
    final User user = authorizationService.getCurrentUser();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    Set<Long> submissionIds = new HashSet<>();
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      Sample newSample = acquisition.getSample();
      Sample oldSample = entityManager.find(Sample.class, acquisition.getSample().getId());
      if (newSample instanceof SubmissionSample) {
        SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
        SubmissionSample oldSubmissionSample = (SubmissionSample) oldSample;
        updateBuilders.add(new SampleStatusUpdateActivityBuilder().oldSample(oldSubmissionSample)
            .newSample(newSubmissionSample));
        Submission newSubmission = newSubmissionSample.getSubmission();
        Submission oldSubmission = oldSubmissionSample.getSubmission();
        if (submissionIds.add(newSubmission.getId())) {
          updateBuilders.add(submissionUpdate(newSubmission).column(qname(qsubmission.analysisDate))
              .oldValue(oldSubmission.getAnalysisDate()).newValue(newSubmission.getAnalysisDate()));
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
    activity.setRecordId(msAnalysis.getId());
    activity.setUser(user);
    activity.setTableName(MsAnalysis.TABLE_NAME);
    activity.setExplanation(null);
    activity.setUpdates(updates);
    return activity;
  }

  private UpdateActivityBuilder submissionUpdate(Submission submission) {
    return new UpdateActivityBuilder().tableName(Submission.TABLE_NAME)
        .actionType(ActionType.UPDATE).recordId(submission.getId());
  }

  /**
   * Creates an activity about update of MS analysis.
   *
   * @param msAnalysis
   *          MS analysis containing new properties/values
   * @param explanation
   *          explanation
   * @return activity about update of MS analysis
   */
  @CheckReturnValue
  public Optional<Activity> update(MsAnalysis msAnalysis, String explanation) {
    User user = authorizationService.getCurrentUser();
    final MsAnalysis oldMsAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    updateBuilders.add(msAnalysisUpdate(msAnalysis).column("massDetectionInstrument")
        .oldValue(oldMsAnalysis.getMassDetectionInstrument())
        .newValue(msAnalysis.getMassDetectionInstrument()));
    updateBuilders.add(msAnalysisUpdate(msAnalysis).column("source")
        .oldValue(oldMsAnalysis.getSource()).newValue(msAnalysis.getSource()));
    Map<Long, Acquisition> oldAcquisitionIds = oldMsAnalysis.getAcquisitions().stream()
        .collect(Collectors.toMap(ts -> ts.getId(), ts -> ts));
    msAnalysis.getAcquisitions().stream().filter(ts -> !oldAcquisitionIds.containsKey(ts.getId()))
        .forEach(ts -> updateBuilders.add(acquisitionAction(ts, ActionType.INSERT)));
    msAnalysis.getAcquisitions().stream().filter(ts -> oldAcquisitionIds.containsKey(ts.getId()))
        .forEach(ts -> {
          updateBuilders.add(acquisitionAction(ts, ActionType.UPDATE).column("sampleId")
              .oldValue(oldAcquisitionIds.get(ts.getId()).getSample().getId())
              .newValue(ts.getSample().getId()));
          updateBuilders.add(acquisitionAction(ts, ActionType.UPDATE).column("containerId")
              .oldValue(oldAcquisitionIds.get(ts.getId()).getContainer().getId())
              .newValue(ts.getContainer().getId()));
          updateBuilders.add(acquisitionAction(ts, ActionType.UPDATE).column("sampleListName")
              .oldValue(oldAcquisitionIds.get(ts.getId()).getSampleListName())
              .newValue(ts.getSampleListName()));
          updateBuilders.add(acquisitionAction(ts, ActionType.UPDATE).column("acquisitionFile")
              .oldValue(oldAcquisitionIds.get(ts.getId()).getAcquisitionFile())
              .newValue(ts.getAcquisitionFile()));
          updateBuilders.add(acquisitionAction(ts, ActionType.UPDATE).column("comment")
              .oldValue(oldAcquisitionIds.get(ts.getId()).getComment()).newValue(ts.getComment()));
        });

    List<UpdateActivity> updates = updateBuilders.stream().filter(builder -> builder.isChanged())
        .map(builder -> builder.build()).collect(Collectors.toList());

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(msAnalysis.getId());
      activity.setUser(user);
      activity.setTableName("msanalysis");
      activity.setExplanation(explanation);
      activity.setUpdates(updates);
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  private UpdateActivityBuilder msAnalysisUpdate(MsAnalysis msAnalysis) {
    return new UpdateActivityBuilder().tableName("msanalysis").recordId(msAnalysis.getId())
        .actionType(ActionType.UPDATE);
  }

  private UpdateActivityBuilder acquisitionAction(Acquisition ts, ActionType actionType) {
    return new UpdateActivityBuilder().tableName("acquisition").recordId(ts.getId())
        .actionType(actionType);
  }

  /**
   * Creates an activity about MS analysis being marked as erroneous.
   *
   * @param msAnalysis
   *          erroneous MS analysis that was undone
   * @param explanation
   *          explanation of what was incorrect with the MS analysis
   * @return activity about MS analysis being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(final MsAnalysis msAnalysis, final String explanation) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(msAnalysis.getId());
    activity.setUser(user);
    activity.setTableName("msanalysis");
    activity.setExplanation(explanation);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about MS analysis being marked as failed.
   *
   * @param msAnalysis
   *          failed MS analysis that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about MS analysis being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(final MsAnalysis msAnalysis, String failedDescription,
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

    final String explanation = failedDescription;
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(msAnalysis.getId());
    activity.setUser(user);
    activity.setTableName("msanalysis");
    activity.setExplanation(explanation);
    activity.setUpdates(updates);
    return activity;
  }
}
