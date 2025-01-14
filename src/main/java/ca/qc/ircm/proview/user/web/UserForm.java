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
  private static final String MESSAGES_PREFIX = messagePrefix(UserForm.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String ADDRESS_PREFIX = messagePrefix(Address.class);
  private static final String PHONE_NUMBER_PREFIX = messagePrefix(PhoneNumber.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final long serialVersionUID = 3285639770914046262L;
  public static final String ID = "user-form";
  public static final String HEADER = "header";
  public static final String EMAIL_PLACEHOLDER = "john.smith@ircm.qc.ca";
  public static final String NAME_PLACEHOLDER = "John Smith";
  public static final String CREATE_NEW_LABORATORY = "createNewLaboratory";
  public static final String LABORATORY_NAME = LaboratoryProperties.NAME;
  public static final String NEW_LABORATORY_NAME = "newLaboratoryName";
  public static final String LABORATORY_NAME_PLACEHOLDER = "Translational Proteomics";
  public static final String NUMBER_PLACEHOLDER = "514-987-5500";
  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  protected TextField email = new TextField();
  protected TextField name = new TextField();
  protected Checkbox admin = new Checkbox();
  protected Checkbox manager = new Checkbox();
  protected PasswordsForm passwords = new PasswordsForm();
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
  private Binder<User> binder = new BeanValidationBinder<>(User.class);
  private ListDataProvider<Laboratory> laboratoriesDataProvider;
  private Binder<Laboratory> laboratoryBinder = new BeanValidationBinder<>(Laboratory.class);
  private Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
  private Binder<PhoneNumber> phoneNumberBinder = new BeanValidationBinder<>(PhoneNumber.class);
  private User user;
  private transient LaboratoryService laboratoryService;
  private transient AuthenticatedUser authenticatedUser;
  private transient DefaultAddressConfiguration defaultAddressConfiguration;

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
    setResponsiveSteps(new ResponsiveStep("15em", 4));
    add(new FormLayout(email, name, admin, manager, passwords),
        new FormLayout(laboratory, createNewLaboratory, newLaboratoryName),
        new FormLayout(addressLine, town, state, country, postalCode),
        new FormLayout(phoneType, number, extension));
    email.setId(id(EMAIL));
    email.setPlaceholder(EMAIL_PLACEHOLDER);
    name.setId(id(NAME));
    name.setPlaceholder(NAME_PLACEHOLDER);
    admin.setId(id(ADMIN));
    admin.setVisible(authenticatedUser.hasRole(UserRole.ADMIN));
    manager.setId(id(MANAGER));
    manager.setVisible(authenticatedUser.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER));
    manager.addValueChangeListener(e -> updateManager());
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
    createNewLaboratory.setId(id(CREATE_NEW_LABORATORY));
    createNewLaboratory.setVisible(authenticatedUser.hasRole(UserRole.ADMIN));
    createNewLaboratory.setEnabled(false);
    createNewLaboratory.addValueChangeListener(e -> updateCreateNewLaboratory());
    newLaboratoryName.setId(id(NEW_LABORATORY_NAME));
    newLaboratoryName.setPlaceholder(LABORATORY_NAME_PLACEHOLDER);
    newLaboratoryName.setVisible(authenticatedUser.hasRole(UserRole.ADMIN));
    newLaboratoryName.setEnabled(false);
    Address address = defaultAddressConfiguration.getAddress();
    addressLine.setId(id(LINE));
    addressLine.setPlaceholder(address.getLine());
    town.setId(id(TOWN));
    town.setPlaceholder(address.getTown());
    state.setId(id(STATE));
    state.setPlaceholder(address.getState());
    country.setId(id(COUNTRY));
    country.setPlaceholder(address.getCountry());
    postalCode.setId(id(POSTAL_CODE));
    postalCode.setPlaceholder(address.getPostalCode());
    phoneType.setId(id(TYPE));
    phoneType.setItems(PhoneNumberType.values());
    phoneType.setItemLabelGenerator(type -> getTranslation(PHONE_NUMBER_TYPE_PREFIX + type.name()));
    phoneType.setValue(PhoneNumberType.WORK);
    number.setId(id(NUMBER));
    number.setPlaceholder(NUMBER_PLACEHOLDER);
    extension.setId(id(EXTENSION));

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

  private Validator<Laboratory> laboratoryRequiredValidator(String errorMessage) {
    return (value, context) -> !createNewLaboratory.getValue() && value == null
        ? ValidationResult.error(errorMessage)
        : ValidationResult.ok();
  }

  private void updateReadOnly() {
    boolean readOnly =
        user.getId() != 0 && !authenticatedUser.hasPermission(user, Permission.WRITE);
    binder.setReadOnly(readOnly);
    laboratory.setReadOnly(!authenticatedUser.hasRole(UserRole.ADMIN));
    laboratory
        .setEnabled(!authenticatedUser.hasRole(UserRole.ADMIN) || !createNewLaboratory.getValue());
    passwords.setVisible(!readOnly);
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
    boolean valid = true;
    valid = validateUser().isOk() && valid;
    valid = passwords.isValid() && valid;
    if (createNewLaboratory.getValue()) {
      valid = validateLaboratory().isOk() && valid;
    }
    valid = validateAddress().isOk() && valid;
    valid = validatePhoneNumber().isOk() && valid;
    return valid;
  }

  @Nullable
  public String getPassword() {
    return passwords.getPassword();
  }

  User getUser() {
    if (laboratory.getValue() != null
        && (!createNewLaboratory.isEnabled() || !createNewLaboratory.getValue())) {
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
    passwords.setRequired(user.getId() == 0);
    addressBinder.setBean(user.getAddress());
    phoneNumberBinder.setBean(user.getPhoneNumbers().get(0));
    updateReadOnly();
  }
}
