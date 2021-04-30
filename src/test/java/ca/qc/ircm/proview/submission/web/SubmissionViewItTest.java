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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.web.SubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;
import static ca.qc.ircm.proview.submission.web.SubmissionView.VIEW_NAME;
import static ca.qc.ircm.proview.text.Strings.property;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.AppResources;
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
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.Download;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration tests for {@link SubmissionView}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionViewItTest extends AbstractTestBenchTestCase {
  private static final QSubmission qsubmission = QSubmission.submission;
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewItTest.class);
  @Autowired
  private SubmissionRepository repository;
  @Value("${spring.application.name}")
  private String applicationName;
  @Value("${download-home}")
  protected Path downloadHome;
  private String experiment = "my experiment";
  private String goal = "my goal";
  private String taxonomy = "my taxon";
  private String protein = "my protein";
  private Double molecularWeight = 12.3;
  private String postTranslationModification = "glyco";
  private SampleType sampleType = SampleType.SOLUTION;
  private int samplesCount = 2;
  private String sampleName1 = "my sample 1";
  private String sampleName2 = "my sample 2";
  private List<String> sampleNames = Arrays.asList(sampleName1, sampleName2);
  private String sampleNamesString = sampleName1 + ", " + sampleName2;
  private String quantity = "13g";
  private String volume = "9 ml";
  private GelSeparation separation = GelSeparation.TWO_DIMENSION;
  private GelThickness thickness = GelThickness.TWO;
  private GelColoration coloration = GelColoration.OTHER;
  private String otherColoration = "my coloration";
  private String developmentTime = "20s";
  private boolean destained = true;
  private Double weightMarkerQuantity = 5.1;
  private String proteinQuantity = "11g";
  private ProteolyticDigestion digestion = ProteolyticDigestion.DIGESTED;
  private String usedDigestion = "my used digestion";
  private String otherDigestion = "my other digestion";
  private ProteinContent proteinContent = ProteinContent.LARGE;
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private ProteinIdentification identification = ProteinIdentification.OTHER;
  private String identificationLink = "http://www.unitprot.org/mydatabase";
  private Quantification quantification = Quantification.SILAC;
  private String quantificationComment = "Heavy: Lys8, Arg10\nMedium: Lys4, Arg6";
  private String solvent = "ethanol";
  private String formula = "ch3oh";
  private double monoisotopicMass = 18.1;
  private double averageMass = 18.2;
  private String toxicity = "poison";
  private boolean lightSensitive = true;
  private StorageTemperature storageTemperature = StorageTemperature.MEDIUM;
  private boolean highResolution = true;
  private List<Solvent> solvents =
      Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3, Solvent.OTHER);
  private String otherSolvent = "acetone";
  private InjectionType injection = InjectionType.LC_MS;
  private MassDetectionInstrumentSource source = MassDetectionInstrumentSource.NSI;
  private String comment = "comment first line\nSecond line";
  private Path file1;
  private Path file2;

  @Before
  public void beforeTest() throws Throwable {
    file1 = Paths.get(getClass().getResource("/gelimages1.png").toURI());
    file2 = Paths.get(getClass().getResource("/structure1.png").toURI());
  }

  private void open() {
    openView(VIEW_NAME);
  }

  private void setFields(SubmissionViewElement view) throws Throwable {
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
    form.sampleType().selectByText(sampleType.getLabel(locale));
    form.samplesCount().setValue(String.valueOf(samplesCount));
    form.samplesNames().setValue(sampleNamesString);
    if (sampleType != SampleType.GEL) {
      form.quantity().setValue(quantity);
      form.volume().setValue(volume);
    } else {
      form.separation().selectByText(separation.getLabel(locale));
      form.thickness().selectByText(thickness.getLabel(locale));
      form.coloration().selectByText(coloration.getLabel(locale));
      form.otherColoration().setValue(otherColoration);
      form.developmentTime().setValue(developmentTime);
      form.destained().setChecked(destained);
      form.weightMarkerQuantity().setValue(String.valueOf(weightMarkerQuantity));
      form.proteinQuantity().setValue(proteinQuantity);
    }
    form.digestion().selectByText(digestion.getLabel(locale));
    form.usedDigestion().setValue(usedDigestion);
    form.otherDigestion().setValue(otherDigestion);
    form.proteinContent().selectByText(proteinContent.getLabel(locale));
    form.instrument().selectByText(instrument.getLabel(locale));
    form.identification().selectByText(identification.getLabel(locale));
    form.identificationLink().setValue(identificationLink);
    form.quantification().selectByText(quantification.getLabel(locale));
    form.quantificationComment().setValue(quantificationComment);
  }

  private void setFields(SmallMoleculeSubmissionFormElement form) {
    Locale locale = currentLocale();
    AppResources submissionResource = new AppResources(Submission.class, locale);
    form.sampleType().selectByText(sampleType.getLabel(locale));
    form.sampleName().setValue(sampleName1);
    form.solvent().setValue(solvent);
    form.formula().setValue(formula);
    form.monoisotopicMass().setValue(String.valueOf(monoisotopicMass));
    form.averageMass().setValue(String.valueOf(averageMass));
    form.toxicity().setValue(toxicity);
    form.lightSensitive().setChecked(lightSensitive);
    form.storageTemperature().selectByText(storageTemperature.getLabel(locale));
    form.highResolution()
        .selectByText(submissionResource.message(property(HIGH_RESOLUTION, highResolution)));
    solvents.forEach(
        solvent -> form.solvents().solvent(solvent).setChecked(solvents.contains(solvent)));
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
    form.sampleType().selectByText(sampleType.getLabel(locale));
    form.samplesCount().setValue(String.valueOf(samplesCount));
    form.samplesNames().setValue(sampleNamesString);
    form.quantity().setValue(quantity);
    form.volume().setValue(volume);
    form.injection().selectByText(injection.getLabel(locale));
    form.source().selectByText(source.getLabel(locale));
    form.instrument().selectByText(instrument.getLabel(locale));
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(SigninView.class, locale).message(TITLE,
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(SubmissionView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).id(ID);
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.service()).isPresent());
    assertTrue(optional(() -> view.lcmsms()).isPresent());
    view.lcmsms().click();
    assertTrue(optional(() -> view.lcmsmsSubmissionForm()).isPresent());
    assertTrue(optional(() -> view.smallMolecule()).isPresent());
    view.smallMolecule().click();
    assertTrue(optional(() -> view.smallMoleculeSubmissionForm()).isPresent());
    assertTrue(optional(() -> view.intactProtein()).isPresent());
    view.intactProtein().click();
    assertTrue(optional(() -> view.intactProteinSubmissionForm()).isPresent());
    assertTrue(optional(() -> view.comment()).isPresent());
    assertTrue(optional(() -> view.upload()).isPresent());
    assertTrue(optional(() -> view.files()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void save_LcmsmsSolution() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).id(ID);
    view.lcmsms().click();
    setFields(view.lcmsmsSubmissionForm(), sampleType);
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(SubmissionView.class);
    assertEquals(resources.message(SAVED, experiment), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).get();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertTrue(submission.getSamples() != null);
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
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void save_LcmsmsGel() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).id(ID);
    view.lcmsms().click();
    setFields(view.lcmsmsSubmissionForm(), SampleType.GEL);
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(SubmissionView.class);
    assertEquals(resources.message(SAVED, experiment), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).get();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertTrue(submission.getSamples() != null);
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
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void save_SmallMolecule() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).id(ID);
    view.smallMolecule().click();
    setFields(view.smallMoleculeSubmissionForm());
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(SubmissionView.class);
    assertEquals(resources.message(SAVED, sampleName1), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(sampleName1)).get();
    assertEquals(sampleName1, submission.getExperiment());
    assertEquals(solvent, submission.getSolutionSolvent());
    assertEquals(formula, submission.getFormula());
    assertEquals(monoisotopicMass, submission.getMonoisotopicMass(), 0.0001);
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
    assertTrue(submission.getSamples() != null);
    assertEquals(1, submission.getSamples().size());
    assertEquals(sampleType, submission.getSamples().get(0).getType());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(comment, submission.getComment());
    assertEquals(2, submission.getFiles().size());
    assertEquals(file1.getFileName().toString(), submission.getFiles().get(0).getFilename());
    assertArrayEquals(Files.readAllBytes(file1), submission.getFiles().get(0).getContent());
    assertEquals(file2.getFileName().toString(), submission.getFiles().get(1).getFilename());
    assertArrayEquals(Files.readAllBytes(file2), submission.getFiles().get(1).getContent());
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void save_IntactProtein() throws Throwable {
    open();
    SubmissionViewElement view = $(SubmissionViewElement.class).id(ID);
    view.intactProtein().click();
    setFields(view.intactProteinSubmissionForm());
    setFields(view);

    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(SubmissionView.class);
    assertEquals(resources.message(SAVED, experiment), notification.getText());
    Submission submission = repository.findOne(qsubmission.experiment.eq(experiment)).get();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertTrue(submission.getSamples() != null);
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
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @Download
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void downloadFile() throws Throwable {
    Files.createDirectories(downloadHome);
    Path downloaded = downloadHome.resolve("protocol.txt");
    Files.deleteIfExists(downloaded);
    Path source = Paths.get(getClass().getResource("/submissionfile1.txt").toURI());
    openView(VIEW_NAME, "1");
    SubmissionViewElement view = $(SubmissionViewElement.class).id(ID);
    AnchorElement filename = view.filename(0);
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
