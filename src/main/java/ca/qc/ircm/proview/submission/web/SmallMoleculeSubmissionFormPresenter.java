package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Service;
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
 * Submission form presenter for {@link Service#LC_MS_MS}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SmallMoleculeSubmissionFormPresenter {
  private static final Logger logger = LoggerFactory
      .getLogger(SmallMoleculeSubmissionFormPresenter.class);
  private SmallMoleculeSubmissionForm form;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder = new BeanValidationBinder<>(
      SubmissionSample.class);

  @Autowired
  protected SmallMoleculeSubmissionFormPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param form
   *          form
   */
  void init(SmallMoleculeSubmissionForm form) {
    this.form = form;
    form.sampleType.addValueChangeListener(e -> sampleTypeChanged());
  }

  void localeChange(Locale locale) {
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    firstSampleBinder.forField(form.sampleType).asRequired(webResources.message(REQUIRED))
        .bind(TYPE);
    firstSampleBinder.forField(form.sampleName).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(NAME);
    binder.forField(form.solvent).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(SOLUTION_SOLVENT);
    binder.forField(form.formula).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(FORMULA);
    binder.forField(form.monoisotopicMass).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(MONOISOTOPIC_MASS);
    binder.forField(form.averageMass).withNullRepresentation("").bind(AVERAGE_MASS);
    binder.forField(form.toxicity).withNullRepresentation("").bind(TOXICITY);
    binder.forField(form.lightSensitive).bind(LIGHT_SENSITIVE);
    binder.forField(form.storageTemperature).asRequired(webResources.message(REQUIRED))
        .bind(STORAGE_TEMPERATURE);
    binder.forField(form.highResolution).asRequired(webResources.message(REQUIRED))
        .bind(HIGH_RESOLUTION);
    sampleTypeChanged();
  }

  private void sampleTypeChanged() {
    SampleType type = form.sampleType.getValue();
    form.solvent.setEnabled(type == SOLUTION);
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