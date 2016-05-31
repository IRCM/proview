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

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Main presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(MainViewPresenter.class);
  private MainView view;
  private Label header;
  private CustomLoginForm signForm;
  private Label forgotPasswordHeader;
  private TextField forgotPasswordEmailField;
  private Button forgotPasswordButton;
  private Label registerHeader;
  private Button registerButton;
  private ObjectProperty<String> username = new ObjectProperty<String>(null, String.class);
  private ObjectProperty<String> password = new ObjectProperty<String>(null, String.class);
  private ObjectProperty<String> forgotPasswordEmail =
      new ObjectProperty<String>(null, String.class);
  @Inject
  private AuthenticationService authenticationService;

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(MainView view) {
    this.view = view;
    setFields();
    bindFields();
    addFieldListeners();
    setCaptions();
    setRequired();
    addValidators();
  }

  private void setFields() {
    header = view.getHeader();
    signForm = view.getSignForm();
    forgotPasswordHeader = view.getForgotPasswordHeader();
    forgotPasswordEmailField = view.getForgotPasswordEmailField();
    forgotPasswordButton = view.getForgotPasswordButton();
    registerHeader = view.getRegisterHeader();
    registerButton = view.getRegisterButton();
  }

  private void bindFields() {
    signForm.getUserNameField().setPropertyDataSource(username);
    signForm.getPasswordField().setPropertyDataSource(password);
    forgotPasswordEmailField.setPropertyDataSource(forgotPasswordEmail);
  }

  private void addFieldListeners() {
    signForm.addLoginListener(e -> sign());
    forgotPasswordButton.addClickListener(e -> forgotPassword());
    registerButton.addClickListener(e -> {
      view.navigateToRegister();
    });
  }

  private void setCaptions() {
    signForm.getHeader().setStyleName("h2");
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    header.setValue(resources.message("header"));
    signForm.getHeader().setValue(resources.message("sign"));
    signForm.getUserNameField().setCaption(resources.message("sign.username"));
    signForm.getUserNameField().setNullRepresentation("");
    signForm.getPasswordField().setCaption(resources.message("sign.password"));
    signForm.getPasswordField().setNullRepresentation("");
    signForm.getLoginButton().setCaption(resources.message("sign.button"));
    forgotPasswordHeader.setValue(resources.message("forgotPassword"));
    forgotPasswordEmailField.setNullRepresentation("");
    forgotPasswordEmailField.setCaption(resources.message("forgotPassword.email"));
    forgotPasswordButton.setCaption(resources.message("forgotPassword.button"));
    registerHeader.setValue(resources.message("register"));
    registerButton.setCaption(resources.message("register.button"));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    signForm.getUserNameField().setRequired(true);
    signForm.getUserNameField().setRequiredError(
        generalResources.message("required", signForm.getUserNameField().getCaption()));
    signForm.getPasswordField().setRequired(true);
    signForm.getPasswordField().setRequiredError(
        generalResources.message("required", signForm.getPasswordField().getCaption()));
    forgotPasswordEmailField.setRequired(true);
    forgotPasswordEmailField.setRequiredError(
        generalResources.message("required", forgotPasswordEmailField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    signForm.getUserNameField()
        .addValidator(new EmailValidator(resources.message("sign.email.invalid")));
    forgotPasswordEmailField
        .addValidator(new EmailValidator(resources.message("forgotPassword.email.invalid")));
  }

  private boolean validateSign() {
    logger.trace("Validate sign user");
    boolean valid = true;
    try {
      signForm.getUserNameField().commit();
      signForm.getPasswordField().commit();
    } catch (InvalidValueException e) {
      logger.debug("Validation failed for sign user with message {}", e.getMessage());
      view.showError(e.getMessage());
      valid = false;
    }
    return valid;
  }

  private void sign() {
    if (validateSign()) {
      MessageResource resources = view.getResources();
      String username = signForm.getUserNameField().getValue();
      String password = signForm.getPasswordField().getValue();
      try {
        logger.debug("User {} tries to signin", username);
        authenticationService.sign(username, password, true);
        view.afterSuccessfulSign();
        logger.debug("User {} signed successfully", username);
      } catch (AuthenticationException ae) {
        logger.debug("User {} could not be authenticated, {}", username, ae.getMessage());
        view.showError(resources.message("sign.fail"));
      }
    }
  }

  private boolean validateForgotPassword() {
    logger.trace("Validate forgot password creation");
    boolean valid = true;
    try {
      forgotPasswordEmailField.commit();
    } catch (InvalidValueException e) {
      logger.debug("Validation failed for forgot password with message {}", e.getMessage());
      view.showError(e.getMessage());
      valid = false;
    }
    return valid;
  }

  private void forgotPassword() {
    if (validateForgotPassword()) {
      String email = forgotPasswordEmail.getValue();
      logger.debug("Create forgot password for user {}", email);
      // TODO Create forgot password.
      MessageResource resources = view.getResources();
      view.afterSuccessfulForgotPassword(resources.message("forgotPassword.done"));
    }
  }
}
