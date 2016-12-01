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

import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.GENERAL_MESSAGES;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SIGN_PANEL = "sign";
  public static final String SIGN_USERNAME = "sign.username";
  public static final String SIGN_PASSWORD = "sign.password";
  public static final String SIGN_BUTTON = "sign.button";
  public static final String FORGOT_PASSWORD = "forgotPassword";
  public static final String FORGOT_PASSWORD_EMAIL = "forgotPassword.email";
  public static final String FORGOT_PASSWORD_BUTTON = "forgotPassword.button";
  public static final String REGISTER = "register";
  public static final String REGISTER_BUTTON = "register.button";
  private static final Logger logger = LoggerFactory.getLogger(MainViewPresenter.class);
  private MainView view;
  private ObjectProperty<String> username = new ObjectProperty<>(null, String.class);
  private ObjectProperty<String> password = new ObjectProperty<>(null, String.class);
  private ObjectProperty<String> forgotPasswordEmail = new ObjectProperty<>(null, String.class);
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private UserService userService;
  @Inject
  private ForgotPasswordService forgotPasswordService;
  @Inject
  private MainUi ui;
  @Value("${spring.application.name}")
  private String applicationName;

  public MainViewPresenter() {
  }

  protected MainViewPresenter(AuthenticationService authenticationService,
      AuthorizationService authorizationService, UserService userService,
      ForgotPasswordService forgotPasswordService, MainUi ui, String applicationName) {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.userService = userService;
    this.forgotPasswordService = forgotPasswordService;
    this.ui = ui;
    this.applicationName = applicationName;
  }

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(MainView view) {
    this.view = view;
    prepareComponents();
    bindFields();
    addFieldListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.setTitle(resources.message(TITLE, applicationName));
    view.header.addStyleName(HEADER);
    view.header.setValue(resources.message(HEADER));
    view.signPanel.addStyleName(SIGN_PANEL);
    view.signPanel.setCaption(resources.message(SIGN_PANEL));
    view.signForm.getUserNameField().addStyleName(SIGN_USERNAME);
    view.signForm.getUserNameField().setCaption(resources.message(SIGN_USERNAME));
    view.signForm.getUserNameField().setNullRepresentation("");
    view.signForm.getUserNameField().setRequired(true);
    view.signForm.getUserNameField().setRequiredError(
        generalResources.message(REQUIRED, view.signForm.getUserNameField().getCaption()));
    view.signForm.getUserNameField()
        .addValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)));
    view.signForm.getPasswordField().addStyleName(SIGN_PASSWORD);
    view.signForm.getPasswordField().setCaption(resources.message(SIGN_PASSWORD));
    view.signForm.getPasswordField().setNullRepresentation("");
    view.signForm.getPasswordField().setRequired(true);
    view.signForm.getPasswordField().setRequiredError(
        generalResources.message(REQUIRED, view.signForm.getPasswordField().getCaption()));
    view.signForm.getLoginButton().addStyleName(SIGN_BUTTON);
    view.signForm.getLoginButton().setCaption(resources.message(SIGN_BUTTON));
    view.forgotPasswordPanel.addStyleName(FORGOT_PASSWORD);
    view.forgotPasswordPanel.setCaption(resources.message(FORGOT_PASSWORD));
    view.forgotPasswordEmailField.addStyleName(FORGOT_PASSWORD_EMAIL);
    view.forgotPasswordEmailField.setNullRepresentation("");
    view.forgotPasswordEmailField.setCaption(resources.message(FORGOT_PASSWORD_EMAIL));
    view.forgotPasswordEmailField.setRequired(true);
    view.forgotPasswordEmailField.setRequiredError(
        generalResources.message(REQUIRED, view.forgotPasswordEmailField.getCaption()));
    view.forgotPasswordEmailField
        .addValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)));
    view.forgotPasswordButton.addStyleName(FORGOT_PASSWORD_BUTTON);
    view.forgotPasswordButton.setCaption(resources.message(FORGOT_PASSWORD_BUTTON));
    view.registerPanel.addStyleName(REGISTER);
    view.registerPanel.setCaption(resources.message(REGISTER));
    view.registerButton.addStyleName(REGISTER_BUTTON);
    view.registerButton.setCaption(resources.message(REGISTER_BUTTON));
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

  private boolean validateSign() {
    logger.trace("Validate sign user");
    boolean valid = true;
    try {
      view.signForm.getUserNameField().commit();
      view.signForm.getPasswordField().commit();
    } catch (InvalidValueException e) {
      final MessageResource generalResources =
          new MessageResource(GENERAL_MESSAGES, view.getLocale());
      logger.debug("Validation failed for sign user with message {}", e.getMessage());
      view.showError(generalResources.message(FIELD_NOTIFICATION));
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
      final MessageResource generalResources =
          new MessageResource(GENERAL_MESSAGES, view.getLocale());
      logger.trace("Validation failed for forgot password with message {}", e.getMessage(), e);
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      valid = false;
    }
    return valid;
  }

  private void forgotPassword() {
    if (validateForgotPassword()) {
      String email = forgotPasswordEmail.getValue();
      logger.debug("Create forgot password for user {}", email);
      if (userService.exists(email)) {
        forgotPasswordService.insert(email,
            (forgotPassword, locale) -> ui.getUrl(MainView.VIEW_NAME) + "/" + forgotPassword.getId()
                + "/" + forgotPassword.getConfirmNumber());
      }
      MessageResource resources = view.getResources();
      view.showWarning(resources.message(FORGOT_PASSWORD + ".done"));
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
