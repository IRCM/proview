package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.property;

import ca.qc.ircm.proview.Constants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.io.Serial;
import org.springframework.lang.Nullable;

/**
 * Passwords form.
 */
public class PasswordsForm extends FormLayout implements LocaleChangeObserver {

  public static final String CLASS_NAME = "passwords";
  public static final String PASSWORD = "password";
  public static final String PASSWORD_CONFIRM = PASSWORD + "Confirm";
  public static final String PASSWORDS_NOT_MATCH = property(PASSWORD, "notMatch");
  private static final String MESSAGES_PREFIX = messagePrefix(PasswordsForm.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = -2396373044368644264L;
  protected PasswordField password = new PasswordField();
  protected PasswordField passwordConfirm = new PasswordField();
  private final Binder<Passwords> passwordBinder = new BeanValidationBinder<>(Passwords.class);
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
    password.setLabel(getTranslation(MESSAGES_PREFIX + PASSWORD));
    passwordConfirm.setLabel(getTranslation(MESSAGES_PREFIX + PASSWORD_CONFIRM));
    passwordBinder.forField(password)
        .withValidator(passwordRequiredValidator(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").withValidator(password -> {
          String confirmPassword = passwordConfirm.getValue();
          return password == null || password.isEmpty() || confirmPassword == null
              || confirmPassword.isEmpty() || password.equals(confirmPassword);
        }, getTranslation(MESSAGES_PREFIX + PASSWORDS_NOT_MATCH))
        .bind(Passwords::getPassword, Passwords::setPassword);
    passwordConfirm.setLabel(getTranslation(MESSAGES_PREFIX + PASSWORD_CONFIRM));
    passwordBinder.forField(passwordConfirm)
        .withValidator(passwordRequiredValidator(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("")
        .bind(Passwords::getConfirmPassword, Passwords::setConfirmPassword);
  }

  private Validator<String> passwordRequiredValidator(String errorMessage) {
    return (value, context) -> required && value.isEmpty() ? ValidationResult.error(errorMessage)
        : ValidationResult.ok();
  }

  @Nullable
  public String getPassword() {
    return passwordBinder.getBean().getPassword();
  }

  public BinderValidationStatus<Passwords> validate() {
    return passwordBinder.validate();
  }

  public boolean isValid() {
    return passwordBinder.validate().isOk();
  }

  public boolean isRequired() {
    return required;
  }

  /**
   * Sets if password is required.
   *
   * @param required true if password is required, false otherwise
   */
  public void setRequired(boolean required) {
    this.required = required;
    password.setRequiredIndicatorVisible(required);
    passwordConfirm.setRequiredIndicatorVisible(required);
  }
}
