package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.INVALID_EMAIL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.AddressProperties.COUNTRY;
import static ca.qc.ircm.proview.user.AddressProperties.LINE;
import static ca.qc.ircm.proview.user.AddressProperties.POSTAL_CODE;
import static ca.qc.ircm.proview.user.AddressProperties.STATE;
import static ca.qc.ircm.proview.user.AddressProperties.TOWN;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.EXTENSION;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.NUMBER;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.TYPE;
import static ca.qc.ircm.proview.user.UserProperties.ADMIN;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.MANAGER;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.Passwords.NOT_MATCH;
import static ca.qc.ircm.proview.user.web.PasswordsProperties.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.PasswordsProperties.PASSWORD;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.Permission;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;

/**
 * User form.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserForm extends FormLayout implements LocaleChangeObserver {

  public static final String ID = "user-form";
  public static final String HEADER = "header";
  public static final String EMAIL_PLACEHOLDER = "john.smith@ircm.qc.ca";
  public static final String NAME_PLACEHOLDER = "John Smith";
  public static final String CREATE_NEW_LABORATORY = "createNewLaboratory";
  public static final String LABORATORY_NAME = LaboratoryProperties.NAME;
  public static final String NEW_LABORATORY_NAME = "newLaboratoryName";
  public static final String LABORATORY_NAME_PLACEHOLDER = "Translational Proteomics";
  public static final String NUMBER_PLACEHOLDER = "514-987-5500";
  private static final String MESSAGES_PREFIX = messagePrefix(UserForm.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String PASSWORDS_PREFIX = messagePrefix(Passwords.class);
  private static final String ADDRESS_PREFIX = messagePrefix(Address.class);
  private static final String PHONE_NUMBER_PREFIX = messagePrefix(PhoneNumber.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 3285639770914046262L;
  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  protected TextField email = new TextField();
  protected TextField name = new TextField();
  protected Checkbox admin = new Checkbox();
  protected Checkbox manager = new Checkbox();
  protected PasswordField password = new PasswordField();
  protected PasswordField confirmPassword = new PasswordField();
  protected ComboBox<Laboratory> laboratory = new ComboBox<>();
  protected Checkbox createNewLaboratory = new Checkbox();
  protected TextField newLaboratoryName = new TextField();
  protected TextField addressLine = new TextField();
  protected TextField town = new TextField();
  protected TextField state = new TextField();
  protected TextField country = new TextField();
  protected TextField postalCode = new TextField();
  protected ComboBox<PhoneNumberType> phoneType = new ComboBox<>();
  protected TextField number = new TextField();
  protected TextField extension = new TextField();
  private final Binder<User> binder = new BeanValidationBinder<>(User.class);
  private final Binder<Passwords> passwordBinder = new BeanValidationBinder<>(Passwords.class);
  private ListDataProvider<Laboratory> laboratoriesDataProvider;
  private final Binder<Laboratory> laboratoryBinder = new BeanValidationBinder<>(Laboratory.class);
  private final Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
  private final Binder<PhoneNumber> phoneNumberBinder = new BeanValidationBinder<>(
      PhoneNumber.class);
  private User user;
  private final transient LaboratoryService laboratoryService;
  private final transient AuthenticatedUser authenticatedUser;
  private final transient DefaultAddressConfiguration defaultAddressConfiguration;

  @Autowired
  protected UserForm(LaboratoryService laboratoryService, AuthenticatedUser authenticatedUser,
      DefaultAddressConfiguration defaultAddressConfiguration) {
    this.laboratoryService = laboratoryService;
    this.authenticatedUser = authenticatedUser;
    this.defaultAddressConfiguration = defaultAddressConfiguration;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  /**
   * Initializes user dialog.
   */
  @PostConstruct
  protected void init() {
    setId(ID);
    setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("30em", 2),
        new ResponsiveStep("60em", 4));
    FormLayout userFields = new FormLayout(email, name, admin, manager, password, confirmPassword);
    userFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    FormLayout laboratoryFields = new FormLayout(laboratory, createNewLaboratory,
        newLaboratoryName);
    laboratoryFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    FormLayout addressFields = new FormLayout(addressLine, town, state, country, postalCode);
    addressFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    FormLayout phoneFields = new FormLayout(phoneType, number, extension);
    phoneFields.setResponsiveSteps(new ResponsiveStep("0", 1));
    add(userFields, laboratoryFields, addressFields, phoneFields);
    email.setId(id(EMAIL));
    email.setPlaceholder(EMAIL_PLACEHOLDER);
    email.setWidthFull();
    name.setId(id(NAME));
    name.setPlaceholder(NAME_PLACEHOLDER);
    name.setWidthFull();
    admin.setId(id(ADMIN));
    admin.setVisible(authenticatedUser.hasRole(UserRole.ADMIN));
    admin.setWidthFull();
    manager.setId(id(MANAGER));
    manager.setVisible(authenticatedUser.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER));
    manager.addValueChangeListener(e -> updateManager());
    manager.setWidthFull();
    password.setId(id(PASSWORD));
    password.setWidthFull();
    confirmPassword.setId(id(CONFIRM_PASSWORD));
    confirmPassword.setWidthFull();
    laboratory.setId(id(LABORATORY));
    if (authenticatedUser.hasRole(UserRole.ADMIN)) {
      laboratoriesDataProvider = DataProvider.ofCollection(laboratoryService.all());
    } else {
      laboratoriesDataProvider = DataProvider.fromStream(
          Stream.of(authenticatedUser.getUser().map(User::getLaboratory).orElse(new Laboratory())));
    }
    laboratory.setItems(laboratoriesDataProvider);
    laboratory.setRequiredIndicatorVisible(true);
    laboratory.setReadOnly(!authenticatedUser.hasRole(UserRole.ADMIN));
    laboratory.setEnabled(authenticatedUser.hasRole(UserRole.ADMIN));
    laboratory.setItemLabelGenerator(lab -> Objects.toString(lab.getName(), ""));
    laboratory.setWidthFull();
    createNewLaboratory.setId(id(CREATE_NEW_LABORATORY));
    createNewLaboratory.setVisible(authenticatedUser.hasRole(UserRole.ADMIN));
    createNewLaboratory.setEnabled(false);
    createNewLaboratory.addValueChangeListener(e -> updateCreateNewLaboratory());
    createNewLaboratory.setWidthFull();
    newLaboratoryName.setId(id(NEW_LABORATORY_NAME));
    newLaboratoryName.setPlaceholder(LABORATORY_NAME_PLACEHOLDER);
    newLaboratoryName.setVisible(authenticatedUser.hasRole(UserRole.ADMIN));
    newLaboratoryName.setEnabled(false);
    newLaboratoryName.setWidthFull();
    Address address = defaultAddressConfiguration.getAddress();
    addressLine.setId(id(LINE));
    addressLine.setPlaceholder(address.getLine());
    addressLine.setWidthFull();
    town.setId(id(TOWN));
    town.setPlaceholder(address.getTown());
    town.setWidthFull();
    state.setId(id(STATE));
    state.setPlaceholder(address.getState());
    state.setWidthFull();
    country.setId(id(COUNTRY));
    country.setPlaceholder(address.getCountry());
    country.setWidthFull();
    postalCode.setId(id(POSTAL_CODE));
    postalCode.setPlaceholder(address.getPostalCode());
    postalCode.setWidthFull();
    phoneType.setId(id(TYPE));
    phoneType.setItems(PhoneNumberType.values());
    phoneType.setItemLabelGenerator(type -> getTranslation(PHONE_NUMBER_TYPE_PREFIX + type.name()));
    phoneType.setValue(PhoneNumberType.WORK);
    phoneType.setWidthFull();
    number.setId(id(NUMBER));
    number.setPlaceholder(NUMBER_PLACEHOLDER);
    number.setWidthFull();
    extension.setId(id(EXTENSION));
    extension.setWidthFull();

    setUser(null);
    updateManager();
    updateCreateNewLaboratory();
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    email.setLabel(getTranslation(USER_PREFIX + EMAIL));
    name.setLabel(getTranslation(USER_PREFIX + NAME));
    admin.setLabel(getTranslation(USER_PREFIX + ADMIN));
    manager.setLabel(getTranslation(USER_PREFIX + MANAGER));
    password.setLabel(getTranslation(PASSWORDS_PREFIX + PASSWORD));
    confirmPassword.setLabel(getTranslation(PASSWORDS_PREFIX + CONFIRM_PASSWORD));
    laboratory.setLabel(getTranslation(USER_PREFIX + LABORATORY));
    createNewLaboratory.setLabel(getTranslation(MESSAGES_PREFIX + CREATE_NEW_LABORATORY));
    newLaboratoryName.setLabel(getTranslation(MESSAGES_PREFIX + NEW_LABORATORY_NAME));
    addressLine.setLabel(getTranslation(ADDRESS_PREFIX + LINE));
    town.setLabel(getTranslation(ADDRESS_PREFIX + TOWN));
    state.setLabel(getTranslation(ADDRESS_PREFIX + STATE));
    country.setLabel(getTranslation(ADDRESS_PREFIX + COUNTRY));
    postalCode.setLabel(getTranslation(ADDRESS_PREFIX + POSTAL_CODE));
    phoneType.setLabel(getTranslation(PHONE_NUMBER_PREFIX + TYPE));
    number.setLabel(getTranslation(PHONE_NUMBER_PREFIX + NUMBER));
    extension.setLabel(getTranslation(PHONE_NUMBER_PREFIX + EXTENSION));

    binder.forField(email).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("")
        .withValidator(new EmailValidator(getTranslation(CONSTANTS_PREFIX + INVALID_EMAIL)))
        .bind(EMAIL);
    binder.forField(name).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(NAME);
    binder.forField(admin).bind(ADMIN);
    binder.forField(manager).bind(MANAGER);
    binder.forField(laboratory)
        .withValidator(laboratoryRequiredValidator(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation(null).bind(LABORATORY);
    passwordBinder.setBean(new Passwords());
    passwordBinder.forField(password)
        .withValidator(passwordRequiredValidator(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").withValidator(password -> {
          String confirmPassword = this.confirmPassword.getValue();
          return password == null || confirmPassword == null || password.equals(confirmPassword);
        }, getTranslation(PASSWORDS_PREFIX + NOT_MATCH)).bind(PASSWORD);
    passwordBinder.forField(confirmPassword)
        .withValidator(passwordRequiredValidator(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").bind(CONFIRM_PASSWORD);
    laboratoryBinder.forField(newLaboratoryName)
        .asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED)).withNullRepresentation("")
        .bind(LABORATORY_NAME);
    addressBinder.forField(addressLine).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(LINE);
    addressBinder.forField(town).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(TOWN);
    addressBinder.forField(state).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(STATE);
    addressBinder.forField(country).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(COUNTRY);
    addressBinder.forField(postalCode).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(POSTAL_CODE);
    phoneNumberBinder.forField(phoneType).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(TYPE);
    phoneNumberBinder.forField(number).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(NUMBER);
    phoneNumberBinder.forField(extension).withNullRepresentation("").bind(EXTENSION);
    updateReadOnly();
  }

  private Validator<String> passwordRequiredValidator(String errorMessage) {
    return (value, context) -> user == null || user.getId() == 0 && value.isEmpty()
        ? ValidationResult.error(errorMessage) : ValidationResult.ok();
  }

  private Validator<Laboratory> laboratoryRequiredValidator(String errorMessage) {
    return (value, context) -> !createNewLaboratory.getValue() && value == null
        ? ValidationResult.error(errorMessage) : ValidationResult.ok();
  }

  private void updateReadOnly() {
    boolean readOnly =
        user.getId() != 0 && !authenticatedUser.hasPermission(user, Permission.WRITE);
    binder.setReadOnly(readOnly);
    laboratory.setReadOnly(!authenticatedUser.hasRole(UserRole.ADMIN));
    laboratory.setEnabled(
        !authenticatedUser.hasRole(UserRole.ADMIN) || !createNewLaboratory.getValue());
    password.setVisible(!readOnly);
    confirmPassword.setVisible(!readOnly);
    addressBinder.setReadOnly(readOnly);
    phoneNumberBinder.setReadOnly(readOnly);
  }

  private void updateManager() {
    if (authenticatedUser.hasRole(UserRole.ADMIN)) {
      createNewLaboratory.setEnabled(manager.getValue());
      if (!manager.getValue()) {
        createNewLaboratory.setValue(false);
        laboratory.setEnabled(true);
        newLaboratoryName.setEnabled(false);
      }
    }
  }

  private void updateCreateNewLaboratory() {
    laboratory.setEnabled(!createNewLaboratory.getValue());
    newLaboratoryName.setEnabled(createNewLaboratory.getValue());
  }

  BinderValidationStatus<User> validateUser() {
    return binder.validate();
  }

  BinderValidationStatus<Passwords> validatePasswords() {
    return passwordBinder.validate();
  }

  BinderValidationStatus<Laboratory> validateLaboratory() {
    return laboratoryBinder.validate();
  }

  BinderValidationStatus<Address> validateAddress() {
    return addressBinder.validate();
  }

  BinderValidationStatus<PhoneNumber> validatePhoneNumber() {
    return phoneNumberBinder.validate();
  }

  boolean isValid() {
    boolean valid = validateUser().isOk();
    valid = validatePasswords().isOk() && valid;
    if (createNewLaboratory.getValue()) {
      valid = validateLaboratory().isOk() && valid;
    }
    valid = validateAddress().isOk() && valid;
    valid = validatePhoneNumber().isOk() && valid;
    return valid;
  }

  @Nullable
  String getPassword() {
    return passwordBinder.getBean().getPassword();
  }

  User getUser() {
    if (laboratory.getValue() != null && (!createNewLaboratory.isEnabled()
        || !createNewLaboratory.getValue())) {
      user.getLaboratory().setId(laboratory.getValue().getId());
      user.getLaboratory().setName(laboratory.getValue().getName());
    } else {
      user.getLaboratory().setId(0);
      user.getLaboratory().setName(newLaboratoryName.getValue());
    }
    return user;
  }

  void setUser(@Nullable User user) {
    if (user == null) {
      user = new User();
      user.setLaboratory(new Laboratory());
      user.setPhoneNumbers(new ArrayList<>());
    }
    if (!laboratoriesDataProvider.getItems().isEmpty()) {
      final Laboratory laboratory = user.getLaboratory();
      this.laboratory.setValue(laboratoriesDataProvider.getItems().stream()
          .filter(lab -> lab.getId() != 0 && lab.getId() == laboratory.getId()).findAny()
          .orElse(laboratoriesDataProvider.getItems().iterator().next()));
      user.setLaboratory(
          laboratoryService.get(this.laboratory.getValue().getId()).orElse(new Laboratory()));
    }
    if (user.getAddress() == null) {
      user.setAddress(defaultAddressConfiguration.getAddress());
    }
    if (user.getPhoneNumbers().isEmpty()) {
      user.setPhoneNumbers(new ArrayList<>());
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.setType(PhoneNumberType.WORK);
      user.getPhoneNumbers().add(phoneNumber);
    }
    this.user = user;
    binder.setBean(user);
    password.setRequiredIndicatorVisible(user.getId() == 0);
    confirmPassword.setRequiredIndicatorVisible(user.getId() == 0);
    addressBinder.setBean(user.getAddress());
    phoneNumberBinder.setBean(user.getPhoneNumbers().get(0));
    updateReadOnly();
  }
}
