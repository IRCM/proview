package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;
import static ca.qc.ircm.proview.submission.web.SubmissionView.VIEW_NAME;
import static ca.qc.ircm.proview.text.Strings.property;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
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
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.Download;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.testbench.BrowserTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionViewIT extends AbstractBrowserTestCase {

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
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewIT.class);
  @Value("${download-home}")
  protected Path downloadHome;
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private MessageSource messageSource;
  @Value("${spring.application.name}")
  private String applicationName;
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

  private void open() {
    openView(VIEW_NAME);
  }

  private void setFields(SubmissionViewElement view) {
    view.comment().setValue(comment);
    view.upload().upload(file1.toFile());
    view.upload().upload(file2.toFile());
  }

  private void setFields(LcmsmsSubmissionFormElement form, SampleType sampleType) {
    Locale locale = currentLocale();
    form.experiment().setValue(experiment);
    form.goal().setValue(goal);
    form.taxonomy().setValue(taxonomy);
    form.protein().setValue(protein);
    form.molecularWeight().setValue(String.valueOf(molecularWeight));
    form.postTranslationModification().setValue(postTranslationModification);
    form.sampleType().selectByText(
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + sampleType.name(), null, locale));
    form.samplesCount().setValue(String.valueOf(samplesCount));
    form.samplesNames().setValue(sampleNamesString);
    if (sampleType != SampleType.GEL) {
      form.quantity().setValue(quantity);
      form.volume().setValue(volume);
    } else {
      form.separation().selectByText(
          messageSource.getMessage(GEL_SEPARATION_PREFIX + separation.name(), null, locale));
      form.thickness().selectByText(
          messageSource.getMessage(GEL_THICKNESS_PREFIX + thickness.name(), null, locale));
      form.coloration().selectByText(
          messageSource.getMessage(GEL_COLORATION_PREFIX + coloration.name(), null, locale));
      form.otherColoration().setValue(otherColoration);
      form.developmentTime().setValue(developmentTime);
      form.destained().setChecked(destained);
      form.weightMarkerQuantity().setValue(String.valueOf(weightMarkerQuantity));
      form.proteinQuantity().setValue(proteinQuantity);
    }
    form.digestion().selectByText(
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + digestion.name(), null, locale));
    form.usedDigestion().setValue(usedDigestion);
    form.otherDigestion().setValue(otherDigestion);
    form.proteinContent().selectByText(
        messageSource.getMessage(PROTEIN_CONTENT_PREFIX + proteinContent.name(), null, locale));
    form.instrument().selectByText(
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name(), null,
            locale));
    form.identification().selectByText(
        messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + identification.name(), null,
            locale));
    form.identificationLink().setValue(identificationLink);
    form.quantification().selectByText(
        messageSource.getMessage(QUANTIFICATION_PREFIX + quantification.name(), null, locale));
    form.quantificationComment().setValue(quantificationComment);
  }

  private void setFields(SmallMoleculeSubmissionFormElement form) {
    Locale locale = currentLocale();
    form.sampleType().selectByText(
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + sampleType.name(), null, locale));
    form.sampleName().setValue(sampleName1);
    form.solvent().setValue(solvent);
    form.formula().setValue(formula);
    form.monoisotopicMass().setValue(String.valueOf(monoisotopicMass));
    form.averageMass().setValue(String.valueOf(averageMass));
    form.toxicity().setValue(toxicity);
    form.lightSensitive().setChecked(lightSensitive);
    form.storageTemperature().selectByText(
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + storageTemperature.name(), null,
            locale));
    form.highResolution().selectByText(
        messageSource.getMessage(SUBMISSION_PREFIX + property(HIGH_RESOLUTION, highResolution),
            null, locale));
    Stream.of(Solvent.values()).forEach(solvent -> form.solvents()
        .deselectByText(messageSource.getMessage(SOLVENT_PREFIX + solvent.name(), null, locale)));
    solvents.forEach(solvent -> form.solvents()
        .selectByText(messageSource.getMessage(SOLVENT_PREFIX + solvent.name(), null, locale)));
    form.otherSolvent().setValue(otherSolvent);
  }

  private void setFields(IntactProteinSubmissionFormElement form) {
    Locale locale = currentLocale();
    form.experiment().setValue(experiment);
    form.goal().setValue(goal);
    form.taxonomy().setValue(taxonomy);
    form.protein().setValue(protein);
    form.molecularWeight().setValue(String.valueOf(molecularWeight));
    form.postTranslationModification().setValue(postTranslationModification);
    form.sampleType().selectByText(
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + sampleType.name(), null, locale));
    form.samplesCount().setValue(String.valueOf(samplesCount));
    form.samplesNames().setValue(sampleNamesString);
    form.quantity().setValue(quantity);
    form.volume().setValue(volume);
    form.injection().selectByText(
        messageSource.getMessage(INJECTION_TYPE_PREFIX + injection.name(), null, locale));
    form.source().selectByText(
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + source.name(), null,
            locale));
    form.instrument().selectByText(
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name(), null,
            locale));
  }

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName = messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null,
        locale);
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).waitForFirst();
    assertTrue(optional(view::service).isPresent());
    assertTrue(optional(view::lcmsms).isPresent());
    view.lcmsms().click();
    assertTrue(optional(view::lcmsmsSubmissionForm).isPresent());
    assertTrue(optional(view::smallMolecule).isPresent());
    view.smallMolecule().click();
    assertTrue(optional(view::smallMoleculeSubmissionForm).isPresent());
    assertTrue(optional(view::intactProtein).isPresent());
    view.intactProtein().click();
    assertTrue(optional(view::intactProteinSubmissionForm).isPresent());
    assertTrue(optional(view::comment).isPresent());
    assertTrue(optional(view::upload).isPresent());
    assertTrue(optional(view::files).isPresent());
    assertTrue(optional(view::save).isPresent());
  }

  @BrowserTest
  public void save_LcmsmsSolution() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).waitForFirst();
    view.lcmsms().click();
    setFields(view.lcmsmsSubmissionForm(), sampleType);
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[]{experiment},
            currentLocale()), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).orElseThrow();
    Assertions.assertEquals(experiment, submission.getExperiment());
    Assertions.assertEquals(goal, submission.getGoal());
    Assertions.assertEquals(taxonomy, submission.getTaxonomy());
    Assertions.assertEquals(protein, submission.getProtein());
    Assertions.assertEquals(postTranslationModification,
        submission.getPostTranslationModification());
    assertNotNull(submission.getSamples());
    Assertions.assertEquals(samplesCount, submission.getSamples().size());
    for (int i = 0; i < samplesCount; i++) {
      Assertions.assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      Assertions.assertEquals(sampleType, submission.getSamples().get(i).getType());
      Assertions.assertEquals(sampleNames.get(i), submission.getSamples().get(i).getName());
      Assertions.assertEquals(quantity, submission.getSamples().get(i).getQuantity());
      Assertions.assertEquals(volume, submission.getSamples().get(i).getVolume());
    }
    Assertions.assertEquals(digestion, submission.getDigestion());
    switch (digestion) {
      case DIGESTED:
        Assertions.assertEquals(usedDigestion, submission.getUsedDigestion());
        break;
      case OTHER:
        Assertions.assertEquals(otherDigestion, submission.getOtherDigestion());
        break;
      default:
    }
    Assertions.assertEquals(proteinContent, submission.getProteinContent());
    Assertions.assertEquals(instrument, submission.getInstrument());
    Assertions.assertEquals(identification, submission.getIdentification());
    if (identification == ProteinIdentification.OTHER) {
      Assertions.assertEquals(identificationLink, submission.getIdentificationLink());
    }
    Assertions.assertEquals(quantification, submission.getQuantification());
    if (quantification == Quantification.SILAC || quantification == Quantification.TMT) {
      Assertions.assertEquals(quantificationComment, submission.getQuantificationComment());
    }
    Assertions.assertEquals(comment, submission.getComment());
    Assertions.assertEquals(2, submission.getFiles().size());
    Assertions.assertEquals(file1.getFileName().toString(),
        submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    Assertions.assertEquals(file2.getFileName().toString(),
        submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void save_LcmsmsGel() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).waitForFirst();
    view.lcmsms().click();
    setFields(view.lcmsmsSubmissionForm(), SampleType.GEL);
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[]{experiment},
            currentLocale()), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).orElseThrow();
    Assertions.assertEquals(experiment, submission.getExperiment());
    Assertions.assertEquals(goal, submission.getGoal());
    Assertions.assertEquals(taxonomy, submission.getTaxonomy());
    Assertions.assertEquals(protein, submission.getProtein());
    Assertions.assertEquals(postTranslationModification,
        submission.getPostTranslationModification());
    assertNotNull(submission.getSamples());
    Assertions.assertEquals(samplesCount, submission.getSamples().size());
    for (int i = 0; i < samplesCount; i++) {
      Assertions.assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      Assertions.assertEquals(SampleType.GEL, submission.getSamples().get(i).getType());
      Assertions.assertEquals(sampleNames.get(i), submission.getSamples().get(i).getName());
    }
    Assertions.assertEquals(separation, submission.getSeparation());
    Assertions.assertEquals(thickness, submission.getThickness());
    Assertions.assertEquals(coloration, submission.getColoration());
    Assertions.assertEquals(otherColoration, submission.getOtherColoration());
    Assertions.assertEquals(developmentTime, submission.getDevelopmentTime());
    Assertions.assertEquals(destained, submission.isDecoloration());
    Assertions.assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity());
    Assertions.assertEquals(proteinQuantity, submission.getProteinQuantity());
    Assertions.assertEquals(digestion, submission.getDigestion());
    switch (digestion) {
      case DIGESTED:
        Assertions.assertEquals(usedDigestion, submission.getUsedDigestion());
        break;
      case OTHER:
        Assertions.assertEquals(otherDigestion, submission.getOtherDigestion());
        break;
      default:
    }
    Assertions.assertEquals(proteinContent, submission.getProteinContent());
    Assertions.assertEquals(instrument, submission.getInstrument());
    Assertions.assertEquals(identification, submission.getIdentification());
    if (identification == ProteinIdentification.OTHER) {
      Assertions.assertEquals(identificationLink, submission.getIdentificationLink());
    }
    Assertions.assertEquals(quantification, submission.getQuantification());
    if (quantification == Quantification.SILAC || quantification == Quantification.TMT) {
      Assertions.assertEquals(quantificationComment, submission.getQuantificationComment());
    }
    Assertions.assertEquals(comment, submission.getComment());
    Assertions.assertEquals(2, submission.getFiles().size());
    Assertions.assertEquals(file1.getFileName().toString(),
        submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    Assertions.assertEquals(file2.getFileName().toString(),
        submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void save_SmallMolecule() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).waitForFirst();
    view.smallMolecule().click();
    setFields(view.smallMoleculeSubmissionForm());
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[]{sampleName1},
            currentLocale()), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(sampleName1))
        .orElseThrow();
    Assertions.assertEquals(sampleName1, submission.getExperiment());
    Assertions.assertEquals(solvent, submission.getSolutionSolvent());
    Assertions.assertEquals(formula, submission.getFormula());
    assertNotNull(submission.getMonoisotopicMass());
    Assertions.assertEquals(monoisotopicMass, submission.getMonoisotopicMass(), 0.0001);
    assertNotNull(submission.getAverageMass());
    Assertions.assertEquals(averageMass, submission.getAverageMass(), 0.0001);
    Assertions.assertEquals(toxicity, submission.getToxicity());
    Assertions.assertEquals(lightSensitive, submission.isLightSensitive());
    Assertions.assertEquals(storageTemperature, submission.getStorageTemperature());
    Assertions.assertEquals(highResolution, submission.isHighResolution());
    Assertions.assertEquals(solvents.size(), submission.getSolvents().size());
    for (Solvent solvent : solvents) {
      assertTrue(submission.getSolvents().contains(solvent));
    }
    Assertions.assertEquals(otherSolvent, submission.getOtherSolvent());
    assertNotNull(submission.getSamples());
    Assertions.assertEquals(1, submission.getSamples().size());
    Assertions.assertEquals(sampleType, submission.getSamples().get(0).getType());
    Assertions.assertEquals(sampleName1, submission.getSamples().get(0).getName());
    Assertions.assertEquals(comment, submission.getComment());
    Assertions.assertEquals(2, submission.getFiles().size());
    Assertions.assertEquals(file1.getFileName().toString(),
        submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    Assertions.assertEquals(file2.getFileName().toString(),
        submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void save_IntactProtein() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).waitForFirst();
    view.intactProtein().click();
    setFields(view.intactProteinSubmissionForm());
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[]{experiment},
            currentLocale()), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).orElseThrow();
    Assertions.assertEquals(experiment, submission.getExperiment());
    Assertions.assertEquals(goal, submission.getGoal());
    Assertions.assertEquals(taxonomy, submission.getTaxonomy());
    Assertions.assertEquals(protein, submission.getProtein());
    Assertions.assertEquals(postTranslationModification,
        submission.getPostTranslationModification());
    assertNotNull(submission.getSamples());
    Assertions.assertEquals(samplesCount, submission.getSamples().size());
    for (int i = 0; i < samplesCount; i++) {
      Assertions.assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      Assertions.assertEquals(sampleType, submission.getSamples().get(i).getType());
      Assertions.assertEquals(sampleNames.get(i), submission.getSamples().get(i).getName());
      Assertions.assertEquals(quantity, submission.getSamples().get(i).getQuantity());
      Assertions.assertEquals(volume, submission.getSamples().get(i).getVolume());
    }
    Assertions.assertEquals(injection, submission.getInjectionType());
    Assertions.assertEquals(source, submission.getSource());
    Assertions.assertEquals(instrument, submission.getInstrument());
    Assertions.assertEquals(comment, submission.getComment());
    Assertions.assertEquals(2, submission.getFiles().size());
    Assertions.assertEquals(file1.getFileName().toString(),
        submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    Assertions.assertEquals(file2.getFileName().toString(),
        submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @BrowserTest
  @Download
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void downloadFile() throws Throwable {
    Files.createDirectories(downloadHome);
    Path downloaded = downloadHome.resolve("protocol.txt");
    Files.deleteIfExists(downloaded);
    Path source = Paths.get(
        Objects.requireNonNull(getClass().getResource("/submissionfile1.txt")).toURI());
    openView(VIEW_NAME, "1");
    SubmissionViewElement view = $(SubmissionViewElement.class).waitForFirst();
    AnchorElement filename = view.files().filename(0);
    filename.click();

    // Wait for file to download.
    Thread.sleep(2000);
    assertTrue(Files.exists(downloaded));
    try {
      assertArrayEquals(Files.readAllBytes(source), Files.readAllBytes(downloaded));
    } finally {
      Files.delete(downloaded);
    }
  }
}
