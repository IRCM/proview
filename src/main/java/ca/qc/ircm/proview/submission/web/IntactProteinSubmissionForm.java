package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.INVALID_INTEGER;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.PLACEHOLDER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.SpotbugsJustifications.INNER_CLASS_EI_EXPOSE_REP;
import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#INTACT_PROTEIN}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IntactProteinSubmissionForm extends FormLayout implements LocaleChangeObserver {
  private static final Logger logger = LoggerFactory.getLogger(IntactProteinSubmissionForm.class);
  public static final String ID = "intact-protein-submission-form";
  public static final String SAMPLES_TYPE = SAMPLES + "Type";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_NAMES = SAMPLES + "Names";
  public static final String SAMPLES_NAMES_DUPLICATES = property(SAMPLES + "Names", "duplicate");
  public static final String SAMPLES_NAMES_EXISTS = property(SAMPLES + "Names", "exists");
  public static final String SAMPLES_NAMES_WRONG_COUNT = property(SAMPLES + "Names", "wrongCount");
  public static final String QUANTITY_PLACEHOLDER = property(QUANTITY, PLACEHOLDER);
  public static final String VOLUME_PLACEHOLDER = property(VOLUME, PLACEHOLDER);
  private static final String MESSAGES_PREFIX = messagePrefix(IntactProteinSubmissionForm.class);
  private static final String SAMPLE_PREFIX = messagePrefix(Sample.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String INJECTION_TYPE_PREFIX = messagePrefix(InjectionType.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX =
      messagePrefix(MassDetectionInstrumentSource.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final long serialVersionUID = 7704703308278059432L;
  protected TextField experiment = new TextField();
  protected TextField goal = new TextField();
  protected TextField taxonomy = new TextField();
  protected TextField protein = new TextField();
  protected TextField molecularWeight = new TextField();
  protected TextField postTranslationModification = new TextField();
  protected RadioButtonGroup<SampleType> sampleType = new RadioButtonGroup<>();
  protected TextField samplesCount = new TextField();
  protected TextArea samplesNames = new TextArea();
  protected TextField quantity = new TextField();
  protected TextField volume = new TextField();
  protected RadioButtonGroup<InjectionType> injection = new RadioButtonGroup<>();
  protected RadioButtonGroup<MassDetectionInstrumentSource> source = new RadioButtonGroup<>();
  protected ComboBox<MassDetectionInstrument> instrument = new ComboBox<>();
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private Binder<Samples> samplesBinder = new BeanValidationBinder<>(Samples.class);
  private transient SubmissionSampleService sampleService;
  private transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected IntactProteinSubmissionForm(SubmissionSampleService sampleService,
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
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3));
    add(new FormLayout(experiment, goal, taxonomy, protein, molecularWeight,
        postTranslationModification),
        new FormLayout(sampleType, samplesCount, samplesNames, quantity, volume),
        new FormLayout(injection, source, instrument));
    experiment.setId(id(EXPERIMENT));
    goal.setId(id(GOAL));
    taxonomy.setId(id(TAXONOMY));
    protein.setId(id(PROTEIN));
    molecularWeight.setId(id(MOLECULAR_WEIGHT));
    postTranslationModification.setId(id(POST_TRANSLATION_MODIFICATION));
    quantity.setId(id(QUANTITY));
    volume.setId(id(VOLUME));
    sampleType.setId(id(SAMPLES_TYPE));
    sampleType.setItems(DRY, SOLUTION);
    sampleType.setRenderer(
        new TextRenderer<>(value -> getTranslation(SAMPLE_TYPE_PREFIX + value.name())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    sampleType.addValueChangeListener(e -> sampleTypeChanged());
    samplesCount.setId(id(SAMPLES_COUNT));
    samplesNames.setId(id(SAMPLES_NAMES));
    samplesNames.setMinHeight("10em");
    injection.setId(id(INJECTION_TYPE));
    injection.setItems(InjectionType.values());
    injection.setRenderer(
        new TextRenderer<>(value -> getTranslation(INJECTION_TYPE_PREFIX + value.name())));
    injection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    source.setId(id(SOURCE));
    source.setItems(MassDetectionInstrumentSource.availables());
    source.setRenderer(new TextRenderer<>(
        value -> getTranslation(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + value.name())));
    source.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    instrument.setId(id(INSTRUMENT));
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setItemLabelGenerator(
        value -> getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + value.name()));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    experiment.setLabel(getTranslation(SUBMISSION_PREFIX + EXPERIMENT));
    goal.setLabel(getTranslation(SUBMISSION_PREFIX + GOAL));
    taxonomy.setLabel(getTranslation(SUBMISSION_PREFIX + TAXONOMY));
    protein.setLabel(getTranslation(SUBMISSION_PREFIX + PROTEIN));
    molecularWeight.setLabel(getTranslation(SUBMISSION_SAMPLE_PREFIX + MOLECULAR_WEIGHT));
    postTranslationModification
        .setLabel(getTranslation(SUBMISSION_PREFIX + POST_TRANSLATION_MODIFICATION));
    quantity.setLabel(getTranslation(SAMPLE_PREFIX + QUANTITY));
    quantity.setPlaceholder(getTranslation(MESSAGES_PREFIX + QUANTITY_PLACEHOLDER));
    volume.setLabel(getTranslation(SAMPLE_PREFIX + VOLUME));
    volume.setPlaceholder(getTranslation(MESSAGES_PREFIX + VOLUME_PLACEHOLDER));
    sampleType.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLES_TYPE));
    samplesCount.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT));
    samplesNames.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES));
    injection.setLabel(getTranslation(SUBMISSION_PREFIX + INJECTION_TYPE));
    source.setLabel(getTranslation(SUBMISSION_PREFIX + SOURCE));
    instrument.setLabel(getTranslation(SUBMISSION_PREFIX + INSTRUMENT));
    binder.forField(experiment).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(goal).withNullRepresentation("").bind(GOAL);
    binder.forField(taxonomy).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(TAXONOMY);
    binder.forField(protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(molecularWeight).withNullRepresentation("")
        .withConverter(
            new StringToDoubleConverter(getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)))
        .bind(MOLECULAR_WEIGHT);
    binder.forField(postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(sampleType).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(TYPE);
    samplesBinder.forField(samplesCount).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("")
        .withConverter(
            new StringToIntegerConverter(getTranslation(CONSTANTS_PREFIX + INVALID_INTEGER)))
        .bind(SAMPLES_COUNT);
    samplesBinder.forField(samplesNames).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("")
        .withConverter(new IntactProteinSubmissionForm.SamplesNamesConverter())
        .withValidator(samplesNamesDuplicates(getLocale()))
        .withValidator(samplesNamesExists(getLocale()))
        .withValidator(samplesNamesCount(getLocale())).bind(SAMPLES_NAMES);
    firstSampleBinder.forField(quantity).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(QUANTITY);
    volume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(volume)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").bind(VOLUME);
    binder.forField(injection).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(INJECTION_TYPE);
    binder.forField(source).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED)).bind(SOURCE);
    binder.forField(instrument).withNullRepresentation(MassDetectionInstrument.NULL)
        .bind(INSTRUMENT);
    sampleTypeChanged();
    setReadOnly();
  }

  public Submission getSubmission() {
    return binder.getBean();
  }

  public void setSubmission(Submission submission) {
    Objects.requireNonNull(submission);
    if (submission.getSamples() == null || submission.getSamples().isEmpty()) {
      throw new IllegalArgumentException("submission must contain at least one sample");
    }
    binder.setBean(submission);
    firstSampleBinder.setBean(submission.getSamples().get(0));
    Samples samples = new Samples();
    samples.setSamplesCount(submission.getSamples().size());
    samples.setSamplesNames(
        submission.getSamples().stream().map(Sample::getName).collect(Collectors.toList()));
    samplesBinder.setBean(samples);
    setReadOnly();
  }

  private void sampleTypeChanged() {
    SampleType type = sampleType.getValue();
    volume.setEnabled(type == SOLUTION);
  }

  private Optional<Integer> samplesCount() {
    try {
      return Optional.of(Integer.parseInt(samplesCount.getValue()));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private Validator<List<String>> samplesNamesDuplicates(Locale locale) {
    return (values, context) -> {
      Set<String> duplicates = new HashSet<>();
      Optional<String> duplicate =
          values.stream().filter(name -> !duplicates.add(name)).findFirst();
      if (duplicate.isPresent()) {
        return ValidationResult
            .error(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_DUPLICATES, duplicate.get()));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<String>> samplesNamesExists(Locale locale) {
    return (values, context) -> {
      Set<String> oldNames =
          binder.getBean().getSamples().stream().map(Sample::getName).collect(Collectors.toSet());
      Optional<String> exists = values.stream()
          .filter(name -> sampleService.exists(name) && !oldNames.contains(name)).findFirst();
      if (exists.isPresent()) {
        return ValidationResult
            .error(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_EXISTS, exists.get()));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<String>> samplesNamesCount(Locale locale) {
    return (values, context) -> {
      Optional<Integer> samplesCount = samplesCount();
      if (samplesCount.isPresent() && samplesCount.get() != values.size()) {
        return ValidationResult.error(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_WRONG_COUNT,
            values.size(), samplesCount.get()));
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

  BinderValidationStatus<Samples> validateSamples() {
    return samplesBinder.validate();
  }

  public boolean isValid() {
    boolean valid = true;
    valid = validateSubmission().isOk() && valid;
    valid = validateFirstSample().isOk() && valid;
    valid = validateSamples().isOk() && valid;
    if (valid) {
      try {
        updateSubmissionSamples();
      } catch (ValidationException e) {
        return false;
      }
    }
    return valid;
  }

  private void updateSubmissionSamples() throws ValidationException {
    Submission submission = binder.getBean();
    Samples samples = samplesBinder.getBean();
    submission.getSamples().get(0).setName(samples.getSamplesNames().get(0));
    while (submission.getSamples().size() < samples.samplesCount) {
      submission.getSamples().add(new SubmissionSample());
    }
    while (submission.getSamples().size() > samples.samplesCount) {
      submission.getSamples().remove(submission.getSamples().size() - 1);
    }
    for (int i = 0; i < samples.samplesCount; i++) {
      SubmissionSample sample = submission.getSamples().get(i);
      try {
        firstSampleBinder.writeBean(sample);
      } catch (ValidationException e) {
        logger.warn(
            "firstSampleBinder validation passed, but failed when writing to sample " + sample);
        throw e;
      }
      submission.getSamples().get(i).setName(samples.getSamplesNames().get(i));
    }
  }

  private void setReadOnly() {
    boolean readOnly = !authenticatedUser.hasPermission(binder.getBean(), WRITE);
    binder.setReadOnly(readOnly);
    firstSampleBinder.setReadOnly(readOnly);
    samplesBinder.setReadOnly(readOnly);
  }

  /**
   * Represents a list of sample names.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = INNER_CLASS_EI_EXPOSE_REP)
  protected static class Samples {
    private int samplesCount;
    private List<String> samplesNames;

    public int getSamplesCount() {
      return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
      this.samplesCount = samplesCount;
    }

    public List<String> getSamplesNames() {
      return samplesNames;
    }

    public void setSamplesNames(List<String> samplesNames) {
      this.samplesNames = samplesNames;
    }
  }

  private static class SamplesNamesConverter implements Converter<String, List<String>> {
    private static final long serialVersionUID = 8024859234735628305L;

    @Override
    public Result<List<String>> convertToModel(String value, ValueContext context) {
      return Result.ok(Arrays.asList(value.split("\\s*[,;\\t\\n]\\s*")).stream()
          .filter(val -> !val.isEmpty()).collect(Collectors.toList()));
    }

    @Override
    public String convertToPresentation(List<String> value, ValueContext context) {
      return value.stream().map(val -> Objects.toString(val, "")).collect(Collectors.joining(", "));
    }
  }
}
