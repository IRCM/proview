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

import static ca.qc.ircm.proview.user.AddressProperties.COUNTRY;
import static ca.qc.ircm.proview.user.AddressProperties.LINE;
import static ca.qc.ircm.proview.user.AddressProperties.POSTAL_CODE;
import static ca.qc.ircm.proview.user.AddressProperties.STATE;
import static ca.qc.ircm.proview.user.AddressProperties.TOWN;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.EXTENSION;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.NUMBER;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.TYPE;
import static ca.qc.ircm.proview.user.UserProperties.ADDRESS;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.UserProperties.PHONE_NUMBERS;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.styleName;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.PLACEHOLDER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * User form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFormPresenter implements BinderValidator {
  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String CONFIRM_PASSWORD = "confirmPassword";
  public static final String NEW_LABORATORY = "newLaboratory";
  public static final String MANAGER = "manager";
  public static final String LABORATORY_ORGANIZATION = LaboratoryProperties.ORGANIZATION;
  public static final String LABORATORY_NAME = LaboratoryProperties.NAME;
  public static final String ADDRESS_LINE = LINE;
  public static final String CLEAR_ADDRESS = "clearAddress";
  public static final String PHONE = "phone";
  public static final String PHONE_TYPE = TYPE;
  public static final String PHONE_NUMBER = NUMBER;
  public static final String PHONE_EXTENSION = EXTENSION;
  public static final String REMOVE_PHONE = "removePhone";
  public static final String ADD_PHONE = "addPhone";
  public static final String REGISTER_WARNING = "registerWarning";
  public static final String SAVE = "save";
  private static final Logger logger = LoggerFactory.getLogger(UserFormPresenter.class);
  private UserForm view;
  private UserFormDesign design;
  private boolean readOnly;
  private Binder<User> userBinder = new BeanValidationBinder<>(User.class);
  private Binder<Passwords> passwordsBinder = new BeanValidationBinder<>(Passwords.class);
  private Binder<User> managerBinder = new BeanValidationBinder<>(User.class);
  private Binder<Laboratory> laboratoryBinder = new BeanValidationBinder<>(Laboratory.class);
  private Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
  private List<Binder<PhoneNumber>> phoneNumberBinders = new ArrayList<>();
  private List<Button> removePhoneNumberButtons = new ArrayList<>();
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private DefaultAddressConfiguration defaultAddressConfiguration;

  protected UserFormPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(UserForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    addListeners();
    passwordsBinder.setBean(new Passwords());
    setValue(null);
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.userPanel.addStyleName(USER);
    design.userPanel.setCaption(resources.message(USER));
    design.emailField.addStyleName(EMAIL);
    design.emailField.setCaption(resources.message(EMAIL));
    design.emailField.setPlaceholder(resources.message(property(EMAIL, PLACEHOLDER)));
    userBinder.forField(design.emailField).asRequired(generalResources.message(REQUIRED))
        .withValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)))
        .withValidator(email -> {
          Long userId = userBinder.getBean().getId();
          return !userService.exists(email)
              || (userId != null && userService.get(userId).getEmail().equals(email));
        }, generalResources.message(ALREADY_EXISTS)).bind(User::getEmail, User::setEmail);
    design.nameField.addStyleName(NAME);
    design.nameField.setCaption(resources.message(NAME));
    design.nameField.setPlaceholder(resources.message(property(NAME, PLACEHOLDER)));
    userBinder.forField(design.nameField).asRequired(generalResources.message(REQUIRED))
        .bind(User::getName, User::setName);
    design.passwordField.addStyleName(PASSWORD);
    design.passwordField.setCaption(resources.message(PASSWORD));
    passwordsBinder.forField(design.passwordField)
        .withValidator(password -> !isNewUser() || !password.isEmpty(),
            generalResources.message(REQUIRED))
        .withValidator(password -> {
          String confirmPassword = design.confirmPasswordField.getValue();
          return password == null || password.isEmpty() || confirmPassword == null
              || confirmPassword.isEmpty() || password.equals(confirmPassword);
        }, resources.message(property(PASSWORD, "notMatch")))
        .bind(Passwords::getPassword, Passwords::setPassword);
    design.confirmPasswordField.addStyleName(CONFIRM_PASSWORD);
    design.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD));
    passwordsBinder.forField(design.confirmPasswordField)
        .withValidator(password -> !isNewUser() || !password.isEmpty(),
            generalResources.message(REQUIRED))
        .bind(Passwords::getConfirmPassword, Passwords::setConfirmPassword);
    design.laboratoryPanel.addStyleName(LABORATORY);
    design.laboratoryPanel.setCaption(resources.message(LABORATORY));
    design.newLaboratoryField.addStyleName(NEW_LABORATORY);
    design.newLaboratoryField.setCaption(resources.message(NEW_LABORATORY));
    design.managerField.addStyleName(MANAGER);
    design.managerField.setCaption(resources.message(MANAGER));
    design.managerField.setPlaceholder(resources.message(property(MANAGER, PLACEHOLDER)));
    managerBinder.forField(design.managerField).asRequired(generalResources.message(REQUIRED))
        .withValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)))
        .withValidator(manager -> userService.isManager(manager),
            resources.message(property(MANAGER, "notExists")))
        .bind(User::getEmail, User::setEmail);
    design.organizationField.addStyleName(LABORATORY_ORGANIZATION);
    design.organizationField
        .setCaption(resources.message(property(LABORATORY, LABORATORY_ORGANIZATION)));
    design.organizationField.setPlaceholder(
        resources.message(property(LABORATORY, LABORATORY_ORGANIZATION, PLACEHOLDER)));
    laboratoryBinder.forField(design.organizationField)
        .asRequired(generalResources.message(REQUIRED))
        .bind(Laboratory::getOrganization, Laboratory::setOrganization);
    design.laboratoryNameField.addStyleName(styleName(LABORATORY, LABORATORY_NAME));
    design.laboratoryNameField.setCaption(resources.message(property(LABORATORY, LABORATORY_NAME)));
    design.laboratoryNameField
        .setPlaceholder(resources.message(property(LABORATORY, LABORATORY_NAME, PLACEHOLDER)));
    laboratoryBinder.forField(design.laboratoryNameField)
        .asRequired(generalResources.message(REQUIRED))
        .bind(Laboratory::getName, Laboratory::setName);
    design.addressPanel.addStyleName(ADDRESS);
    design.addressPanel.setCaption(resources.message(ADDRESS));
    design.addressLineField.addStyleName(ADDRESS_LINE);
    design.addressLineField.setCaption(resources.message(property(ADDRESS, ADDRESS_LINE)));
    design.addressLineField
        .setPlaceholder(resources.message(property(ADDRESS, ADDRESS_LINE, PLACEHOLDER)));
    addressBinder.forField(design.addressLineField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getLine, Address::setLine);
    design.townField.addStyleName(TOWN);
    design.townField.setCaption(resources.message(property(ADDRESS, TOWN)));
    design.townField.setPlaceholder(resources.message(property(ADDRESS, TOWN, PLACEHOLDER)));
    addressBinder.forField(design.townField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getTown, Address::setTown);
    design.stateField.addStyleName(STATE);
    design.stateField.setCaption(resources.message(property(ADDRESS, STATE)));
    design.stateField.setPlaceholder(resources.message(property(ADDRESS, STATE, PLACEHOLDER)));
    addressBinder.forField(design.stateField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getState, Address::setState);
    design.countryField.addStyleName(COUNTRY);
    design.countryField.setCaption(resources.message(property(ADDRESS, COUNTRY)));
    design.countryField.setPlaceholder(resources.message(property(ADDRESS, COUNTRY, PLACEHOLDER)));
    addressBinder.forField(design.countryField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getCountry, Address::setCountry);
    design.postalCodeField.addStyleName(POSTAL_CODE);
    design.postalCodeField.setCaption(resources.message(property(ADDRESS, POSTAL_CODE)));
    design.postalCodeField
        .setPlaceholder(resources.message(property(ADDRESS, POSTAL_CODE, PLACEHOLDER)));
    addressBinder.forField(design.postalCodeField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getPostalCode, Address::setPostalCode);
    design.clearAddressButton.addStyleName(CLEAR_ADDRESS);
    design.clearAddressButton.setCaption(resources.message(CLEAR_ADDRESS));
    design.phoneNumbersPanel.addStyleName(PHONE_NUMBERS);
    design.phoneNumbersPanel.setCaption(resources.message(PHONE_NUMBERS));
    design.addPhoneNumberButton.addStyleName(ADD_PHONE);
    design.addPhoneNumberButton.setCaption(resources.message(ADD_PHONE));
    design.registerWarningLabel.addStyleName(REGISTER_WARNING);
    design.registerWarningLabel.setValue(resources.message(REGISTER_WARNING));
    design.saveButton.addStyleName(SAVE);
    design.saveButton.setCaption(resources.message(SAVE));
  }

  private void addListeners() {
    design.confirmPasswordField.addValueChangeListener(e -> {
      passwordsBinder.validate();
    });
    design.newLaboratoryField.addValueChangeListener(e -> updateVisible());
    design.clearAddressButton.addClickListener(e -> clearAddress());
    design.addPhoneNumberButton.addClickListener(e -> addPhoneNumber());
    design.saveButton.addClickListener(e -> save());
  }

  private void updateReadOnly() {
    final boolean newUser = isNewUser();
    final boolean manager = isManager();
    final boolean admin = isAdmin();
    design.emailField.setReadOnly(readOnly);
    design.nameField.setReadOnly(readOnly);
    design.newLaboratoryField.setReadOnly(readOnly || !newUser);
    design.managerField.setReadOnly(readOnly || !newUser);
    design.organizationField.setReadOnly(readOnly || (!newUser && !manager) || (newUser && admin));
    design.laboratoryNameField
        .setReadOnly(readOnly || (!newUser && !manager) || (newUser && admin));
    design.addressLineField.setReadOnly(readOnly);
    design.townField.setReadOnly(readOnly);
    design.stateField.setReadOnly(readOnly);
    design.countryField.setReadOnly(readOnly);
    design.postalCodeField.setReadOnly(readOnly);
    phoneNumberBinders.forEach(binder -> binder.setReadOnly(readOnly));
    updateVisible();
  }

  private void updateVisible() {
    final boolean newUser = isNewUser();
    final boolean newLaboratory = design.newLaboratoryField.getValue();
    final boolean admin = isAdmin();
    design.passwordField.setVisible(!readOnly);
    design.confirmPasswordField.setVisible(!readOnly);
    design.newLaboratoryField.setVisible(newUser && !readOnly && !admin);
    design.managerField.setVisible(newUser && !readOnly && !newLaboratory && !admin);
    design.organizationField
        .setVisible(!newUser || readOnly || newLaboratory || (newUser && admin));
    design.laboratoryNameField
        .setVisible(!newUser || readOnly || newLaboratory || (newUser && admin));
    design.clearAddressButton.setVisible(!readOnly);
    removePhoneNumberButtons.forEach(button -> button.setVisible(!readOnly));
    design.addPhoneNumberButton.setVisible(!readOnly);
    design.saveLayout.setVisible(!readOnly);
    design.registerWarningLabel.setVisible(newUser && !readOnly && !admin);
    design.saveButton.setVisible(!readOnly);
  }

  private void clearAddress() {
    if (!readOnly) {
      design.addressLineField.setValue("");
      design.townField.setValue("");
      design.stateField.setValue("");
      design.countryField.setValue("");
      design.postalCodeField.setValue("");
    }
  }

  private void addPhoneNumber() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    addPhoneNumber(phoneNumber);
  }

  private void addPhoneNumber(PhoneNumber phoneNumber) {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    Binder<PhoneNumber> phoneNumberBinder = new BeanValidationBinder<>(PhoneNumber.class);
    phoneNumberBinder.setBean(phoneNumber);
    phoneNumberBinders.add(phoneNumberBinder);
    FormLayout layout = new FormLayout();
    layout.setMargin(false);
    design.phoneNumbersLayout.addComponent(layout);
    ComboBox<PhoneNumberType> typeField = new ComboBox<>();
    typeField.addStyleName(PHONE_TYPE);
    typeField.setCaption(resources.message(property(PHONE, PHONE_TYPE)));
    typeField.setEmptySelectionAllowed(false);
    typeField.setItems(PhoneNumberType.values());
    typeField.setItemCaptionGenerator(type -> type.getLabel(view.getLocale()));
    phoneNumberBinder.forField(typeField).asRequired(generalResources.message(REQUIRED))
        .bind(PhoneNumber::getType, PhoneNumber::setType);
    layout.addComponent(typeField);
    TextField numberField = new TextField();
    numberField.addStyleName(PHONE_NUMBER);
    numberField.setCaption(resources.message(property(PHONE, PHONE_NUMBER)));
    numberField.setPlaceholder(resources.message(property(PHONE, PHONE_NUMBER, PLACEHOLDER)));
    phoneNumberBinder.forField(numberField).asRequired(generalResources.message(REQUIRED))
        .withValidator(new RegexpValidator(
            resources.message(property(PHONE, PHONE_NUMBER, "invalid")), "[\\d\\-]*"))
        .bind(PhoneNumber::getNumber, PhoneNumber::setNumber);
    layout.addComponent(numberField);
    TextField extensionField = new TextField();
    extensionField.addStyleName(PHONE_EXTENSION);
    extensionField.setCaption(resources.message(property(PHONE, PHONE_EXTENSION)));
    extensionField.setPlaceholder(resources.message(property(PHONE, PHONE_EXTENSION, PLACEHOLDER)));
    phoneNumberBinder.forField(extensionField)
        .withValidator(new RegexpValidator(
            resources.message(property(PHONE, PHONE_EXTENSION, "invalid")), "[\\d\\-]*"))
        .bind(PhoneNumber::getExtension, PhoneNumber::setExtension);
    layout.addComponent(extensionField);
    Button removeButton = new Button();
    removeButton.addStyleName(REMOVE_PHONE);
    removeButton.setCaption(resources.message(REMOVE_PHONE));
    removeButton.addClickListener(e -> removePhoneNumber(phoneNumberBinder, layout, removeButton));
    removePhoneNumberButtons.add(removeButton);
    layout.addComponent(removeButton);
    updateReadOnly();
  }

  private void removePhoneNumber(Binder<PhoneNumber> phoneNumberBinder, Component layout,
      Button remove) {
    phoneNumberBinders.remove(phoneNumberBinder);
    design.phoneNumbersLayout.removeComponent(layout);
    removePhoneNumberButtons.remove(remove);
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    valid &= validate(userBinder);
    valid &= validate(passwordsBinder);
    if (isNewUser() && !isAdmin()) {
      if (design.newLaboratoryField.getValue()) {
        valid &= validate(laboratoryBinder);
      } else {
        valid &= validate(managerBinder);
      }
    }
    valid &= validate(addressBinder);
    for (Binder<PhoneNumber> phoneNumberBinder : phoneNumberBinders) {
      valid &= validate(phoneNumberBinder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("User validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      User user = userBinder.getBean();
      logger.debug("Save user {}", user);
      user.setAddress(addressBinder.getBean());
      if (user.getPhoneNumbers() == null) {
        user.setPhoneNumbers(new ArrayList<>());
      }
      user.getPhoneNumbers().clear();
      for (Binder<PhoneNumber> phoneNumberBinder : phoneNumberBinders) {
        user.getPhoneNumbers().add(phoneNumberBinder.getBean());
      }
      String password = design.passwordField.getValue();
      if (password.isEmpty()) {
        password = null;
      }
      if (isNewUser()) {
        User manager = null;
        if (!design.newLaboratoryField.getValue()) {
          manager = new User(null, design.managerField.getValue());
        } else {
          user.setLaboratory(laboratoryBinder.getBean());
        }
        user.setLocale(view.getLocale());
        if (isAdmin()) {
          user.setAdmin(true);
        }
        userService.register(user, password, manager,
            locale -> view.getUrl(ValidateView.VIEW_NAME));
      } else {
        userService.update(user, password);
      }
      final MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message("save.done", user.getEmail()));
      view.fireSaveEvent(user);
    }
  }

  private boolean isNewUser() {
    return userBinder.getBean().getId() == null;
  }

  private boolean isManager() {
    return authorizationService.hasManagerRole();
  }

  private boolean isAdmin() {
    return authorizationService.hasAdminRole();
  }

  public User getValue() {
    return userBinder.getBean();
  }

  /**
   * Sets user.
   *
   * @param user
   *          user
   */
  public void setValue(User user) {
    if (user == null) {
      Address address = new Address();
      address.setLine(defaultAddressConfiguration.getAddress());
      address.setTown(defaultAddressConfiguration.getTown());
      address.setState(defaultAddressConfiguration.getState());
      address.setCountry(defaultAddressConfiguration.getCountry());
      address.setPostalCode(defaultAddressConfiguration.getPostalCode());
      user = new User();
      user.setAddress(address);
      user.setLaboratory(new Laboratory());
      if (isAdmin()) {
        User currentUser = authorizationService.getCurrentUser();
        user.getLaboratory().setName(currentUser.getLaboratory().getName());
        user.getLaboratory().setOrganization(currentUser.getLaboratory().getOrganization());
      }
      user.setPhoneNumbers(new ArrayList<>());
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.setType(PhoneNumberType.WORK);
      user.getPhoneNumbers().add(phoneNumber);
    }

    userBinder.setBean(user);
    laboratoryBinder.setBean(user.getLaboratory());
    addressBinder.setBean(user.getAddress());
    final boolean newUser = isNewUser();
    design.passwordField.setRequiredIndicatorVisible(newUser);
    design.confirmPasswordField.setRequiredIndicatorVisible(newUser);
    design.managerField.setRequiredIndicatorVisible(newUser);
    design.organizationField.setRequiredIndicatorVisible(newUser);
    design.laboratoryNameField.setRequiredIndicatorVisible(newUser);
    phoneNumberBinders.clear();
    removePhoneNumberButtons.clear();
    design.phoneNumbersLayout.removeAllComponents();
    if (user.getPhoneNumbers() != null) {
      user.getPhoneNumbers().forEach(phoneNumber -> addPhoneNumber(phoneNumber));
    }
    updateReadOnly();
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    updateReadOnly();
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
