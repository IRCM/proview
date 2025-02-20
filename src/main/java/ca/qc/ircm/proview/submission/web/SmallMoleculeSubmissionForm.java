package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLVENTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#SMALL_MOLECULE}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SmallMoleculeSubmissionForm extends FormLayout implements LocaleChangeObserver {

  public static final String ID = "small-molecule-submission-form";
  public static final String SAMPLE = "sample";
  public static final String SAMPLE_TYPE = SAMPLE + "Type";
  public static final String SAMPLE_NAME = SAMPLE + "Name";
  private static final String MESSAGES_PREFIX = messagePrefix(SmallMoleculeSubmissionForm.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final String STORAGE_TEMPERATURE_PREFIX = messagePrefix(StorageTemperature.class);
  private static final String SOLVENT_PREFIX = messagePrefix(Solvent.class);
  @Serial
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SmallMoleculeSubmissionForm.class);
  protected RadioButtonGroup<SampleType> sampleType = new RadioButtonGroup<>();
  protected TextField sampleName = new TextField();
  protected TextField solvent = new TextField();
  protected TextField formula = new TextField();
  protected TextField monoisotopicMass = new TextField();
  protected TextField averageMass = new TextField();
  protected TextField toxicity = new TextField();
  protected Checkbox lightSensitive = new Checkbox();
  protected RadioButtonGroup<StorageTemperature> storageTemperature = new RadioButtonGroup<>();
  protected RadioButtonGroup<Boolean> highResolution = new RadioButtonGroup<>();
  protected CheckboxGroup<Solvent> solvents = new CheckboxGroup<>();
  protected TextField otherSolvent = new TextField();
  private final Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private final Binder<SubmissionSample> firstSampleBinder = new BeanValidationBinder<>(
      SubmissionSample.class);
  private final transient SubmissionSampleService sampleService;
  private final transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected SmallMoleculeSubmissionForm(SubmissionSampleService sampleService,
      AuthenticatedUser authenticatedUser) {
    this.sampleService = sampleService;
    this.authenticatedUser = authenticatedUser;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    setId(ID);
    setMaxWidth("80em");
    setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("30em", 2),
        new ResponsiveStep("60em", 3));
    FormLayout submissionFields = new FormLayout(sampleType, sampleName, solvent, formula);
    submissionFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    FormLayout sampleFields = new FormLayout(monoisotopicMass, averageMass, toxicity,
        lightSensitive, storageTemperature);
    sampleFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    FormLayout analysisFields = new FormLayout(highResolution, solvents, otherSolvent);
    analysisFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    add(submissionFields, sampleFields, analysisFields);
    sampleType.setId(id(SAMPLE_TYPE));
    sampleType.setItems(DRY, SOLUTION);
    sampleType.setRenderer(
        new TextRenderer<>(value -> getTranslation(SAMPLE_TYPE_PREFIX + value.name())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    sampleType.addValueChangeListener(e -> sampleTypeChanged());
    sampleName.setId(id(SAMPLE_NAME));
    solvent.setId(id(SOLUTION_SOLVENT));
    formula.setId(id(FORMULA));
    monoisotopicMass.setId(id(MONOISOTOPIC_MASS));
    averageMass.setId(id(AVERAGE_MASS));
    toxicity.setId(id(TOXICITY));
    lightSensitive.setId(id(LIGHT_SENSITIVE));
    storageTemperature.setId(id(STORAGE_TEMPERATURE));
    storageTemperature.setItems(StorageTemperature.values());
    storageTemperature.setRenderer(
        new TextRenderer<>(value -> getTranslation(STORAGE_TEMPERATURE_PREFIX + value.name())));
    storageTemperature.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    highResolution.setId(id(HIGH_RESOLUTION));
    highResolution.setItems(false, true);
    highResolution.setRenderer(new TextRenderer<>(
        value -> getTranslation(SUBMISSION_PREFIX + property(HIGH_RESOLUTION, value))));
    highResolution.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    solvents.setId(id(SOLVENTS));
    solvents.setItems(Solvent.values());
    solvents.setItemLabelGenerator(solvent -> getTranslation(SOLVENT_PREFIX + solvent.name()));
    solvents.addValueChangeListener(e -> solventsChanged());
    otherSolvent.setId(id(OTHER_SOLVENT));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    Locale locale = event.getLocale();
    sampleType.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLE_TYPE));
    sampleName.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLE_NAME));
    solvent.setLabel(getTranslation(SUBMISSION_PREFIX + SOLUTION_SOLVENT));
    formula.setLabel(getTranslation(SUBMISSION_PREFIX + FORMULA));
    monoisotopicMass.setLabel(getTranslation(SUBMISSION_PREFIX + MONOISOTOPIC_MASS));
    averageMass.setLabel(getTranslation(SUBMISSION_PREFIX + AVERAGE_MASS));
    toxicity.setLabel(getTranslation(SUBMISSION_PREFIX + TOXICITY));
    lightSensitive.setLabel(getTranslation(SUBMISSION_PREFIX + LIGHT_SENSITIVE));
    storageTemperature.setLabel(getTranslation(SUBMISSION_PREFIX + STORAGE_TEMPERATURE));
    highResolution.setLabel(getTranslation(SUBMISSION_PREFIX + HIGH_RESOLUTION));
    solvents.setLabel(getTranslation(SUBMISSION_PREFIX + SOLVENTS));
    otherSolvent.setLabel(getTranslation(SUBMISSION_PREFIX + OTHER_SOLVENT));
    firstSampleBinder.forField(sampleType).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(TYPE);
    firstSampleBinder.forField(sampleName).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").withValidator(sampleNameExists(locale)).bind(NAME);
    solvent.setRequiredIndicatorVisible(true);
    binder.forField(solvent).withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").bind(SOLUTION_SOLVENT);
    binder.forField(formula).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(FORMULA);
    binder.forField(monoisotopicMass).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").withConverter(
            new StringToDoubleConverter(getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)))
        .bind(MONOISOTOPIC_MASS);
    binder.forField(averageMass).withNullRepresentation("").withConverter(
            new StringToDoubleConverter(getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)))
        .bind(AVERAGE_MASS);
    binder.forField(toxicity).withNullRepresentation("").bind(TOXICITY);
    binder.forField(lightSensitive).bind(LIGHT_SENSITIVE);
    binder.forField(storageTemperature).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(STORAGE_TEMPERATURE);
    binder.forField(highResolution).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(HIGH_RESOLUTION);
    binder.forField(solvents).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withConverter(value -> value != null ? (List<Solvent>) new ArrayList<>(value) : null,
            value -> value != null ? new HashSet<>(value) : new HashSet<>())
        .withValidator(solventsNotEmpty(locale)).bind(SOLVENTS);
    otherSolvent.setRequiredIndicatorVisible(true);
    binder.forField(otherSolvent).withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(OTHER_SOLVENT);
    sampleTypeChanged();
    solventsChanged();
    setReadOnly();
  }

  private void sampleTypeChanged() {
    SampleType type = sampleType.getValue();
    solvent.setEnabled(type == SOLUTION);
  }

  private void solventsChanged() {
    Set<Solvent> solvents = this.solvents.getValue();
    otherSolvent.setEnabled(solvents != null && solvents.contains(Solvent.OTHER));
  }

  private Validator<String> sampleNameExists(Locale locale) {
    return (value, context) -> {
      if (sampleService.exists(value)) {
        return ValidationResult.error(getTranslation(CONSTANTS_PREFIX + ALREADY_EXISTS, value));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<Solvent>> solventsNotEmpty(Locale locale) {
    return (value, context) -> {
      if (value.isEmpty()) {
        return ValidationResult.error(getTranslation(CONSTANTS_PREFIX + REQUIRED, value));
      }
      return ValidationResult.ok();
    };
  }

  BinderValidationStatus<Submission> validateSubmission() {
    return binder.validate();
  }

  BinderValidationStatus<SubmissionSample> validateFirstSample() {
    return firstSampleBinder.validate();
  }

  boolean isValid() {
    boolean valid = validateSubmission().isOk();
    valid = validateFirstSample().isOk() && valid;
    return valid;
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
    Objects.requireNonNull(submission);
    if (submission.getSamples().isEmpty()) {
      throw new IllegalArgumentException("submission must contain at least one sample");
    }
    binder.setBean(submission);
    firstSampleBinder.setBean(submission.getSamples().get(0));
    setReadOnly();
  }

  private void setReadOnly() {
    boolean readOnly = !authenticatedUser.hasPermission(binder.getBean(), WRITE);
    binder.setReadOnly(readOnly);
    firstSampleBinder.setReadOnly(readOnly);
  }
}
