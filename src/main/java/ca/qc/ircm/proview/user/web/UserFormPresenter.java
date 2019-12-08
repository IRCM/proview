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
import static ca.qc.ircm.proview.user.web.UserForm.LABORATORY_NAME;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

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
public class UserFormPresenter {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(UserFormPresenter.class);
  private UserForm form;
  private Binder<User> binder = new BeanValidationBinder<>(User.class);
  private ListDataProvider<Laboratory> laboratoriesDataProvider;
  private Binder<Laboratory> laboratoryBinder = new BeanValidationBinder<>(Laboratory.class);
  private Binder<LaboratoryContainer> laboratoryContainerBinder =
      new BeanValidationBinder<>(LaboratoryContainer.class);
  private Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
  private Binder<PhoneNumber> phoneNumberBinder = new BeanValidationBinder<>(PhoneNumber.class);
  private User user;
  private LaboratoryService laboratoryService;
  private AuthorizationService authorizationService;
  private DefaultAddressConfiguration defaultAddressConfiguration;

  @Autowired
  protected UserFormPresenter(LaboratoryService laboratoryService,
      AuthorizationService authorizationService,
      DefaultAddressConfiguration defaultAddressConfiguration) {
    this.laboratoryService = laboratoryService;
    this.authorizationService = authorizationService;
    this.defaultAddressConfiguration = defaultAddressConfiguration;
  }

  void init(UserForm form) {
    this.form = form;
    form.admin.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    form.manager.setVisible(authorizationService.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER));
    form.manager.addValueChangeListener(e -> updateManager());
    if (authorizationService.hasRole(UserRole.ADMIN)) {
      laboratoriesDataProvider = DataProvider.ofCollection(laboratoryService.all());
    } else {
      laboratoriesDataProvider =
          DataProvider.fromStream(Stream.of(authorizationService.getCurrentUser().getLaboratory()));
    }
    form.laboratory.setDataProvider(laboratoriesDataProvider);
    form.laboratory.setItemLabelGenerator(lab -> lab.getName());
    form.laboratory.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    form.laboratory.addValueChangeListener(e -> {
      laboratoryBinder.readBean(e.getValue());
    });
    form.createNewLaboratory.addValueChangeListener(e -> updateCreateNewLaboratory());
    form.createNewLaboratory.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    setUser(null);
    updateManager();
    updateCreateNewLaboratory();
  }

  void localeChange(Locale locale) {
    final AppResources webResources = new AppResources(WebConstants.class, locale);
    binder.forField(form.email).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("")
        .withValidator(new EmailValidator(webResources.message(INVALID_EMAIL))).bind(EMAIL);
    binder.forField(form.name).asRequired(webResources.message(REQUIRED)).withNullRepresentation("")
        .bind(NAME);
    binder.forField(form.admin).bind(ADMIN);
    binder.forField(form.manager).bind(MANAGER);
    if (!laboratoriesDataProvider.getItems().isEmpty()) {
      form.laboratory.setRequiredIndicatorVisible(true);
    }
    laboratoryContainerBinder.forField(form.laboratory)
        .withValidator(laboratoryRequiredValidator(webResources.message(REQUIRED)))
        .withNullRepresentation(null).bind(LABORATORY);
    laboratoryBinder.forField(form.laboratoryName).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(LABORATORY_NAME);
    addressBinder.forField(form.addressLine).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(LINE);
    addressBinder.forField(form.town).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(TOWN);
    addressBinder.forField(form.state).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(STATE);
    addressBinder.forField(form.country).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(COUNTRY);
    addressBinder.forField(form.postalCode).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(POSTAL_CODE);
    phoneNumberBinder.forField(form.phoneType).asRequired(webResources.message(REQUIRED))
        .bind(TYPE);
    phoneNumberBinder.forField(form.number).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(NUMBER);
    phoneNumberBinder.forField(form.extension).withNullRepresentation("").bind(EXTENSION);
    updateReadOnly();
  }

  private Validator<Laboratory> laboratoryRequiredValidator(String errorMessage) {
    return (value, context) -> !form.createNewLaboratory.getValue() && value == null
        ? ValidationResult.error(errorMessage)
        : ValidationResult.ok();
  }

  private void updateReadOnly() {
    boolean readOnly =
        user.getId() != null && !authorizationService.hasPermission(user, BasePermission.WRITE);
    binder.setReadOnly(readOnly);
    form.laboratoryName.setReadOnly(
        readOnly || !authorizationService.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER));
    form.passwords.setVisible(!readOnly);
    addressBinder.setReadOnly(readOnly);
    phoneNumberBinder.setReadOnly(readOnly);
  }

  private void updateManager() {
    if (authorizationService.hasRole(UserRole.ADMIN)) {
      form.createNewLaboratory.setEnabled(form.manager.getValue());
      if (!form.manager.getValue()) {
        form.createNewLaboratory.setValue(false);
        updateCreateNewLaboratory();
      }
    }
  }

  private void updateCreateNewLaboratory() {
    boolean createNew = form.createNewLaboratory.getValue();
    form.laboratory.setEnabled(!createNew);
    laboratoryBinder
        .readBean(createNew || user.getLaboratory() == null || form.laboratory.getValue() == null
            ? new Laboratory(laboratoryBinder.getBean().getName())
            : laboratoryService.get(form.laboratory.getValue().getId()));
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

  boolean isValid() {
    boolean valid = true;
    valid = validateUser().isOk() && valid;
    valid = form.passwords.validate().isOk() && valid;
    valid = validateLaboratoryContainer().isOk() && valid;
    valid = validateLaboratory().isOk() && valid;
    valid = validateAddress().isOk() && valid;
    valid = validatePhoneNumber().isOk() && valid;
    return valid;
  }

  public String getPassword() {
    return form.passwords.getPassword();
  }

  User getUser() {
    if (form.laboratory.getValue() != null
        && (!form.createNewLaboratory.isEnabled() || !form.createNewLaboratory.getValue())) {
      user.getLaboratory().setId(form.laboratory.getValue().getId());
    } else {
      user.getLaboratory().setId(null);
    }
    return user;
  }

  void setUser(User user) {
    if (user == null) {
      user = new User();
    }
    if (user.getLaboratory() == null) {
      user.setLaboratory(new Laboratory());
    }
    if (!laboratoriesDataProvider.getItems().isEmpty()) {
      final Laboratory laboratory = user.getLaboratory();
      form.laboratory.setValue(laboratoriesDataProvider.getItems().stream()
          .filter(lab -> lab.getId().equals(laboratory.getId())).findAny()
          .orElse(laboratoriesDataProvider.getItems().iterator().next()));
      user.setLaboratory(laboratoryService.get(form.laboratory.getValue().getId()));
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
    form.passwords.setRequired(user.getId() == null);
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
