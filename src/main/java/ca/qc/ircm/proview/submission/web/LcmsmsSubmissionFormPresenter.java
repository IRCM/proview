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

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.GEL;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.IgnoreConversionIfDisabledConverter;
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
 * Submission form presenter for {@link Service#LC_MS_MS}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LcmsmsSubmissionFormPresenter {
  private static final Logger logger = LoggerFactory.getLogger(LcmsmsSubmissionFormPresenter.class);
  private LcmsmsSubmissionForm form;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private Binder<Samples> samplesBinder = new BeanValidationBinder<>(Samples.class);
  private SubmissionSampleService sampleService;

  @Autowired
  protected LcmsmsSubmissionFormPresenter(SubmissionSampleService sampleService) {
    this.sampleService = sampleService;
  }

  /**
   * Initializes presenter.
   *
   * @param form
   *          form
   */
  void init(LcmsmsSubmissionForm form) {
    this.form = form;
    form.samplesNames.addValueChangeListener(
        e -> logger.debug("sampleNames: {}", samplesBinder.getBean().getSamplesNames()));
    form.sampleType.addValueChangeListener(e -> sampleTypeChanged());
    form.coloration.addValueChangeListener(e -> colorationChanged());
    form.digestion.addValueChangeListener(e -> digestionChanged());
    form.identification.addValueChangeListener(e -> identificationChanged());
    form.quantification.addValueChangeListener(e -> quantificationChanged());
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
    form.quantity.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(form.quantity)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(QUANTITY);
    form.volume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(form.volume)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(VOLUME);
    form.separation.setRequiredIndicatorVisible(true);
    binder.forField(form.separation)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(SEPARATION);
    form.thickness.setRequiredIndicatorVisible(true);
    binder.forField(form.thickness)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(THICKNESS);
    form.coloration.setRequiredIndicatorVisible(true);
    binder.forField(form.coloration)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(COLORATION);
    form.otherColoration.setRequiredIndicatorVisible(true);
    binder.forField(form.otherColoration)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(OTHER_COLORATION);
    binder.forField(form.developmentTime).bind(DEVELOPMENT_TIME);
    binder.forField(form.destained).bind(DECOLORATION);
    binder.forField(form.weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new IgnoreConversionIfDisabledConverter<>(
            new StringToDoubleConverter(webResources.message(INVALID_NUMBER))))
        .bind(WEIGHT_MARKER_QUANTITY);
    binder.forField(form.proteinQuantity).bind(PROTEIN_QUANTITY);
    binder.forField(form.digestion).asRequired(webResources.message(REQUIRED)).bind(DIGESTION);
    form.usedDigestion.setRequiredIndicatorVisible(true);
    binder.forField(form.usedDigestion)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(USED_DIGESTION);
    form.otherDigestion.setRequiredIndicatorVisible(true);
    binder.forField(form.otherDigestion)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(OTHER_DIGESTION);
    binder.forField(form.proteinContent).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_CONTENT);
    binder.forField(form.instrument).withNullRepresentation(MassDetectionInstrument.NULL)
        .bind(INSTRUMENT);
    binder.forField(form.identification).asRequired(webResources.message(REQUIRED))
        .bind(IDENTIFICATION);
    form.identificationLink.setRequiredIndicatorVisible(true);
    binder.forField(form.identificationLink)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(IDENTIFICATION_LINK);
    binder.forField(form.quantification).withNullRepresentation(Quantification.NULL)
        .bind(QUANTIFICATION);
    form.quantificationComment.setRequiredIndicatorVisible(true);
    binder.forField(form.quantificationComment)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(QUANTIFICATION_COMMENT);
    sampleTypeChanged();
    digestionChanged();
    identificationChanged();
    quantificationChanged();
  }

  private void sampleTypeChanged() {
    SampleType type = form.sampleType.getValue();
    form.quantity.setEnabled(type != GEL);
    form.volume.setEnabled(type != GEL && type != DRY);
    form.separation.setEnabled(type == GEL);
    form.thickness.setEnabled(type == GEL);
    form.coloration.setEnabled(type == GEL);
    form.developmentTime.setEnabled(type == GEL);
    form.destained.setEnabled(type == GEL);
    form.weightMarkerQuantity.setEnabled(type == GEL);
    form.proteinQuantity.setEnabled(type == GEL);
    colorationChanged();
  }

  private void colorationChanged() {
    SampleType type = form.sampleType.getValue();
    GelColoration coloration = form.coloration.getValue();
    form.otherColoration.setEnabled(type == SampleType.GEL && coloration == GelColoration.OTHER);
  }

  private void digestionChanged() {
    ProteolyticDigestion digestion = form.digestion.getValue();
    form.usedDigestion.setEnabled(digestion == ProteolyticDigestion.DIGESTED);
    form.otherDigestion.setEnabled(digestion == ProteolyticDigestion.OTHER);
  }

  private void identificationChanged() {
    ProteinIdentification identification = form.identification.getValue();
    form.identificationLink.setEnabled(identification == ProteinIdentification.OTHER);
  }

  private void quantificationChanged() {
    Quantification quantification = form.quantification.getValue();
    form.quantificationComment
        .setEnabled(quantification == Quantification.SILAC || quantification == Quantification.TMT);
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
        final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
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
        final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
        return ValidationResult.error(resources.message(SAMPLES_NAMES_EXISTS, exists.get()));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<String>> samplesNamesCount(Locale locale) {
    return (values, context) -> {
      Optional<Integer> samplesCount = samplesCount();
      if (samplesCount.isPresent() && samplesCount.get() != values.size()) {
        final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
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
    boolean valid = validateSubmission().isOk();
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
