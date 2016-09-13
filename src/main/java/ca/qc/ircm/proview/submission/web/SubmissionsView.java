package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;

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
}
