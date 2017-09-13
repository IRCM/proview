package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.component.BaseComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Submission history form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionHistoryForm extends SubmissionHistoryFormDesign implements BaseComponent {
  private static final long serialVersionUID = 4814629523385144606L;
  @Inject
  private SubmissionHistoryFormPresenter presenter;

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Submission getBean() {
    return presenter.getBean();
  }

  public void setBean(Submission submission) {
    presenter.setBean(submission);
  }
}
