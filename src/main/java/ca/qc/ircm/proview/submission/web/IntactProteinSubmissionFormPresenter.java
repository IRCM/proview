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
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import java.util.Objects;
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
  private static final Logger logger = LoggerFactory
      .getLogger(IntactProteinSubmissionFormPresenter.class);
  private IntactProteinSubmissionForm form;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder = new BeanValidationBinder<>(
      SubmissionSample.class);

  @Autowired
  protected IntactProteinSubmissionFormPresenter() {
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
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    binder.forField(form.experiment).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(form.goal).withNullRepresentation("").bind(GOAL);
    binder.forField(form.taxonomy).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(TAXONOMY);
    binder.forField(form.protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(form.molecularWeight).withNullRepresentation("")
        .bind(MOLECULAR_WEIGHT);
    binder.forField(form.postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(form.sampleType).asRequired(webResources.message(REQUIRED))
        .bind(TYPE);
    firstSampleBinder.forField(form.quantity).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(QUANTITY);
    firstSampleBinder.forField(form.volume).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(VOLUME);
    binder.forField(form.injection).asRequired(webResources.message(REQUIRED)).bind(INJECTION_TYPE);
    binder.forField(form.source).asRequired(webResources.message(REQUIRED)).bind(SOURCE);
    binder.forField(form.instrument).bind(INSTRUMENT);
    sampleTypeChanged();
  }

  private void sampleTypeChanged() {
    SampleType type = form.sampleType.getValue();
    form.volume.setEnabled(type == SOLUTION);
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
  }
}
