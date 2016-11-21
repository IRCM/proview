package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import ca.qc.ircm.proview.web.SaveEvent;
import com.vaadin.ui.Notification;

/**
 * User form.
 */
public class NewUserForm extends NewUserFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -7630525674289902028L;
  private NewUserFormPresenter presenter;

  public void setPresenter(NewUserFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    phoneNumbersLayout.removeAllComponents();
    presenter.init(this);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  public void showWarning(String message) {
    Notification.show(message, Notification.Type.WARNING_MESSAGE);
  }

  public void showTrayNotification(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
  }

  public void fireSaveEvent(User user) {
    fireEvent(new SaveEvent(this, user));
  }
}
