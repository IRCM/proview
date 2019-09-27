/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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
import static ca.qc.ircm.proview.user.UserProperties.ADMIN;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.MANAGER;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.UserDialog.LABORATORY_NAME;
import static ca.qc.ircm.proview.web.WebConstants.CANCEL;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;

/**
 * Users dialog presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserDialogPresenter {
  private static final Logger logger = LoggerFactory.getLogger(UserDialogPresenter.class);
  private UserDialog dialog;
  private Binder<User> binder = new BeanValidationBinder<>(User.class);
  private ListDataProvider<Laboratory> laboratoriesDataProvider;
  private Binder<Laboratory> laboratoryBinder = new BeanValidationBinder<>(Laboratory.class);
  private Binder<LaboratoryContainer> laboratoryContainerBinder =
      new BeanValidationBinder<>(LaboratoryContainer.class);
  private Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
  private Binder<PhoneNumber> phoneNumberBinder = new BeanValidationBinder<>(PhoneNumber.class);
  private User user;
  private UserService userService;
  private LaboratoryService laboratoryService;
  private AuthorizationService authorizationService;
  private DefaultAddressConfiguration defaultAddressConfiguration;

  @Autowired
  protected UserDialogPresenter(UserService userService, LaboratoryService laboratoryService,
      AuthorizationService authorizationService,
      DefaultAddressConfiguration defaultAddressConfiguration) {
    this.userService = userService;
    this.laboratoryService = laboratoryService;
    this.authorizationService = authorizationService;
    this.defaultAddressConfiguration = defaultAddressConfiguration;
  }

  void init(UserDialog dialog) {
    this.dialog = dialog;
    dialog.admin.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    dialog.manager.setVisible(authorizationService.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER));
    dialog.manager.addValueChangeListener(e -> updateManager());
    if (authorizationService.hasRole(UserRole.ADMIN)) {
      laboratoriesDataProvider = DataProvider.ofCollection(laboratoryService.all());
    } else {
      laboratoriesDataProvider =
          DataProvider.fromStream(Stream.of(authorizationService.getCurrentUser().getLaboratory()));
    }
    dialog.laboratory.setDataProvider(laboratoriesDataProvider);
    dialog.laboratory.setItemLabelGenerator(lab -> lab.getName());
    dialog.laboratory.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    dialog.laboratory.addValueChangeListener(e -> {
      laboratoryBinder.readBean(e.getValue());
    });
    dialog.createNewLaboratory.addValueChangeListener(e -> updateCreateNewLaboratory());
    dialog.createNewLaboratory.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    setUser(null);
    updateManager();
    updateCreateNewLaboratory();
  }

  void localeChange(Locale locale) {
    final AppResources webResources = new AppResources(WebConstants.class, locale);
    binder.forField(dialog.email).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("")
        .withValidator(new EmailValidator(webResources.message(INVALID_EMAIL))).bind(EMAIL);
    binder.forField(dialog.name).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(NAME);
    binder.forField(dialog.admin).bind(ADMIN);
    binder.forField(dialog.manager).bind(MANAGER);
    if (!laboratoriesDataProvider.getItems().isEmpty()) {
      dialog.laboratory.setRequiredIndicatorVisible(true);
    }
    laboratoryContainerBinder.forField(dialog.laboratory)
        .withValidator(laboratoryRequiredValidator(webResources.message(REQUIRED)))
        .withNullRepresentation(null).bind(LABORATORY);
    laboratoryBinder.forField(dialog.laboratoryName).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(LABORATORY_NAME);
    addressBinder.forField(dialog.addressLine).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(LINE);
    addressBinder.forField(dialog.town).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(TOWN);
    addressBinder.forField(dialog.state).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(STATE);
    addressBinder.forField(dialog.country).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(COUNTRY);
    addressBinder.forField(dialog.postalCode).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(POSTAL_CODE);
    phoneNumberBinder.forField(dialog.phoneType).asRequired(webResources.message(REQUIRED))
        .bind(TYPE);
    phoneNumberBinder.forField(dialog.number).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(NUMBER);
    phoneNumberBinder.forField(dialog.extension).withNullRepresentation("").bind(EXTENSION);
    dialog.save.setText(webResources.message(SAVE));
    dialog.cancel.setText(webResources.message(CANCEL));
    updateReadOnly();
  }

  private Validator<Laboratory> laboratoryRequiredValidator(String errorMessage) {
    return (value, context) -> !dialog.createNewLaboratory.getValue() && value == null
        ? ValidationResult.error(errorMessage)
        : ValidationResult.ok();
  }

  private void updateReadOnly() {
    boolean readOnly =
        user.getId() != null && !authorizationService.hasPermission(user, BasePermission.WRITE);
    binder.setReadOnly(readOnly);
    dialog.laboratoryName.setReadOnly(
        readOnly || !authorizationService.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER));
    dialog.passwords.setVisible(!readOnly);
    addressBinder.setReadOnly(readOnly);
    phoneNumberBinder.setReadOnly(readOnly);
  }

  private void updateManager() {
    if (authorizationService.hasRole(UserRole.ADMIN)) {
      dialog.createNewLaboratory.setEnabled(dialog.manager.getValue());
      if (!dialog.manager.getValue()) {
        dialog.createNewLaboratory.setValue(false);
        updateCreateNewLaboratory();
      }
    }
  }

  private void updateCreateNewLaboratory() {
    boolean createNew = dialog.createNewLaboratory.getValue();
    dialog.laboratory.setEnabled(!createNew);
    laboratoryBinder
        .setBean(createNew || user.getLaboratory() == null || dialog.laboratory.getValue() == null
            ? new Laboratory(laboratoryBinder.getBean().getName())
            : laboratoryService.get(dialog.laboratory.getValue().getId()));
  }

  BinderValidationStatus<User> validateUser() {
    return binder.validate();
  }

  BinderValidationStatus<LaboratoryContainer> validateLaboratoryContainer() {
    return laboratoryContainerBinder.validate();
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

  private boolean validate() {
    boolean valid = true;
    valid = validateUser().isOk() && valid;
    valid = dialog.passwords.validate().isOk() && valid;
    valid = validateLaboratoryContainer().isOk() && valid;
    valid = validateLaboratory().isOk() && valid;
    valid = validateAddress().isOk() && valid;
    valid = validatePhoneNumber().isOk() && valid;
    return valid;
  }

  void save() {
    if (validate()) {
      if (dialog.laboratory.getValue() != null
          && (!dialog.createNewLaboratory.isEnabled() || !dialog.createNewLaboratory.getValue())) {
        user.getLaboratory().setId(dialog.laboratory.getValue().getId());
      } else {
        user.getLaboratory().setId(null);
      }
      String password = dialog.passwords.getPassword();
      logger.debug("save user {} in laboratory {}", user, user.getLaboratory());
      userService.save(user, password);
      dialog.close();
      dialog.fireSavedEvent();
    }
  }

  void cancel() {
    dialog.close();
  }

  User getUser() {
    return user;
  }

  /**
   * Sets user.
   *
   * @param user
   *          user
   */
  void setUser(User user) {
    if (user == null) {
      user = new User();
    }
    if (user.getLaboratory() == null) {
      user.setLaboratory(new Laboratory());
    }
    if (!laboratoriesDataProvider.getItems().isEmpty()) {
      final Laboratory laboratory = user.getLaboratory();
      dialog.laboratory.setValue(laboratoriesDataProvider.getItems().stream()
          .filter(lab -> lab.getId().equals(laboratory.getId())).findAny()
          .orElse(laboratoriesDataProvider.getItems().iterator().next()));
      user.setLaboratory(laboratoryService.get(dialog.laboratory.getValue().getId()));
    }
    if (user.getAddress() == null) {
      user.setAddress(defaultAddressConfiguration.getAddress());
    }
    if (user.getPhoneNumbers() == null || user.getPhoneNumbers().isEmpty()) {
      user.setPhoneNumbers(new ArrayList<>());
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.setType(PhoneNumberType.WORK);
      user.getPhoneNumbers().add(phoneNumber);
    }
    this.user = user;
    binder.setBean(user);
    dialog.passwords.setRequired(user.getId() == null);
    laboratoryBinder.setBean(user.getLaboratory());
    addressBinder.setBean(user.getAddress());
    phoneNumberBinder.setBean(user.getPhoneNumbers().get(0));
    updateReadOnly();
  }

  static class LaboratoryContainer {
    private Laboratory laboratory;

    public Laboratory getLaboratory() {
      return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
      this.laboratory = laboratory;
    }
  }
}
