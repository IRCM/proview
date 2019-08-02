package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
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

  @Autowired
  protected PrintSubmissionViewPresenter() {
  }

  void init(PrintSubmissionView view) {
    this.view = view;
  }

  void setParameter(Long parameter) {
    // TODO Auto-generated method stub
  }

  Submission getSubmission() {
    return submission;
  }
}
