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
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
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
  private Binder<SigninInformation> signBinder = new Binder<>(SigninInformation.class);
  private Binder<SigninInformation> forgotPasswordBinder = new Binder<>(SigninInformation.class);
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private UserService userService;
  @Inject
  private ForgotPasswordService forgotPasswordService;
  @Value("${spring.application.name}")
  private String applicationName;

  public MainViewPresenter() {
  }

  protected MainViewPresenter(AuthenticationService authenticationService,
      AuthorizationService authorizationService, UserService userService,
      ForgotPasswordService forgotPasswordService, String applicationName) {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.userService = userService;
    this.forgotPasswordService = forgotPasswordService;
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
    signBinder.setBean(new SigninInformation());
    forgotPasswordBinder.setBean(new SigninInformation());
    prepareComponents();
    addFieldListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.header.addStyleName(HEADER);
    view.header.setValue(resources.message(HEADER));
    view.signPanel.addStyleName(SIGN_PANEL);
    view.signPanel.setCaption(resources.message(SIGN_PANEL));
    view.signForm.getUserNameField().addStyleName(SIGN_USERNAME);
    view.signForm.getUserNameField().setCaption(resources.message(SIGN_USERNAME));
    signBinder.forField(view.signForm.getUserNameField())
        .asRequired(generalResources.message(REQUIRED))
        .withValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)))
        .bind(SigninInformation::getUsername, SigninInformation::setUsername);
    view.signForm.getPasswordField().addStyleName(SIGN_PASSWORD);
    view.signForm.getPasswordField().setCaption(resources.message(SIGN_PASSWORD));
    signBinder.forField(view.signForm.getPasswordField())
        .asRequired(generalResources.message(REQUIRED))
        .bind(SigninInformation::getPassword, SigninInformation::setPassword);
    view.signForm.getLoginButton().addStyleName(SIGN_BUTTON);
    view.signForm.getLoginButton().setCaption(resources.message(SIGN_BUTTON));
    view.forgotPasswordPanel.addStyleName(FORGOT_PASSWORD);
    view.forgotPasswordPanel.setCaption(resources.message(FORGOT_PASSWORD));
    view.forgotPasswordEmailField.addStyleName(FORGOT_PASSWORD_EMAIL);
    view.forgotPasswordEmailField.setCaption(resources.message(FORGOT_PASSWORD_EMAIL));
    forgotPasswordBinder.forField(view.forgotPasswordEmailField)
        .asRequired(generalResources.message(REQUIRED))
        .withValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)))
        .bind(SigninInformation::getUsername, SigninInformation::setUsername);
    view.forgotPasswordButton.addStyleName(FORGOT_PASSWORD_BUTTON);
    view.forgotPasswordButton.setCaption(resources.message(FORGOT_PASSWORD_BUTTON));
    view.registerPanel.addStyleName(REGISTER);
    view.registerPanel.setCaption(resources.message(REGISTER));
    view.registerButton.addStyleName(REGISTER_BUTTON);
    view.registerButton.setCaption(resources.message(REGISTER_BUTTON));
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
    BinderValidationStatus<SigninInformation> validation = signBinder.validate();
    if (!validation.isOk()) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.debug("Validation failed for sign user with messages {}",
          validation.getValidationErrors());
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return validation.isOk();
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
    BinderValidationStatus<SigninInformation> validation = forgotPasswordBinder.validate();
    if (!validation.isOk()) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Validation failed for forgot password with message {}",
          validation.getValidationErrors());
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return validation.isOk();
  }

  private void forgotPassword() {
    if (validateForgotPassword()) {
      String email = forgotPasswordBinder.getBean().getUsername();
      logger.debug("Create forgot password for user {}", email);
      if (userService.exists(email)) {
        forgotPasswordService.insert(email,
            (forgotPassword, locale) -> view.getUrl(MainView.VIEW_NAME) + "/"
                + forgotPassword.getId() + "/" + forgotPassword.getConfirmNumber());
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

  public static class SigninInformation {
    private String username;
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
