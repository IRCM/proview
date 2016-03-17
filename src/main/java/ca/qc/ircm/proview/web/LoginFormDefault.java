package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * Login form.
 */
public class LoginFormDefault extends LoginForm implements MessageResourcesComponent {
  private static final long serialVersionUID = -3289207896401263662L;
  private Label header = new Label();

  @Override
  protected Component createContent(TextField userNameField, PasswordField passwordField,
      Button loginButton) {
    FormLayout layout = new FormLayout();
    layout.setSpacing(true);

    layout.addComponent(header);
    layout.addComponent(userNameField);
    layout.addComponent(passwordField);
    layout.addComponent(loginButton);
    return layout;
  }

  @Override
  protected String getUserNameFieldCaption() {
    return getResources().message("username");
  }

  @Override
  protected String getPasswordFieldCaption() {
    return getResources().message("password");
  }

  @Override
  protected String getLoginButtonCaption() {
    return getResources().message("button");
  }

  public Label getHeader() {
    return header;
  }
}
