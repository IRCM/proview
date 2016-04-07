package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * User form.
 */
public class UserForm extends UserFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -4585597583437283309L;
  private UserFormPresenter presenter;

  public void setPresenter(UserFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public TextField getEmailField() {
    return emailField;
  }

  public TextField getNameField() {
    return nameField;
  }

  public PasswordField getPasswordField() {
    return passwordField;
  }

  public PasswordField getConfirmPasswordField() {
    return confirmPasswordField;
  }
}
