package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLVENTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.ID;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_TYPE;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.id;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.text.Strings.property;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SmallMoleculeSubmissionForm}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SmallMoleculeSubmissionFormTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(SmallMoleculeSubmissionForm.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private static final String STORAGE_TEMPERATURE_PREFIX = messagePrefix(StorageTemperature.class);
  private static final String SOLVENT_PREFIX = messagePrefix(Solvent.class);
  private SmallMoleculeSubmissionForm form;
  @MockitoBean
  private SubmissionSampleService sampleService;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private SubmissionRepository repository;
  private final Locale locale = ENGLISH;
  private Submission newSubmission;
  private final SampleType sampleType = SampleType.SOLUTION;
  private final String sampleName = "my sample";
  private final String solvent = "ethanol";
  private final String formula = "ch3oh";
  private final double monoisotopicMass = 18.1;
  private final double averageMass = 18.2;
  private final String toxicity = "poison";
  private final boolean lightSensitive = true;
  private final StorageTemperature storageTemperature = StorageTemperature.MEDIUM;
  private final boolean highResolution = true;
  private final List<Solvent> solvents = Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3);
  private final String otherSolvent = "acetone";

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    newSubmission = new Submission();
    newSubmission.setSamples(new ArrayList<>());
    SubmissionSample sample = new SubmissionSample();
    sample.setType(SampleType.DRY);
    newSubmission.getSamples().add(sample);
    SubmissionView view = navigate(SubmissionView.class);
    test(test(view).find(Tabs.class).id(SERVICE))
        .select(view.getTranslation(SERVICE_PREFIX + SMALL_MOLECULE.name()));
    form = test(view).find(SmallMoleculeSubmissionForm.class).id(SmallMoleculeSubmissionForm.ID);
  }

  private Submission submission() {
    Submission submission = new Submission();
    submission.setSamples(new ArrayList<>());
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
    form.solvents.setValue(new HashSet<>(solvents));
    form.otherSolvent.setValue(otherSolvent);
  }

  @Test
  public void styles() {
    assertEquals(ID, form.getId().orElse(""));
    assertEquals(id(SAMPLE_TYPE), form.sampleType.getId().orElse(""));
    assertEquals(id(SAMPLE_NAME), form.sampleName.getId().orElse(""));
    assertEquals(id(SOLUTION_SOLVENT), form.solvent.getId().orElse(""));
    assertEquals(id(FORMULA), form.formula.getId().orElse(""));
    assertEquals(id(MONOISOTOPIC_MASS), form.monoisotopicMass.getId().orElse(""));
    assertEquals(id(AVERAGE_MASS), form.averageMass.getId().orElse(""));
    assertEquals(id(TOXICITY), form.toxicity.getId().orElse(""));
    assertEquals(id(LIGHT_SENSITIVE), form.lightSensitive.getId().orElse(""));
    assertEquals(id(STORAGE_TEMPERATURE), form.storageTemperature.getId().orElse(""));
    assertEquals(id(HIGH_RESOLUTION), form.highResolution.getId().orElse(""));
    assertEquals(id(SOLVENTS), form.solvents.getId().orElse(""));
    assertEquals(id(OTHER_SOLVENT), form.otherSolvent.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLE_TYPE), form.sampleType.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLE_NAME), form.sampleName.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + SOLUTION_SOLVENT),
        form.solvent.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + FORMULA), form.formula.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + MONOISOTOPIC_MASS),
        form.monoisotopicMass.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + AVERAGE_MASS),
        form.averageMass.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + TOXICITY), form.toxicity.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + LIGHT_SENSITIVE),
        form.lightSensitive.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + STORAGE_TEMPERATURE),
        form.storageTemperature.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + HIGH_RESOLUTION),
        form.highResolution.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + SOLVENTS), form.solvents.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + OTHER_SOLVENT),
        form.otherSolvent.getLabel());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLE_TYPE), form.sampleType.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLE_NAME), form.sampleName.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + SOLUTION_SOLVENT),
        form.solvent.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + FORMULA), form.formula.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + MONOISOTOPIC_MASS),
        form.monoisotopicMass.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + AVERAGE_MASS),
        form.averageMass.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + TOXICITY), form.toxicity.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + LIGHT_SENSITIVE),
        form.lightSensitive.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + STORAGE_TEMPERATURE),
        form.storageTemperature.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + HIGH_RESOLUTION),
        form.highResolution.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + SOLVENTS), form.solvents.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + OTHER_SOLVENT),
        form.otherSolvent.getLabel());
  }

  @Test
  public void sampleTypes() {
    List<SampleType> items = items(form.sampleType);
    assertEquals(2, items.size());
    for (SampleType value : new SampleType[]{DRY, SOLUTION}) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(SAMPLE_TYPE_PREFIX + value.name()),
          form.sampleType.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void storageTemperatures() {
    List<StorageTemperature> items = items(form.storageTemperature);
    assertEquals(StorageTemperature.values().length, items.size());
    for (StorageTemperature value : StorageTemperature.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(STORAGE_TEMPERATURE_PREFIX + value.name()),
          form.storageTemperature.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void solvents() {
    Set<Solvent> items = form.solvents.getListDataView().getItems().collect(Collectors.toSet());
    assertEquals(Solvent.values().length, items.size());
    for (Solvent solvent : Solvent.values()) {
      assertTrue(items.contains(solvent));
      assertEquals(form.getTranslation(SOLVENT_PREFIX + solvent.name()),
          form.solvents.getItemLabelGenerator().apply(solvent));
    }
  }

  @Test
  public void highResolution() {
    List<Boolean> items = items(form.highResolution);
    assertEquals(2, items.size());
    for (Boolean value : new Boolean[]{false, true}) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(SUBMISSION_PREFIX + property(HIGH_RESOLUTION, value)),
          form.highResolution.getItemRenderer().createComponent(value).getElement().getText());
    }
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
    form.solvents.setValue(Collections.singleton(Solvent.METHANOL));
    assertFalse(form.otherSolvent.isEnabled());
  }

  @Test
  public void enabled_OtherSolvent() {
    form.solvents.setValue(Collections.singleton(Solvent.OTHER));
    assertTrue(form.otherSolvent.isEnabled());
  }

  @Test
  public void isValid_EmptySampleType() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleType);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySampleName() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleName.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleName);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNames() {
    when(sampleService.exists(sampleName)).thenReturn(true);
    form.setSubmission(newSubmission);
    setFields();

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleName);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + ALREADY_EXISTS, sampleName)),
        error.getMessage());
  }

  @Test
  public void isValid_EmptySolvent_Solution() {
    form.setSubmission(newSubmission);
    setFields();
    form.solvent.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.solvent);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySolvent_Dry() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.DRY);
    form.solvent.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyFormula() {
    form.setSubmission(newSubmission);
    setFields();
    form.formula.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.formula);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyMonoisotopicMass() {
    form.setSubmission(newSubmission);
    setFields();
    form.monoisotopicMass.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.monoisotopicMass);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_InvalidMonoisotopicMass() {
    form.setSubmission(newSubmission);
    setFields();
    form.monoisotopicMass.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.monoisotopicMass);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)),
        error.getMessage());
  }

  @Test
  public void isValid_InvalidAverageMass() {
    form.setSubmission(newSubmission);
    setFields();
    form.averageMass.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.averageMass);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)),
        error.getMessage());
  }

  @Test
  public void isValid_EmptyStorageTemperature() {
    form.setSubmission(newSubmission);
    setFields();
    form.storageTemperature.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.storageTemperature);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyHighResolution() {
    form.setSubmission(newSubmission);
    setFields();
    form.highResolution.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.highResolution);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySolvents() {
    form.setSubmission(newSubmission);
    setFields();
    form.solvents.setValue(new HashSet<>());

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.solvents);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid() {
    form.setSubmission(newSubmission);
    setFields();

    assertTrue(form.isValid());
    assertTrue(form.validateSubmission().isOk());
    assertTrue(form.validateFirstSample().isOk());
  }

  @Test
  public void getSubmission_NoChanges() {
    Submission database = repository.findById(33L).orElseThrow();
    form.setSubmission(database);

    assertTrue(form.isValid());
    Submission submission = form.getSubmission();
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
    form.setSubmission(newSubmission);
    setFields();

    assertTrue(form.isValid());
    Submission submission = form.getSubmission();
    assertEquals(1, submission.getSamples().size());
    assertEquals(sampleName, submission.getSamples().get(0).getName());
    assertEquals(sampleType, submission.getSamples().get(0).getType());
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
    for (Solvent value : solvents) {
      assertTrue(submission.getSolvents().contains(value));
    }
    assertEquals(otherSolvent, submission.getOtherSolvent());
  }

  @Test
  public void setSubmission() {
    form.setSubmission(submission());

    assertEquals(sampleType, form.sampleType.getValue());
    assertFalse(form.sampleType.isReadOnly());
    assertEquals(sampleName, form.sampleName.getValue());
    assertFalse(form.sampleName.isReadOnly());
    assertEquals(solvent, form.solvent.getValue());
    assertFalse(form.solvent.isReadOnly());
    assertEquals(formula, form.formula.getValue());
    assertFalse(form.formula.isReadOnly());
    assertEquals(String.valueOf(monoisotopicMass), form.monoisotopicMass.getValue());
    assertFalse(form.monoisotopicMass.isReadOnly());
    assertEquals(String.valueOf(averageMass), form.averageMass.getValue());
    assertFalse(form.averageMass.isReadOnly());
    assertEquals(toxicity, form.toxicity.getValue());
    assertFalse(form.toxicity.isReadOnly());
    assertEquals(lightSensitive, form.lightSensitive.getValue());
    assertFalse(form.lightSensitive.isReadOnly());
    assertEquals(storageTemperature, form.storageTemperature.getValue());
    assertFalse(form.storageTemperature.isReadOnly());
    assertEquals(highResolution, form.highResolution.getValue());
    assertFalse(form.highResolution.isReadOnly());
    assertEquals(solvents.size(), form.solvents.getValue().size());
    assertTrue(form.solvents.getValue().containsAll(solvents));
    assertFalse(form.solvents.isReadOnly());
    assertEquals(otherSolvent, form.otherSolvent.getValue());
    assertFalse(form.otherSolvent.isReadOnly());
  }

  @Test
  public void setSubmission_ReadOnly() {
    Submission submission = repository.findById(33L).orElseThrow();

    form.setSubmission(submission);

    assertEquals(SOLUTION, form.sampleType.getValue());
    assertTrue(form.sampleType.isReadOnly());
    assertEquals("CAP_20111013_05", form.sampleName.getValue());
    assertTrue(form.sampleName.isReadOnly());
    assertEquals("MeOH/TFA 0.1%", form.solvent.getValue());
    assertTrue(form.solvent.isReadOnly());
    assertEquals("C100H100O100", form.formula.getValue());
    assertTrue(form.formula.isReadOnly());
    assertEquals("654.654", form.monoisotopicMass.getValue());
    assertTrue(form.monoisotopicMass.isReadOnly());
    assertEquals("654.654", form.averageMass.getValue());
    assertTrue(form.averageMass.isReadOnly());
    assertEquals("", form.toxicity.getValue());
    assertTrue(form.toxicity.isReadOnly());
    assertEquals(false, form.lightSensitive.getValue());
    assertTrue(form.lightSensitive.isReadOnly());
    assertEquals(StorageTemperature.MEDIUM, form.storageTemperature.getValue());
    assertTrue(form.storageTemperature.isReadOnly());
    assertEquals(false, form.highResolution.getValue());
    assertTrue(form.highResolution.isReadOnly());
    Set<Solvent> solvents = form.solvents.getValue();
    assertEquals(1, solvents.size());
    assertTrue(solvents.contains(Solvent.METHANOL));
    assertTrue(form.solvents.isReadOnly());
    assertEquals("", form.otherSolvent.getValue());
    assertTrue(form.otherSolvent.isReadOnly());
  }
}
