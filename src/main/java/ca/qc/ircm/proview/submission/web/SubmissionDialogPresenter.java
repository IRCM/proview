package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
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
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
  }

  void edit() {
    UI.getCurrent().navigate(SubmissionView.class, binder.getBean().getId());
  }

  void print() {
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
    if (submission == null) {
      submission = new Submission();
    }
    binder.setBean(submission);
    if (submission.getId() != null) {
      dialog.header.setText(submission.getName());
    }
    dialog.printContent.setSubmission(submission);
  }
}
