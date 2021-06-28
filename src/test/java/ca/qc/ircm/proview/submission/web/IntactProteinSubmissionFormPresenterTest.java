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
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.web.IntactProteinSubmissionFormPresenter.Samples;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
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
 * Tests for {@link IntactProteinSubmissionFormPresenter}.
 */
@ServiceTestAnnotations
public class IntactProteinSubmissionFormPresenterTest {
  @Autowired
  private IntactProteinSubmissionFormPresenter presenter;
  @Mock
  private IntactProteinSubmissionForm form;
  @MockBean
  private SubmissionSampleService sampleService;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(IntactProteinSubmissionForm.class, locale);
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
  private InjectionType injection = InjectionType.LC_MS;
  private MassDetectionInstrumentSource source = MassDetectionInstrumentSource.LDTD;
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;

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
    form.injection = new RadioButtonGroup<>();
    form.source = new RadioButtonGroup<>();
    form.instrument = new ComboBox<>();
    form.instrument.setItems(MassDetectionInstrument.values());
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
    submission.setInjectionType(injection);
    submission.setSource(source);
    submission.setInstrument(instrument);
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
    form.injection.setValue(injection);
    form.source.setValue(source);
    form.instrument.setValue(instrument);
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
    assertTrue(form.injection.isRequiredIndicatorVisible());
    assertTrue(form.source.isRequiredIndicatorVisible());
    assertFalse(form.instrument.isRequiredIndicatorVisible());
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
    assertTrue(form.source.isEnabled());
    assertTrue(form.injection.isEnabled());
    assertTrue(form.instrument.isEnabled());
  }

  @Test
  public void enabled_Solution() {
    form.sampleType.setValue(SampleType.SOLUTION);
    assertTrue(form.volume.isEnabled());
  }

  @Test
  public void enabled_Dry() {
    form.sampleType.setValue(SampleType.DRY);
    assertFalse(form.volume.isEnabled());
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
  public void isValid_EmptyVolume_Solution() {
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
  public void isValid_Injection() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.injection.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.injection);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_Source() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.source.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.source);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
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
    database.setInjectionType(InjectionType.DIRECT_INFUSION);
    database.setSource(MassDetectionInstrumentSource.ESI);
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
    assertEquals(database.getInjectionType(), submission.getInjectionType());
    assertEquals(database.getSource(), submission.getSource());
    assertEquals(database.getInstrument(), submission.getInstrument());
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
    assertEquals(injection, submission.getInjectionType());
    assertEquals(source, submission.getSource());
    assertEquals(instrument, submission.getInstrument());
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
    assertEquals(injection, form.injection.getValue());
    assertEquals(source, form.source.getValue());
    assertEquals(instrument, form.instrument.getValue());
  }

  @Test
  public void setSubmission_NullInstrument() {
    Submission submission = submission();
    submission.setInstrument(null);
    presenter.setSubmission(submission);

    assertEquals(MassDetectionInstrument.NULL, form.instrument.getValue());
  }
}
