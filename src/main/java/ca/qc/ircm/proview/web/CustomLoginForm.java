/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web;

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
public class CustomLoginForm extends LoginForm {
  private static final long serialVersionUID = -3289207896401263662L;
  private Label header = new Label();
  private TextField userNameField;
  private PasswordField passwordField;
  private Button loginButton;

  @Override
  protected Component createContent(TextField userNameField, PasswordField passwordField,
      Button loginButton) {
    this.userNameField = userNameField;
    this.passwordField = passwordField;
    this.loginButton = loginButton;
    FormLayout layout = new FormLayout();
    layout.setSpacing(true);

    layout.addComponent(header);
    header.setId("loginForm-header");
    layout.addComponent(userNameField);
    userNameField.setId("loginForm-userNameField");
    layout.addComponent(passwordField);
    passwordField.setId("loginForm-passwordField");
    layout.addComponent(loginButton);
    loginButton.setId("loginForm-loginButton");
    return layout;
  }

  public Label getHeader() {
    return header;
  }

  public TextField getUserNameField() {
    return userNameField;
  }

  public PasswordField getPasswordField() {
    return passwordField;
  }

  public Button getLoginButton() {
    return loginButton;
  }
}
