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

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.QPlate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleActivityService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.User;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates activities about {@link Submission} that can be recorded.
 */
@org.springframework.stereotype.Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SubmissionActivityService {
  private static final QSubmission qsubmission = QSubmission.submission;
  private static final QPlate qplate = QPlate.plate;
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
    activity.setTableName(Submission.TABLE_NAME);
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
  public Optional<Activity> update(final Submission submission, final String explanation) {
    User user = authorizationService.getCurrentUser();

    final Submission oldSubmission = entityManager.find(Submission.class, submission.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    class SubmissionUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName(Submission.TABLE_NAME);
        actionType(ActionType.UPDATE);
        recordId(submission.getId());
      }
    }

    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.service))
        .oldValue(oldSubmission.getService()).newValue(submission.getService()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.taxonomy))
        .oldValue(oldSubmission.getTaxonomy()).newValue(submission.getTaxonomy()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.experiment))
        .oldValue(oldSubmission.getExperiment()).newValue(submission.getExperiment()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.goal))
        .oldValue(oldSubmission.getGoal()).newValue(submission.getGoal()));
    updateBuilders.add(
        new SubmissionUpdateActivityBuilder().column(qname(qsubmission.massDetectionInstrument))
            .oldValue(oldSubmission.getMassDetectionInstrument())
            .newValue(submission.getMassDetectionInstrument()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.source))
        .oldValue(oldSubmission.getSource()).newValue(submission.getSource()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.injectionType))
            .oldValue(oldSubmission.getInjectionType()).newValue(submission.getInjectionType()));
    updateBuilders.add(
        new SubmissionUpdateActivityBuilder().column(qname(qsubmission.proteolyticDigestionMethod))
            .oldValue(oldSubmission.getProteolyticDigestionMethod())
            .newValue(submission.getProteolyticDigestionMethod()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.usedProteolyticDigestionMethod))
        .oldValue(oldSubmission.getUsedProteolyticDigestionMethod())
        .newValue(submission.getUsedProteolyticDigestionMethod()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.otherProteolyticDigestionMethod))
        .oldValue(oldSubmission.getOtherProteolyticDigestionMethod())
        .newValue(submission.getOtherProteolyticDigestionMethod()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.proteinIdentification))
            .oldValue(oldSubmission.getProteinIdentification())
            .newValue(submission.getProteinIdentification()));
    updateBuilders.add(
        new SubmissionUpdateActivityBuilder().column(qname(qsubmission.proteinIdentificationLink))
            .oldValue(oldSubmission.getProteinIdentificationLink())
            .newValue(submission.getProteinIdentificationLink()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.enrichmentType))
            .oldValue(oldSubmission.getEnrichmentType()).newValue(submission.getEnrichmentType()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.otherEnrichmentType))
            .oldValue(oldSubmission.getOtherEnrichmentType())
            .newValue(submission.getOtherEnrichmentType()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.lowResolution))
            .oldValue(oldSubmission.isLowResolution()).newValue(submission.isLowResolution()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.highResolution))
            .oldValue(oldSubmission.isHighResolution()).newValue(submission.isHighResolution()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.msms))
        .oldValue(oldSubmission.isMsms()).newValue(submission.isMsms()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.exactMsms))
        .oldValue(oldSubmission.isExactMsms()).newValue(submission.isExactMsms()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.protein))
        .oldValue(oldSubmission.getProtein()).newValue(submission.getProtein()));
    updateBuilders.add(
        new SubmissionUpdateActivityBuilder().column(qname(qsubmission.postTranslationModification))
            .oldValue(oldSubmission.getPostTranslationModification())
            .newValue(submission.getPostTranslationModification()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.mudPitFraction))
            .oldValue(oldSubmission.getMudPitFraction()).newValue(submission.getMudPitFraction()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.proteinContent))
            .oldValue(oldSubmission.getProteinContent()).newValue(submission.getProteinContent()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.separation))
        .oldValue(oldSubmission.getSeparation()).newValue(submission.getSeparation()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.thickness))
        .oldValue(oldSubmission.getThickness()).newValue(submission.getThickness()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.coloration))
        .oldValue(oldSubmission.getColoration()).newValue(submission.getColoration()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.otherColoration)).oldValue(oldSubmission.getOtherColoration())
        .newValue(submission.getOtherColoration()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.developmentTime)).oldValue(oldSubmission.getDevelopmentTime())
        .newValue(submission.getDevelopmentTime()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.decoloration))
        .oldValue(oldSubmission.isDecoloration()).newValue(submission.isDecoloration()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.weightMarkerQuantity))
            .oldValue(oldSubmission.getWeightMarkerQuantity())
            .newValue(submission.getWeightMarkerQuantity()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.proteinQuantity)).oldValue(oldSubmission.getProteinQuantity())
        .newValue(submission.getProteinQuantity()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.formula))
        .oldValue(oldSubmission.getFormula()).newValue(submission.getFormula()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.monoisotopicMass)).oldValue(oldSubmission.getMonoisotopicMass())
        .newValue(submission.getMonoisotopicMass()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.averageMass))
        .oldValue(oldSubmission.getAverageMass()).newValue(submission.getAverageMass()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.solutionSolvent)).oldValue(oldSubmission.getSolutionSolvent())
        .newValue(submission.getSolutionSolvent()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.otherSolvent))
        .oldValue(oldSubmission.getOtherSolvent()).newValue(submission.getOtherSolvent()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.toxicity))
        .oldValue(oldSubmission.getToxicity()).newValue(submission.getToxicity()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.lightSensitive))
            .oldValue(oldSubmission.isLightSensitive()).newValue(submission.isLightSensitive()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.storageTemperature))
            .oldValue(oldSubmission.getStorageTemperature())
            .newValue(submission.getStorageTemperature()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.quantification))
            .oldValue(oldSubmission.getQuantification()).newValue(submission.getQuantification()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.quantificationComment))
            .oldValue(oldSubmission.getQuantificationComment())
            .newValue(submission.getQuantificationComment()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.comment))
        .oldValue(oldSubmission.getComment()).newValue(submission.getComment()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.additionalPrice)).oldValue(oldSubmission.getAdditionalPrice())
        .newValue(submission.getAdditionalPrice()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.user) + "Id")
        .oldValue(oldSubmission.getUser().getId()).newValue(submission.getUser().getId()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.laboratory) + "Id")
            .oldValue(oldSubmission.getLaboratory().getId())
            .newValue(submission.getLaboratory().getId()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.submissionDate))
            .oldValue(oldSubmission.getSubmissionDate()).newValue(submission.getSubmissionDate()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.sampleDeliveryDate))
            .oldValue(oldSubmission.getSampleDeliveryDate())
            .newValue(submission.getSampleDeliveryDate()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.digestionDate))
            .oldValue(oldSubmission.getDigestionDate()).newValue(submission.getDigestionDate()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.analysisDate))
        .oldValue(oldSubmission.getAnalysisDate()).newValue(submission.getAnalysisDate()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder()
        .column(qname(qsubmission.dataAvailableDate)).oldValue(oldSubmission.getDataAvailableDate())
        .newValue(submission.getDataAvailableDate()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.hidden))
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
        if (sample.getOriginalContainer() instanceof Well) {
          Plate plate = ((Well) sample.getOriginalContainer()).getPlate();
          Plate oldPlate = entityManager.find(Plate.class, plate.getId());
          updateBuilders.add(plateUpdate(plate).column(qname(qplate.name))
              .oldValue(oldPlate.getName()).newValue(plate.getName()));
        }
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
      activity.setTableName(Submission.TABLE_NAME);
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

  private UpdateActivityBuilder plateUpdate(Plate plate) {
    UpdateActivityBuilder builder = new UpdateActivityBuilder();
    builder.tableName(Plate.TABLE_NAME);
    builder.actionType(ActionType.UPDATE);
    builder.recordId(plate.getId());
    return builder;
  }
}
