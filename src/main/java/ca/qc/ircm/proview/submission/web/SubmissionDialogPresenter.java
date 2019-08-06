package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission dialog presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionDialogPresenter {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialogPresenter.class);
  private SubmissionDialog dialog;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private SubmissionService service;

  @Autowired
  protected SubmissionDialogPresenter(SubmissionService service) {
    this.service = service;
  }

  /**
   * Initializes presenter.
   *
   * @param dialog
   *          dialog
   */
  void init(SubmissionDialog dialog) {
    this.dialog = dialog;
    setSubmission(null);
  }

  void localeChange(Locale locale) {
    binder.forField(dialog.sampleDeliveryDate).bind(SAMPLE_DELIVERY_DATE);
    binder.forField(dialog.digestionDate).bind(DIGESTION_DATE);
    binder.forField(dialog.analysisDate).bind(ANALYSIS_DATE);
    binder.forField(dialog.dataAvailableDate).bind(DATA_AVAILABLE_DATE);
  }

  private boolean validate() {
    return validateSubmission().isOk();
  }

  BinderValidationStatus<Submission> validateSubmission() {
    return binder.validate();
  }

  void save() {
    if (validate()) {
      service.update(binder.getBean(), null);
    }
  }

  void edit() {
    UI.getCurrent().navigate(SubmissionView.class, binder.getBean().getId());
  }

  void print() {
    UI.getCurrent().navigate(PrintSubmissionView.class, binder.getBean().getId());
    dialog.close();
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
    if (submission == null) {
      submission = new Submission();
    }
    binder.setBean(submission);
    dialog.printContent.setSubmission(submission);
  }
}
