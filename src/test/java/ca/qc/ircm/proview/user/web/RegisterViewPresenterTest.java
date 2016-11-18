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

import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.ADDRESS_FORM_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.CLEAR_ADDRESS_BUTTON_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.HEADER_LABEL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.LABORATORY_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.LABORATORY_NAME_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.MANAGER_EMAIL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.NEW_LABORATORY_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.ORGANIZATION_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.PHONE_NUMBER_FORM_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.REGISTER_BUTTON_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.REGISTER_HEADER_LABEL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.REQUIRED_LABEL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.USER_FORM_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.RegisterUserWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class RegisterViewPresenterTest {
  private RegisterViewPresenter presenter;
  @Mock
  private RegisterView view;
  @Mock
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Mock
  private UserService userService;
  @Mock
  private VaadinUtils vaadinUtils;
  @Mock
  private UserFormPresenter userFormPresenter;
  @Mock
  private AddressFormPresenter addressFormPresenter;
  @Mock
  private PhoneNumberFormPresenter phoneNumberFormPresenter;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<User> managerCaptor;
  @Captor
  private ArgumentCaptor<Item> userItemCaptor;
  @Captor
  private ArgumentCaptor<Item> passwordItemCaptor;
  @Captor
  private ArgumentCaptor<Item> addressItemCaptor;
  @Captor
  private ArgumentCaptor<Item> phoneNumberItemCaptor;
  @Captor
  private ArgumentCaptor<RegisterUserWebContext> registerUserWebContextCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private String defaultAddress = "110 avenue des Pins Ouest";
  private String defaultTown = "Montreal";
  private String defaultState = "Quebec";
  private String defaultPostalCode = "H2W 1R7";
  private String defaultCountry = "Canada";
  private String email = "unit.test@ircm.qc.ca";
  private String name = "Unit Test";
  private String password = "unittestpassword";
  private String managerEmail = "benoit.coulombe@ircm.qc.ca";
  private String laboratoryName = "Test lab";
  private String organization = "IRCM";
  private String addressLine = "123 Papineau";
  private String town = "Laval";
  private String state = "Ontario";
  private String country = "USA";
  private String postalCode = "12345";
  private PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private String phoneNumber = "514-555-5555";
  private String phoneExtension = "234";
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(RegisterView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new RegisterViewPresenter(defaultAddressConfiguration, userService, vaadinUtils,
        userFormPresenter, addressFormPresenter, phoneNumberFormPresenter, applicationName);
    when(defaultAddressConfiguration.getAddress()).thenReturn(defaultAddress);
    when(defaultAddressConfiguration.getTown()).thenReturn(defaultTown);
    when(defaultAddressConfiguration.getState()).thenReturn(defaultState);
    when(defaultAddressConfiguration.getPostalCode()).thenReturn(defaultPostalCode);
    when(defaultAddressConfiguration.getCountry()).thenReturn(defaultCountry);
    view.headerLabel = new Label();
    view.userPanel = new Panel();
    view.userForm = new UserForm();
    view.laboratoryPanel = new Panel();
    view.newLaboratoryField = new CheckBox();
    view.organizationField = new TextField();
    view.laboratoryNameField = new TextField();
    view.managerEmailField = new TextField();
    view.addressPanel = new Panel();
    view.addressForm = new AddressForm();
    view.clearAddressButton = new Button();
    view.phoneNumberPanel = new Panel();
    view.phoneNumberForm = new PhoneNumberForm();
    view.registerHeaderLabel = new Label();
    view.registerButton = new Button();
    view.requiredLabel = new Label();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    view.phoneNumberForm.typeField.setNullSelectionAllowed(false);
    view.phoneNumberForm.typeField.setNewItemsAllowed(false);
    view.phoneNumberForm.typeField.removeAllItems();
    for (PhoneNumberType type : PhoneNumberType.values()) {
      view.phoneNumberForm.typeField.addItem(type);
    }
    presenter.init(view);
    presenter.attach();
  }

  @SuppressWarnings("unchecked")
  private void setFields() {
    verify(userFormPresenter).setItemDataSource(userItemCaptor.capture());
    verify(userFormPresenter).setPasswordItemDataSource(passwordItemCaptor.capture());
    verify(addressFormPresenter).setItemDataSource(addressItemCaptor.capture());
    verify(phoneNumberFormPresenter).setItemDataSource(phoneNumberItemCaptor.capture());
    Item userItem = userItemCaptor.getValue();
    view.userForm.emailField.setValue(email);
    userItem.getItemProperty(UserFormPresenter.EMAIL_PROPERTY).setValue(email);
    view.userForm.nameField.setValue(name);
    userItem.getItemProperty(UserFormPresenter.NAME_PROPERTY).setValue(name);
    Item passwordItem = passwordItemCaptor.getValue();
    view.userForm.passwordField.setValue(password);
    passwordItem.getItemProperty(UserFormPresenter.PASSWORD_PROPERTY).setValue(password);
    view.userForm.confirmPasswordField.setValue(password);
    view.managerEmailField.setValue(managerEmail);
    view.laboratoryNameField.setValue(laboratoryName);
    view.organizationField.setValue(organization);
    Item addressItem = addressItemCaptor.getValue();
    view.addressForm.lineField.setValue(addressLine);
    addressItem.getItemProperty(AddressFormPresenter.LINE_PROPERTY).setValue(addressLine);
    view.addressForm.townField.setValue(town);
    addressItem.getItemProperty(AddressFormPresenter.TOWN_PROPERTY).setValue(town);
    view.addressForm.stateField.setValue(state);
    addressItem.getItemProperty(AddressFormPresenter.STATE_PROPERTY).setValue(state);
    view.addressForm.countryField.setValue(country);
    addressItem.getItemProperty(AddressFormPresenter.COUNTRY_PROPERTY).setValue(country);
    view.addressForm.postalCodeField.setValue(postalCode);
    addressItem.getItemProperty(AddressFormPresenter.POSTAL_CODE_PROPERTY).setValue(postalCode);
    Item phoneNumberItem = phoneNumberItemCaptor.getValue();
    view.phoneNumberForm.typeField.setValue(phoneType);
    phoneNumberItem.getItemProperty(PhoneNumberFormPresenter.TYPE_PROPERTY).setValue(phoneType);
    view.phoneNumberForm.numberField.setValue(phoneNumber);
    phoneNumberItem.getItemProperty(PhoneNumberFormPresenter.NUMBER_PROPERTY).setValue(phoneNumber);
    view.phoneNumberForm.extensionField.setValue(phoneExtension);
    phoneNumberItem.getItemProperty(PhoneNumberFormPresenter.EXTENSION_PROPERTY)
        .setValue(phoneExtension);
  }

  private String errorMessage(AbstractField<?> field) {
    try {
      field.validate();
      return null;
    } catch (InvalidValueException e) {
      if (e.getCauses() != null) {
        for (InvalidValueException cause : e.getCauses()) {
          if (cause.getMessage() != null) {
            return cause.getMessage();
          }
        }
      }
      return e.getMessage();
    }
  }

  @Test
  public void newLaboratoryVisibleFields_True() {
    view.newLaboratoryField.setValue(true);
    view.newLaboratoryField.valueChange(new ValueChangeEvent(view.newLaboratoryField));

    assertFalse(view.managerEmailField.isVisible());
    assertTrue(view.organizationField.isVisible());
    assertTrue(view.laboratoryNameField.isVisible());
  }

  @Test
  public void newLaboratoryVisibleFields_False() {
    view.newLaboratoryField.setValue(false);
    view.newLaboratoryField.valueChange(new ValueChangeEvent(view.newLaboratoryField));

    assertTrue(view.managerEmailField.isVisible());
    assertFalse(view.organizationField.isVisible());
    assertFalse(view.laboratoryNameField.isVisible());
  }

  @Test
  public void init() {
    verify(userFormPresenter).init(view.userForm);
    verify(addressFormPresenter).init(view.addressForm);
    verify(phoneNumberFormPresenter).init(view.phoneNumberForm);
  }

  @Test
  public void title() {
    verify(view).setTitle(resources.message("title", applicationName));
  }

  @Test
  public void ids() {
    assertEquals(HEADER_LABEL_ID, view.headerLabel.getId());
    assertEquals(USER_FORM_ID, view.userPanel.getId());
    assertEquals(LABORATORY_ID, view.laboratoryPanel.getId());
    assertEquals(NEW_LABORATORY_ID, view.newLaboratoryField.getId());
    assertEquals(ORGANIZATION_ID, view.organizationField.getId());
    assertEquals(LABORATORY_NAME_ID, view.laboratoryNameField.getId());
    assertEquals(MANAGER_EMAIL_ID, view.managerEmailField.getId());
    assertEquals(ADDRESS_FORM_ID, view.addressPanel.getId());
    assertEquals(CLEAR_ADDRESS_BUTTON_ID, view.clearAddressButton.getId());
    assertEquals(PHONE_NUMBER_FORM_ID, view.phoneNumberPanel.getId());
    assertEquals(REGISTER_HEADER_LABEL_ID, view.registerHeaderLabel.getId());
    assertEquals(REGISTER_BUTTON_ID, view.registerButton.getId());
    assertEquals(REQUIRED_LABEL_ID, view.requiredLabel.getId());
  }

  @Test
  public void captions() {
    assertEquals(resources.message(HEADER_LABEL_ID), view.headerLabel.getValue());
    assertEquals(resources.message(USER_FORM_ID), view.userPanel.getCaption());
    assertEquals(resources.message(LABORATORY_ID), view.laboratoryPanel.getCaption());
    assertEquals(resources.message(NEW_LABORATORY_ID), view.newLaboratoryField.getCaption());
    assertEquals(resources.message(ORGANIZATION_ID), view.organizationField.getCaption());
    assertEquals(resources.message(LABORATORY_NAME_ID), view.laboratoryNameField.getCaption());
    assertEquals(resources.message(MANAGER_EMAIL_ID), view.managerEmailField.getCaption());
    assertEquals(resources.message(ADDRESS_FORM_ID), view.addressPanel.getCaption());
    assertEquals(resources.message(CLEAR_ADDRESS_BUTTON_ID), view.clearAddressButton.getCaption());
    assertEquals(resources.message(PHONE_NUMBER_FORM_ID), view.phoneNumberPanel.getCaption());
    assertEquals(resources.message(REGISTER_HEADER_LABEL_ID), view.registerHeaderLabel.getValue());
    assertEquals(resources.message(REGISTER_BUTTON_ID), view.registerButton.getCaption());
    assertEquals(resources.message(REQUIRED_LABEL_ID), view.requiredLabel.getValue());
  }

  private String requiredError(String caption) {
    return generalResources.message("required", caption);
  }

  @Test
  public void requiredFields() {
    assertTrue(view.organizationField.isRequired());
    assertEquals(requiredError(resources.message(ORGANIZATION_ID)),
        view.organizationField.getRequiredError());
    assertTrue(view.laboratoryNameField.isRequired());
    assertEquals(requiredError(resources.message(LABORATORY_NAME_ID)),
        view.laboratoryNameField.getRequiredError());
    assertTrue(view.managerEmailField.isRequired());
    assertEquals(requiredError(resources.message(MANAGER_EMAIL_ID)),
        view.managerEmailField.getRequiredError());
  }

  @Test
  public void setItems() {
    verify(userFormPresenter).setItemDataSource(any());
    verify(userFormPresenter).setPasswordItemDataSource(any());
    verify(addressFormPresenter).setItemDataSource(any());
    verify(phoneNumberFormPresenter).setItemDataSource(any());
  }

  @Test
  public void editable() {
    verify(userFormPresenter).setEditable(true);
    verify(addressFormPresenter).setEditable(true);
    verify(phoneNumberFormPresenter).setEditable(true);
  }

  @Test
  public void defaults() {
    assertEquals(defaultAddress, view.addressForm.lineField.getValue());
    assertEquals(defaultTown, view.addressForm.townField.getValue());
    assertEquals(defaultState, view.addressForm.stateField.getValue());
    assertEquals(defaultCountry, view.addressForm.countryField.getValue());
    assertEquals(defaultPostalCode, view.addressForm.postalCodeField.getValue());
    assertEquals(PhoneNumberType.WORK, view.phoneNumberForm.typeField.getValue());
  }

  @Test
  public void clearAddress() {
    setFields();

    view.clearAddressButton.click();

    assertEquals("", view.addressForm.lineField.getValue());
    assertEquals("", view.addressForm.townField.getValue());
    assertEquals("", view.addressForm.stateField.getValue());
    assertEquals("", view.addressForm.postalCodeField.getValue());
    assertEquals("", view.addressForm.countryField.getValue());
  }

  @Test
  public void newLaboratory_Default() {
    assertFalse(view.laboratoryNameField.isVisible());
    assertFalse(view.organizationField.isVisible());
    assertTrue(view.managerEmailField.isVisible());
  }

  @Test
  public void newLaboratory_False() {
    view.newLaboratoryField.setValue(false);

    assertFalse(view.laboratoryNameField.isVisible());
    assertFalse(view.organizationField.isVisible());
    assertTrue(view.managerEmailField.isVisible());
  }

  @Test
  public void newLaboratory_True() {
    view.newLaboratoryField.setValue(true);

    assertTrue(view.laboratoryNameField.isVisible());
    assertTrue(view.organizationField.isVisible());
    assertFalse(view.managerEmailField.isVisible());
  }

  @Test
  public void managerEmailEmailValidator() {
    view.managerEmailField.setValue("aaa");

    assertFalse(view.managerEmailField.isValid());
    assertEquals(resources.message(MANAGER_EMAIL_ID + ".invalid"),
        errorMessage(view.managerEmailField));
  }

  @Test
  public void managerEmailManagerValidtor_Manager() {
    when(userService.isManager(any())).thenReturn(true);

    view.managerEmailField.setValue(managerEmail);

    assertTrue(view.managerEmailField.isValid());
    verify(userService).isManager(managerEmail);
  }

  @Test
  public void managerEmailManagerValidtor_NotManager() {
    when(userService.isManager(any())).thenReturn(false);

    view.managerEmailField.setValue(managerEmail);

    assertFalse(view.managerEmailField.isValid());
    verify(userService).isManager(managerEmail);
    assertEquals(resources.message(MANAGER_EMAIL_ID + ".notExists"),
        errorMessage(view.managerEmailField));
  }

  @Test
  public void register_NewLaboratory() throws Throwable {
    setFields();
    view.newLaboratoryField.setValue(true);
    String validationUrl = "validationUrl";
    when(vaadinUtils.getUrl(any())).thenReturn(validationUrl);

    view.registerButton.click();

    verify(userFormPresenter).commit();
    verify(addressFormPresenter).commit();
    verify(phoneNumberFormPresenter).commit();
    verify(userService).register(userCaptor.capture(), eq(password), managerCaptor.capture(),
        registerUserWebContextCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    assertNull(user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
    assertEquals(organization, user.getLaboratory().getOrganization());
    assertNotNull(user.getAddress());
    Address address = user.getAddress();
    assertEquals(this.addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertNotNull(user.getPhoneNumbers());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(phoneType, phoneNumber.getType());
    assertEquals(this.phoneNumber, phoneNumber.getNumber());
    assertEquals(phoneExtension, phoneNumber.getExtension());
    User manager = managerCaptor.getValue();
    assertNull(manager);
    RegisterUserWebContext registerUserWebContext = registerUserWebContextCaptor.getValue();
    assertEquals(validationUrl, registerUserWebContext.getValidateUserUrl(locale));
    verify(view).afterSuccessfulRegister(resources.message("done", email));
  }

  @Test
  public void register_ExistingLaboratory() throws Throwable {
    setFields();
    view.newLaboratoryField.setValue(false);
    String validationUrl = "validationUrl";
    when(userService.isManager(any())).thenReturn(true);
    when(vaadinUtils.getUrl(any())).thenReturn(validationUrl);

    view.registerButton.click();

    verify(userFormPresenter).commit();
    verify(addressFormPresenter).commit();
    verify(phoneNumberFormPresenter).commit();
    verify(userService).register(userCaptor.capture(), eq(password), managerCaptor.capture(),
        registerUserWebContextCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    assertNull(user.getLaboratory());
    assertNotNull(user.getAddress());
    Address address = user.getAddress();
    assertEquals(this.addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertNotNull(user.getPhoneNumbers());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(phoneType, phoneNumber.getType());
    assertEquals(this.phoneNumber, phoneNumber.getNumber());
    assertEquals(phoneExtension, phoneNumber.getExtension());
    User manager = managerCaptor.getValue();
    assertNotNull(manager);
    assertEquals(managerEmail, manager.getEmail());
    RegisterUserWebContext registerUserWebContext = registerUserWebContextCaptor.getValue();
    assertEquals(validationUrl, registerUserWebContext.getValidateUserUrl(locale));
    verify(view).afterSuccessfulRegister(resources.message("done", email));
  }

  @Test
  public void register_UserError() throws Throwable {
    setFields();
    doThrow(new CommitException()).when(userFormPresenter).commit();

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_OrganizationEmpty() {
    setFields();
    view.newLaboratoryField.setValue(true);
    view.organizationField.setValue("");

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_LaboratoryNameEmpty() {
    setFields();
    view.newLaboratoryField.setValue(true);
    view.laboratoryNameField.setValue("");

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailEmpty() {
    setFields();
    view.newLaboratoryField.setValue(false);
    view.managerEmailField.setValue("");

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailInvalid() {
    setFields();
    view.newLaboratoryField.setValue(false);
    view.managerEmailField.setValue("aaa");

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailNotManager() {
    when(userService.isManager(any())).thenReturn(false);
    setFields();
    view.newLaboratoryField.setValue(false);
    view.managerEmailField.setValue("aaa@ircm.qc.ca");

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_AddressError() throws Throwable {
    setFields();
    doThrow(new CommitException()).when(addressFormPresenter).commit();

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PhoneNumberError() throws Throwable {
    setFields();
    doThrow(new CommitException()).when(phoneNumberFormPresenter).commit();

    view.registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }
}
