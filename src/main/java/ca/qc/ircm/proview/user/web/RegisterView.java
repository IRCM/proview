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
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Registers user.
 */
@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterView extends RegisterDesign implements MessageResourcesView {
  private static final long serialVersionUID = 7586918222688019429L;
  public static final String VIEW_NAME = "user/register";
  private static final Logger logger = LoggerFactory.getLogger(RegisterView.class);
  private static final String emailProperty = QUser.user.email.getMetadata().getName();
  private static final String nameProperty = QUser.user.name.getMetadata().getName();
  private static final String laboratoryNameProperty =
      QLaboratory.laboratory.name.getMetadata().getName();
  private static final String organizationProperty =
      QLaboratory.laboratory.organization.getMetadata().getName();
  private static final String addressProperty = QAddress.address1.address.getMetadata().getName();
  private static final String addressSecondProperty =
      QAddress.address1.addressSecond.getMetadata().getName();
  private static final String townProperty = QAddress.address1.town.getMetadata().getName();
  private static final String stateProperty = QAddress.address1.state.getMetadata().getName();
  private static final String countryProperty = QAddress.address1.country.getMetadata().getName();
  private static final String postalCodeProperty =
      QAddress.address1.postalCode.getMetadata().getName();
  private static final String phoneNumberProperty =
      QPhoneNumber.phoneNumber.number.getMetadata().getName();
  private static final String phoneExtensionProperty =
      QPhoneNumber.phoneNumber.extension.getMetadata().getName();
  private User user = new User();
  private Laboratory laboratory = new Laboratory();
  private User manager = new User();
  private Address userAddress = new Address();
  private PhoneNumber userPhoneNumber = new PhoneNumber();
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private BeanFieldGroup<User> managerFieldGroup = new BeanFieldGroup<>(User.class);
  private BeanFieldGroup<Address> addressFieldGroup = new BeanFieldGroup<>(Address.class);
  private BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup =
      new BeanFieldGroup<>(PhoneNumber.class);
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  @Inject
  private UserService userService;
  @Inject
  private VaadinUtils vaadinUtils;
  @Inject
  private UI ui;

  /**
   * Initialize view.
   */
  @PostConstruct
  public void init() {
    newLaboratory.addValueChangeListener(e -> {
      boolean value = (boolean) e.getProperty().getValue();
      laboratoryName.setVisible(value);
      organization.setVisible(value);
      managerEmail.setVisible(!value);
      if (value) {
        organization.focus();
      } else {
        managerEmail.focus();
      }
    });
    userAddress.setAddress(applicationConfiguration.getAddress());
    userAddress.setTown(applicationConfiguration.getTown());
    userAddress.setState(applicationConfiguration.getState());
    userAddress.setPostalCode(applicationConfiguration.getPostalCode());
    String[] countries = applicationConfiguration.getCountries();
    for (String country : countries) {
      this.country.addItem(country);
    }
    if (countries.length > 0) {
      userAddress.setCountry(countries[0]);
    }
    register.addClickListener(e -> {
      registerUser();
    });
    clear.addClickListener(e -> {
      address.setValue("");
      town.setValue("");
      state.setValue("");
      postalCode.setValue("");
    });
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Register view");
    final MessageResource resources = getResources();
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, getLocale());
    ui.getPage().setTitle(resources.message("title"));
    header.setValue(resources.message("header"));
    userFieldGroup.setItemDataSource(user);
    userFieldGroup.bind(email, emailProperty);
    userFieldGroup.bind(name, nameProperty);
    laboratoryFieldGroup.setItemDataSource(laboratory);
    laboratoryFieldGroup.bind(laboratoryName, laboratoryNameProperty);
    laboratoryFieldGroup.bind(organization, organizationProperty);
    managerFieldGroup.setItemDataSource(manager);
    managerFieldGroup.bind(managerEmail, emailProperty);
    addressFieldGroup.setItemDataSource(userAddress);
    addressFieldGroup.bind(address, addressProperty);
    addressFieldGroup.bind(addressSecond, addressSecondProperty);
    addressFieldGroup.bind(town, townProperty);
    addressFieldGroup.bind(state, stateProperty);
    addressFieldGroup.bind(country, countryProperty);
    addressFieldGroup.bind(postalCode, postalCodeProperty);
    phoneNumberFieldGroup.setItemDataSource(userPhoneNumber);
    phoneNumberFieldGroup.bind(phoneNumber, phoneNumberProperty);
    phoneNumberFieldGroup.bind(phoneExtension, phoneExtensionProperty);
    email.setCaption(resources.message("email"));
    email.setRequiredError(generalResources.message("required", email.getCaption()));
    email.addValidator(new EmailValidator(resources.message("email.invalid")));
    email.addValidator((value) -> {
      if (userService.exists(email.getValue())) {
        throw new InvalidValueException(resources.message("email.exists"));
      }
    });
    name.setCaption(resources.message("name"));
    name.setRequiredError(generalResources.message("required", name.getCaption()));
    password.setCaption(resources.message("password"));
    password.setRequiredError(generalResources.message("required", password.getCaption()));
    password.addFocusListener(e -> validatePasswords());
    confirmPassword.setCaption(resources.message("confirmPassword"));
    confirmPassword
        .setRequiredError(generalResources.message("required", confirmPassword.getCaption()));
    confirmPassword.addFocusListener(e -> validatePasswords());
    laboratoryHeader.setValue(resources.message("laboratoryHeader"));
    newLaboratory.setCaption(resources.message("newLaboratory"));
    organization.setCaption(resources.message("organization"));
    organization.setRequiredError(generalResources.message("required", organization.getCaption()));
    laboratoryName.setCaption(resources.message("laboratoryName"));
    laboratoryName
        .setRequiredError(generalResources.message("required", laboratoryName.getCaption()));
    managerEmail.setCaption(resources.message("manager"));
    managerEmail.setRequiredError(generalResources.message("required", managerEmail.getCaption()));
    managerEmail.addValidator(new EmailValidator(resources.message("manager.invalid")));
    managerEmail.addValidator(value -> {
      if (!userService.isManager(managerEmail.getValue())) {
        throw new InvalidValueException(resources.message("manager.notExists"));
      }
    });
    addressHeader.setValue(resources.message("addressHeader"));
    address.setCaption(resources.message("address"));
    address.setRequiredError(generalResources.message("required", address.getCaption()));
    addressSecond.setCaption(resources.message("addressSecond"));
    town.setCaption(resources.message("town"));
    town.setRequiredError(generalResources.message("required", town.getCaption()));
    state.setCaption(resources.message("state"));
    state.setRequiredError(generalResources.message("required", state.getCaption()));
    country.setCaption(resources.message("country"));
    country.setRequiredError(generalResources.message("required", country.getCaption()));
    postalCode.setCaption(resources.message("postalCode"));
    postalCode.setRequiredError(generalResources.message("required", postalCode.getCaption()));
    clear.setCaption(resources.message("clear"));
    phoneNumberHeader.setValue(resources.message("phoneNumberHeader"));
    phoneNumber.setCaption(resources.message("phoneNumber"));
    phoneNumber.setRequiredError(generalResources.message("required", phoneNumber.getCaption()));
    phoneNumber
        .addValidator(new RegexpValidator("[\\d\\-]*", resources.message("phoneNumber.invalid")));
    phoneExtension.setCaption(resources.message("phoneExtension"));
    phoneExtension
        .setRequiredError(generalResources.message("required", phoneExtension.getCaption()));
    phoneExtension.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message("phoneExtension.invalid")));
    registerHeader.setValue(resources.message("registerHeader"));
    register.setCaption(resources.message("register"));
    required.setValue(resources.message("required"));
  }

  private void validatePasswords() {
    password.setComponentError(null);
    if (password.getValue() != null && !password.getValue().isEmpty()
        && confirmPassword.getValue() != null && !confirmPassword.getValue().isEmpty()
        && !password.getValue().equals(confirmPassword.getValue())) {
      final MessageResource resources = getResources();
      password.setComponentError(new UserError(resources.message("password.notMatch")));
    }
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      userFieldGroup.commit();
      password.commit();
      confirmPassword.commit();
      if (newLaboratory.getValue()) {
        laboratoryFieldGroup.commit();
      } else {
        managerFieldGroup.commit();
      }
      addressFieldGroup.commit();
      phoneNumberFieldGroup.commit();
    } catch (InvalidValueException e) {
      logger.debug("Validation failed with message {}", e.getMessage());
      Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
      valid = false;
    } catch (CommitException e) {
      String message = vaadinUtils.getFieldMessage(e, getLocale());
      logger.debug("Validation failed with message {}", message);
      Notification.show(message, Notification.Type.ERROR_MESSAGE);
      valid = false;
    }
    return valid;
  }

  private void registerUser() {
    if (validate()) {
      final MessageResource resources = getResources();
      user.setAddresses(new ArrayList<>());
      user.getAddresses().add(userAddress);
      user.setPhoneNumbers(new ArrayList<>());
      userPhoneNumber.setType(PhoneNumberType.WORK);
      user.getPhoneNumbers().add(userPhoneNumber);
      User manager = this.manager;
      if (newLaboratory.getValue()) {
        user.setLaboratory(laboratory);
        manager = null;
      }
      userService.register(user, password.getValue(), manager,
          locale -> vaadinUtils.getUrl(ValidateView.VIEW_NAME));
      Notification.show(resources.message("done", user.getEmail()),
          Notification.Type.TRAY_NOTIFICATION);
      ui.getNavigator().navigateTo(MainView.VIEW_NAME);
    }
  }
}
