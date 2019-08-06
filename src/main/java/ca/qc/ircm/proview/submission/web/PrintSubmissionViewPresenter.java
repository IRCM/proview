package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Print submission view.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrintSubmissionViewPresenter {
  private PrintSubmissionView view;
  private Submission submission;
  private SubmissionService service;

  @Autowired
  protected PrintSubmissionViewPresenter(SubmissionService service) {
    this.service = service;
  }

  void init(PrintSubmissionView view) {
    this.view = view;
  }

  void setParameter(Long parameter) {
    if (parameter != null) {
      submission = service.get(parameter);
    }
    view.printContent.setSubmission(submission);
  }

  Submission getSubmission() {
    return submission;
  }
}
