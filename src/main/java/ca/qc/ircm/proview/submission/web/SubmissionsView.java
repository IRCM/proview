package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Submissions view.
 */
@SpringView(name = SubmissionsView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class SubmissionsView extends SubmissionsViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "submissions";
  private static final long serialVersionUID = -7912663074202035516L;
  @Inject
  private SubmissionsViewPresenter presenter;
  protected Menu menu = new Menu();

  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  /**
   * Open view submission window.
   *
   * @param submission
   *          submission to view
   */
  public void viewSubmission(Submission submission) {
    // TODO Replace by submission window.
  }

  /**
   * Open view submission's results window.
   *
   * @param submission
   *          submission to view
   */
  public void viewSubmissionResults(Submission submission) {
    // TODO Replace by submission results window.
  }
}
