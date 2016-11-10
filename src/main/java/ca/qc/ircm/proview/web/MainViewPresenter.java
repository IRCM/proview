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
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
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
  private ObjectProperty<String> username = new ObjectProperty<>(null, String.class);
  private ObjectProperty<String> password = new ObjectProperty<>(null, String.class);
  private ObjectProperty<String> forgotPasswordEmail = new ObjectProperty<>(null, String.class);
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private AuthorizationService authorizationService;

  public MainViewPresenter() {
  }

  protected MainViewPresenter(AuthenticationService authenticationService,
      AuthorizationService authorizationService) {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
  }

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(MainView view) {
    this.view = view;
    bindFields();
    addFieldListeners();
    setCaptions();
    setRequired();
    addValidators();
  }

  private void bindFields() {
    view.signForm.getUserNameField().setPropertyDataSource(username);
    view.signForm.getPasswordField().setPropertyDataSource(password);
    view.forgotPasswordEmailField.setPropertyDataSource(forgotPasswordEmail);
  }

  private void addFieldListeners() {
    view.signForm.addLoginListener(e -> sign());
    view.forgotPasswordButton.addClickListener(e -> forgotPassword());
    view.registerButton.addClickListener(e -> {
      view.navigateTo(RegisterView.VIEW_NAME);
    });
  }

  private void setCaptions() {
    view.signForm.getHeader().setStyleName("h2");
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    view.header.setValue(resources.message("header"));
    view.signForm.getHeader().setValue(resources.message("sign"));
    view.signForm.getUserNameField().setCaption(resources.message("sign.username"));
    view.signForm.getUserNameField().setNullRepresentation("");
    view.signForm.getPasswordField().setCaption(resources.message("sign.password"));
    view.signForm.getPasswordField().setNullRepresentation("");
    view.signForm.getLoginButton().setCaption(resources.message("sign.button"));
    view.forgotPasswordHeader.setValue(resources.message("forgotPassword"));
    view.forgotPasswordEmailField.setNullRepresentation("");
    view.forgotPasswordEmailField.setCaption(resources.message("forgotPassword.email"));
    view.forgotPasswordButton.setCaption(resources.message("forgotPassword.button"));
    view.registerHeader.setValue(resources.message("register"));
    view.registerButton.setCaption(resources.message("register.button"));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.signForm.getUserNameField().setRequired(true);
    view.signForm.getUserNameField().setRequiredError(
        generalResources.message("required", view.signForm.getUserNameField().getCaption()));
    view.signForm.getPasswordField().setRequired(true);
    view.signForm.getPasswordField().setRequiredError(
        generalResources.message("required", view.signForm.getPasswordField().getCaption()));
    view.forgotPasswordEmailField.setRequired(true);
    view.forgotPasswordEmailField.setRequiredError(
        generalResources.message("required", view.forgotPasswordEmailField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    view.signForm.getUserNameField()
        .addValidator(new EmailValidator(resources.message("sign.email.invalid")));
    view.forgotPasswordEmailField
        .addValidator(new EmailValidator(resources.message("forgotPassword.email.invalid")));
  }

  private boolean validateSign() {
    logger.trace("Validate sign user");
    boolean valid = true;
    try {
      view.signForm.getUserNameField().commit();
      view.signForm.getPasswordField().commit();
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
      String username = view.signForm.getUserNameField().getValue();
      String password = view.signForm.getPasswordField().getValue();
      try {
        logger.debug("User {} tries to signin", username);
        authenticationService.sign(username, password, true);
        String viewName = MainView.VIEW_NAME;
        if (authorizationService.isUser()) {
          viewName = SubmissionsView.VIEW_NAME;
        }
        view.navigateTo(viewName);
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
      view.forgotPasswordEmailField.commit();
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
      view.showWarning(resources.message("forgotPassword.done"));
    }
  }

  /**
   * Go to submissions view if user is signed.
   * 
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    if (authorizationService.isUser()) {
      view.navigateTo(SubmissionsView.VIEW_NAME);
    }
  }
}
