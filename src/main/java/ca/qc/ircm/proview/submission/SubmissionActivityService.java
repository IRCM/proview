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

package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleActivityService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
 * Creates activities about {@link Submission} that can be recorded.
 */
@org.springframework.stereotype.Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SubmissionActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionActivityService() {
  }

  protected SubmissionActivityService(EntityManager entityManager,
      SampleActivityService sampleActivityService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.sampleActivityService = sampleActivityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of samples submission.
   *
   * @param submission
   *          samples submission
   * @return activity about insertion of samples submission
   */
  @CheckReturnValue
  public Activity insert(final Submission submission) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(submission.getId());
    activity.setUser(user);
    activity.setTableName("submission");
    activity.setExplanation(null);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about update of samples submission.
   *
   * @param newSubmission
   *          submission containing new properties/values
   * @return activity about update of samples submission
   */
  @CheckReturnValue
  public Activity update(final Submission newSubmission) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.UPDATE);
    activity.setRecordId(newSubmission.getId());
    activity.setUser(user);
    activity.setTableName("submission");
    activity.setExplanation(null);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about forced update of samples submission.
   *
   * @param submission
   *          submission containing new properties/values
   * @param explanation
   *          explanation for the changes
   * @return activity about update of samples submission
   */
  @CheckReturnValue
  public Optional<Activity> forceUpdate(final Submission submission, final String explanation) {
    User user = authorizationService.getCurrentUser();

    final Submission oldSubmission = entityManager.find(Submission.class, submission.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    class SubmissionUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("submission");
        actionType(ActionType.UPDATE);
        recordId(submission.getId());
      }
    }

    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("service")
        .oldValue(oldSubmission.getService()).newValue(submission.getService()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("taxonomy")
        .oldValue(oldSubmission.getTaxonomy()).newValue(submission.getTaxonomy()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("experiment")
        .oldValue(oldSubmission.getExperiment()).newValue(submission.getExperiment()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("goal")
        .oldValue(oldSubmission.getGoal()).newValue(submission.getGoal()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("massDetectionInstrument")
        .oldValue(oldSubmission.getMassDetectionInstrument())
        .newValue(submission.getMassDetectionInstrument()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("source")
        .oldValue(oldSubmission.getSource()).newValue(submission.getSource()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("injectionType")
        .oldValue(oldSubmission.getInjectionType()).newValue(submission.getInjectionType()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("proteolyticDigestionMethod")
        .oldValue(oldSubmission.getProteolyticDigestionMethod())
        .newValue(submission.getProteolyticDigestionMethod()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column("usedProteolyticDigestionMethod")
            .oldValue(oldSubmission.getUsedProteolyticDigestionMethod())
            .newValue(submission.getUsedProteolyticDigestionMethod()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column("otherProteolyticDigestionMethod")
            .oldValue(oldSubmission.getOtherProteolyticDigestionMethod())
            .newValue(submission.getOtherProteolyticDigestionMethod()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("proteinIdentification")
        .oldValue(oldSubmission.getProteinIdentification())
        .newValue(submission.getProteinIdentification()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("proteinIdentificationLink")
        .oldValue(oldSubmission.getProteinIdentificationLink())
        .newValue(submission.getProteinIdentificationLink()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("enrichmentType")
        .oldValue(oldSubmission.getEnrichmentType()).newValue(submission.getEnrichmentType()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("otherEnrichmentType")
        .oldValue(oldSubmission.getOtherEnrichmentType())
        .newValue(submission.getOtherEnrichmentType()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("lowResolution")
        .oldValue(oldSubmission.isLowResolution()).newValue(submission.isLowResolution()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("highResolution")
        .oldValue(oldSubmission.isHighResolution()).newValue(submission.isHighResolution()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("msms")
        .oldValue(oldSubmission.isMsms()).newValue(submission.isMsms()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("exactMsms")
        .oldValue(oldSubmission.isExactMsms()).newValue(submission.isExactMsms()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("protein")
        .oldValue(oldSubmission.getProtein()).newValue(submission.getProtein()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("postTranslationModification")
        .oldValue(oldSubmission.getPostTranslationModification())
        .newValue(submission.getPostTranslationModification()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("mudPitFraction")
        .oldValue(oldSubmission.getMudPitFraction()).newValue(submission.getMudPitFraction()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("proteinContent")
        .oldValue(oldSubmission.getProteinContent()).newValue(submission.getProteinContent()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("separation")
        .oldValue(oldSubmission.getSeparation()).newValue(submission.getSeparation()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("thickness")
        .oldValue(oldSubmission.getThickness()).newValue(submission.getThickness()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("coloration")
        .oldValue(oldSubmission.getColoration()).newValue(submission.getColoration()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("otherColoration")
        .oldValue(oldSubmission.getOtherColoration()).newValue(submission.getOtherColoration()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("developmentTime")
        .oldValue(oldSubmission.getDevelopmentTime()).newValue(submission.getDevelopmentTime()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("decoloration")
        .oldValue(oldSubmission.isDecoloration()).newValue(submission.isDecoloration()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("weightMarkerQuantity")
        .oldValue(oldSubmission.getWeightMarkerQuantity())
        .newValue(submission.getWeightMarkerQuantity()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("proteinQuantity")
        .oldValue(oldSubmission.getProteinQuantity()).newValue(submission.getProteinQuantity()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("formula")
        .oldValue(oldSubmission.getFormula()).newValue(submission.getFormula()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("monoisotopicMass")
        .oldValue(oldSubmission.getMonoisotopicMass())
        .newValue(submission.getMonoisotopicMass()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("averageMass")
        .oldValue(oldSubmission.getAverageMass()).newValue(submission.getAverageMass()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("solutionSolvent")
        .oldValue(oldSubmission.getSolutionSolvent()).newValue(submission.getSolutionSolvent()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("otherSolvent")
        .oldValue(oldSubmission.getOtherSolvent()).newValue(submission.getOtherSolvent()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("toxicity")
        .oldValue(oldSubmission.getToxicity()).newValue(submission.getToxicity()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("lightSensitive")
        .oldValue(oldSubmission.isLightSensitive()).newValue(submission.isLightSensitive()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("storageTemperature")
        .oldValue(oldSubmission.getStorageTemperature())
        .newValue(submission.getStorageTemperature()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("quantification")
        .oldValue(oldSubmission.getQuantification()).newValue(submission.getQuantification()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("quantificationComment")
        .oldValue(oldSubmission.getQuantificationComment())
        .newValue(submission.getQuantificationComment()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("comment")
        .oldValue(oldSubmission.getComment()).newValue(submission.getComment()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("additionalPrice")
        .oldValue(oldSubmission.getAdditionalPrice()).newValue(submission.getAdditionalPrice()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("userId")
        .oldValue(oldSubmission.getUser().getId()).newValue(submission.getUser().getId()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("laboratoryId")
        .oldValue(oldSubmission.getLaboratory().getId())
        .newValue(submission.getLaboratory().getId()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("submissionDate")
        .oldValue(oldSubmission.getSubmissionDate()).newValue(submission.getSubmissionDate()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("hidden")
        .oldValue(oldSubmission.isHidden()).newValue(submission.isHidden()));
    // Sample.
    Set<Long> oldSampleIds =
        oldSubmission.getSamples().stream().filter(sample -> sample.getId() != null)
            .map(sample -> sample.getId()).collect(Collectors.toSet());
    Set<Long> newSampleIds =
        submission.getSamples().stream().filter(sample -> sample.getId() != null)
            .map(sample -> sample.getId()).collect(Collectors.toSet());
    for (SubmissionSample sample : oldSubmission.getSamples()) {
      if (!newSampleIds.contains(sample.getId())) {
        updateBuilders.add(new UpdateActivityBuilder().actionType(ActionType.DELETE)
            .tableName(Sample.TABLE_NAME).recordId(sample.getId()));
      }
    }
    for (SubmissionSample sample : submission.getSamples()) {
      if (oldSampleIds.contains(sample.getId())) {
        Optional<Activity> optionalActivity = sampleActivityService.update(sample, explanation);
        optionalActivity.ifPresent(activity -> updateBuilders.addAll(activity.getUpdates().stream()
            .map(ua -> new UpdateActivityBuilder(ua)).collect(Collectors.toList())));
      } else {
        updateBuilders.add(new UpdateActivityBuilder().actionType(ActionType.INSERT)
            .tableName(Sample.TABLE_NAME).recordId(sample.getId()));
      }
    }
    // Solvents.
    List<Solvent> oldSolvents = oldSubmission.getSolvents();
    List<Solvent> newSolvents = submission.getSolvents();
    Collections.sort(oldSolvents);
    Collections.sort(newSolvents);
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("solvent").oldValue(oldSolvents)
        .newValue(newSolvents));
    // Files.
    List<String> oldFiles = oldSubmission.getFiles() != null ? oldSubmission.getFiles().stream()
        .map(file -> file.getFilename()).collect(Collectors.toList()) : new ArrayList<>();
    List<String> newFiles = submission.getFiles() != null ? submission.getFiles().stream()
        .map(file -> file.getFilename()).collect(Collectors.toList()) : new ArrayList<>();
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("submissionfiles")
        .oldValue(oldFiles).newValue(newFiles));

    // Keep updates that changed.
    final Collection<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(submission.getId());
      activity.setUser(user);
      activity.setTableName("submission");
      activity.setExplanation(explanation);
      activity.setUpdates(new LinkedList<>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Creates an activity aobut approval of analysis of samples in submission.
   *
   * @param submission
   *          submission with new sample statuses
   * @return activity about approval of analysis of samples in submission
   */
  public Optional<Activity> approve(final Submission submission) {
    User user = authorizationService.getCurrentUser();

    Submission old = entityManager.find(Submission.class, submission.getId());
    Map<Long, SubmissionSample> oldSamplesMap =
        old.getSamples().stream().collect(Collectors.toMap(Sample::getId, sample -> sample));
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();

    for (SubmissionSample sample : submission.getSamples()) {
      oldSamplesMap.get(sample.getId());
      UpdateActivityBuilder builder = sampleUpdate(sample);
      builder.column(submissionSample.status.getMetadata().getName());
      builder.newValue(sample.getStatus().name());
      builder.oldValue(oldSamplesMap.get(sample.getId()).getStatus().name());
      updateBuilders.add(builder);
    }

    // Keep updates that changed.
    final Collection<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(submission.getId());
      activity.setUser(user);
      activity.setTableName(Submission.TABLE_NAME);
      activity.setUpdates(new LinkedList<>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  private UpdateActivityBuilder sampleUpdate(SubmissionSample sample) {
    UpdateActivityBuilder builder = new UpdateActivityBuilder();
    builder.tableName(Sample.TABLE_NAME);
    builder.actionType(ActionType.UPDATE);
    builder.recordId(sample.getId());
    return builder;
  }
}
