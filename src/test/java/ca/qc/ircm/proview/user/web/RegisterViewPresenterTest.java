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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class RegisterViewPresenterTest {
  @InjectMocks
  private RegisterViewPresenter presenter = new RegisterViewPresenter();
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
  private Label headerLabel = new Label();
  private Panel userPanel = new Panel();
  private UserForm userForm = new UserForm();
  private Panel laboratoryPanel = new Panel();
  private CheckBox newLaboratoryField = new CheckBox();
  private TextField organizationField = new TextField();
  private TextField laboratoryNameField = new TextField();
  private TextField managerEmailField = new TextField();
  private Panel addressPanel = new Panel();
  private AddressForm addressForm = new AddressForm();
  private Button clearAddressButton = new Button();
  private Panel phoneNumberPanel = new Panel();
  private PhoneNumberForm phoneNumberForm = new PhoneNumberForm();
  private Label registerHeaderLabel = new Label();
  private Button registerButton = new Button();
  private Label requiredLabel = new Label();
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
    when(defaultAddressConfiguration.getAddress()).thenReturn(defaultAddress);
    when(defaultAddressConfiguration.getTown()).thenReturn(defaultTown);
    when(defaultAddressConfiguration.getState()).thenReturn(defaultState);
    when(defaultAddressConfiguration.getPostalCode()).thenReturn(defaultPostalCode);
    when(defaultAddressConfiguration.getCountry()).thenReturn(defaultCountry);
    when(view.getHeaderLabel()).thenReturn(headerLabel);
    when(view.getUserPanel()).thenReturn(userPanel);
    when(view.getUserForm()).thenReturn(userForm);
    when(view.getLaboratoryPanel()).thenReturn(laboratoryPanel);
    when(view.getNewLaboratoryField()).thenReturn(newLaboratoryField);
    when(view.getOrganizationField()).thenReturn(organizationField);
    when(view.getLaboratoryNameField()).thenReturn(laboratoryNameField);
    when(view.getManagerEmailField()).thenReturn(managerEmailField);
    when(view.getAddressPanel()).thenReturn(addressPanel);
    when(view.getAddressForm()).thenReturn(addressForm);
    when(view.getClearAddressButton()).thenReturn(clearAddressButton);
    when(view.getPhoneNumberPanel()).thenReturn(phoneNumberPanel);
    when(view.getPhoneNumberForm()).thenReturn(phoneNumberForm);
    when(view.getRegisterHeaderLabel()).thenReturn(registerHeaderLabel);
    when(view.getRegisterButton()).thenReturn(registerButton);
    when(view.getRequiredLabel()).thenReturn(requiredLabel);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    view.getPhoneNumberForm().getTypeField().setNullSelectionAllowed(false);
    view.getPhoneNumberForm().getTypeField().setNewItemsAllowed(false);
    view.getPhoneNumberForm().getTypeField().removeAllItems();
    for (PhoneNumberType type : PhoneNumberType.values()) {
      view.getPhoneNumberForm().getTypeField().addItem(type);
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
    view.getUserForm().getEmailField().setValue(email);
    userItem.getItemProperty(UserFormPresenter.EMAIL_PROPERTY).setValue(email);
    view.getUserForm().getNameField().setValue(name);
    userItem.getItemProperty(UserFormPresenter.NAME_PROPERTY).setValue(name);
    Item passwordItem = passwordItemCaptor.getValue();
    view.getUserForm().getPasswordField().setValue(password);
    passwordItem.getItemProperty(UserFormPresenter.PASSWORD_PROPERTY).setValue(password);
    view.getUserForm().getConfirmPasswordField().setValue(password);
    view.getManagerEmailField().setValue(managerEmail);
    view.getLaboratoryNameField().setValue(laboratoryName);
    view.getOrganizationField().setValue(organization);
    Item addressItem = addressItemCaptor.getValue();
    view.getAddressForm().lineField.setValue(addressLine);
    addressItem.getItemProperty(AddressFormPresenter.LINE_PROPERTY).setValue(addressLine);
    view.getAddressForm().townField.setValue(town);
    addressItem.getItemProperty(AddressFormPresenter.TOWN_PROPERTY).setValue(town);
    view.getAddressForm().stateField.setValue(state);
    addressItem.getItemProperty(AddressFormPresenter.STATE_PROPERTY).setValue(state);
    view.getAddressForm().countryField.setValue(country);
    addressItem.getItemProperty(AddressFormPresenter.COUNTRY_PROPERTY).setValue(country);
    view.getAddressForm().postalCodeField.setValue(postalCode);
    addressItem.getItemProperty(AddressFormPresenter.POSTAL_CODE_PROPERTY).setValue(postalCode);
    Item phoneNumberItem = phoneNumberItemCaptor.getValue();
    view.getPhoneNumberForm().getTypeField().setValue(phoneType);
    phoneNumberItem.getItemProperty(PhoneNumberFormPresenter.TYPE_PROPERTY).setValue(phoneType);
    view.getPhoneNumberForm().getNumberField().setValue(phoneNumber);
    phoneNumberItem.getItemProperty(PhoneNumberFormPresenter.NUMBER_PROPERTY).setValue(phoneNumber);
    view.getPhoneNumberForm().getExtensionField().setValue(phoneExtension);
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
    view.getNewLaboratoryField().setValue(true);
    view.getNewLaboratoryField().valueChange(new ValueChangeEvent(view.getNewLaboratoryField()));

    assertFalse(view.getManagerEmailField().isVisible());
    assertTrue(view.getOrganizationField().isVisible());
    assertTrue(view.getLaboratoryNameField().isVisible());
  }

  @Test
  public void newLaboratoryVisibleFields_False() {
    view.getNewLaboratoryField().setValue(false);
    view.getNewLaboratoryField().valueChange(new ValueChangeEvent(view.getNewLaboratoryField()));

    assertTrue(view.getManagerEmailField().isVisible());
    assertFalse(view.getOrganizationField().isVisible());
    assertFalse(view.getLaboratoryNameField().isVisible());
  }

  @Test
  public void init() {
    verify(userFormPresenter).init(view.getUserForm());
    verify(addressFormPresenter).init(view.getAddressForm());
    verify(phoneNumberFormPresenter).init(view.getPhoneNumberForm());
  }

  @Test
  public void title() {
    verify(view).setTitle(resources.message("title"));
  }

  @Test
  public void ids() {
    assertEquals(HEADER_LABEL_ID, view.getHeaderLabel().getId());
    assertEquals(USER_FORM_ID, view.getUserPanel().getId());
    assertEquals(LABORATORY_ID, view.getLaboratoryPanel().getId());
    assertEquals(NEW_LABORATORY_ID, view.getNewLaboratoryField().getId());
    assertEquals(ORGANIZATION_ID, view.getOrganizationField().getId());
    assertEquals(LABORATORY_NAME_ID, view.getLaboratoryNameField().getId());
    assertEquals(MANAGER_EMAIL_ID, view.getManagerEmailField().getId());
    assertEquals(ADDRESS_FORM_ID, view.getAddressPanel().getId());
    assertEquals(CLEAR_ADDRESS_BUTTON_ID, view.getClearAddressButton().getId());
    assertEquals(PHONE_NUMBER_FORM_ID, view.getPhoneNumberPanel().getId());
    assertEquals(REGISTER_HEADER_LABEL_ID, view.getRegisterHeaderLabel().getId());
    assertEquals(REGISTER_BUTTON_ID, view.getRegisterButton().getId());
    assertEquals(REQUIRED_LABEL_ID, view.getRequiredLabel().getId());
  }

  @Test
  public void captions() {
    assertEquals(resources.message(HEADER_LABEL_ID), view.getHeaderLabel().getValue());
    assertEquals(resources.message(USER_FORM_ID), view.getUserPanel().getCaption());
    assertEquals(resources.message(LABORATORY_ID), view.getLaboratoryPanel().getCaption());
    assertEquals(resources.message(NEW_LABORATORY_ID), view.getNewLaboratoryField().getCaption());
    assertEquals(resources.message(ORGANIZATION_ID), view.getOrganizationField().getCaption());
    assertEquals(resources.message(LABORATORY_NAME_ID), view.getLaboratoryNameField().getCaption());
    assertEquals(resources.message(MANAGER_EMAIL_ID), view.getManagerEmailField().getCaption());
    assertEquals(resources.message(ADDRESS_FORM_ID), view.getAddressPanel().getCaption());
    assertEquals(resources.message(CLEAR_ADDRESS_BUTTON_ID),
        view.getClearAddressButton().getCaption());
    assertEquals(resources.message(PHONE_NUMBER_FORM_ID), view.getPhoneNumberPanel().getCaption());
    assertEquals(resources.message(REGISTER_HEADER_LABEL_ID),
        view.getRegisterHeaderLabel().getValue());
    assertEquals(resources.message(REGISTER_BUTTON_ID), view.getRegisterButton().getCaption());
    assertEquals(resources.message(REQUIRED_LABEL_ID), view.getRequiredLabel().getValue());
  }

  private String requiredError(String caption) {
    return generalResources.message("required", caption);
  }

  @Test
  public void requiredFields() {
    assertTrue(view.getOrganizationField().isRequired());
    assertEquals(requiredError(resources.message(ORGANIZATION_ID)),
        view.getOrganizationField().getRequiredError());
    assertTrue(view.getLaboratoryNameField().isRequired());
    assertEquals(requiredError(resources.message(LABORATORY_NAME_ID)),
        view.getLaboratoryNameField().getRequiredError());
    assertTrue(view.getManagerEmailField().isRequired());
    assertEquals(requiredError(resources.message(MANAGER_EMAIL_ID)),
        view.getManagerEmailField().getRequiredError());
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
    assertEquals(defaultAddress, view.getAddressForm().lineField.getValue());
    assertEquals(defaultTown, view.getAddressForm().townField.getValue());
    assertEquals(defaultState, view.getAddressForm().stateField.getValue());
    assertEquals(defaultCountry, view.getAddressForm().countryField.getValue());
    assertEquals(defaultPostalCode, view.getAddressForm().postalCodeField.getValue());
    assertEquals(PhoneNumberType.WORK, view.getPhoneNumberForm().getTypeField().getValue());
  }

  @Test
  public void clearAddress() {
    setFields();

    view.getClearAddressButton().click();

    assertEquals("", view.getAddressForm().lineField.getValue());
    assertEquals("", view.getAddressForm().townField.getValue());
    assertEquals("", view.getAddressForm().stateField.getValue());
    assertEquals("", view.getAddressForm().postalCodeField.getValue());
    assertEquals("", view.getAddressForm().countryField.getValue());
  }

  @Test
  public void newLaboratory_Default() {
    assertFalse(view.getLaboratoryNameField().isVisible());
    assertFalse(view.getOrganizationField().isVisible());
    assertTrue(view.getManagerEmailField().isVisible());
  }

  @Test
  public void newLaboratory_False() {
    view.getNewLaboratoryField().setValue(false);

    assertFalse(view.getLaboratoryNameField().isVisible());
    assertFalse(view.getOrganizationField().isVisible());
    assertTrue(view.getManagerEmailField().isVisible());
  }

  @Test
  public void newLaboratory_True() {
    view.getNewLaboratoryField().setValue(true);

    assertTrue(view.getLaboratoryNameField().isVisible());
    assertTrue(view.getOrganizationField().isVisible());
    assertFalse(view.getManagerEmailField().isVisible());
  }

  @Test
  public void managerEmailEmailValidator() {
    view.getManagerEmailField().setValue("aaa");

    assertFalse(view.getManagerEmailField().isValid());
    assertEquals(resources.message(MANAGER_EMAIL_ID + ".invalid"),
        errorMessage(view.getManagerEmailField()));
  }

  @Test
  public void managerEmailManagerValidtor_Manager() {
    when(userService.isManager(any())).thenReturn(true);

    view.getManagerEmailField().setValue(managerEmail);

    assertTrue(view.getManagerEmailField().isValid());
    verify(userService).isManager(managerEmail);
  }

  @Test
  public void managerEmailManagerValidtor_NotManager() {
    when(userService.isManager(any())).thenReturn(false);

    view.getManagerEmailField().setValue(managerEmail);

    assertFalse(view.getManagerEmailField().isValid());
    verify(userService).isManager(managerEmail);
    assertEquals(resources.message(MANAGER_EMAIL_ID + ".notExists"),
        errorMessage(view.getManagerEmailField()));
  }

  @Test
  public void register_NewLaboratory() throws Throwable {
    setFields();
    view.getNewLaboratoryField().setValue(true);
    String validationUrl = "validationUrl";
    when(vaadinUtils.getUrl(any())).thenReturn(validationUrl);

    view.getRegisterButton().click();

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
    view.getNewLaboratoryField().setValue(false);
    String validationUrl = "validationUrl";
    when(userService.isManager(any())).thenReturn(true);
    when(vaadinUtils.getUrl(any())).thenReturn(validationUrl);

    view.getRegisterButton().click();

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

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_OrganizationEmpty() {
    setFields();
    view.getNewLaboratoryField().setValue(true);
    view.getOrganizationField().setValue("");

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_LaboratoryNameEmpty() {
    setFields();
    view.getNewLaboratoryField().setValue(true);
    view.getLaboratoryNameField().setValue("");

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailEmpty() {
    setFields();
    view.getNewLaboratoryField().setValue(false);
    view.getManagerEmailField().setValue("");

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailInvalid() {
    setFields();
    view.getNewLaboratoryField().setValue(false);
    view.getManagerEmailField().setValue("aaa");

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailNotManager() {
    when(userService.isManager(any())).thenReturn(false);
    setFields();
    view.getNewLaboratoryField().setValue(false);
    view.getManagerEmailField().setValue("aaa@ircm.qc.ca");

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_AddressError() throws Throwable {
    setFields();
    doThrow(new CommitException()).when(addressFormPresenter).commit();

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PhoneNumberError() throws Throwable {
    setFields();
    doThrow(new CommitException()).when(phoneNumberFormPresenter).commit();

    view.getRegisterButton().click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }
}
