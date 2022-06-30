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

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.INVALID_INTEGER;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.web.LcmsmsSubmissionFormPresenter.Samples;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link LcmsmsSubmissionFormPresenter}.
 */
@ServiceTestAnnotations
public class LcmsmsSubmissionFormPresenterTest {
  @Autowired
  private LcmsmsSubmissionFormPresenter presenter;
  @Mock
  private LcmsmsSubmissionForm form;
  @MockBean
  private SubmissionSampleService sampleService;
  @MockBean
  private AuthorizationService authorizationService;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
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
  @BeforeEach
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
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
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
    assertFalse(form.goal.isRequiredIndicatorVisible());
    assertTrue(form.taxonomy.isRequiredIndicatorVisible());
    assertFalse(form.protein.isRequiredIndicatorVisible());
    assertFalse(form.molecularWeight.isRequiredIndicatorVisible());
    assertFalse(form.postTranslationModification.isRequiredIndicatorVisible());
    assertTrue(form.sampleType.isRequiredIndicatorVisible());
    assertTrue(form.samplesCount.isRequiredIndicatorVisible());
    assertTrue(form.samplesNames.isRequiredIndicatorVisible());
    assertTrue(form.quantity.isRequiredIndicatorVisible());
    assertTrue(form.volume.isRequiredIndicatorVisible());
    assertTrue(form.separation.isRequiredIndicatorVisible());
    assertTrue(form.thickness.isRequiredIndicatorVisible());
    assertTrue(form.coloration.isRequiredIndicatorVisible());
    assertTrue(form.otherColoration.isRequiredIndicatorVisible());
    assertFalse(form.developmentTime.isRequiredIndicatorVisible());
    assertFalse(form.destained.isRequiredIndicatorVisible());
    assertFalse(form.weightMarkerQuantity.isRequiredIndicatorVisible());
    assertFalse(form.proteinQuantity.isRequiredIndicatorVisible());
    assertTrue(form.digestion.isRequiredIndicatorVisible());
    assertTrue(form.usedDigestion.isRequiredIndicatorVisible());
    assertTrue(form.otherDigestion.isRequiredIndicatorVisible());
    assertTrue(form.proteinContent.isRequiredIndicatorVisible());
    assertFalse(form.instrument.isRequiredIndicatorVisible());
    assertTrue(form.identification.isRequiredIndicatorVisible());
    assertTrue(form.identificationLink.isRequiredIndicatorVisible());
    assertFalse(form.quantification.isRequiredIndicatorVisible());
    assertTrue(form.quantificationComment.isRequiredIndicatorVisible());
  }

  @Test
  public void enabled() {
    assertTrue(form.experiment.isEnabled());
    assertTrue(form.goal.isEnabled());
    assertTrue(form.taxonomy.isEnabled());
    assertTrue(form.protein.isEnabled());
    assertTrue(form.molecularWeight.isEnabled());
    assertTrue(form.postTranslationModification.isEnabled());
    assertTrue(form.sampleType.isEnabled());
    assertTrue(form.samplesCount.isEnabled());
    assertTrue(form.samplesNames.isEnabled());
    assertTrue(form.quantity.isEnabled());
    assertTrue(form.digestion.isEnabled());
    assertFalse(form.usedDigestion.isEnabled());
    assertFalse(form.otherDigestion.isEnabled());
    assertTrue(form.proteinContent.isEnabled());
    assertTrue(form.instrument.isEnabled());
    assertTrue(form.identification.isEnabled());
    assertFalse(form.identificationLink.isEnabled());
    assertTrue(form.quantification.isEnabled());
    assertFalse(form.quantificationComment.isEnabled());
  }

  @Test
  public void enabled_Solution() {
    form.sampleType.setValue(SampleType.SOLUTION);
    assertTrue(form.volume.isEnabled());
    assertFalse(form.separation.isEnabled());
    assertFalse(form.thickness.isEnabled());
    assertFalse(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertFalse(form.developmentTime.isEnabled());
    assertFalse(form.destained.isEnabled());
    assertFalse(form.weightMarkerQuantity.isEnabled());
    assertFalse(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_Dry() {
    form.sampleType.setValue(SampleType.DRY);
    assertFalse(form.volume.isEnabled());
    assertFalse(form.separation.isEnabled());
    assertFalse(form.thickness.isEnabled());
    assertFalse(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertFalse(form.developmentTime.isEnabled());
    assertFalse(form.destained.isEnabled());
    assertFalse(form.weightMarkerQuantity.isEnabled());
    assertFalse(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_Gel() {
    form.sampleType.setValue(SampleType.GEL);
    assertFalse(form.volume.isEnabled());
    assertTrue(form.separation.isEnabled());
    assertTrue(form.thickness.isEnabled());
    assertTrue(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertTrue(form.developmentTime.isEnabled());
    assertTrue(form.destained.isEnabled());
    assertTrue(form.weightMarkerQuantity.isEnabled());
    assertTrue(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_Beads() {
    form.sampleType.setValue(SampleType.AGAROSE_BEADS);
    assertTrue(form.volume.isEnabled());
    assertFalse(form.separation.isEnabled());
    assertFalse(form.thickness.isEnabled());
    assertFalse(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertFalse(form.developmentTime.isEnabled());
    assertFalse(form.destained.isEnabled());
    assertFalse(form.weightMarkerQuantity.isEnabled());
    assertFalse(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_UsedDigestion() {
    form.digestion.setValue(ProteolyticDigestion.DIGESTED);
    assertTrue(form.usedDigestion.isEnabled());
    assertFalse(form.otherDigestion.isEnabled());
  }

  @Test
  public void enabled_OtherDigestion() {
    form.digestion.setValue(ProteolyticDigestion.OTHER);
    assertFalse(form.usedDigestion.isEnabled());
    assertTrue(form.otherDigestion.isEnabled());
  }

  @Test
  public void enabled_RefseqIdentification() {
    form.identification.setValue(ProteinIdentification.REFSEQ);
    assertFalse(form.identificationLink.isEnabled());
  }

  @Test
  public void enabled_OtherIdentification() {
    form.identification.setValue(ProteinIdentification.OTHER);
    assertTrue(form.identificationLink.isEnabled());
  }

  @Test
  public void enabled_LabelFreeQuantification() {
    form.quantification.setValue(Quantification.LABEL_FREE);
    assertFalse(form.quantificationComment.isEnabled());
  }

  @Test
  public void enabled_SilacQuantification() {
    form.quantification.setValue(Quantification.SILAC);
    assertTrue(form.quantificationComment.isEnabled());
  }

  @Test
  public void enabled_TmtQuantification() {
    form.quantification.setValue(Quantification.TMT);
    assertTrue(form.quantificationComment.isEnabled());
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
  public void isValid_SamplesNamesCommaSeparator() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesCommaSpaceSeparator() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + ", " + sampleName2);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesSemicolonSeparator() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + ";" + sampleName2);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesSemicolonSpaceSeparator() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "; " + sampleName2);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesTabSeparator() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "\t" + sampleName2);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesNewlineSeparator() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "\n" + sampleName2);

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
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
  public void isValid_EmptyFirstSampleName() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(", abc");

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
  public void isValid_EmptySecondSampleName() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue("abc, ");

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
  public void isValid_LastEmptySamplesNames() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2 + ",");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertTrue(status.isOk());
    Submission submission = presenter.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
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
  public void isValid_WrongNumberOfSamplesNames_Below() {
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
  public void isValid_WrongNumberOfSamplesNames_Above() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2 + ",other_sample_name");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Samples> status = presenter.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_WRONG_COUNT, 3, samplesCount)),
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
  public void isValid_EmptyQuantity_Gel() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.quantity.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertTrue(status.isOk());
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
  public void getSubmission_NoChanges() {
    Submission database = repository.findById(34L).orElse(null);
    presenter.setSubmission(database);

    assertTrue(presenter.isValid());
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
      assertEquals(database.getSamples().get(i).getId(), submission.getSamples().get(i).getId());
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
  public void getSubmission_ModifiedFields() {
    presenter.setSubmission(newSubmission);
    setFields();

    assertTrue(presenter.isValid());
    Submission submission = presenter.getSubmission();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(samplesCount, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
    for (int i = 0; i < samplesCount; i++) {
      assertEquals(sampleType, submission.getSamples().get(i).getType());
      assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      assertEquals(quantity, submission.getSamples().get(0).getQuantity());
      assertEquals(volume, submission.getSamples().get(0).getVolume());
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
    assertEquals(usedDigestion, submission.getUsedDigestion());
    assertEquals(otherDigestion, submission.getOtherDigestion());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(instrument, submission.getInstrument());
    assertEquals(identification, submission.getIdentification());
    assertEquals(identificationLink, submission.getIdentificationLink());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationComment, submission.getQuantificationComment());
  }

  @Test
  public void setSubmission() {
    presenter.setSubmission(submission());

    assertEquals(experiment, form.experiment.getValue());
    assertFalse(form.experiment.isReadOnly());
    assertEquals(goal, form.goal.getValue());
    assertFalse(form.goal.isReadOnly());
    assertEquals(taxonomy, form.taxonomy.getValue());
    assertFalse(form.taxonomy.isReadOnly());
    assertEquals(protein, form.protein.getValue());
    assertFalse(form.protein.isReadOnly());
    assertEquals(molecularWeight, Double.parseDouble(form.molecularWeight.getValue()), 0.00001);
    assertFalse(form.molecularWeight.isReadOnly());
    assertEquals(postTranslationModification, form.postTranslationModification.getValue());
    assertFalse(form.postTranslationModification.isReadOnly());
    assertEquals(sampleType, form.sampleType.getValue());
    assertFalse(form.sampleType.isReadOnly());
    assertEquals(samplesCount, Integer.parseInt(form.samplesCount.getValue()));
    assertFalse(form.samplesCount.isReadOnly());
    assertEquals(samplesNames, form.samplesNames.getValue());
    assertFalse(form.samplesNames.isReadOnly());
    assertEquals(quantity, form.quantity.getValue());
    assertFalse(form.quantity.isReadOnly());
    assertEquals(volume, form.volume.getValue());
    assertFalse(form.volume.isReadOnly());
    assertEquals(separation, form.separation.getValue());
    assertFalse(form.separation.isReadOnly());
    assertEquals(thickness, form.thickness.getValue());
    assertFalse(form.thickness.isReadOnly());
    assertEquals(coloration, form.coloration.getValue());
    assertFalse(form.coloration.isReadOnly());
    assertEquals(otherColoration, form.otherColoration.getValue());
    assertFalse(form.otherColoration.isReadOnly());
    assertEquals(developmentTime, form.developmentTime.getValue());
    assertFalse(form.developmentTime.isReadOnly());
    assertEquals(destained, form.destained.getValue());
    assertFalse(form.destained.isReadOnly());
    assertEquals(weightMarkerQuantity, Double.parseDouble(form.weightMarkerQuantity.getValue()),
        0.00001);
    assertFalse(form.weightMarkerQuantity.isReadOnly());
    assertEquals(proteinQuantity, form.proteinQuantity.getValue());
    assertFalse(form.proteinQuantity.isReadOnly());
    assertEquals(digestion, form.digestion.getValue());
    assertFalse(form.digestion.isReadOnly());
    assertEquals(usedDigestion, form.usedDigestion.getValue());
    assertFalse(form.usedDigestion.isReadOnly());
    assertEquals(otherDigestion, form.otherDigestion.getValue());
    assertFalse(form.otherDigestion.isReadOnly());
    assertEquals(proteinContent, form.proteinContent.getValue());
    assertFalse(form.proteinContent.isReadOnly());
    assertEquals(instrument, form.instrument.getValue());
    assertFalse(form.instrument.isReadOnly());
    assertEquals(identification, form.identification.getValue());
    assertFalse(form.identification.isReadOnly());
    assertEquals(identificationLink, form.identificationLink.getValue());
    assertFalse(form.identificationLink.isReadOnly());
    assertEquals(quantification, form.quantification.getValue());
    assertFalse(form.quantification.isReadOnly());
    assertEquals(quantificationComment, form.quantificationComment.getValue());
    assertFalse(form.quantificationComment.isReadOnly());
  }

  @Test
  public void setSubmission_ReadOnly() {
    when(authorizationService.hasPermission(any(), any())).thenReturn(false);

    presenter.setSubmission(submission());

    assertEquals(experiment, form.experiment.getValue());
    assertTrue(form.experiment.isReadOnly());
    assertEquals(goal, form.goal.getValue());
    assertTrue(form.goal.isReadOnly());
    assertEquals(taxonomy, form.taxonomy.getValue());
    assertTrue(form.taxonomy.isReadOnly());
    assertEquals(protein, form.protein.getValue());
    assertTrue(form.protein.isReadOnly());
    assertEquals(molecularWeight, Double.parseDouble(form.molecularWeight.getValue()), 0.00001);
    assertTrue(form.molecularWeight.isReadOnly());
    assertEquals(postTranslationModification, form.postTranslationModification.getValue());
    assertTrue(form.postTranslationModification.isReadOnly());
    assertEquals(sampleType, form.sampleType.getValue());
    assertTrue(form.sampleType.isReadOnly());
    assertEquals(samplesCount, Integer.parseInt(form.samplesCount.getValue()));
    assertTrue(form.samplesCount.isReadOnly());
    assertEquals(samplesNames, form.samplesNames.getValue());
    assertTrue(form.samplesNames.isReadOnly());
    assertEquals(quantity, form.quantity.getValue());
    assertTrue(form.quantity.isReadOnly());
    assertEquals(volume, form.volume.getValue());
    assertTrue(form.volume.isReadOnly());
    assertEquals(separation, form.separation.getValue());
    assertTrue(form.separation.isReadOnly());
    assertEquals(thickness, form.thickness.getValue());
    assertTrue(form.thickness.isReadOnly());
    assertEquals(coloration, form.coloration.getValue());
    assertTrue(form.coloration.isReadOnly());
    assertEquals(otherColoration, form.otherColoration.getValue());
    assertTrue(form.otherColoration.isReadOnly());
    assertEquals(developmentTime, form.developmentTime.getValue());
    assertTrue(form.developmentTime.isReadOnly());
    assertEquals(destained, form.destained.getValue());
    assertTrue(form.destained.isReadOnly());
    assertEquals(weightMarkerQuantity, Double.parseDouble(form.weightMarkerQuantity.getValue()),
        0.00001);
    assertTrue(form.weightMarkerQuantity.isReadOnly());
    assertEquals(proteinQuantity, form.proteinQuantity.getValue());
    assertTrue(form.proteinQuantity.isReadOnly());
    assertEquals(digestion, form.digestion.getValue());
    assertTrue(form.digestion.isReadOnly());
    assertEquals(usedDigestion, form.usedDigestion.getValue());
    assertTrue(form.usedDigestion.isReadOnly());
    assertEquals(otherDigestion, form.otherDigestion.getValue());
    assertTrue(form.otherDigestion.isReadOnly());
    assertEquals(proteinContent, form.proteinContent.getValue());
    assertTrue(form.proteinContent.isReadOnly());
    assertEquals(instrument, form.instrument.getValue());
    assertTrue(form.instrument.isReadOnly());
    assertEquals(identification, form.identification.getValue());
    assertTrue(form.identification.isReadOnly());
    assertEquals(identificationLink, form.identificationLink.getValue());
    assertTrue(form.identificationLink.isReadOnly());
    assertEquals(quantification, form.quantification.getValue());
    assertTrue(form.quantification.isReadOnly());
    assertEquals(quantificationComment, form.quantificationComment.getValue());
    assertTrue(form.quantificationComment.isReadOnly());
  }

  @Test
  public void setSubmission_NullInstrument() {
    Submission submission = submission();
    submission.setInstrument(null);
    presenter.setSubmission(submission);

    assertEquals(MassDetectionInstrument.NULL, form.instrument.getValue());
  }

  @Test
  public void setSubmission_NullQuantification() {
    Submission submission = submission();
    submission.setQuantification(null);
    presenter.setSubmission(submission);

    assertEquals(Quantification.NULL, form.quantification.getValue());
  }
}
