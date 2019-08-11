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
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.IgnoreConversionIfDisabledConverter;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import java.util.Objects;
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
  private Binder<SubmissionSample> firstSampleBinder = new BeanValidationBinder<>(
      SubmissionSample.class);
  private Binder<Samples> samplesBinder = new BeanValidationBinder<>(Samples.class);

  @Autowired
  protected LcmsmsSubmissionFormPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param form
   *          form
   */
  void init(LcmsmsSubmissionForm form) {
    this.form = form;
    form.sampleType.addValueChangeListener(e -> sampleTypeChanged());
    form.coloration.addValueChangeListener(e -> colorationChanged());
    form.digestion.addValueChangeListener(e -> digestionChanged());
    form.identification.addValueChangeListener(e -> identificationChanged());
    form.quantification.addValueChangeListener(e -> quantificationChanged());
  }

  void localeChange(Locale locale) {
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
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
        .withNullRepresentation("").bind(SAMPLES_NAMES);
    firstSampleBinder.forField(form.quantity).asRequired(webResources.message(REQUIRED))
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
    binder.forField(form.instrument).bind(INSTRUMENT);
    binder.forField(form.identification).asRequired(webResources.message(REQUIRED))
        .bind(IDENTIFICATION);
    form.identificationLink.setRequiredIndicatorVisible(true);
    binder.forField(form.identificationLink)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(IDENTIFICATION_LINK);
    binder.forField(form.quantification).bind(QUANTIFICATION);
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
    return valid;
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
        submission.getSamples().stream().map(Sample::getName).collect(Collectors.joining(", ")));
    samplesBinder.setBean(samples);
  }

  public static class Samples {
    private int samplesCount;
    private String samplesNames;

    public int getSamplesCount() {
      return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
      this.samplesCount = samplesCount;
    }

    public String getSamplesNames() {
      return samplesNames;
    }

    public void setSamplesNames(String samplesNames) {
      this.samplesNames = samplesNames;
    }
  }
}
