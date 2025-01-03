package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleActivityService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.User;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates activities about {@link Submission} that can be recorded.
 */
@org.springframework.stereotype.Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SubmissionActivityService {
  private static final QSubmission qsubmission = QSubmission.submission;
  private final SampleActivityService sampleActivityService;
  private final SubmissionRepository repository;
  private final AuthenticatedUser authenticatedUser;

  @Autowired
  protected SubmissionActivityService(SampleActivityService sampleActivityService,
      SubmissionRepository repository, AuthenticatedUser authenticatedUser) {
    this.sampleActivityService = sampleActivityService;
    this.repository = repository;
    this.authenticatedUser = authenticatedUser;
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
    User user = authenticatedUser.getUser().orElse(null);

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
    final User user = authenticatedUser.getUser().orElse(null);

    final Submission oldSubmission = repository.findById(submission.getId()).orElse(null);

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
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.instrument))
        .oldValue(oldSubmission.getInstrument()).newValue(submission.getInstrument()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.source))
        .oldValue(oldSubmission.getSource()).newValue(submission.getSource()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.injectionType))
            .oldValue(oldSubmission.getInjectionType()).newValue(submission.getInjectionType()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.digestion))
        .oldValue(oldSubmission.getDigestion()).newValue(submission.getDigestion()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.usedDigestion))
            .oldValue(oldSubmission.getUsedDigestion()).newValue(submission.getUsedDigestion()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.otherDigestion))
            .oldValue(oldSubmission.getOtherDigestion()).newValue(submission.getOtherDigestion()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.identification))
            .oldValue(oldSubmission.getIdentification()).newValue(submission.getIdentification()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.identificationLink))
            .oldValue(oldSubmission.getIdentificationLink())
            .newValue(submission.getIdentificationLink()));
    updateBuilders
        .add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.highResolution))
            .oldValue(oldSubmission.isHighResolution()).newValue(submission.isHighResolution()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.protein))
        .oldValue(oldSubmission.getProtein()).newValue(submission.getProtein()));
    updateBuilders.add(
        new SubmissionUpdateActivityBuilder().column(qname(qsubmission.postTranslationModification))
            .oldValue(oldSubmission.getPostTranslationModification())
            .newValue(submission.getPostTranslationModification()));
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
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.contaminants))
        .oldValue(oldSubmission.getContaminants()).newValue(submission.getContaminants()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.standards))
        .oldValue(oldSubmission.getStandards()).newValue(submission.getStandards()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column(qname(qsubmission.comment))
        .oldValue(oldSubmission.getComment()).newValue(submission.getComment()));
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
        oldSubmission.getSamples().stream().filter(sample -> sample.getId() != 0)
            .map(sample -> sample.getId()).collect(Collectors.toSet());
    Set<Long> newSampleIds = submission.getSamples().stream().filter(sample -> sample.getId() != 0)
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
      activity.setTableName(Submission.TABLE_NAME);
      activity.setExplanation(explanation);
      activity.setUpdates(new LinkedList<>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
