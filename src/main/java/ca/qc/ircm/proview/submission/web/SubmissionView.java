package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Submission view.
 */
@SpringView(name = SubmissionView.VIEW_NAME)
public class SubmissionView extends SubmissionViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "submission";
  private static final long serialVersionUID = -6009778227571187664L;
  @Inject
  private SubmissionViewPresenter presenter;
  @Inject
  protected SubmissionFormPresenter submissionFormPresenter;
  protected Menu menu = new Menu();
  protected SubmissionForm submissionForm = new SubmissionForm();

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    submissionFormLayout.addComponent(submissionForm);
    submissionForm.setPresenter(submissionFormPresenter);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }
}
