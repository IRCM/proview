package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;
import static ca.qc.ircm.proview.submission.web.SubmissionView.VIEW_NAME;
import static ca.qc.ircm.proview.text.Strings.property;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionViewIT extends SpringUIUnitTest {

  private static final QSubmission qsubmission = QSubmission.submission;
  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionView.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String INJECTION_TYPE_PREFIX = messagePrefix(InjectionType.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX = messagePrefix(
      MassDetectionInstrument.class);
  private static final String MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX = messagePrefix(
      MassDetectionInstrumentSource.class);
  private static final String PROTEIN_IDENTIFICATION_PREFIX = messagePrefix(
      ProteinIdentification.class);
  private static final String PROTEOLYTIC_DIGESTION_PREFIX = messagePrefix(
      ProteolyticDigestion.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final String GEL_COLORATION_PREFIX = messagePrefix(GelColoration.class);
  private static final String GEL_SEPARATION_PREFIX = messagePrefix(GelSeparation.class);
  private static final String GEL_THICKNESS_PREFIX = messagePrefix(GelThickness.class);
  private static final String PROTEIN_CONTENT_PREFIX = messagePrefix(ProteinContent.class);
  private static final String QUANTIFICATION_PREFIX = messagePrefix(Quantification.class);
  private static final String STORAGE_TEMPERATURE_PREFIX = messagePrefix(StorageTemperature.class);
  private static final String SOLVENT_PREFIX = messagePrefix(Solvent.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewIT.class);
  @Autowired
  private SubmissionRepository repository;
  private final String experiment = "my experiment";
  private final String goal = "my goal";
  private final String taxonomy = "my taxon";
  private final String protein = "my protein";
  private final Double molecularWeight = 12.3;
  private final String postTranslationModification = "glyco";
  private final SampleType sampleType = SampleType.SOLUTION;
  private final int samplesCount = 2;
  private final String sampleName1 = "my sample 1";
  private final String sampleName2 = "my sample 2";
  private final List<String> sampleNames = Arrays.asList(sampleName1, sampleName2);
  private final String sampleNamesString = sampleName1 + ", " + sampleName2;
  private final String quantity = "13g";
  private final String volume = "9 ml";
  private final GelSeparation separation = GelSeparation.TWO_DIMENSION;
  private final GelThickness thickness = GelThickness.TWO;
  private final GelColoration coloration = GelColoration.OTHER;
  private final String otherColoration = "my coloration";
  private final String developmentTime = "20s";
  private final boolean destained = true;
  private final Double weightMarkerQuantity = 5.1;
  private final String proteinQuantity = "11g";
  private final ProteolyticDigestion digestion = ProteolyticDigestion.DIGESTED;
  private final String usedDigestion = "my used digestion";
  private final String otherDigestion = "my other digestion";
  private final ProteinContent proteinContent = ProteinContent.LARGE;
  private final MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private final ProteinIdentification identification = ProteinIdentification.OTHER;
  private final String identificationLink = "http://www.unitprot.org/mydatabase";
  private final Quantification quantification = Quantification.SILAC;
  private final String quantificationComment = "Heavy: Lys8, Arg10\nMedium: Lys4, Arg6";
  private final String solvent = "ethanol";
  private final String formula = "ch3oh";
  private final double monoisotopicMass = 18.1;
  private final double averageMass = 18.2;
  private final String toxicity = "poison";
  private final boolean lightSensitive = true;
  private final StorageTemperature storageTemperature = StorageTemperature.MEDIUM;
  private final boolean highResolution = true;
  private final List<Solvent> solvents = Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3,
      Solvent.OTHER);
  private final String otherSolvent = "acetone";
  private final InjectionType injection = InjectionType.LC_MS;
  private final MassDetectionInstrumentSource source = MassDetectionInstrumentSource.NSI;
  private final String comment = "comment first line\nSecond line";
  private Path file1;
  private Path file2;

  @BeforeEach
  public void beforeTest() throws Throwable {
    file1 = Paths.get(Objects.requireNonNull(getClass().getResource("/gelimages1.png")).toURI());
    file2 = Paths.get(Objects.requireNonNull(getClass().getResource("/structure1.png")).toURI());
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  private void setFields(SubmissionView view) {
    test(view.comment).setValue(comment);
    test(view.upload).upload(file1.toFile());
    test(view.upload).upload(file2.toFile());
  }

  private void setFields(LcmsmsSubmissionForm form, SampleType sampleType) {
    test(form.experiment).setValue(experiment);
    test(form.goal).setValue(goal);
    test(form.taxonomy).setValue(taxonomy);
    test(form.protein).setValue(protein);
    test(form.molecularWeight).setValue(String.valueOf(molecularWeight));
    test(form.postTranslationModification).setValue(postTranslationModification);
    test(form.sampleType).selectItem(form.getTranslation(SAMPLE_TYPE_PREFIX + sampleType.name()));
    test(form.samplesCount).setValue(String.valueOf(samplesCount));
    test(form.samplesNames).setValue(sampleNamesString);
    if (sampleType != SampleType.GEL) {
      test(form.quantity).setValue(quantity);
      test(form.volume).setValue(volume);
    } else {
      test(form.separation).selectItem(
          form.getTranslation(GEL_SEPARATION_PREFIX + separation.name()));
      test(form.thickness).selectItem(form.getTranslation(GEL_THICKNESS_PREFIX + thickness.name()));
      test(form.coloration).selectItem(
          form.getTranslation(GEL_COLORATION_PREFIX + coloration.name()));
      test(form.otherColoration).setValue(otherColoration);
      test(form.developmentTime).setValue(developmentTime);
      if (form.destained.getValue() != destained) {
        test(form.destained).click();
      }
      test(form.weightMarkerQuantity).setValue(String.valueOf(weightMarkerQuantity));
      test(form.proteinQuantity).setValue(proteinQuantity);
    }
    test(form.digestion).selectItem(
        form.getTranslation(PROTEOLYTIC_DIGESTION_PREFIX + digestion.name()));
    if (digestion == ProteolyticDigestion.DIGESTED) {
      test(form.usedDigestion).setValue(usedDigestion);
    } else if (digestion == ProteolyticDigestion.OTHER) {
      test(form.otherDigestion).setValue(otherDigestion);
    }
    test(form.proteinContent).selectItem(
        form.getTranslation(PROTEIN_CONTENT_PREFIX + proteinContent.name()));
    test(form.instrument).selectItem(
        form.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()));
    test(form.identification).selectItem(
        form.getTranslation(PROTEIN_IDENTIFICATION_PREFIX + identification.name()));
    test(form.identificationLink).setValue(identificationLink);
    test(form.quantification).selectItem(
        form.getTranslation(QUANTIFICATION_PREFIX + quantification.name()));
    test(form.quantificationComment).setValue(quantificationComment);
  }

  private void setFields(SmallMoleculeSubmissionForm form) {
    test(form.sampleType).selectItem(form.getTranslation(SAMPLE_TYPE_PREFIX + sampleType.name()));
    test(form.sampleName).setValue(sampleName1);
    test(form.solvent).setValue(solvent);
    test(form.formula).setValue(formula);
    test(form.monoisotopicMass).setValue(String.valueOf(monoisotopicMass));
    test(form.averageMass).setValue(String.valueOf(averageMass));
    test(form.toxicity).setValue(toxicity);
    if (form.lightSensitive.getValue() != lightSensitive) {
      test(form.lightSensitive).click();
    }
    test(form.storageTemperature).selectItem(
        form.getTranslation(STORAGE_TEMPERATURE_PREFIX + storageTemperature.name()));
    test(form.highResolution).selectItem(
        form.getTranslation(SUBMISSION_PREFIX + property(HIGH_RESOLUTION, highResolution)));
    test(form.solvents).deselectAll();
    solvents.forEach(solvent -> test(form.solvents).selectItem(
        form.getTranslation(SOLVENT_PREFIX + solvent.name())));
    test(form.otherSolvent).setValue(otherSolvent);
  }

  private void setFields(IntactProteinSubmissionForm form) {
    test(form.experiment).setValue(experiment);
    test(form.goal).setValue(goal);
    test(form.taxonomy).setValue(taxonomy);
    test(form.protein).setValue(protein);
    test(form.molecularWeight).setValue(String.valueOf(molecularWeight));
    test(form.postTranslationModification).setValue(postTranslationModification);
    test(form.sampleType).selectItem(form.getTranslation(SAMPLE_TYPE_PREFIX + sampleType.name()));
    test(form.samplesCount).setValue(String.valueOf(samplesCount));
    test(form.samplesNames).setValue(sampleNamesString);
    test(form.quantity).setValue(quantity);
    test(form.volume).setValue(volume);
    test(form.injection).selectItem(form.getTranslation(INJECTION_TYPE_PREFIX + injection.name()));
    test(form.source).selectItem(
        form.getTranslation(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + source.name()));
    test(form.instrument).selectItem(
        form.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()));
  }

  @Test
  public void save_LcmsmsSolution() throws Throwable {
    SubmissionView view = navigate(SubmissionView.class);
    test(view.service).select(view.lcmsms.getLabel());
    setFields(view.lcmsmsSubmissionForm, sampleType);
    setFields(view);

    test(view.save).click();

    Notification notification = $(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, experiment),
        test(notification).getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).orElseThrow();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertNotNull(submission.getSamples());
    assertEquals(samplesCount, submission.getSamples().size());
    for (int i = 0; i < samplesCount; i++) {
      assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      assertEquals(sampleType, submission.getSamples().get(i).getType());
      assertEquals(sampleNames.get(i), submission.getSamples().get(i).getName());
      assertEquals(quantity, submission.getSamples().get(i).getQuantity());
      assertEquals(volume, submission.getSamples().get(i).getVolume());
    }
    assertEquals(digestion, submission.getDigestion());
    switch (digestion) {
      case DIGESTED:
        assertEquals(usedDigestion, submission.getUsedDigestion());
        break;
      case OTHER:
        assertEquals(otherDigestion, submission.getOtherDigestion());
        break;
      default:
    }
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(instrument, submission.getInstrument());
    assertEquals(identification, submission.getIdentification());
    if (identification == ProteinIdentification.OTHER) {
      assertEquals(identificationLink, submission.getIdentificationLink());
    }
    assertEquals(quantification, submission.getQuantification());
    if (quantification == Quantification.SILAC || quantification == Quantification.TMT) {
      assertEquals(quantificationComment, submission.getQuantificationComment());
    }
    assertEquals(comment, submission.getComment());
    assertEquals(2, submission.getFiles().size());
    assertEquals(file1.getFileName().toString(), submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    assertEquals(file2.getFileName().toString(), submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsView.class).single();
  }

  @Test
  public void save_LcmsmsGel() throws Throwable {
    SubmissionView view = navigate(SubmissionView.class);
    test(view.service).select(view.lcmsms.getLabel());
    setFields(view.lcmsmsSubmissionForm, SampleType.GEL);
    setFields(view);

    test(view.save).click();

    Notification notification = $(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, experiment),
        test(notification).getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).orElseThrow();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertNotNull(submission.getSamples());
    assertEquals(samplesCount, submission.getSamples().size());
    for (int i = 0; i < samplesCount; i++) {
      assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      assertEquals(SampleType.GEL, submission.getSamples().get(i).getType());
      assertEquals(sampleNames.get(i), submission.getSamples().get(i).getName());
    }
    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(coloration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(destained, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity());
    assertEquals(proteinQuantity, submission.getProteinQuantity());
    assertEquals(digestion, submission.getDigestion());
    switch (digestion) {
      case DIGESTED:
        assertEquals(usedDigestion, submission.getUsedDigestion());
        break;
      case OTHER:
        assertEquals(otherDigestion, submission.getOtherDigestion());
        break;
      default:
    }
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(instrument, submission.getInstrument());
    assertEquals(identification, submission.getIdentification());
    if (identification == ProteinIdentification.OTHER) {
      assertEquals(identificationLink, submission.getIdentificationLink());
    }
    assertEquals(quantification, submission.getQuantification());
    if (quantification == Quantification.SILAC || quantification == Quantification.TMT) {
      assertEquals(quantificationComment, submission.getQuantificationComment());
    }
    assertEquals(comment, submission.getComment());
    assertEquals(2, submission.getFiles().size());
    assertEquals(file1.getFileName().toString(), submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    assertEquals(file2.getFileName().toString(), submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsView.class).single();
  }

  @Test
  public void save_SmallMolecule() throws Throwable {
    SubmissionView view = navigate(SubmissionView.class);
    test(view.service).select(view.smallMolecule.getLabel());
    setFields(view.smallMoleculeSubmissionForm);
    setFields(view);

    test(view.save).click();

    Notification notification = $(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, sampleName1),
        test(notification).getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(sampleName1))
        .orElseThrow();
    assertEquals(sampleName1, submission.getExperiment());
    assertEquals(solvent, submission.getSolutionSolvent());
    assertEquals(formula, submission.getFormula());
    assertNotNull(submission.getMonoisotopicMass());
    assertEquals(monoisotopicMass, submission.getMonoisotopicMass(), 0.0001);
    assertNotNull(submission.getAverageMass());
    assertEquals(averageMass, submission.getAverageMass(), 0.0001);
    assertEquals(toxicity, submission.getToxicity());
    assertEquals(lightSensitive, submission.isLightSensitive());
    assertEquals(storageTemperature, submission.getStorageTemperature());
    assertEquals(highResolution, submission.isHighResolution());
    assertEquals(solvents.size(), submission.getSolvents().size());
    for (Solvent solvent : solvents) {
      assertTrue(submission.getSolvents().contains(solvent));
    }
    assertEquals(otherSolvent, submission.getOtherSolvent());
    assertNotNull(submission.getSamples());
    assertEquals(1, submission.getSamples().size());
    assertEquals(sampleType, submission.getSamples().get(0).getType());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(comment, submission.getComment());
    assertEquals(2, submission.getFiles().size());
    assertEquals(file1.getFileName().toString(), submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    assertEquals(file2.getFileName().toString(), submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsView.class).single();
  }

  @Test
  public void save_IntactProtein() throws Throwable {
    SubmissionView view = navigate(SubmissionView.class);
    test(view.service).select(view.intactProtein.getLabel());
    setFields(view.intactProteinSubmissionForm);
    setFields(view);

    test(view.save).click();

    Notification notification = $(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, experiment),
        test(notification).getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).orElseThrow();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertNotNull(submission.getSamples());
    assertEquals(samplesCount, submission.getSamples().size());
    for (int i = 0; i < samplesCount; i++) {
      assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      assertEquals(sampleType, submission.getSamples().get(i).getType());
      assertEquals(sampleNames.get(i), submission.getSamples().get(i).getName());
      assertEquals(quantity, submission.getSamples().get(i).getQuantity());
      assertEquals(volume, submission.getSamples().get(i).getVolume());
    }
    assertEquals(injection, submission.getInjectionType());
    assertEquals(source, submission.getSource());
    assertEquals(instrument, submission.getInstrument());
    assertEquals(comment, submission.getComment());
    assertEquals(2, submission.getFiles().size());
    assertEquals(file1.getFileName().toString(), submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    assertEquals(file2.getFileName().toString(), submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsView.class).single();
  }
}
