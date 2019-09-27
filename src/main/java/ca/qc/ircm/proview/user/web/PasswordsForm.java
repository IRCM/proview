package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

/**
 * Passwords form.
 */
public class PasswordsForm extends FormLayout implements LocaleChangeObserver {
  public static final String CLASS_NAME = "passwords";
  public static final String PASSWORD = "password";
  public static final String PASSWORD_CONFIRM = PASSWORD + "Confirm";
  public static final String PASSWORDS_NOT_MATCH = property(PASSWORD, "notMatch");
  private static final long serialVersionUID = -2396373044368644264L;
  protected PasswordField password = new PasswordField();
  protected PasswordField passwordConfirm = new PasswordField();
  private Binder<Passwords> passwordBinder = new BeanValidationBinder<>(Passwords.class);
  private boolean required;

  /**
   * Initializes passwords form.
   */
  public PasswordsForm() {
    addClassName(CLASS_NAME);
    add(password, passwordConfirm);
    password.addClassName(PASSWORD);
    passwordConfirm.addClassName(PASSWORD_CONFIRM);
    passwordBinder.setBean(new Passwords());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(PasswordsForm.class, getLocale());
    final AppResources webResources = new AppResources(WebConstants.class, getLocale());
    password.setLabel(resources.message(PASSWORD));
    passwordConfirm.setLabel(resources.message(PASSWORD_CONFIRM));
    passwordBinder.forField(password)
        .withValidator(passwordRequiredValidator(webResources.message(REQUIRED)))
        .withNullRepresentation("").withValidator(password -> {
          String confirmPassword = passwordConfirm.getValue();
          return password == null || password.isEmpty() || confirmPassword == null
              || confirmPassword.isEmpty() || password.equals(confirmPassword);
        }, resources.message(PASSWORDS_NOT_MATCH))
        .bind(Passwords::getPassword, Passwords::setPassword);
    passwordConfirm.setLabel(resources.message(PASSWORD_CONFIRM));
    passwordBinder.forField(passwordConfirm)
        .withValidator(passwordRequiredValidator(webResources.message(REQUIRED)))
        .withNullRepresentation("")
        .bind(Passwords::getConfirmPassword, Passwords::setConfirmPassword);
  }

  private Validator<String> passwordRequiredValidator(String errorMessage) {
    return (value, context) -> required && value.isEmpty() ? ValidationResult.error(errorMessage)
        : ValidationResult.ok();
  }

  public String getPassword() {
    return password.getValue();
  }

  public BinderValidationStatus<Passwords> validate() {
    return passwordBinder.validate();
  }

  public boolean isRequired() {
    return required;
  }

  /**
   * Sets if password is required.
   *
   * @param required
   *          true if password is required, false otherwise
   */
  public void setRequired(boolean required) {
    this.required = required;
    password.setRequiredIndicatorVisible(required);
    passwordConfirm.setRequiredIndicatorVisible(required);
  }
}
