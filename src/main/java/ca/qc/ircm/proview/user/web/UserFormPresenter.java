package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.UserError;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFormPresenter {
  public static final String ID_PROPERTY = QUser.user.id.getMetadata().getName();
  public static final String EMAIL_PROPERTY = QUser.user.email.getMetadata().getName();
  public static final String NAME_PROPERTY = QUser.user.name.getMetadata().getName();
  public static final String PASSWORD_PROPERTY = "password";
  public static final String CONFIRM_PASSWORD_PROPERTY = "confirmPassword";
  public static final String LABORATORY_PROPERTY = QUser.user.laboratory.getMetadata().getName();
  public static final String ADDRESSES_PROPERTY = QUser.user.address.getMetadata().getName();
  public static final String PHONE_NUMBERS_PROPERTY =
      QUser.user.phoneNumbers.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private Long userId;
  private UserForm view;
  private TextField emailField;
  private TextField nameField;
  private PasswordField passwordField;
  private PasswordField confirmPasswordField;
  @Inject
  private UserService userService;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(UserForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    bindFields();
    addFieldListeners();
    updateEditable();
  }

  private void setFields() {
    emailField = view.getEmailField();
    nameField = view.getNameField();
    passwordField = view.getPasswordField();
    confirmPasswordField = view.getConfirmPasswordField();
  }

  private void bindFields() {
    userFieldGroup.setItemDataSource(new BeanItem<>(new User()));
    userFieldGroup.bind(emailField, EMAIL_PROPERTY);
    userFieldGroup.bind(nameField, NAME_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    passwordField.addValueChangeListener(e -> validatePasswordsMatch());
    confirmPasswordField.addValueChangeListener(e -> validatePasswordsMatch());
  }

  private void validatePasswordsMatch() {
    passwordField.setComponentError(null);
    String password = passwordField.getValue();
    String confirmPassword = confirmPasswordField.getValue();
    if (password != null && !password.isEmpty() && confirmPassword != null
        && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
      final MessageResource resources = view.getResources();
      passwordField.setComponentError(new UserError(resources.message("password.notMatch")));
    }
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    emailField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    emailField.setReadOnly(!editable);
    nameField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    nameField.setReadOnly(!editable);
    passwordField.setVisible(editable);
    confirmPasswordField.setVisible(editable);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
    setRequired();
    addValidators();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    emailField.setCaption(resources.message(EMAIL_PROPERTY));
    nameField.setCaption(resources.message(NAME_PROPERTY));
    passwordField.setCaption(resources.message(PASSWORD_PROPERTY));
    confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD_PROPERTY));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    emailField.setRequired(true);
    emailField.setRequiredError(generalResources.message("required", emailField.getCaption()));
    nameField.setRequired(true);
    nameField.setRequiredError(generalResources.message("required", nameField.getCaption()));
    passwordField
        .setRequiredError(generalResources.message("required", passwordField.getCaption()));
    confirmPasswordField
        .setRequiredError(generalResources.message("required", confirmPasswordField.getCaption()));
    passwordField.setRequired(userId == null);
    confirmPasswordField.setRequired(userId == null);
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    emailField.addValidator(new EmailValidator(resources.message("email.invalid")));
    emailField.addValidator((value) -> {
      String email = emailField.getValue();
      if (userService.exists(email) && userId != null
          && !userService.get(userId).getEmail().equals(email)) {
        throw new InvalidValueException(resources.message("email.exists"));
      }
    });
  }

  public void commit() throws CommitException {
    userFieldGroup.commit();
  }

  public boolean isValid() {
    return userFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return userFieldGroup.getItemDataSource();
  }

  /**
   * Sets user as an item.
   *
   * @param item
   *          user as an item
   */
  public void setItemDataSource(Item item) {
    userFieldGroup.setItemDataSource(item);
    userId = (Long) item.getItemProperty(ID_PROPERTY).getValue();
    setRequired();
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
