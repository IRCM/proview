package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.QAddress;
import ca.qc.ircm.proview.user.QPhoneNumber;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Registers user presenter.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegisterPresenter {
  public static final String emailProperty = QUser.user.email.getMetadata().getName();
  public static final String nameProperty = QUser.user.name.getMetadata().getName();
  public static final String laboratoryNameProperty =
      QLaboratory.laboratory.name.getMetadata().getName();
  public static final String organizationProperty =
      QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String addressProperty = QAddress.address1.address.getMetadata().getName();
  public static final String addressSecondProperty =
      QAddress.address1.addressSecond.getMetadata().getName();
  public static final String townProperty = QAddress.address1.town.getMetadata().getName();
  public static final String stateProperty = QAddress.address1.state.getMetadata().getName();
  public static final String countryProperty = QAddress.address1.country.getMetadata().getName();
  public static final String postalCodeProperty =
      QAddress.address1.postalCode.getMetadata().getName();
  public static final String phoneNumberProperty =
      QPhoneNumber.phoneNumber.number.getMetadata().getName();
  public static final String phoneExtensionProperty =
      QPhoneNumber.phoneNumber.extension.getMetadata().getName();
  public static final String passwordProperty = "password";
  public static final String confirmPasswordProperty = "confirmPassword";
  private static final Logger logger = LoggerFactory.getLogger(RegisterPresenter.class);
  private RegisterView view;
  private User user = new User();
  private Laboratory laboratory = new Laboratory();
  private User manager = new User();
  private Address userAddress = new Address();
  private PhoneNumber userPhoneNumber = new PhoneNumber();
  private Passwords passwords = new Passwords();
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private BeanFieldGroup<Passwords> passwordsFieldGroup = new BeanFieldGroup<>(Passwords.class);
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private BeanFieldGroup<User> managerFieldGroup = new BeanFieldGroup<>(User.class);
  private BeanFieldGroup<Address> addressFieldGroup = new BeanFieldGroup<>(Address.class);
  private BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup =
      new BeanFieldGroup<>(PhoneNumber.class);
  private Label headerLabel;
  private TextField emailField;
  private TextField nameField;
  private PasswordField passwordField;
  private PasswordField confirmPasswordField;
  private Label laboratoryHeaderLabel;
  private CheckBox newLaboratoryField;
  private TextField organizationField;
  private TextField laboratoryNameField;
  private TextField managerEmailField;
  private Label addressHeaderLabel;
  private TextField addressField;
  private TextField addressSecondField;
  private TextField townField;
  private TextField stateField;
  private ComboBox countryField;
  private TextField postalCodeField;
  private Button clearAddressButton;
  private Label phoneNumberHeaderLabel;
  private TextField phoneNumberField;
  private TextField phoneExtensionField;
  private Label registerHeaderLabel;
  private Button registerButton;
  private Label requiredLabel;
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  @Inject
  private UserService userService;
  @Inject
  private VaadinUtils vaadinUtils;

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(RegisterView view) {
    this.view = view;
    setFields();
    setDefaultAddress();
    bindFields();
    setFieldValues();
    addFieldListeners();
    setCaptions();
    setRequired();
    addValidators();
  }

  private void setFields() {
    headerLabel = view.getHeaderLabel();
    emailField = view.getEmailField();
    nameField = view.getNameField();
    passwordField = view.getPasswordField();
    confirmPasswordField = view.getConfirmPasswordField();
    laboratoryHeaderLabel = view.getLaboratoryHeaderLabel();
    newLaboratoryField = view.getNewLaboratoryField();
    organizationField = view.getOrganizationField();
    laboratoryNameField = view.getLaboratoryNameField();
    managerEmailField = view.getManagerEmailField();
    addressHeaderLabel = view.getAddressHeaderLabel();
    addressField = view.getAddressField();
    addressSecondField = view.getAddressSecondField();
    townField = view.getTownField();
    stateField = view.getStateField();
    countryField = view.getCountryField();
    postalCodeField = view.getPostalCodeField();
    clearAddressButton = view.getClearAddressButton();
    phoneNumberHeaderLabel = view.getPhoneNumberHeaderLabel();
    phoneNumberField = view.getPhoneNumberField();
    phoneExtensionField = view.getPhoneExtensionField();
    registerHeaderLabel = view.getRegisterHeaderLabel();
    registerButton = view.getRegisterButton();
    requiredLabel = view.getRequiredLabel();
  }

  private void bindFields() {
    userFieldGroup.setItemDataSource(user);
    userFieldGroup.bind(emailField, emailProperty);
    userFieldGroup.bind(nameField, nameProperty);
    passwordsFieldGroup.setItemDataSource(passwords);
    passwordsFieldGroup.bind(passwordField, passwordProperty);
    passwordsFieldGroup.bind(confirmPasswordField, confirmPasswordProperty);
    laboratoryFieldGroup.setItemDataSource(laboratory);
    laboratoryFieldGroup.bind(laboratoryNameField, laboratoryNameProperty);
    laboratoryFieldGroup.bind(organizationField, organizationProperty);
    managerFieldGroup.setItemDataSource(manager);
    managerFieldGroup.bind(managerEmailField, emailProperty);
    addressFieldGroup.setItemDataSource(userAddress);
    addressFieldGroup.bind(addressField, addressProperty);
    addressFieldGroup.bind(addressSecondField, addressSecondProperty);
    addressFieldGroup.bind(townField, townProperty);
    addressFieldGroup.bind(stateField, stateProperty);
    addressFieldGroup.bind(countryField, countryProperty);
    addressFieldGroup.bind(postalCodeField, postalCodeProperty);
    phoneNumberFieldGroup.setItemDataSource(userPhoneNumber);
    phoneNumberFieldGroup.bind(phoneNumberField, phoneNumberProperty);
    phoneNumberFieldGroup.bind(phoneExtensionField, phoneExtensionProperty);
  }

  private void setFieldValues() {
    String[] countries = applicationConfiguration.getCountries();
    for (String country : countries) {
      countryField.addItem(country);
    }
  }

  private void addFieldListeners() {
    newLaboratoryField.addValueChangeListener(e -> {
      boolean value = (boolean) e.getProperty().getValue();
      laboratoryNameField.setVisible(value);
      organizationField.setVisible(value);
      managerEmailField.setVisible(!value);
      if (value) {
        organizationField.focus();
      } else {
        managerEmailField.focus();
      }
    });
    registerButton.addClickListener(e -> {
      registerUser();
    });
    clearAddressButton.addClickListener(e -> {
      addressField.setValue("");
      townField.setValue("");
      stateField.setValue("");
      postalCodeField.setValue("");
      countryField.setValue(getDefaultCountry());
    });
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

  private String getDefaultCountry() {
    String[] countries = applicationConfiguration.getCountries();
    if (countries.length > 0) {
      return countries[0];
    } else {
      return null;
    }
  }

  private void setDefaultAddress() {
    userAddress.setAddress(applicationConfiguration.getAddress());
    userAddress.setTown(applicationConfiguration.getTown());
    userAddress.setState(applicationConfiguration.getState());
    userAddress.setPostalCode(applicationConfiguration.getPostalCode());
    userAddress.setCountry(getDefaultCountry());
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    headerLabel.setValue(resources.message("header"));
    emailField.setCaption(resources.message("email"));
    nameField.setCaption(resources.message("name"));
    passwordField.setCaption(resources.message("password"));
    confirmPasswordField.setCaption(resources.message("confirmPassword"));
    laboratoryHeaderLabel.setValue(resources.message("laboratoryHeader"));
    newLaboratoryField.setCaption(resources.message("newLaboratory"));
    organizationField.setCaption(resources.message("organization"));
    laboratoryNameField.setCaption(resources.message("laboratoryName"));
    managerEmailField.setCaption(resources.message("manager"));
    addressHeaderLabel.setValue(resources.message("addressHeader"));
    addressField.setCaption(resources.message("address"));
    addressSecondField.setCaption(resources.message("addressSecond"));
    townField.setCaption(resources.message("town"));
    stateField.setCaption(resources.message("state"));
    countryField.setCaption(resources.message("country"));
    postalCodeField.setCaption(resources.message("postalCode"));
    clearAddressButton.setCaption(resources.message("clearAddress"));
    phoneNumberHeaderLabel.setValue(resources.message("phoneNumberHeader"));
    phoneNumberField.setCaption(resources.message("phoneNumber"));
    phoneExtensionField.setCaption(resources.message("phoneExtension"));
    registerHeaderLabel.setValue(resources.message("registerHeader"));
    registerButton.setCaption(resources.message("register"));
    requiredLabel.setValue(resources.message("required"));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    emailField.setRequired(true);
    emailField.setRequiredError(generalResources.message("required", emailField.getCaption()));
    nameField.setRequired(true);
    nameField.setRequiredError(generalResources.message("required", nameField.getCaption()));
    passwordField.setRequired(true);
    passwordField
        .setRequiredError(generalResources.message("required", passwordField.getCaption()));
    confirmPasswordField.setRequired(true);
    confirmPasswordField
        .setRequiredError(generalResources.message("required", confirmPasswordField.getCaption()));
    organizationField.setRequired(true);
    organizationField
        .setRequiredError(generalResources.message("required", organizationField.getCaption()));
    laboratoryNameField.setRequired(true);
    laboratoryNameField
        .setRequiredError(generalResources.message("required", laboratoryNameField.getCaption()));
    managerEmailField.setRequired(true);
    managerEmailField
        .setRequiredError(generalResources.message("required", managerEmailField.getCaption()));
    addressField.setRequired(true);
    addressField.setRequiredError(generalResources.message("required", addressField.getCaption()));
    townField.setRequired(true);
    townField.setRequiredError(generalResources.message("required", townField.getCaption()));
    stateField.setRequired(true);
    stateField.setRequiredError(generalResources.message("required", stateField.getCaption()));
    countryField.setRequired(true);
    countryField.setRequiredError(generalResources.message("required", countryField.getCaption()));
    postalCodeField.setRequired(true);
    postalCodeField
        .setRequiredError(generalResources.message("required", postalCodeField.getCaption()));
    phoneNumberField.setRequired(true);
    phoneNumberField
        .setRequiredError(generalResources.message("required", phoneNumberField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    emailField.addValidator(new EmailValidator(resources.message("email.invalid")));
    emailField.addValidator((value) -> {
      if (userService.exists(emailField.getValue())) {
        throw new InvalidValueException(resources.message("email.exists"));
      }
    });
    managerEmailField.addValidator(new EmailValidator(resources.message("manager.invalid")));
    managerEmailField.addValidator(value -> {
      if (!userService.isManager(managerEmailField.getValue())) {
        throw new InvalidValueException(resources.message("manager.notExists"));
      }
    });
    phoneNumberField
        .addValidator(new RegexpValidator("[\\d\\-]*", resources.message("phoneNumber.invalid")));
    phoneExtensionField.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message("phoneExtension.invalid")));
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      userFieldGroup.commit();
      passwordsFieldGroup.commit();
      if (newLaboratoryField.getValue()) {
        laboratoryFieldGroup.commit();
      } else {
        managerFieldGroup.commit();
      }
      addressFieldGroup.commit();
      phoneNumberFieldGroup.commit();
    } catch (InvalidValueException e) {
      logger.debug("Validation failed with message {}", e.getMessage());
      view.showError(e.getMessage());
      valid = false;
    } catch (CommitException e) {
      String message = vaadinUtils.getFieldMessage(e, view.getLocale());
      logger.debug("Validation failed with message {}", message);
      view.showError(message);
      valid = false;
    }
    return valid;
  }

  private void registerUser() {
    if (validate()) {
      logger.debug("Register user {}", user);
      user.setAddress(userAddress);
      user.setPhoneNumbers(new ArrayList<>());
      userPhoneNumber.setType(PhoneNumberType.WORK);
      user.getPhoneNumbers().add(userPhoneNumber);
      User manager = this.manager;
      if (newLaboratoryField.getValue()) {
        user.setLaboratory(laboratory);
        manager = null;
      }
      user.setLocale(view.getLocale());
      userService.register(user, passwords.getPassword(), manager,
          locale -> vaadinUtils.getUrl(ValidateView.VIEW_NAME));
      view.afterSuccessfulRegister();
    }
  }
}
