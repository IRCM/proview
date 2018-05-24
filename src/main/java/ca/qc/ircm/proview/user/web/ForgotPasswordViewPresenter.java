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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Binder;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Forgot password view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ForgotPasswordViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PASSWORD_PANEL = "passwordPanel";
  public static final String PASSWORD = "password";
  public static final String CONFIRM_PASSWORD = "confirmPassword";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String INVALID_FORGOT_PASSWORD = "invalidForgotPassword";
  private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordViewPresenter.class);
  private ForgotPasswordView view;
  private ForgotPasswordViewDesign design;
  private Binder<Passwords> passwordsBinder = new Binder<>(Passwords.class);
  private ForgotPassword forgotPassword;
  @Inject
  private ForgotPasswordService forgotPasswordService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected ForgotPasswordViewPresenter() {
  }

  protected ForgotPasswordViewPresenter(ForgotPasswordService forgotPasswordService,
      String applicationName) {
    this.forgotPasswordService = forgotPasswordService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(ForgotPasswordView view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    passwordsBinder.setBean(new Passwords());
    addListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    design.passwordPanel.addStyleName(PASSWORD_PANEL);
    design.passwordPanel.setCaption(resources.message(PASSWORD_PANEL));
    design.passwordField.addStyleName(PASSWORD);
    design.passwordField.setCaption(resources.message(PASSWORD));
    passwordsBinder.forField(design.passwordField).asRequired(generalResources.message(REQUIRED))
        .withValidator(password -> {
          String confirmPassword = design.confirmPasswordField.getValue();
          return password == null || password.isEmpty() || confirmPassword == null
              || confirmPassword.isEmpty() || password.equals(confirmPassword);
        }, resources.message(property(PASSWORD, "notMatch")))
        .bind(Passwords::getPassword, Passwords::setPassword);
    design.confirmPasswordField.addStyleName(CONFIRM_PASSWORD);
    design.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD));
    passwordsBinder.forField(design.confirmPasswordField)
        .asRequired(generalResources.message(REQUIRED))
        .bind(Passwords::getConfirmPassword, Passwords::setConfirmPassword);
    design.saveButton.addStyleName(SAVE);
    design.saveButton.setCaption(resources.message(SAVE));
  }

  private void addListeners() {
    design.confirmPasswordField.addValueChangeListener(e -> {
      passwordsBinder.validate();
    });
    design.saveButton.addClickListener(e -> save());
  }

  private boolean validate() {
    if (forgotPassword == null) {
      view.showError(view.getResources().message(INVALID_FORGOT_PASSWORD));
      return false;
    }

    boolean valid = validate(passwordsBinder);
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Forgot password validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      String password = design.passwordField.getValue();
      forgotPasswordService.updatePassword(forgotPassword, password);
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(SAVED));
      view.navigateTo(MainView.VIEW_NAME);
    }
  }

  /**
   * Called by view when view is entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    forgotPassword = null;
    if (parameters != null && !parameters.isEmpty()) {
      String[] values = parameters.split("/", -1);
      if (values.length == 2) {
        try {
          Long id = Long.valueOf(values[0]);
          Integer confirmNumber = Integer.valueOf(values[1]);
          forgotPassword = forgotPasswordService.get(id, confirmNumber);
        } catch (NumberFormatException e) {
          forgotPassword = null;
        }
      }
    } else {
      forgotPassword = null;
    }

    if (forgotPassword == null) {
      view.showError(view.getResources().message(INVALID_FORGOT_PASSWORD));
    }
  }

  private static class Passwords {
    private String password;
    private String confirmPassword;

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getConfirmPassword() {
      return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
      this.confirmPassword = confirmPassword;
    }
  }
}
