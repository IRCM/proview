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

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
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
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Registers user presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegisterViewPresenter {
  public static final String HEADER_LABEL_ID = "header";
  public static final String USER_FORM_ID = QUser.user.getMetadata().getName();
  public static final String NEW_LABORATORY_ID = "newLaboratory";
  public static final String MANAGER_ID = "manager";
  public static final String MANAGER_EMAIL_ID =
      MANAGER_ID + "." + QUser.user.email.getMetadata().getName();
  public static final String MANAGER_EMAIL_PROPERTY = QUser.user.email.getMetadata().getName();
  public static final String LABORATORY_ID = QLaboratory.laboratory.getMetadata().getName();
  public static final String LABORATORY_NAME_ID =
      LABORATORY_ID + "." + QLaboratory.laboratory.name.getMetadata().getName();
  public static final String LABORATORY_NAME_PROPERTY =
      QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION_ID =
      LABORATORY_ID + "." + QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String ORGANIZATION_PROPERTY =
      QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String ADDRESS_FORM_ID = QAddress.address.getMetadata().getName();
  public static final String CLEAR_ADDRESS_BUTTON_ID = "clearAddress";
  public static final String PHONE_NUMBER_FORM_ID =
      QPhoneNumber.phoneNumber.getMetadata().getName();
  public static final String REGISTER_HEADER_LABEL_ID = "registerHeader";
  public static final String REGISTER_BUTTON_ID = "register";
  public static final String REQUIRED_LABEL_ID = "required";
  private static final Logger logger = LoggerFactory.getLogger(RegisterViewPresenter.class);
  private RegisterView view;
  private User user = new User();
  private PropertysetItem passwordItem = new PropertysetItem();
  private ObjectProperty<String> passwordProperty = new ObjectProperty<>(null, String.class);
  private ObjectProperty<String> confirmPasswordProperty = new ObjectProperty<>(null, String.class);
  private Laboratory laboratory = new Laboratory();
  private User manager = new User();
  private BeanItem<User> userItem = new BeanItem<>(user);
  private BeanItem<Address> addressItem;
  private BeanItem<PhoneNumber> phoneNumberItem;
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private BeanFieldGroup<User> managerFieldGroup = new BeanFieldGroup<>(User.class);
  private Label headerLabel;
  private Panel userPanel;
  private UserForm userForm;
  private Panel laboratoryPanel;
  private CheckBox newLaboratoryField;
  private TextField organizationField;
  private TextField laboratoryNameField;
  private TextField managerEmailField;
  private Panel addressPanel;
  private AddressForm addressForm;
  private Button clearAddressButton;
  private Panel phoneNumberPanel;
  private PhoneNumberForm phoneNumberForm;
  private Label registerHeaderLabel;
  private Button registerButton;
  private Label requiredLabel;
  @Inject
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Inject
  private UserService userService;
  @Inject
  private VaadinUtils vaadinUtils;
  @Inject
  private UserFormPresenter userFormPresenter;
  @Inject
  private AddressFormPresenter addressFormPresenter;
  @Inject
  private PhoneNumberFormPresenter phoneNumberFormPresenter;

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(RegisterView view) {
    this.view = view;
    setFields();
    user.setAddress(new Address());
    user.setPhoneNumbers(new ArrayList<>());
    user.getPhoneNumbers().add(new PhoneNumber());
    addressItem = new BeanItem<>(user.getAddress());
    phoneNumberItem = new BeanItem<>(user.getPhoneNumbers().get(0));
    userFormPresenter.init(userForm);
    addressFormPresenter.init(addressForm);
    phoneNumberFormPresenter.init(phoneNumberForm);
  }

  private void setFields() {
    headerLabel = view.getHeaderLabel();
    userPanel = view.getUserPanel();
    userForm = view.getUserForm();
    laboratoryPanel = view.getLaboratoryPanel();
    newLaboratoryField = view.getNewLaboratoryField();
    organizationField = view.getOrganizationField();
    laboratoryNameField = view.getLaboratoryNameField();
    managerEmailField = view.getManagerEmailField();
    addressPanel = view.getAddressPanel();
    addressForm = view.getAddressForm();
    clearAddressButton = view.getClearAddressButton();
    phoneNumberPanel = view.getPhoneNumberPanel();
    phoneNumberForm = view.getPhoneNumberForm();
    registerHeaderLabel = view.getRegisterHeaderLabel();
    registerButton = view.getRegisterButton();
    requiredLabel = view.getRequiredLabel();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    logger.debug("Register user view");
    setIds();
    bindFields();
    addFieldListeners();
    setCaptions();
    setRequired();
    addValidators();
    setDefaults();
  }

  private void setIds() {
    headerLabel.setId(HEADER_LABEL_ID);
    userPanel.setId(USER_FORM_ID);
    laboratoryPanel.setId(LABORATORY_ID);
    newLaboratoryField.setId(NEW_LABORATORY_ID);
    managerEmailField.setId(MANAGER_EMAIL_ID);
    laboratoryNameField.setId(LABORATORY_NAME_ID);
    organizationField.setId(ORGANIZATION_ID);
    addressPanel.setId(ADDRESS_FORM_ID);
    phoneNumberPanel.setId(PHONE_NUMBER_FORM_ID);
    clearAddressButton.setId(CLEAR_ADDRESS_BUTTON_ID);
    registerHeaderLabel.setId(REGISTER_HEADER_LABEL_ID);
    registerButton.setId(REGISTER_BUTTON_ID);
    requiredLabel.setId(REQUIRED_LABEL_ID);
  }

  private void bindFields() {
    userFormPresenter.setItemDataSource(userItem);
    passwordItem.addItemProperty(UserFormPresenter.PASSWORD_PROPERTY, passwordProperty);
    passwordItem.addItemProperty(UserFormPresenter.CONFIRM_PASSWORD_PROPERTY,
        confirmPasswordProperty);
    userFormPresenter.setPasswordItemDataSource(passwordItem);
    userFormPresenter.setEditable(true);
    laboratoryFieldGroup.setItemDataSource(laboratory);
    laboratoryFieldGroup.bind(laboratoryNameField, LABORATORY_NAME_PROPERTY);
    laboratoryFieldGroup.bind(organizationField, ORGANIZATION_PROPERTY);
    managerFieldGroup.setItemDataSource(manager);
    managerFieldGroup.bind(managerEmailField, MANAGER_EMAIL_PROPERTY);
    addressFormPresenter.setItemDataSource(addressItem);
    addressFormPresenter.setEditable(true);
    phoneNumberFormPresenter.setItemDataSource(phoneNumberItem);
    phoneNumberFormPresenter.setEditable(true);
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
      addressForm.lineField.setValue("");
      addressForm.townField.setValue("");
      addressForm.stateField.setValue("");
      addressForm.countryField.setValue("");
      addressForm.postalCodeField.setValue("");
    });
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    userPanel.setCaption(resources.message(USER_FORM_ID));
    laboratoryPanel.setCaption(resources.message(LABORATORY_ID));
    newLaboratoryField.setCaption(resources.message(NEW_LABORATORY_ID));
    organizationField.setCaption(resources.message(ORGANIZATION_ID));
    laboratoryNameField.setCaption(resources.message(LABORATORY_NAME_ID));
    managerEmailField.setCaption(resources.message(MANAGER_EMAIL_ID));
    addressPanel.setCaption(resources.message(ADDRESS_FORM_ID));
    clearAddressButton.setCaption(resources.message(CLEAR_ADDRESS_BUTTON_ID));
    phoneNumberPanel.setCaption(resources.message(PHONE_NUMBER_FORM_ID));
    registerHeaderLabel.setValue(resources.message(REGISTER_HEADER_LABEL_ID));
    registerButton.setCaption(resources.message(REGISTER_BUTTON_ID));
    requiredLabel.setValue(resources.message("required"));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    organizationField.setRequired(true);
    organizationField
        .setRequiredError(generalResources.message("required", organizationField.getCaption()));
    laboratoryNameField.setRequired(true);
    laboratoryNameField
        .setRequiredError(generalResources.message("required", laboratoryNameField.getCaption()));
    managerEmailField.setRequired(true);
    managerEmailField
        .setRequiredError(generalResources.message("required", managerEmailField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    managerEmailField
        .addValidator(new EmailValidator(resources.message(MANAGER_EMAIL_ID + ".invalid")));
    managerEmailField.addValidator(value -> {
      if (!userService.isManager(managerEmailField.getValue())) {
        throw new InvalidValueException(resources.message(MANAGER_EMAIL_ID + ".notExists"));
      }
    });
  }

  private void setDefaults() {
    view.getAddressForm().lineField.setValue(defaultAddressConfiguration.getAddress());
    view.getAddressForm().townField.setValue(defaultAddressConfiguration.getTown());
    view.getAddressForm().stateField.setValue(defaultAddressConfiguration.getState());
    view.getAddressForm().postalCodeField.setValue(defaultAddressConfiguration.getPostalCode());
    view.getAddressForm().countryField.setValue(defaultAddressConfiguration.getCountry());
    view.getPhoneNumberForm().typeField.setValue(PhoneNumberType.WORK);
    laboratoryNameField.setVisible(false);
    organizationField.setVisible(false);
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      userFormPresenter.commit();
      if (newLaboratoryField.getValue()) {
        laboratoryFieldGroup.commit();
      } else {
        managerFieldGroup.commit();
      }
      addressFormPresenter.commit();
      phoneNumberFormPresenter.commit();
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
      User manager = this.manager;
      if (newLaboratoryField.getValue()) {
        user.setLaboratory(laboratory);
        manager = null;
      }
      user.setLocale(view.getLocale());
      userService.register(user, passwordProperty.getValue(), manager,
          locale -> vaadinUtils.getUrl(ValidateView.VIEW_NAME));
      MessageResource resources = view.getResources();
      view.afterSuccessfulRegister(resources.message("done", user.getEmail()));
    }
  }
}
