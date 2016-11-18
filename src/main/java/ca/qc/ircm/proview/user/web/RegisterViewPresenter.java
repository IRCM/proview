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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
  @Value("${spring.application.name}")
  private String applicationName;

  public RegisterViewPresenter() {
  }

  protected RegisterViewPresenter(DefaultAddressConfiguration defaultAddressConfiguration,
      UserService userService, VaadinUtils vaadinUtils, UserFormPresenter userFormPresenter,
      AddressFormPresenter addressFormPresenter, PhoneNumberFormPresenter phoneNumberFormPresenter,
      String applicationName) {
    this.defaultAddressConfiguration = defaultAddressConfiguration;
    this.userService = userService;
    this.vaadinUtils = vaadinUtils;
    this.userFormPresenter = userFormPresenter;
    this.addressFormPresenter = addressFormPresenter;
    this.phoneNumberFormPresenter = phoneNumberFormPresenter;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(RegisterView view) {
    this.view = view;
    user.setAddress(new Address());
    user.setPhoneNumbers(new ArrayList<>());
    user.getPhoneNumbers().add(new PhoneNumber());
    addressItem = new BeanItem<>(user.getAddress());
    phoneNumberItem = new BeanItem<>(user.getPhoneNumbers().get(0));
    userFormPresenter.init(view.userForm);
    addressFormPresenter.init(view.addressForm);
    phoneNumberFormPresenter.init(view.phoneNumberForm);
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
    view.headerLabel.setId(HEADER_LABEL_ID);
    view.userPanel.setId(USER_FORM_ID);
    view.laboratoryPanel.setId(LABORATORY_ID);
    view.newLaboratoryField.setId(NEW_LABORATORY_ID);
    view.managerEmailField.setId(MANAGER_EMAIL_ID);
    view.laboratoryNameField.setId(LABORATORY_NAME_ID);
    view.organizationField.setId(ORGANIZATION_ID);
    view.addressPanel.setId(ADDRESS_FORM_ID);
    view.phoneNumberPanel.setId(PHONE_NUMBER_FORM_ID);
    view.clearAddressButton.setId(CLEAR_ADDRESS_BUTTON_ID);
    view.registerHeaderLabel.setId(REGISTER_HEADER_LABEL_ID);
    view.registerButton.setId(REGISTER_BUTTON_ID);
    view.requiredLabel.setId(REQUIRED_LABEL_ID);
  }

  private void bindFields() {
    userFormPresenter.setItemDataSource(userItem);
    passwordItem.addItemProperty(UserFormPresenter.PASSWORD_PROPERTY, passwordProperty);
    passwordItem.addItemProperty(UserFormPresenter.CONFIRM_PASSWORD_PROPERTY,
        confirmPasswordProperty);
    userFormPresenter.setPasswordItemDataSource(passwordItem);
    userFormPresenter.setEditable(true);
    laboratoryFieldGroup.setItemDataSource(laboratory);
    laboratoryFieldGroup.bind(view.laboratoryNameField, LABORATORY_NAME_PROPERTY);
    laboratoryFieldGroup.bind(view.organizationField, ORGANIZATION_PROPERTY);
    managerFieldGroup.setItemDataSource(manager);
    managerFieldGroup.bind(view.managerEmailField, MANAGER_EMAIL_PROPERTY);
    addressFormPresenter.setItemDataSource(addressItem);
    addressFormPresenter.setEditable(true);
    phoneNumberFormPresenter.setItemDataSource(phoneNumberItem);
    phoneNumberFormPresenter.setEditable(true);
  }

  private void addFieldListeners() {
    view.newLaboratoryField.addValueChangeListener(e -> {
      boolean value = (boolean) e.getProperty().getValue();
      view.laboratoryNameField.setVisible(value);
      view.organizationField.setVisible(value);
      view.managerEmailField.setVisible(!value);
      if (value) {
        view.organizationField.focus();
      } else {
        view.managerEmailField.focus();
      }
    });
    view.registerButton.addClickListener(e -> {
      registerUser();
    });
    view.clearAddressButton.addClickListener(e -> {
      view.addressForm.lineField.setValue("");
      view.addressForm.townField.setValue("");
      view.addressForm.stateField.setValue("");
      view.addressForm.countryField.setValue("");
      view.addressForm.postalCodeField.setValue("");
    });
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title", applicationName));
    view.headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    view.userPanel.setCaption(resources.message(USER_FORM_ID));
    view.laboratoryPanel.setCaption(resources.message(LABORATORY_ID));
    view.newLaboratoryField.setCaption(resources.message(NEW_LABORATORY_ID));
    view.organizationField.setCaption(resources.message(ORGANIZATION_ID));
    view.laboratoryNameField.setCaption(resources.message(LABORATORY_NAME_ID));
    view.managerEmailField.setCaption(resources.message(MANAGER_EMAIL_ID));
    view.addressPanel.setCaption(resources.message(ADDRESS_FORM_ID));
    view.clearAddressButton.setCaption(resources.message(CLEAR_ADDRESS_BUTTON_ID));
    view.phoneNumberPanel.setCaption(resources.message(PHONE_NUMBER_FORM_ID));
    view.registerHeaderLabel.setValue(resources.message(REGISTER_HEADER_LABEL_ID));
    view.registerButton.setCaption(resources.message(REGISTER_BUTTON_ID));
    view.requiredLabel.setValue(resources.message("required"));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.organizationField.setRequired(true);
    view.organizationField.setRequiredError(
        generalResources.message("required", view.organizationField.getCaption()));
    view.laboratoryNameField.setRequired(true);
    view.laboratoryNameField.setRequiredError(
        generalResources.message("required", view.laboratoryNameField.getCaption()));
    view.managerEmailField.setRequired(true);
    view.managerEmailField.setRequiredError(
        generalResources.message("required", view.managerEmailField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    view.managerEmailField
        .addValidator(new EmailValidator(resources.message(MANAGER_EMAIL_ID + ".invalid")));
    view.managerEmailField.addValidator(value -> {
      if (!userService.isManager(view.managerEmailField.getValue())) {
        throw new InvalidValueException(resources.message(MANAGER_EMAIL_ID + ".notExists"));
      }
    });
  }

  private void setDefaults() {
    view.addressForm.lineField.setValue(defaultAddressConfiguration.getAddress());
    view.addressForm.townField.setValue(defaultAddressConfiguration.getTown());
    view.addressForm.stateField.setValue(defaultAddressConfiguration.getState());
    view.addressForm.postalCodeField.setValue(defaultAddressConfiguration.getPostalCode());
    view.addressForm.countryField.setValue(defaultAddressConfiguration.getCountry());
    view.phoneNumberForm.typeField.setValue(PhoneNumberType.WORK);
    view.laboratoryNameField.setVisible(false);
    view.organizationField.setVisible(false);
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      userFormPresenter.commit();
      if (view.newLaboratoryField.getValue()) {
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
      if (view.newLaboratoryField.getValue()) {
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
