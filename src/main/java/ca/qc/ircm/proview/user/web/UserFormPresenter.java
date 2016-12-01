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
import static ca.qc.ircm.proview.web.WebConstants.GENERAL_MESSAGES;
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
import ca.qc.ircm.proview.web.MainUi;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
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
public class UserFormPresenter {
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
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private PropertysetItem passwordItem = new PropertysetItem();
  private FieldGroup passwordFieldGroup = new FieldGroup();
  private List<BeanFieldGroup<PhoneNumber>> phoneNumberFieldGroups = new ArrayList<>();
  private List<Button> removePhoneNumberButtons = new ArrayList<>();
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Inject
  private MainUi ui;

  protected UserFormPresenter() {
  }

  protected UserFormPresenter(UserService userService, AuthorizationService authorizationService,
      DefaultAddressConfiguration defaultAddressConfiguration, MainUi ui) {
    this.userService = userService;
    this.authorizationService = authorizationService;
    this.defaultAddressConfiguration = defaultAddressConfiguration;
    this.ui = ui;
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
    bindFields();
    addListeners();
    setItemDataSource(null);
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    view.userPanel.addStyleName(USER);
    view.userPanel.setCaption(resources.message(USER));
    view.emailField.addStyleName(EMAIL);
    view.emailField.setCaption(resources.message(EMAIL));
    view.emailField.setNullRepresentation("");
    view.emailField.setRequired(true);
    view.emailField.setRequiredError(generalResources.message(REQUIRED));
    view.emailField.addValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)));
    view.emailField.addValidator((value) -> {
      String email = view.emailField.getValue();
      Long userId = userFieldGroup.getItemDataSource().getBean().getId();
      if (userService.exists(email)
          && (userId == null || !userService.get(userId).getEmail().equals(email))) {
        throw new InvalidValueException(generalResources.message(ALREADY_EXISTS));
      }
    });
    view.nameField.addStyleName(NAME);
    view.nameField.setCaption(resources.message(NAME));
    view.nameField.setNullRepresentation("");
    view.nameField.setRequired(true);
    view.nameField.setRequiredError(generalResources.message(REQUIRED));
    passwordItem.addItemProperty(PASSWORD, new ObjectProperty<>(null, String.class));
    passwordItem.addItemProperty(CONFIRM_PASSWORD, new ObjectProperty<>(null, String.class));
    passwordFieldGroup.setItemDataSource(passwordItem);
    view.passwordField.addStyleName(PASSWORD);
    view.passwordField.setCaption(resources.message(PASSWORD));
    view.passwordField.setNullRepresentation("");
    view.passwordField.setRequiredError(generalResources.message(REQUIRED));
    view.passwordField.addValidator((value) -> {
      String password = view.passwordField.getValue();
      String confirmPassword = view.confirmPasswordField.getValue();
      if (password != null && !password.isEmpty() && confirmPassword != null
          && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
        throw new InvalidValueException(resources.message(PASSWORD + ".notMatch"));
      }
    });
    view.confirmPasswordField.addStyleName(CONFIRM_PASSWORD);
    view.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD));
    view.confirmPasswordField.setNullRepresentation("");
    view.confirmPasswordField.setRequiredError(generalResources.message(REQUIRED));
    view.laboratoryPanel.addStyleName(LABORATORY);
    view.laboratoryPanel.setCaption(resources.message(LABORATORY));
    view.newLaboratoryField.addStyleName(NEW_LABORATORY);
    view.newLaboratoryField.setCaption(resources.message(NEW_LABORATORY));
    view.managerField.addStyleName(MANAGER);
    view.managerField.setCaption(resources.message(MANAGER));
    view.managerField.setNullRepresentation("");
    view.managerField.setRequiredError(generalResources.message(REQUIRED));
    view.managerField.addValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)));
    view.managerField.addValidator((value) -> {
      String manager = view.managerField.getValue();
      if (!userService.isManager(manager)) {
        throw new InvalidValueException(resources.message(MANAGER + ".notExists"));
      }
    });
    view.organizationField.addStyleName(LABORATORY_ORGANIZATION);
    view.organizationField
        .setCaption(resources.message(LABORATORY + "." + LABORATORY_ORGANIZATION));
    view.organizationField.setNullRepresentation("");
    view.organizationField.setRequiredError(generalResources.message(REQUIRED));
    view.laboratoryNameField.addStyleName(LABORATORY + "-" + LABORATORY_NAME);
    view.laboratoryNameField.setCaption(resources.message(LABORATORY + "." + LABORATORY_NAME));
    view.laboratoryNameField.setNullRepresentation("");
    view.laboratoryNameField.setRequiredError(generalResources.message(REQUIRED));
    view.addressPanel.addStyleName(ADDRESS);
    view.addressPanel.setCaption(resources.message(ADDRESS));
    view.addressLineField.addStyleName(ADDRESS_LINE);
    view.addressLineField.setCaption(resources.message(ADDRESS + "." + ADDRESS_LINE));
    view.addressLineField.setNullRepresentation("");
    view.addressLineField.setRequired(true);
    view.addressLineField.setRequiredError(generalResources.message(REQUIRED));
    view.townField.addStyleName(ADDRESS_TOWN);
    view.townField.setCaption(resources.message(ADDRESS + "." + ADDRESS_TOWN));
    view.townField.setNullRepresentation("");
    view.townField.setRequired(true);
    view.townField.setRequiredError(generalResources.message(REQUIRED));
    view.stateField.addStyleName(ADDRESS_STATE);
    view.stateField.setCaption(resources.message(ADDRESS + "." + ADDRESS_STATE));
    view.stateField.setNullRepresentation("");
    view.stateField.setRequired(true);
    view.stateField.setRequiredError(generalResources.message(REQUIRED));
    view.countryField.addStyleName(ADDRESS_COUNTRY);
    view.countryField.setCaption(resources.message(ADDRESS + "." + ADDRESS_COUNTRY));
    view.countryField.setNullRepresentation("");
    view.countryField.setRequired(true);
    view.countryField.setRequiredError(generalResources.message(REQUIRED));
    view.postalCodeField.addStyleName(ADDRESS_POSTAL_CODE);
    view.postalCodeField.setCaption(resources.message(ADDRESS + "." + ADDRESS_POSTAL_CODE));
    view.postalCodeField.setNullRepresentation("");
    view.postalCodeField.setRequired(true);
    view.postalCodeField.setRequiredError(generalResources.message(REQUIRED));
    view.clearAddressButton.addStyleName(CLEAR_ADDRESS);
    view.clearAddressButton.setCaption(resources.message(CLEAR_ADDRESS));
    view.phoneNumbersPanel.addStyleName(PHONE_NUMBERS);
    view.phoneNumbersPanel.setCaption(resources.message(PHONE_NUMBERS));
    view.addPhoneNumberButton.addStyleName(ADD_PHONE_NUMBER);
    view.addPhoneNumberButton.setCaption(resources.message(ADD_PHONE_NUMBER));
    view.registerWarningLabel.addStyleName(REGISTER_WARNING);
    view.registerWarningLabel.setValue(resources.message(REGISTER_WARNING));
    view.registerWarningLabel.setIcon(FontAwesome.WARNING);
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void bindFields() {
    userFieldGroup.bind(view.emailField, EMAIL);
    userFieldGroup.bind(view.nameField, NAME);
    passwordFieldGroup.bind(view.passwordField, PASSWORD);
    passwordFieldGroup.bind(view.confirmPasswordField, CONFIRM_PASSWORD);
    laboratoryFieldGroup.bind(view.organizationField, LABORATORY_ORGANIZATION);
    laboratoryFieldGroup.bind(view.laboratoryNameField, LABORATORY_NAME);
    userFieldGroup.bind(view.addressLineField, ADDRESS + "." + ADDRESS_LINE);
    userFieldGroup.bind(view.townField, ADDRESS + "." + ADDRESS_TOWN);
    userFieldGroup.bind(view.stateField, ADDRESS + "." + ADDRESS_STATE);
    userFieldGroup.bind(view.countryField, ADDRESS + "." + ADDRESS_COUNTRY);
    userFieldGroup.bind(view.postalCodeField, ADDRESS + "." + ADDRESS_POSTAL_CODE);
  }

  private void addListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    view.confirmPasswordField.addValueChangeListener(e -> {
      view.passwordField.isValid();
      view.passwordField.markAsDirty();
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
    phoneNumberFieldGroups.forEach(fieldGroup -> {
      fieldGroup.getField(PHONE_NUMBER_TYPE).setReadOnly(!editable);
      fieldGroup.getField(PHONE_NUMBER_NUMBER).setReadOnly(!editable);
      fieldGroup.getField(PHONE_NUMBER_EXTENSION).setReadOnly(!editable);
    });
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
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup = new BeanFieldGroup<>(PhoneNumber.class);
    phoneNumberFieldGroup.setItemDataSource(new BeanItem<>(phoneNumber));
    phoneNumberFieldGroups.add(phoneNumberFieldGroup);
    FormLayout layout = new FormLayout();
    layout.setMargin(false);
    view.phoneNumbersLayout.addComponent(layout);
    ComboBox typeField = new ComboBox();
    typeField.addStyleName(PHONE_NUMBER_TYPE);
    typeField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_TYPE));
    typeField.setNullSelectionAllowed(false);
    typeField.setNewItemsAllowed(false);
    for (PhoneNumberType type : PhoneNumberType.values()) {
      typeField.addItem(type);
      typeField.setItemCaption(type, type.getLabel(view.getLocale()));
    }
    typeField.setRequired(true);
    typeField.setRequiredError(generalResources.message(REQUIRED));
    layout.addComponent(typeField);
    TextField numberField = new TextField();
    numberField.setNullRepresentation("");
    numberField.addStyleName(PHONE_NUMBER_NUMBER);
    numberField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER));
    numberField.setNullRepresentation("");
    numberField.setRequired(true);
    numberField.setRequiredError(generalResources.message(REQUIRED));
    numberField.addValidator(new RegexpValidator("[\\d\\-]*",
        resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER + ".invalid")));
    layout.addComponent(numberField);
    TextField extensionField = new TextField();
    extensionField.setNullRepresentation("");
    extensionField.addStyleName(PHONE_NUMBER_EXTENSION);
    extensionField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION));
    extensionField.setNullRepresentation("");
    extensionField.addValidator(new RegexpValidator("[\\d\\-]*",
        resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION + ".invalid")));
    layout.addComponent(extensionField);
    Button removeButton = new Button();
    removeButton.addStyleName(REMOVE_PHONE_NUMBER);
    removeButton.setCaption(resources.message(REMOVE_PHONE_NUMBER));
    removeButton
        .addClickListener(e -> removePhoneNumber(phoneNumberFieldGroup, layout, removeButton));
    removePhoneNumberButtons.add(removeButton);
    layout.addComponent(removeButton);
    phoneNumberFieldGroup.bind(typeField, PHONE_NUMBER_TYPE);
    phoneNumberFieldGroup.bind(numberField, PHONE_NUMBER_NUMBER);
    phoneNumberFieldGroup.bind(extensionField, PHONE_NUMBER_EXTENSION);
    updateEditable();
  }

  private void removePhoneNumber(BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup,
      Component layout, Button remove) {
    phoneNumberFieldGroups.remove(phoneNumberFieldGroup);
    view.phoneNumbersLayout.removeComponent(layout);
    removePhoneNumberButtons.remove(remove);
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      userFieldGroup.commit();
      passwordFieldGroup.commit();
      if (isNewUser() && !isAdmin()) {
        if (view.newLaboratoryField.getValue()) {
          laboratoryFieldGroup.commit();
        } else {
          view.managerField.validate();
        }
      }
      for (BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup : phoneNumberFieldGroups) {
        phoneNumberFieldGroup.commit();
      }
    } catch (InvalidValueException | CommitException e) {
      final MessageResource generalResources =
          new MessageResource(GENERAL_MESSAGES, view.getLocale());
      logger.trace("Validation {} failed with message {}",
          e instanceof CommitException ? "commit" : "value", e.getMessage(), e);
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      valid = false;
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      User user = userFieldGroup.getItemDataSource().getBean();
      if (user.getPhoneNumbers() == null) {
        user.setPhoneNumbers(new ArrayList<>());
      }
      user.getPhoneNumbers().clear();
      for (BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup : phoneNumberFieldGroups) {
        user.getPhoneNumbers().add(phoneNumberFieldGroup.getItemDataSource().getBean());
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
          user.setLaboratory(laboratoryFieldGroup.getItemDataSource().getBean());
        }
        user.setLocale(view.getLocale());
        if (isAdmin()) {
          user.setAdmin(true);
        }
        userService.register(user, password, manager, locale -> ui.getUrl(ValidateView.VIEW_NAME));
      } else {
        userService.update(user, password);
      }
      final MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message("save.done", user.getEmail()));
      view.fireSaveEvent(user);
    }
  }

  private boolean isNewUser() {
    return userFieldGroup.getItemDataSource().getBean().getId() == null;
  }

  private boolean isAdmin() {
    return authorizationService.hasAdminRole();
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
    if (item == null) {
      Address address = new Address();
      address.setLine(defaultAddressConfiguration.getAddress());
      address.setTown(defaultAddressConfiguration.getTown());
      address.setState(defaultAddressConfiguration.getState());
      address.setCountry(defaultAddressConfiguration.getCountry());
      address.setPostalCode(defaultAddressConfiguration.getPostalCode());
      User user = new User();
      user.setAddress(address);
      user.setLaboratory(new Laboratory());
      if (isAdmin()) {
        User currentUser = authorizationService.getCurrentUser();
        user.getLaboratory().setName(currentUser.getLaboratory().getName());
        user.getLaboratory().setOrganization(currentUser.getLaboratory().getOrganization());
      }
      user.setPhoneNumbers(new ArrayList<>());
      item = new BeanItem<>(user);
    } else if (!(item instanceof BeanItem)) {
      throw new IllegalArgumentException("item must be an instance of BeanItem");
    }

    userFieldGroup.setItemDataSource(item);
    final User user = userFieldGroup.getItemDataSource().getBean();
    laboratoryFieldGroup.setItemDataSource(new BeanItem<>(user.getLaboratory()));
    final boolean newUser = isNewUser();
    view.passwordField.setRequired(newUser);
    view.confirmPasswordField.setRequired(newUser);
    view.managerField.setRequired(newUser);
    view.organizationField.setRequired(newUser);
    view.laboratoryNameField.setRequired(newUser);
    phoneNumberFieldGroups.clear();
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

  public void addSaveListener(SaveListener listener) {
    view.addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  public void removeSaveListener(SaveListener listener) {
    view.removeListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }
}
