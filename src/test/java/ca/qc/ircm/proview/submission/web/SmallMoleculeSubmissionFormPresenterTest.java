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

import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SmallMoleculeSubmissionFormPresenterTest {
  @Autowired
  private SmallMoleculeSubmissionFormPresenter presenter;
  @Mock
  private SmallMoleculeSubmissionForm form;
  @MockBean
  private SubmissionSampleService sampleService;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private AppResources webResources = new AppResources(WebConstants.class, locale);
  private Submission newSubmission;
  private SampleType sampleType = SampleType.SOLUTION;
  private String sampleName = "my sample";
  private String solvent = "ethanol";
  private String formula = "ch3oh";
  private double monoisotopicMass = 18.1;
  private double averageMass = 18.2;
  private String toxicity = "poison";
  private boolean lightSensitive = true;
  private StorageTemperature storageTemperature = StorageTemperature.MEDIUM;
  private boolean highResolution = true;
  private List<Solvent> solvents = Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3);
  private String otherSolvent = "acetone";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    form.sampleType = new RadioButtonGroup<>();
    form.sampleName = new TextField();
    form.solvent = new TextField();
    form.formula = new TextField();
    form.monoisotopicMass = new TextField();
    form.averageMass = new TextField();
    form.toxicity = new TextField();
    form.lightSensitive = new Checkbox();
    form.storageTemperature = new RadioButtonGroup<>();
    form.highResolution = new RadioButtonGroup<>();
    form.solvents = new SolventsField();
    form.otherSolvent = new TextField();
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
    submission.setSamples(new ArrayList<SubmissionSample>());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().get(0).setType(sampleType);
    submission.getSamples().get(0).setName(sampleName);
    submission.setSolutionSolvent(solvent);
    submission.setFormula(formula);
    submission.setMonoisotopicMass(monoisotopicMass);
    submission.setAverageMass(averageMass);
    submission.setToxicity(toxicity);
    submission.setLightSensitive(lightSensitive);
    submission.setStorageTemperature(storageTemperature);
    submission.setHighResolution(highResolution);
    submission.setSolvents(solvents);
    submission.setOtherSolvent(otherSolvent);
    return submission;
  }

  private void setFields() {
    form.sampleType.setValue(sampleType);
    form.sampleName.setValue(sampleName);
    form.solvent.setValue(solvent);
    form.formula.setValue(formula);
    form.monoisotopicMass.setValue(String.valueOf(monoisotopicMass));
    form.averageMass.setValue(String.valueOf(averageMass));
    form.toxicity.setValue(toxicity);
    form.lightSensitive.setValue(lightSensitive);
    form.storageTemperature.setValue(storageTemperature);
    form.highResolution.setValue(highResolution);
    form.solvents.setValue(solvents);
    form.otherSolvent.setValue(otherSolvent);
  }

  @Test
  public void required() {
    assertTrue(form.sampleType.isRequiredIndicatorVisible());
    assertTrue(form.sampleName.isRequiredIndicatorVisible());
    assertTrue(form.solvent.isRequiredIndicatorVisible());
    assertTrue(form.formula.isRequiredIndicatorVisible());
    assertTrue(form.monoisotopicMass.isRequiredIndicatorVisible());
    assertFalse(form.averageMass.isRequiredIndicatorVisible());
    assertFalse(form.toxicity.isRequiredIndicatorVisible());
    assertFalse(form.lightSensitive.isRequiredIndicatorVisible());
    assertTrue(form.storageTemperature.isRequiredIndicatorVisible());
    assertTrue(form.highResolution.isRequiredIndicatorVisible());
    assertTrue(form.solvents.isRequiredIndicatorVisible());
    assertTrue(form.otherSolvent.isRequiredIndicatorVisible());
  }

  @Test
  public void enabled() {
    assertTrue(form.sampleType.isEnabled());
    assertTrue(form.sampleName.isEnabled());
    assertTrue(form.formula.isEnabled());
    assertTrue(form.monoisotopicMass.isEnabled());
    assertTrue(form.averageMass.isEnabled());
    assertTrue(form.toxicity.isEnabled());
    assertTrue(form.lightSensitive.isEnabled());
    assertTrue(form.storageTemperature.isEnabled());
    assertTrue(form.highResolution.isEnabled());
    assertTrue(form.solvents.isEnabled());
    assertFalse(form.otherSolvent.isEnabled());
  }

  @Test
  public void enabled_Solution() {
    form.sampleType.setValue(SampleType.SOLUTION);
    assertTrue(form.solvent.isEnabled());
  }

  @Test
  public void enabled_Dry() {
    form.sampleType.setValue(SampleType.DRY);
    assertFalse(form.solvent.isEnabled());
  }

  @Test
  public void enabled_MethanolSolvent() {
    form.solvents.setValue(Arrays.asList(Solvent.METHANOL));
    assertFalse(form.otherSolvent.isEnabled());
  }

  @Test
  public void enabled_OtherSolvent() {
    form.solvents.setValue(Arrays.asList(Solvent.OTHER));
    assertTrue(form.otherSolvent.isEnabled());
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
  public void isValid_EmptySampleName() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleName.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<SubmissionSample> status = presenter.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleName);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySolvent_Solution() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.solvent.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.solvent);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySolvent_Dry() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.DRY);
    form.solvent.setValue("");

    assertTrue(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyFormula() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.formula.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.formula);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyMonoisotopicMass() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.monoisotopicMass.setValue("");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.monoisotopicMass);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_InvalidMonoisotopicMass() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.monoisotopicMass.setValue("a");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.monoisotopicMass);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_NUMBER)), error.getMessage());
  }

  @Test
  public void isValid_InvalidAverageMass() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.averageMass.setValue("a");

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.averageMass);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_NUMBER)), error.getMessage());
  }

  @Test
  public void isValid_EmptyStorageTemperature() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.storageTemperature.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.storageTemperature);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyHighResolution() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.highResolution.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.highResolution);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySolvents() {
    presenter.setSubmission(newSubmission);
    setFields();
    form.solvents.setValue(null);

    assertFalse(presenter.isValid());
    BinderValidationStatus<Submission> status = presenter.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.solvents);
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
  }

  @Test
  public void getSubmission_NoChanges() {
    Submission database = repository.findById(33L).orElse(null);
    presenter.setSubmission(database);

    assertTrue(presenter.isValid());
    Submission submission = presenter.getSubmission();
    assertEquals(database.getId(), submission.getId());
    assertEquals(database.getSamples().get(0).getType(), submission.getSamples().get(0).getType());
    assertEquals(database.getSamples().get(0).getName(), submission.getSamples().get(0).getName());
    assertEquals(database.getSolutionSolvent(), submission.getSolutionSolvent());
    assertEquals(database.getFormula(), submission.getFormula());
    assertEquals(database.getMonoisotopicMass(), submission.getMonoisotopicMass());
    assertEquals(database.getAverageMass(), submission.getAverageMass());
    assertEquals(database.getToxicity(), submission.getToxicity());
    assertEquals(database.isLightSensitive(), submission.isLightSensitive());
    assertEquals(database.getStorageTemperature(), submission.getStorageTemperature());
    assertEquals(database.isHighResolution(), submission.isHighResolution());
    assertEquals(database.getSolvents().size(), submission.getSolvents().size());
    for (Solvent value : database.getSolvents()) {
      assertTrue(submission.getSolvents().contains(value));
    }
    assertEquals(database.getOtherSolvent(), submission.getOtherSolvent());
  }

  @Test
  public void getSubmission_ModifiedFields() {
    presenter.setSubmission(newSubmission);
    setFields();

    assertTrue(presenter.isValid());
    Submission submission = presenter.getSubmission();
    assertEquals(1, submission.getSamples().size());
    assertEquals(sampleName, submission.getSamples().get(0).getName());
    assertEquals(sampleType, submission.getSamples().get(0).getType());
    assertEquals(solvent, submission.getSolutionSolvent());
    assertEquals(formula, submission.getFormula());
    assertEquals(monoisotopicMass, submission.getMonoisotopicMass(), 0.0001);
    assertEquals(averageMass, submission.getAverageMass(), 0.0001);
    assertEquals(toxicity, submission.getToxicity());
    assertEquals(lightSensitive, submission.isLightSensitive());
    assertEquals(storageTemperature, submission.getStorageTemperature());
    assertEquals(highResolution, submission.isHighResolution());
    assertEquals(solvents.size(), submission.getSolvents().size());
    for (Solvent value : solvents) {
      assertTrue(submission.getSolvents().contains(value));
    }
    assertEquals(otherSolvent, submission.getOtherSolvent());
  }

  @Test
  public void setSubmission() {
    presenter.setSubmission(submission());

    assertEquals(sampleType, form.sampleType.getValue());
    assertEquals(sampleName, form.sampleName.getValue());
    assertEquals(solvent, form.solvent.getValue());
    assertEquals(formula, form.formula.getValue());
    assertEquals(String.valueOf(monoisotopicMass), form.monoisotopicMass.getValue());
    assertEquals(String.valueOf(averageMass), form.averageMass.getValue());
    assertEquals(toxicity, form.toxicity.getValue());
    assertEquals(lightSensitive, form.lightSensitive.getValue());
    assertEquals(storageTemperature, form.storageTemperature.getValue());
    assertEquals(highResolution, form.highResolution.getValue());
    assertEquals(solvents, form.solvents.getValue());
    assertEquals(otherSolvent, form.otherSolvent.getValue());
  }
}
