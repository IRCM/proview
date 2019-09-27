package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import ca.qc.ircm.proview.web.WebConstants;
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
import com.vaadin.flow.spring.annotation.SpringComponent;
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
 * Submission view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IntactProteinSubmissionFormPresenter {
  private static final Logger logger =
      LoggerFactory.getLogger(IntactProteinSubmissionFormPresenter.class);
  private IntactProteinSubmissionForm form;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private Binder<Samples> samplesBinder = new BeanValidationBinder<>(Samples.class);
  private SubmissionSampleService sampleService;

  @Autowired
  protected IntactProteinSubmissionFormPresenter(SubmissionSampleService sampleService) {
    this.sampleService = sampleService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  void init(IntactProteinSubmissionForm form) {
    this.form = form;
    form.sampleType.addValueChangeListener(e -> sampleTypeChanged());
  }

  void localeChange(Locale locale) {
    final AppResources webResources = new AppResources(WebConstants.class, locale);
    binder.forField(form.experiment).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(form.goal).withNullRepresentation("").bind(GOAL);
    binder.forField(form.taxonomy).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(TAXONOMY);
    binder.forField(form.protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(form.molecularWeight).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(MOLECULAR_WEIGHT);
    binder.forField(form.postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(form.sampleType).asRequired(webResources.message(REQUIRED))
        .bind(TYPE);
    samplesBinder.forField(form.samplesCount).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToIntegerConverter(webResources.message(INVALID_INTEGER)))
        .bind(SAMPLES_COUNT);
    samplesBinder.forField(form.samplesNames).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").withConverter(new SamplesNamesConverter())
        .withValidator(samplesNamesDuplicates(locale)).withValidator(samplesNamesExists(locale))
        .withValidator(samplesNamesCount(locale)).bind(SAMPLES_NAMES);
    firstSampleBinder.forField(form.quantity).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(QUANTITY);
    form.volume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(form.volume)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(VOLUME);
    binder.forField(form.injection).asRequired(webResources.message(REQUIRED)).bind(INJECTION_TYPE);
    binder.forField(form.source).asRequired(webResources.message(REQUIRED)).bind(SOURCE);
    binder.forField(form.instrument).withNullRepresentation(MassDetectionInstrument.NULL)
        .bind(INSTRUMENT);
    sampleTypeChanged();
  }

  private void sampleTypeChanged() {
    SampleType type = form.sampleType.getValue();
    form.volume.setEnabled(type == SOLUTION);
  }

  private Optional<Integer> samplesCount() {
    try {
      return Optional.of(Integer.parseInt(form.samplesCount.getValue()));
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
        final AppResources resources = new AppResources(IntactProteinSubmissionForm.class, locale);
        return ValidationResult.error(resources.message(SAMPLES_NAMES_DUPLICATES, duplicate.get()));
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
        final AppResources resources = new AppResources(IntactProteinSubmissionForm.class, locale);
        return ValidationResult.error(resources.message(SAMPLES_NAMES_EXISTS, exists.get()));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<String>> samplesNamesCount(Locale locale) {
    return (values, context) -> {
      Optional<Integer> samplesCount = samplesCount();
      if (samplesCount.isPresent() && samplesCount.get() != values.size()) {
        final AppResources resources = new AppResources(IntactProteinSubmissionForm.class, locale);
        return ValidationResult
            .error(resources.message(SAMPLES_NAMES_WRONG_COUNT, values.size(), samplesCount.get()));
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

  boolean isValid() {
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

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
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
  }

  public static class Samples {
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
