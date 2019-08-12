/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.web.LcmsmsSubmissionFormPresenter.Samples;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class LcmsmsSubmissionFormPresenterTest {
  @Autowired
  private LcmsmsSubmissionFormPresenter presenter;
  @Mock
  private LcmsmsSubmissionForm form;
  @MockBean
  private SubmissionSampleService sampleService;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(LcmsmsSubmissionForm.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);
  private Submission newSubmission;
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
  private String samplesNames = sampleName1 + ", " + sampleName2;
  private String quantity = "13g";
  private String volume = "9 ml";
  private GelSeparation separation = GelSeparation.TWO_DIMENSION;
  private GelThickness thickness = GelThickness.TWO;
  private GelColoration coloration = GelColoration.SYPRO;
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
  private ProteinIdentification identification = ProteinIdentification.UNIPROT;
  private String identificationLink = "http://www.unitprot.org/mydatabase";
  private Quantification quantification = Quantification.SILAC;
  private String quantificationComment = "Heavy: Lys8, Arg10\nMedium: Lys4, Arg6";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    form.experiment = new TextField();
    form.goal = new TextField();
    form.taxonomy = new TextField();
    form.protein = new TextField();
    form.molecularWeight = new TextField();
    form.postTranslationModification = new TextField();
    form.sampleType = new RadioButtonGroup<>();
    form.samplesCount = new TextField();
    form.samplesNames = new TextArea();
    form.quantity = new TextField();
    form.volume = new TextField();
    form.separation = new ComboBox<>();
    form.separation.setItems(GelSeparation.values());
    form.thickness = new ComboBox<>();
    form.thickness.setItems(GelThickness.values());
    form.coloration = new ComboBox<>();
    form.coloration.setItems(GelColoration.values());
    form.otherColoration = new TextField();
    form.developmentTime = new TextField();
    form.destained = new Checkbox();
    form.weightMarkerQuantity = new TextField();
    form.proteinQuantity = new TextField();
    form.digestion = new ComboBox<>();
    form.digestion.setItems(ProteolyticDigestion.values());
    form.usedDigestion = new TextField();
    form.otherDigestion = new TextField();
    form.proteinContent = new RadioButtonGroup<>();
    form.instrument = new ComboBox<>();
    form.instrument.setItems(MassDetectionInstrument.values());
    form.identification = new RadioButtonGroup<>();
    form.identificationLink = new TextField();
    form.quantification = new ComboBox<>();
    form.quantification.setItems(Quantification.values());
    form.quantificationComment = new TextArea();
    presenter.init(form);
    presenter.localeChange(locale);
    newSubmission = new Submission();
    newSubmission.setSamples(new ArrayList<>());
    SubmissionSample sample = new SubmissionSample();
    sample.setType(SampleType.DRY);
    newSubmission.getSamples().add(sample);
  }

  private Submission submission() {
    Submission submission = new Submission();
    submission.setExperiment(experiment);
    submission.setGoal(goal);
    submission.setTaxonomy(taxonomy);
    submission.setProtein(protein);
    submission.setSamples(new ArrayList<SubmissionSample>());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().get(0).setMolecularWeight(molecularWeight);
    submission.setPostTranslationModification(postTranslationModification);
    submission.getSamples().get(0).setType(sampleType);
    submission.getSamples().get(0).setName(sampleName1);
    submission.getSamples().get(1).setName(sampleName2);
    submission.getSamples().get(0).setQuantity(quantity);
    submission.getSamples().get(0).setVolume(volume);
    submission.setSeparation(separation);
    submission.setThickness(thickness);
    submission.setColoration(coloration);
    submission.setOtherColoration(otherColoration);
    submission.setDevelopmentTime(developmentTime);
    submission.setDecoloration(destained);
    submission.setWeightMarkerQuantity(weightMarkerQuantity);
    submission.setProteinQuantity(proteinQuantity);
    submission.setDigestion(digestion);
    submission.setUsedDigestion(usedDigestion);
    submission.setOtherDigestion(otherDigestion);
    submission.setProteinContent(proteinContent);
    submission.setInstrument(instrument);
    submission.setIdentification(identification);
    submission.setIdentificationLink(identificationLink);
    submission.setQuantification(quantification);
    submission.setQuantificationComment(quantificationComment);
    return submission;
  }

  private void setFields() {
    form.experiment.setValue(experiment);
    form.goal.setValue(goal);
    form.taxonomy.setValue(taxonomy);
    form.protein.setValue(protein);
    form.molecularWeight.setValue(String.valueOf(molecularWeight));
    form.postTranslationModification.setValue(postTranslationModification);
    form.sampleType.setValue(sampleType);
    form.samplesCount.setValue(String.valueOf(samplesCount));
    form.samplesNames.setValue(samplesNames);
    form.quantity.setValue(quantity);
    form.volume.setValue(volume);
    form.separation.setValue(separation);
    form.thickness.setValue(thickness);
    form.coloration.setValue(coloration);
    form.otherColoration.setValue(otherColoration);
    form.developmentTime.setValue(developmentTime);
    form.destained.setValue(destained);
    form.weightMarkerQuantity.setValue(String.valueOf(weightMarkerQuantity));
    form.proteinQuantity.setValue(proteinQuantity);
    form.digestion.setValue(digestion);
    form.usedDigestion.setValue(usedDigestion);
    form.otherDigestion.setValue(otherDigestion);
    form.proteinContent.setValue(proteinContent);
    form.instrument.setValue(instrument);
    form.identification.setValue(identification);
    form.identificationLink.setValue(identificationLink);
    form.quantification.setValue(quantification);
    form.quantificationComment.setValue(quantificationComment);
  }

  @Test
  public void required() {
    assertTrue(form.experiment.isRequiredIndicatorVisible());
    assertTrue(form.taxonomy.isRequiredIndicatorVisible());
    assertTrue(form.sampleType.isRequiredIndicatorVisible());
    assertTrue(form.samplesCount.isRequiredIndicatorVisible());
    assertTrue(form.samplesNames.isRequiredIndicatorVisible());
    assertTrue(form.quantity.isRequiredIndicatorVisible());
    assertTrue(form.volume.isRequiredIndicatorVisible());
    assertTrue(form.separation.isRequiredIndicatorVisible());
    assertTrue(form.thickness.isRequiredIndicatorVisible());
    assertTrue(form.coloration.isRequiredIndicatorVisible());
    assertTrue(form.otherColoration.isRequiredIndicatorVisible());
    assertTrue(form.digestion.isRequiredIndicatorVisible());
    assertTrue(form.usedDigestion.isRequiredIndicatorVisible());
    assertTrue(form.otherDigestion.isRequiredIndicatorVisible());
    assertTrue(form.proteinContent.isRequiredIndicatorVisible());
    assertTrue(form.identification.isRequiredIndicatorVisible());
    assertTrue(form.identificationLink.isRequiredIndicatorVisible());
    assertTrue(form.quantificationComment.isRequiredIndicatorVisible());
  }

  @Test
  public void isValid_EmptyExperiment() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.experiment.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.experiment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyTaxonomy() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.taxonomy.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.taxonomy);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyMolecularWeight() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.molecularWeight.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_InvalidMolecularWeight() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.molecularWeight.setValue("a");

    assertFalse(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.molecularWeight);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_NUMBER)), error.getMessage());
  }

  @Test
  public void isValid_EmptySampleType() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleType);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySamplesCount() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesCount.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesCount);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_InvalidSamplesCount() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesCount.setValue("a");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesCount);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_INTEGER)), error.getMessage());
  }

  @Test
  public void isValid_EmptySamplesNames() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_DuplicatedSamplesNames() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesCount.setValue("3");
    form.samplesNames.setValue(
        Stream.of(sampleName1, sampleName2, sampleName2).collect(Collectors.joining(", ")));

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_DUPLICATES, sampleName2)),
        error.getMessage());
  }

  @Test
  public void isValid_WrongNumberOfSamplesNames() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNames() {
    when(sampleService.exists(sampleName2)).thenReturn(true);
    presenter.setSubmission(newSubmission);
    setFields();

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_EXISTS, sampleName2)),
        error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNamesInSubmission() {
    when(sampleService.exists(sampleName2)).thenReturn(true);
    SubmissionSample sample = new SubmissionSample();
    sample.setName(sampleName2);
    newSubmission.getSamples().add(sample);
    presenter.setSubmission(newSubmission);
    setFields();

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyQuantity() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.quantity.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantity);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyVolume() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.volume.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.volume);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyVolume_Dry() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.DRY);
    form.volume.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyVolume_Beads() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.AGAROSE_BEADS);
    form.volume.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.volume);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySeparation_Gel() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.separation.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.separation);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySeparation_Solution() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.separation.setValue(null);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyThickness_Gel() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.thickness.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.thickness);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyThickness_Solution() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.thickness.setValue(null);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyColoration_Gel() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.coloration.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.coloration);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyColoration_Solution() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.coloration.setValue(null);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyOtherColoration_GelAndOtherColoration() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.coloration.setValue(GelColoration.OTHER);
    form.otherColoration.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.otherColoration);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyOtherColoration_GelAndAnyColoration() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.coloration.setValue(GelColoration.COOMASSIE);
    form.otherColoration.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyOtherColoration_SolutionAndOtherColoration() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.coloration.setValue(GelColoration.OTHER);
    form.otherColoration.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_InvalidWeightMarker_Gel() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.weightMarkerQuantity.setValue("a");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.weightMarkerQuantity);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_NUMBER)), error.getMessage());
  }

  @Test
  public void isValid_InvalidWeightMarker_Solution() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.weightMarkerQuantity.setValue("a");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyDigestion() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.digestion);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyUsedDigestion_UsedDigestion() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.DIGESTED);
    form.usedDigestion.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.usedDigestion);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyUsedDigestion_AnyDigestion() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.TRYPSIN);
    form.usedDigestion.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyOtherDigestion_OtherDigestion() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.OTHER);
    form.otherDigestion.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.otherDigestion);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyOtherDigestion_AnyDigestion() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.TRYPSIN);
    form.otherDigestion.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyProteinContent() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.proteinContent.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.proteinContent);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyIdentification() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.identification.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.identification);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyIdentificationLink_OtherIdentification() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.identification.setValue(ProteinIdentification.OTHER);
    form.identificationLink.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.identificationLink);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyIdentificationLink_AnyIdentification() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.identification.setValue(ProteinIdentification.REFSEQ);
    form.identificationLink.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyQuantificationComment_Silac() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.quantification.setValue(Quantification.SILAC);
    form.quantificationComment.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantificationComment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyQuantificationComment_Tmt() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.quantification.setValue(Quantification.TMT);
    form.quantificationComment.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantificationComment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyQuantificationComment_Any() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.quantification.setValue(Quantification.LABEL_FREE);
    form.quantificationComment.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid() {
    presenter.setSubmission(newSubmission);
    setFields();

    assertTrue(presenter.isValid());
    assertTrue(presenter.validateSubmission().isOk());
    assertTrue(presenter.validateFirstSample().isOk());
    assertTrue(presenter.validateSamples().isOk());
  }

  @Test
  public void getSubmission() {
    Submission database = repository.findById(34L).orElse(null);
    presenter.setSubmission(database);

    Submission submission = presenter.getSubmission();
    assertEquals(database.getId(), submission.getId());
    assertEquals(database.getExperiment(), submission.getExperiment());
    assertEquals(database.getGoal(), submission.getGoal());
    assertEquals(database.getTaxonomy(), submission.getTaxonomy());
    assertEquals(database.getProtein(), submission.getProtein());
    assertEquals(database.getSamples().get(0).getMolecularWeight(),
        submission.getSamples().get(0).getMolecularWeight());
    assertEquals(database.getPostTranslationModification(),
        submission.getPostTranslationModification());
    assertEquals(database.getSamples().get(0).getType(), submission.getSamples().get(0).getType());
    assertEquals(database.getSamples().size(), submission.getSamples().size());
    for (int i = 0; i < database.getSamples().size(); i++) {
      assertEquals(database.getSamples().get(i).getName(),
          submission.getSamples().get(i).getName());
    }
    assertEquals(database.getSamples().get(0).getQuantity(),
        submission.getSamples().get(0).getQuantity());
    assertEquals(database.getSamples().get(0).getVolume(),
        submission.getSamples().get(0).getVolume());
    assertEquals(database.getSeparation(), submission.getSeparation());
    assertEquals(database.getThickness(), submission.getThickness());
    assertEquals(database.getColoration(), submission.getColoration());
    assertEquals(database.getOtherColoration(), submission.getOtherColoration());
    assertEquals(database.getDevelopmentTime(), submission.getDevelopmentTime());
    assertEquals(database.isDecoloration(), submission.isDecoloration());
    assertEquals(database.getWeightMarkerQuantity(), submission.getWeightMarkerQuantity());
    assertEquals(database.getProteinQuantity(), submission.getProteinQuantity());
    assertEquals(database.getDigestion(), submission.getDigestion());
    assertEquals(database.getUsedDigestion(), submission.getUsedDigestion());
    assertEquals(database.getOtherDigestion(), submission.getOtherDigestion());
    assertEquals(database.getProteinContent(), submission.getProteinContent());
    assertEquals(database.getInstrument(), submission.getInstrument());
    assertEquals(database.getIdentification(), submission.getIdentification());
    assertEquals(database.getIdentificationLink(), submission.getIdentificationLink());
    assertEquals(database.getQuantification(), submission.getQuantification());
    assertEquals(database.getQuantificationComment(), submission.getQuantificationComment());
  }

  @Test
  public void setSubmission() {
    presenter.setSubmission(submission());

    assertEquals(experiment, form.experiment.getValue());
    assertEquals(goal, form.goal.getValue());
    assertEquals(taxonomy, form.taxonomy.getValue());
    assertEquals(protein, form.protein.getValue());
    assertEquals(molecularWeight, Double.parseDouble(form.molecularWeight.getValue()), 0.00001);
    assertEquals(postTranslationModification, form.postTranslationModification.getValue());
    assertEquals(sampleType, form.sampleType.getValue());
    assertEquals(samplesCount, Integer.parseInt(form.samplesCount.getValue()));
    assertEquals(samplesNames, form.samplesNames.getValue());
    assertEquals(quantity, form.quantity.getValue());
    assertEquals(volume, form.volume.getValue());
    assertEquals(separation, form.separation.getValue());
    assertEquals(thickness, form.thickness.getValue());
    assertEquals(coloration, form.coloration.getValue());
    assertEquals(otherColoration, form.otherColoration.getValue());
    assertEquals(developmentTime, form.developmentTime.getValue());
    assertEquals(destained, form.destained.getValue());
    assertEquals(weightMarkerQuantity, Double.parseDouble(form.weightMarkerQuantity.getValue()),
        0.00001);
    assertEquals(proteinQuantity, form.proteinQuantity.getValue());
    assertEquals(digestion, form.digestion.getValue());
    assertEquals(usedDigestion, form.usedDigestion.getValue());
    assertEquals(otherDigestion, form.otherDigestion.getValue());
    assertEquals(proteinContent, form.proteinContent.getValue());
    assertEquals(instrument, form.instrument.getValue());
    assertEquals(identification, form.identification.getValue());
    assertEquals(identificationLink, form.identificationLink.getValue());
    assertEquals(quantification, form.quantification.getValue());
    assertEquals(quantificationComment, form.quantificationComment.getValue());
  }
}
