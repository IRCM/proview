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

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QAddress.address;
import static ca.qc.ircm.proview.user.QPhoneNumber.phoneNumber;
import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * User form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFormPresenter implements BinderValidator {
  public static final String USER = user.getMetadata().getName();
  public static final String ID = user.id.getMetadata().getName();
  public static final String EMAIL = user.email.getMetadata().getName();
  public static final String NAME = user.name.getMetadata().getName();
  public static final String PASSWORD = "password";
  public static final String CONFIRM_PASSWORD = "confirmPassword";
  public static final String LABORATORY = laboratory.getMetadata().getName();
  public static final String NEW_LABORATORY = "newLaboratory";
  public static final String MANAGER = "manager";
  public static final String LABORATORY_ORGANIZATION =
      laboratory.organization.getMetadata().getName();
  public static final String LABORATORY_NAME = laboratory.name.getMetadata().getName();
  public static final String ADDRESS = address.getMetadata().getName();
  public static final String ADDRESS_LINE = address.line.getMetadata().getName();
  public static final String ADDRESS_TOWN = address.town.getMetadata().getName();
  public static final String ADDRESS_STATE = address.state.getMetadata().getName();
  public static final String ADDRESS_COUNTRY = address.country.getMetadata().getName();
  public static final String ADDRESS_POSTAL_CODE = address.postalCode.getMetadata().getName();
  public static final String CLEAR_ADDRESS = "clearAddress";
  public static final String PHONE_NUMBERS = user.phoneNumbers.getMetadata().getName();
  public static final String PHONE_NUMBER = phoneNumber.getMetadata().getName();
  public static final String PHONE_NUMBER_TYPE = phoneNumber.type.getMetadata().getName();
  public static final String PHONE_NUMBER_NUMBER = phoneNumber.number.getMetadata().getName();
  public static final String PHONE_NUMBER_EXTENSION = phoneNumber.extension.getMetadata().getName();
  public static final String REMOVE_PHONE_NUMBER = "removePhoneNumber";
  public static final String ADD_PHONE_NUMBER = "addPhoneNumber";
  public static final String REGISTER_WARNING = "registerWarning";
  public static final String SAVE = "save";
  private static final Logger logger = LoggerFactory.getLogger(UserFormPresenter.class);
  private UserForm view;
  private CheckBox editableProperty = new CheckBox(null, false);
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

  protected UserFormPresenter(UserService userService, AuthorizationService authorizationService,
      DefaultAddressConfiguration defaultAddressConfiguration) {
    this.userService = userService;
    this.authorizationService = authorizationService;
    this.defaultAddressConfiguration = defaultAddressConfiguration;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(UserForm view) {
    this.view = view;
    prepareComponents();
    addListeners();
    passwordsBinder.setBean(new Passwords());
    setBean(null);
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.userPanel.addStyleName(USER);
    view.userPanel.setCaption(resources.message(USER));
    view.emailField.addStyleName(EMAIL);
    view.emailField.setCaption(resources.message(EMAIL));
    userBinder.forField(view.emailField).asRequired(generalResources.message(REQUIRED))
        .withValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)))
        .withValidator(email -> {
          Long userId = userBinder.getBean().getId();
          return !userService.exists(email)
              || (userId != null && userService.get(userId).getEmail().equals(email));
        }, generalResources.message(ALREADY_EXISTS)).bind(User::getEmail, User::setEmail);
    view.nameField.addStyleName(NAME);
    view.nameField.setCaption(resources.message(NAME));
    userBinder.forField(view.nameField).asRequired(generalResources.message(REQUIRED))
        .bind(User::getName, User::setName);
    view.passwordField.addStyleName(PASSWORD);
    view.passwordField.setCaption(resources.message(PASSWORD));
    passwordsBinder.forField(view.passwordField)
        .withValidator(password -> !isNewUser() || !password.isEmpty(),
            generalResources.message(REQUIRED))
        .withValidator(password -> {
          String confirmPassword = view.confirmPasswordField.getValue();
          return password == null || password.isEmpty() || confirmPassword == null
              || confirmPassword.isEmpty() || password.equals(confirmPassword);
        }, resources.message(PASSWORD + ".notMatch"))
        .bind(Passwords::getPassword, Passwords::setPassword);
    view.confirmPasswordField.addStyleName(CONFIRM_PASSWORD);
    view.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD));
    passwordsBinder.forField(view.confirmPasswordField)
        .withValidator(password -> !isNewUser() || !password.isEmpty(),
            generalResources.message(REQUIRED))
        .bind(Passwords::getConfirmPassword, Passwords::setConfirmPassword);
    view.laboratoryPanel.addStyleName(LABORATORY);
    view.laboratoryPanel.setCaption(resources.message(LABORATORY));
    view.newLaboratoryField.addStyleName(NEW_LABORATORY);
    view.newLaboratoryField.setCaption(resources.message(NEW_LABORATORY));
    view.managerField.addStyleName(MANAGER);
    view.managerField.setCaption(resources.message(MANAGER));
    managerBinder.forField(view.managerField).asRequired(generalResources.message(REQUIRED))
        .withValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)))
        .withValidator(manager -> userService.isManager(manager),
            resources.message(MANAGER + ".notExists"))
        .bind(User::getEmail, User::setEmail);
    view.organizationField.addStyleName(LABORATORY_ORGANIZATION);
    view.organizationField
        .setCaption(resources.message(LABORATORY + "." + LABORATORY_ORGANIZATION));
    laboratoryBinder.forField(view.organizationField).asRequired(generalResources.message(REQUIRED))
        .bind(Laboratory::getOrganization, Laboratory::setOrganization);
    view.laboratoryNameField.addStyleName(LABORATORY + "-" + LABORATORY_NAME);
    view.laboratoryNameField.setCaption(resources.message(LABORATORY + "." + LABORATORY_NAME));
    laboratoryBinder.forField(view.laboratoryNameField)
        .asRequired(generalResources.message(REQUIRED))
        .bind(Laboratory::getName, Laboratory::setName);
    view.addressPanel.addStyleName(ADDRESS);
    view.addressPanel.setCaption(resources.message(ADDRESS));
    view.addressLineField.addStyleName(ADDRESS_LINE);
    view.addressLineField.setCaption(resources.message(ADDRESS + "." + ADDRESS_LINE));
    addressBinder.forField(view.addressLineField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getLine, Address::setLine);
    view.townField.addStyleName(ADDRESS_TOWN);
    view.townField.setCaption(resources.message(ADDRESS + "." + ADDRESS_TOWN));
    addressBinder.forField(view.townField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getTown, Address::setTown);
    view.stateField.addStyleName(ADDRESS_STATE);
    view.stateField.setCaption(resources.message(ADDRESS + "." + ADDRESS_STATE));
    addressBinder.forField(view.stateField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getState, Address::setState);
    view.countryField.addStyleName(ADDRESS_COUNTRY);
    view.countryField.setCaption(resources.message(ADDRESS + "." + ADDRESS_COUNTRY));
    addressBinder.forField(view.countryField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getCountry, Address::setCountry);
    view.postalCodeField.addStyleName(ADDRESS_POSTAL_CODE);
    view.postalCodeField.setCaption(resources.message(ADDRESS + "." + ADDRESS_POSTAL_CODE));
    addressBinder.forField(view.postalCodeField).asRequired(generalResources.message(REQUIRED))
        .bind(Address::getPostalCode, Address::setPostalCode);
    view.clearAddressButton.addStyleName(CLEAR_ADDRESS);
    view.clearAddressButton.setCaption(resources.message(CLEAR_ADDRESS));
    view.phoneNumbersPanel.addStyleName(PHONE_NUMBERS);
    view.phoneNumbersPanel.setCaption(resources.message(PHONE_NUMBERS));
    view.addPhoneNumberButton.addStyleName(ADD_PHONE_NUMBER);
    view.addPhoneNumberButton.setCaption(resources.message(ADD_PHONE_NUMBER));
    view.registerWarningLabel.addStyleName(REGISTER_WARNING);
    view.registerWarningLabel.setValue(resources.message(REGISTER_WARNING));
    view.registerWarningLabel.setIcon(VaadinIcons.WARNING);
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void addListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    view.confirmPasswordField.addValueChangeListener(e -> {
      passwordsBinder.validate();
    });
    view.newLaboratoryField.addValueChangeListener(e -> updateVisible());
    view.clearAddressButton.addClickListener(e -> clearAddress());
    view.addPhoneNumberButton.addClickListener(e -> addPhoneNumber());
    view.saveButton.addClickListener(e -> save());
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    final boolean newUser = isNewUser();
    final boolean admin = isAdmin();
    view.emailField.setReadOnly(!editable);
    view.nameField.setReadOnly(!editable);
    view.newLaboratoryField.setReadOnly(!editable || !newUser);
    view.managerField.setReadOnly(!editable || !newUser);
    view.organizationField.setReadOnly(!editable || !newUser || (newUser && admin));
    view.laboratoryNameField.setReadOnly(!editable || !newUser || (newUser && admin));
    view.addressLineField.setReadOnly(!editable);
    view.townField.setReadOnly(!editable);
    view.stateField.setReadOnly(!editable);
    view.countryField.setReadOnly(!editable);
    view.postalCodeField.setReadOnly(!editable);
    phoneNumberBinders.forEach(binder -> binder.setReadOnly(!editable));
    updateVisible();
  }

  private void updateVisible() {
    final boolean editable = editableProperty.getValue();
    final boolean newUser = isNewUser();
    final boolean newLaboratory = view.newLaboratoryField.getValue();
    final boolean admin = isAdmin();
    view.passwordField.setVisible(editable);
    view.confirmPasswordField.setVisible(editable);
    view.newLaboratoryField.setVisible(newUser && editable && !admin);
    view.managerField.setVisible(newUser && editable && !newLaboratory && !admin);
    view.organizationField.setVisible(!newUser || !editable || newLaboratory || (newUser && admin));
    view.laboratoryNameField
        .setVisible(!newUser || !editable || newLaboratory || (newUser && admin));
    view.clearAddressButton.setVisible(editable);
    removePhoneNumberButtons.forEach(button -> button.setVisible(editable));
    view.addPhoneNumberButton.setVisible(editable);
    view.saveLayout.setVisible(editable);
    view.registerWarningLabel.setVisible(newUser && editable && !admin);
    view.saveButton.setVisible(editable);
  }

  private void clearAddress() {
    boolean editable = editableProperty.getValue();
    if (editable) {
      view.addressLineField.setValue("");
      view.townField.setValue("");
      view.stateField.setValue("");
      view.countryField.setValue("");
      view.postalCodeField.setValue("");
    }
  }

  /**
   * Adds fields for a phone number.
   */
  public void addPhoneNumber() {
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
    view.phoneNumbersLayout.addComponent(layout);
    ComboBox<PhoneNumberType> typeField = new ComboBox<>();
    typeField.addStyleName(PHONE_NUMBER_TYPE);
    typeField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_TYPE));
    typeField.setEmptySelectionAllowed(false);
    typeField.setItems(PhoneNumberType.values());
    typeField.setItemCaptionGenerator(type -> type.getLabel(view.getLocale()));
    phoneNumberBinder.forField(typeField).asRequired(generalResources.message(REQUIRED))
        .bind(PhoneNumber::getType, PhoneNumber::setType);
    layout.addComponent(typeField);
    TextField numberField = new TextField();
    numberField.addStyleName(PHONE_NUMBER_NUMBER);
    numberField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER));
    phoneNumberBinder.forField(numberField).asRequired(generalResources.message(REQUIRED))
        .withValidator(new RegexpValidator(
            resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER + ".invalid"), "[\\d\\-]*"))
        .bind(PhoneNumber::getNumber, PhoneNumber::setNumber);
    layout.addComponent(numberField);
    TextField extensionField = new TextField();
    extensionField.addStyleName(PHONE_NUMBER_EXTENSION);
    extensionField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION));
    phoneNumberBinder.forField(extensionField)
        .withValidator(new RegexpValidator(
            resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION + ".invalid"),
            "[\\d\\-]*"))
        .bind(PhoneNumber::getExtension, PhoneNumber::setExtension);
    layout.addComponent(extensionField);
    Button removeButton = new Button();
    removeButton.addStyleName(REMOVE_PHONE_NUMBER);
    removeButton.setCaption(resources.message(REMOVE_PHONE_NUMBER));
    removeButton.addClickListener(e -> removePhoneNumber(phoneNumberBinder, layout, removeButton));
    removePhoneNumberButtons.add(removeButton);
    layout.addComponent(removeButton);
    updateEditable();
  }

  private void removePhoneNumber(Binder<PhoneNumber> phoneNumberBinder, Component layout,
      Button remove) {
    phoneNumberBinders.remove(phoneNumberBinder);
    view.phoneNumbersLayout.removeComponent(layout);
    removePhoneNumberButtons.remove(remove);
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    valid &= validate(userBinder);
    valid &= validate(passwordsBinder);
    if (isNewUser() && !isAdmin()) {
      if (view.newLaboratoryField.getValue()) {
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
      String password = view.passwordField.getValue();
      if (password.isEmpty()) {
        password = null;
      }
      if (isNewUser()) {
        User manager = null;
        if (!view.newLaboratoryField.getValue()) {
          manager = new User(null, view.managerField.getValue());
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

  private boolean isAdmin() {
    return authorizationService.hasAdminRole();
  }

  public User getBean() {
    return userBinder.getBean();
  }

  /**
   * Sets user.
   *
   * @param user
   *          user
   */
  public void setBean(User user) {
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
    }

    userBinder.setBean(user);
    laboratoryBinder.setBean(user.getLaboratory());
    addressBinder.setBean(user.getAddress());
    final boolean newUser = isNewUser();
    view.passwordField.setRequiredIndicatorVisible(newUser);
    view.confirmPasswordField.setRequiredIndicatorVisible(newUser);
    view.managerField.setRequiredIndicatorVisible(newUser);
    view.organizationField.setRequiredIndicatorVisible(newUser);
    view.laboratoryNameField.setRequiredIndicatorVisible(newUser);
    phoneNumberBinders.clear();
    removePhoneNumberButtons.clear();
    view.phoneNumbersLayout.removeAllComponents();
    if (user.getPhoneNumbers() != null) {
      user.getPhoneNumbers().forEach(phoneNumber -> addPhoneNumber(phoneNumber));
    }
    updateEditable();
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
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
