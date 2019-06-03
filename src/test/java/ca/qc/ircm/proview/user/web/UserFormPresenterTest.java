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
import static ca.qc.ircm.proview.user.AddressProperties.POSTAL_CODE;
import static ca.qc.ircm.proview.user.AddressProperties.STATE;
import static ca.qc.ircm.proview.user.AddressProperties.TOWN;
import static ca.qc.ircm.proview.user.UserProperties.ADDRESS;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.UserProperties.PHONE_NUMBERS;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.ADDRESS_LINE;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.ADD_PHONE;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.CLEAR_ADDRESS;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.LABORATORY_ORGANIZATION;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.MANAGER;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.NEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.PASSWORD;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.PHONE;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.PHONE_EXTENSION;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.PHONE_NUMBER;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.PHONE_TYPE;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.REGISTER_WARNING;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.REMOVE_PHONE;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.SAVE;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.USER;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.styleName;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.LABEL_WARNING;
import static ca.qc.ircm.proview.web.WebConstants.PLACEHOLDER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.RegisterUserWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserFormPresenterTest {
  @Inject
  private UserFormPresenter presenter;
  @Inject
  private UserRepository repository;
  @MockBean
  private UserService userService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Mock
  private UserForm view;
  @Mock
  private SaveListener<User> listener;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Component> componentCaptor;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<RegisterUserWebContext> registerUserWebContextCaptor;
  private UserFormDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(UserForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private User user;
  private User currentUser;
  private String email = "unit.test@ircm.qc.ca";
  private String name = "Unit Test";
  private String password = "unittestpassword";
  private String manager = "manager@ircm.qc.ca";
  private String organization = "ircm";
  private String laboratoryName = "coulombe";
  private String defaultAddressLine = "110 avenue des Pins Ouest";
  private String defaultTown = "Montreal";
  private String defaultState = "Quebec";
  private String defaultCountry = "Canada";
  private String defaultPostalCode = "H2W 1R7";
  private String addressLine = "123 Papineau";
  private String town = "Laval";
  private String state = "Ontario";
  private String country = "USA";
  private String postalCode = "12345";
  private PhoneNumberType type1 = PhoneNumberType.MOBILE;
  private String number1 = "514-555-5555";
  private String extension1 = "234";
  private PhoneNumberType type2 = PhoneNumberType.WORK;
  private String number2 = "514-555-5566";
  private String extension2 = "123";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new UserFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(defaultAddressConfiguration.getAddress()).thenReturn(defaultAddressLine);
    when(defaultAddressConfiguration.getTown()).thenReturn(defaultTown);
    when(defaultAddressConfiguration.getState()).thenReturn(defaultState);
    when(defaultAddressConfiguration.getCountry()).thenReturn(defaultCountry);
    when(defaultAddressConfiguration.getPostalCode()).thenReturn(defaultPostalCode);
    presenter.init(view);
    user = repository.findById(10L).orElse(null);
    when(userService.isManager(any())).thenReturn(true);
    currentUser = repository.findById(1L).orElse(null);
    when(authorizationService.getCurrentUser()).thenReturn(currentUser);
  }

  private void addPhoneNumber() {
    design.addPhoneNumberButton.click();
  }

  private boolean isNewUser() {
    return presenter.getValue().getId() == null;
  }

  private boolean isAdmin() {
    return authorizationService.hasRole(UserRole.ADMIN);
  }

  private void setFields() {
    design.emailField.setValue(email);
    design.nameField.setValue(name);
    design.passwordField.setValue(password);
    design.confirmPasswordField.setValue(password);
    if (isNewUser() && !isAdmin()) {
      design.managerField.setValue(manager);
      design.organizationField.setValue(organization);
      design.laboratoryNameField.setValue(laboratoryName);
    }
    design.addressLineField.setValue(addressLine);
    design.townField.setValue(town);
    design.stateField.setValue(state);
    design.countryField.setValue(country);
    design.postalCodeField.setValue(postalCode);
    int phoneNumberIndex = design.phoneNumbersLayout.getComponentCount() - 1;
    typeField(phoneNumberIndex).setValue(type1);
    numberField(phoneNumberIndex).setValue(number1);
    extensionField(phoneNumberIndex).setValue(extension1);
    design.addPhoneNumberButton.click();
    phoneNumberIndex++;
    typeField(phoneNumberIndex).setValue(type2);
    numberField(phoneNumberIndex).setValue(number2);
    extensionField(phoneNumberIndex).setValue(extension2);
  }

  @SuppressWarnings("unchecked")
  private ComboBox<PhoneNumberType> typeField(int index) {
    FormLayout layout = (FormLayout) design.phoneNumbersLayout.getComponent(index);
    return (ComboBox<PhoneNumberType>) layout.getComponent(0);
  }

  private TextField numberField(int index) {
    FormLayout layout = (FormLayout) design.phoneNumbersLayout.getComponent(index);
    return (TextField) layout.getComponent(1);
  }

  private TextField extensionField(int index) {
    FormLayout layout = (FormLayout) design.phoneNumbersLayout.getComponent(index);
    return (TextField) layout.getComponent(2);
  }

  private Button removePhoneNumberButton(int index) {
    FormLayout layout = (FormLayout) design.phoneNumbersLayout.getComponent(index);
    return (Button) layout.getComponent(3);
  }

  private String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    assertTrue(design.userPanel.getStyleName().contains(USER));
    assertTrue(design.emailField.getStyleName().contains(EMAIL));
    assertTrue(design.nameField.getStyleName().contains(NAME));
    assertTrue(design.passwordField.getStyleName().contains(PASSWORD));
    assertTrue(design.confirmPasswordField.getStyleName().contains(CONFIRM_PASSWORD));
    assertTrue(design.laboratoryPanel.getStyleName().contains(LABORATORY));
    assertTrue(design.newLaboratoryField.getStyleName().contains(NEW_LABORATORY));
    assertTrue(design.managerField.getStyleName().contains(MANAGER));
    assertTrue(design.organizationField.getStyleName().contains(LABORATORY_ORGANIZATION));
    assertTrue(
        design.laboratoryNameField.getStyleName().contains(styleName(LABORATORY, LABORATORY_NAME)));
    assertTrue(design.addressPanel.getStyleName().contains(ADDRESS));
    assertTrue(design.addressLineField.getStyleName().contains(ADDRESS_LINE));
    assertTrue(design.townField.getStyleName().contains(TOWN));
    assertTrue(design.stateField.getStyleName().contains(STATE));
    assertTrue(design.countryField.getStyleName().contains(COUNTRY));
    assertTrue(design.postalCodeField.getStyleName().contains(POSTAL_CODE));
    assertTrue(design.clearAddressButton.getStyleName().contains(CLEAR_ADDRESS));
    assertTrue(design.phoneNumbersPanel.getStyleName().contains(PHONE_NUMBERS));
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).getStyleName().contains(PHONE_TYPE));
    assertTrue(numberField(0).getStyleName().contains(PHONE_NUMBER));
    assertTrue(extensionField(0).getStyleName().contains(PHONE_EXTENSION));
    assertTrue(removePhoneNumberButton(0).getStyleName().contains(REMOVE_PHONE));
    assertTrue(design.addPhoneNumberButton.getStyleName().contains(ADD_PHONE));
    assertTrue(design.registerWarningLabel.getStyleName().contains(REGISTER_WARNING));
    assertTrue(design.registerWarningLabel.getStyleName().contains(LABEL_WARNING));
    assertTrue(design.saveButton.getStyleName().contains(SAVE));
    assertTrue(design.saveButton.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(USER), design.userPanel.getCaption());
    assertEquals(resources.message(EMAIL), design.emailField.getCaption());
    assertEquals(resources.message(property(EMAIL, PLACEHOLDER)),
        design.emailField.getPlaceholder());
    assertEquals(resources.message(NAME), design.nameField.getCaption());
    assertEquals(resources.message(property(NAME, PLACEHOLDER)), design.nameField.getPlaceholder());
    assertEquals(resources.message(PASSWORD), design.passwordField.getCaption());
    assertEquals(resources.message(CONFIRM_PASSWORD), design.confirmPasswordField.getCaption());
    assertEquals(resources.message(NEW_LABORATORY), design.newLaboratoryField.getCaption());
    assertEquals(resources.message(MANAGER), design.managerField.getCaption());
    assertEquals(resources.message(property(MANAGER, PLACEHOLDER)),
        design.managerField.getPlaceholder());
    assertEquals(resources.message(property(LABORATORY, LABORATORY_ORGANIZATION)),
        design.organizationField.getCaption());
    assertEquals(resources.message(property(LABORATORY, LABORATORY_ORGANIZATION, PLACEHOLDER)),
        design.organizationField.getPlaceholder());
    assertEquals(resources.message(property(LABORATORY, LABORATORY_NAME)),
        design.laboratoryNameField.getCaption());
    assertEquals(resources.message(property(LABORATORY, LABORATORY_NAME, PLACEHOLDER)),
        design.laboratoryNameField.getPlaceholder());
    assertEquals(resources.message(ADDRESS), design.addressPanel.getCaption());
    assertEquals(resources.message(property(ADDRESS, ADDRESS_LINE)),
        design.addressLineField.getCaption());
    assertEquals(resources.message(property(ADDRESS, ADDRESS_LINE, PLACEHOLDER)),
        design.addressLineField.getPlaceholder());
    assertEquals(resources.message(property(ADDRESS, TOWN)), design.townField.getCaption());
    assertEquals(resources.message(property(ADDRESS, TOWN, PLACEHOLDER)),
        design.townField.getPlaceholder());
    assertEquals(resources.message(property(ADDRESS, STATE)), design.stateField.getCaption());
    assertEquals(resources.message(property(ADDRESS, STATE, PLACEHOLDER)),
        design.stateField.getPlaceholder());
    assertEquals(resources.message(property(ADDRESS, COUNTRY)), design.countryField.getCaption());
    assertEquals(resources.message(property(ADDRESS, COUNTRY, PLACEHOLDER)),
        design.countryField.getPlaceholder());
    assertEquals(resources.message(property(ADDRESS, POSTAL_CODE)),
        design.postalCodeField.getCaption());
    assertEquals(resources.message(property(ADDRESS, POSTAL_CODE, PLACEHOLDER)),
        design.postalCodeField.getPlaceholder());
    assertEquals(resources.message(CLEAR_ADDRESS), design.clearAddressButton.getCaption());
    assertEquals(resources.message(PHONE_NUMBERS), design.phoneNumbersPanel.getCaption());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertEquals(resources.message(property(PHONE, PHONE_TYPE)), typeField(0).getCaption());
    assertEquals(resources.message(property(PHONE, PHONE_NUMBER)), numberField(0).getCaption());
    assertEquals(resources.message(property(PHONE, PHONE_NUMBER, PLACEHOLDER)),
        numberField(0).getPlaceholder());
    assertEquals(resources.message(property(PHONE, PHONE_EXTENSION)),
        extensionField(0).getCaption());
    assertEquals(resources.message(property(PHONE, PHONE_EXTENSION, PLACEHOLDER)),
        extensionField(0).getPlaceholder());
    assertEquals(resources.message(REMOVE_PHONE), removePhoneNumberButton(0).getCaption());
    assertEquals(resources.message(ADD_PHONE), design.addPhoneNumberButton.getCaption());
    assertEquals(resources.message(REGISTER_WARNING), design.registerWarningLabel.getValue());
    assertEquals(resources.message(SAVE), design.saveButton.getCaption());
  }

  @Test
  public void required_NewUser() {
    assertTrue(design.emailField.isRequiredIndicatorVisible());
    assertTrue(design.nameField.isRequiredIndicatorVisible());
    assertTrue(design.passwordField.isRequiredIndicatorVisible());
    assertTrue(design.confirmPasswordField.isRequiredIndicatorVisible());
    assertTrue(design.managerField.isRequiredIndicatorVisible());
    assertTrue(design.organizationField.isRequiredIndicatorVisible());
    assertTrue(design.laboratoryNameField.isRequiredIndicatorVisible());
    assertTrue(design.addressLineField.isRequiredIndicatorVisible());
    assertTrue(design.townField.isRequiredIndicatorVisible());
    assertTrue(design.stateField.isRequiredIndicatorVisible());
    assertTrue(design.countryField.isRequiredIndicatorVisible());
    assertTrue(design.postalCodeField.isRequiredIndicatorVisible());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isRequiredIndicatorVisible());
    assertTrue(numberField(0).isRequiredIndicatorVisible());
    assertFalse(extensionField(0).isRequiredIndicatorVisible());
  }

  @Test
  public void required_ExistingUser() {
    presenter.setValue(user);

    assertTrue(design.emailField.isRequiredIndicatorVisible());
    assertTrue(design.nameField.isRequiredIndicatorVisible());
    assertFalse(design.passwordField.isRequiredIndicatorVisible());
    assertFalse(design.confirmPasswordField.isRequiredIndicatorVisible());
    assertFalse(design.managerField.isRequiredIndicatorVisible());
    assertFalse(design.organizationField.isRequiredIndicatorVisible());
    assertFalse(design.laboratoryNameField.isRequiredIndicatorVisible());
    assertTrue(design.addressLineField.isRequiredIndicatorVisible());
    assertTrue(design.townField.isRequiredIndicatorVisible());
    assertTrue(design.stateField.isRequiredIndicatorVisible());
    assertTrue(design.countryField.isRequiredIndicatorVisible());
    assertTrue(design.postalCodeField.isRequiredIndicatorVisible());
    addPhoneNumber();
    assertTrue(typeField(0).isRequiredIndicatorVisible());
    assertTrue(numberField(0).isRequiredIndicatorVisible());
    assertFalse(extensionField(0).isRequiredIndicatorVisible());
  }

  @Test
  public void readOnly_True_NewUser() {
    presenter.setReadOnly(true);
    assertTrue(design.emailField.isReadOnly());
    assertTrue(design.nameField.isReadOnly());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertTrue(design.addressLineField.isReadOnly());
    assertTrue(design.townField.isReadOnly());
    assertTrue(design.stateField.isReadOnly());
    assertTrue(design.countryField.isReadOnly());
    assertTrue(design.postalCodeField.isReadOnly());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_True_NewAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(null);
    presenter.setReadOnly(true);

    assertTrue(design.emailField.isReadOnly());
    assertTrue(design.nameField.isReadOnly());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertTrue(design.addressLineField.isReadOnly());
    assertTrue(design.townField.isReadOnly());
    assertTrue(design.stateField.isReadOnly());
    assertTrue(design.countryField.isReadOnly());
    assertTrue(design.postalCodeField.isReadOnly());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_True_ExistsUser() {
    presenter.setValue(user);
    presenter.setReadOnly(true);

    assertTrue(design.emailField.isReadOnly());
    assertTrue(design.nameField.isReadOnly());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertTrue(design.addressLineField.isReadOnly());
    assertTrue(design.townField.isReadOnly());
    assertTrue(design.stateField.isReadOnly());
    assertTrue(design.countryField.isReadOnly());
    assertTrue(design.postalCodeField.isReadOnly());
    addPhoneNumber();
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_True_ExistsManagerUser() {
    when(authorizationService.hasRole(UserRole.MANAGER)).thenReturn(true);
    presenter.setValue(user);
    presenter.setReadOnly(true);

    assertTrue(design.emailField.isReadOnly());
    assertTrue(design.nameField.isReadOnly());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertTrue(design.addressLineField.isReadOnly());
    assertTrue(design.townField.isReadOnly());
    assertTrue(design.stateField.isReadOnly());
    assertTrue(design.countryField.isReadOnly());
    assertTrue(design.postalCodeField.isReadOnly());
    addPhoneNumber();
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_True_ExistsAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(user);
    presenter.setReadOnly(true);

    assertTrue(design.emailField.isReadOnly());
    assertTrue(design.nameField.isReadOnly());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertTrue(design.addressLineField.isReadOnly());
    assertTrue(design.townField.isReadOnly());
    assertTrue(design.stateField.isReadOnly());
    assertTrue(design.countryField.isReadOnly());
    assertTrue(design.postalCodeField.isReadOnly());
    addPhoneNumber();
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_False_NewUser() {
    assertFalse(design.emailField.isReadOnly());
    assertFalse(design.nameField.isReadOnly());
    assertTrue(design.passwordField.isVisible());
    assertFalse(design.passwordField.isReadOnly());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.confirmPasswordField.isReadOnly());
    assertFalse(design.newLaboratoryField.isReadOnly());
    assertFalse(design.managerField.isReadOnly());
    assertFalse(design.organizationField.isReadOnly());
    assertFalse(design.laboratoryNameField.isReadOnly());
    assertFalse(design.addressLineField.isReadOnly());
    assertFalse(design.townField.isReadOnly());
    assertFalse(design.stateField.isReadOnly());
    assertFalse(design.countryField.isReadOnly());
    assertFalse(design.postalCodeField.isReadOnly());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertFalse(typeField(0).isReadOnly());
    assertFalse(numberField(0).isReadOnly());
    assertFalse(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_False_NewAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(null);

    assertFalse(design.emailField.isReadOnly());
    assertFalse(design.nameField.isReadOnly());
    assertTrue(design.passwordField.isVisible());
    assertFalse(design.passwordField.isReadOnly());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.confirmPasswordField.isReadOnly());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertFalse(design.addressLineField.isReadOnly());
    assertFalse(design.townField.isReadOnly());
    assertFalse(design.stateField.isReadOnly());
    assertFalse(design.countryField.isReadOnly());
    assertFalse(design.postalCodeField.isReadOnly());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertFalse(typeField(0).isReadOnly());
    assertFalse(numberField(0).isReadOnly());
    assertFalse(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_False_ExistsUser() {
    presenter.setValue(user);

    assertFalse(design.emailField.isReadOnly());
    assertFalse(design.nameField.isReadOnly());
    assertTrue(design.passwordField.isVisible());
    assertFalse(design.passwordField.isReadOnly());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.confirmPasswordField.isReadOnly());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertFalse(design.addressLineField.isReadOnly());
    assertFalse(design.townField.isReadOnly());
    assertFalse(design.stateField.isReadOnly());
    assertFalse(design.countryField.isReadOnly());
    assertFalse(design.postalCodeField.isReadOnly());
    addPhoneNumber();
    assertFalse(typeField(0).isReadOnly());
    assertFalse(numberField(0).isReadOnly());
    assertFalse(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_False_ExistsManagerUser() {
    when(authorizationService.hasRole(UserRole.MANAGER)).thenReturn(true);
    presenter.setValue(user);

    assertFalse(design.emailField.isReadOnly());
    assertFalse(design.nameField.isReadOnly());
    assertTrue(design.passwordField.isVisible());
    assertFalse(design.passwordField.isReadOnly());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.confirmPasswordField.isReadOnly());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertFalse(design.organizationField.isReadOnly());
    assertFalse(design.laboratoryNameField.isReadOnly());
    assertFalse(design.addressLineField.isReadOnly());
    assertFalse(design.townField.isReadOnly());
    assertFalse(design.stateField.isReadOnly());
    assertFalse(design.countryField.isReadOnly());
    assertFalse(design.postalCodeField.isReadOnly());
    addPhoneNumber();
    assertFalse(typeField(0).isReadOnly());
    assertFalse(numberField(0).isReadOnly());
    assertFalse(extensionField(0).isReadOnly());
  }

  @Test
  public void readOnly_False_ExistsAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(user);

    assertFalse(design.emailField.isReadOnly());
    assertFalse(design.nameField.isReadOnly());
    assertTrue(design.passwordField.isVisible());
    assertFalse(design.passwordField.isReadOnly());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.confirmPasswordField.isReadOnly());
    assertTrue(design.newLaboratoryField.isReadOnly());
    assertTrue(design.managerField.isReadOnly());
    assertTrue(design.organizationField.isReadOnly());
    assertTrue(design.laboratoryNameField.isReadOnly());
    assertFalse(design.addressLineField.isReadOnly());
    assertFalse(design.townField.isReadOnly());
    assertFalse(design.stateField.isReadOnly());
    assertFalse(design.countryField.isReadOnly());
    assertFalse(design.postalCodeField.isReadOnly());
    addPhoneNumber();
    assertFalse(typeField(0).isReadOnly());
    assertFalse(numberField(0).isReadOnly());
    assertFalse(extensionField(0).isReadOnly());
  }

  @Test
  public void visible_ReadOnly_NewUser() {
    presenter.setReadOnly(true);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertFalse(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertFalse(removePhoneNumberButton(0).isVisible());
    assertFalse(design.addPhoneNumberButton.isVisible());
    assertFalse(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void visible_ReadOnly_NewAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(null);
    presenter.setReadOnly(true);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertFalse(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertFalse(removePhoneNumberButton(0).isVisible());
    assertFalse(design.addPhoneNumberButton.isVisible());
    assertFalse(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void visible_ReadOnly_ExistsUser() {
    presenter.setValue(user);
    presenter.setReadOnly(true);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertFalse(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    addPhoneNumber();
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertFalse(removePhoneNumberButton(0).isVisible());
    assertFalse(design.addPhoneNumberButton.isVisible());
    assertFalse(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void visible_ReadOnly_ExistsAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(user);
    presenter.setReadOnly(true);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertFalse(design.passwordField.isVisible());
    assertFalse(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertFalse(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    addPhoneNumber();
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertFalse(removePhoneNumberButton(0).isVisible());
    assertFalse(design.addPhoneNumberButton.isVisible());
    assertFalse(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void visible_NewUser() {
    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertTrue(design.passwordField.isVisible());
    assertTrue(design.confirmPasswordField.isVisible());
    assertTrue(design.newLaboratoryField.isVisible());
    assertTrue(design.managerField.isVisible());
    assertFalse(design.organizationField.isVisible());
    assertFalse(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertTrue(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertTrue(removePhoneNumberButton(0).isVisible());
    assertTrue(design.addPhoneNumberButton.isVisible());
    assertTrue(design.saveLayout.isVisible());
    assertTrue(design.registerWarningLabel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void visible_NewAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(null);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertTrue(design.passwordField.isVisible());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertTrue(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    assertEquals(1, design.phoneNumbersLayout.getComponentCount());
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertTrue(removePhoneNumberButton(0).isVisible());
    assertTrue(design.addPhoneNumberButton.isVisible());
    assertTrue(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void visible_NewUser_NewLaboratory() {
    design.newLaboratoryField.setValue(true);
    assertTrue(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
  }

  @Test
  public void visible_ExistsUser() {
    presenter.setValue(user);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertTrue(design.passwordField.isVisible());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertTrue(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    addPhoneNumber();
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertTrue(removePhoneNumberButton(0).isVisible());
    assertTrue(design.addPhoneNumberButton.isVisible());
    assertTrue(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void visible_ExistsAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(user);

    assertTrue(design.userPanel.isVisible());
    assertTrue(design.emailField.isVisible());
    assertTrue(design.nameField.isVisible());
    assertTrue(design.passwordField.isVisible());
    assertTrue(design.confirmPasswordField.isVisible());
    assertFalse(design.newLaboratoryField.isVisible());
    assertFalse(design.managerField.isVisible());
    assertTrue(design.organizationField.isVisible());
    assertTrue(design.laboratoryNameField.isVisible());
    assertTrue(design.addressPanel.isVisible());
    assertTrue(design.addressLineField.isVisible());
    assertTrue(design.townField.isVisible());
    assertTrue(design.stateField.isVisible());
    assertTrue(design.countryField.isVisible());
    assertTrue(design.postalCodeField.isVisible());
    assertTrue(design.clearAddressButton.isVisible());
    assertTrue(design.phoneNumbersPanel.isVisible());
    addPhoneNumber();
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertTrue(removePhoneNumberButton(0).isVisible());
    assertTrue(design.addPhoneNumberButton.isVisible());
    assertTrue(design.saveLayout.isVisible());
    assertFalse(design.registerWarningLabel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void defaultLaboratory_NewUser() {
    assertTrue(design.organizationField.getValue() == null
        || design.organizationField.getValue().isEmpty());
    assertTrue(design.laboratoryNameField.getValue() == null
        || design.laboratoryNameField.getValue().isEmpty());
  }

  @Test
  public void defaultLaboratory_NewAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(null);

    assertEquals(currentUser.getLaboratory().getOrganization(),
        design.organizationField.getValue());
    assertEquals(currentUser.getLaboratory().getName(), design.laboratoryNameField.getValue());
  }

  @Test
  public void defaultLaboratory_ExistingUser() {
    presenter.setValue(user);

    assertEquals(user.getLaboratory().getOrganization(), design.organizationField.getValue());
    assertEquals(user.getLaboratory().getName(), design.laboratoryNameField.getValue());
  }

  @Test
  public void defaultLaboratory_ExistingAdminUser() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(user);

    assertEquals(user.getLaboratory().getOrganization(), design.organizationField.getValue());
    assertEquals(user.getLaboratory().getName(), design.laboratoryNameField.getValue());
  }

  @Test
  public void defaultAddress() {
    assertEquals(defaultAddressLine, design.addressLineField.getValue());
    assertEquals(defaultTown, design.townField.getValue());
    assertEquals(defaultState, design.stateField.getValue());
    assertEquals(defaultCountry, design.countryField.getValue());
    assertEquals(defaultPostalCode, design.postalCodeField.getValue());
  }

  @Test
  public void clearAddress() {
    design.clearAddressButton.click();

    assertTrue(design.addressLineField.getValue().isEmpty());
    assertTrue(design.townField.getValue().isEmpty());
    assertTrue(design.stateField.getValue().isEmpty());
    assertTrue(design.countryField.getValue().isEmpty());
    assertTrue(design.postalCodeField.getValue().isEmpty());
  }

  @Test
  public void defaultPhoneType() {
    assertEquals(PhoneNumberType.WORK, typeField(0).getValue());
  }

  @Test
  public void addPhoneNumber_Button() {
    design.addPhoneNumberButton.click();
    assertEquals(2, design.phoneNumbersLayout.getComponentCount());
  }

  @Test
  public void removePhoneNumber() {
    removePhoneNumberButton(0).click();
    assertEquals(0, design.phoneNumbersLayout.getComponentCount());
  }

  @Test
  public void save_Email_Empty() {
    setFields();
    design.emailField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Email_Invalid() {
    setFields();
    design.emailField.setValue("abc");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_EMAIL)),
        design.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Email_AlreadyExists_NewUser() {
    when(userService.exists(any())).thenReturn(true);
    setFields();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        design.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Email_AlreadyExists_ExistingUser() {
    Long userId = user.getId();
    presenter.setValue(user);
    User databaseUser = new User(userId);
    databaseUser.setEmail("other@email.com");
    when(userService.exists(any())).thenReturn(true);
    when(userService.get(any(Long.class))).thenReturn(databaseUser);
    setFields();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        design.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService, atLeastOnce()).get(userId);
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Email_AlreadyExists_ExistingUser_DatabaseEmail() throws Throwable {
    Long userId = user.getId();
    presenter.setValue(user);
    User databaseUser = new User(userId);
    databaseUser.setEmail(email);
    when(userService.exists(any())).thenReturn(true);
    when(userService.get(any(Long.class))).thenReturn(databaseUser);
    setFields();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService, atLeastOnce()).get(userId);
    verify(userService).update(any(), any());
  }

  @Test
  public void save_Name_Empty() throws Throwable {
    setFields();
    design.nameField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Password_Empty_NewUser() throws Throwable {
    setFields();
    design.passwordField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_ConfirmPassword_Empty_NewUser() throws Throwable {
    setFields();
    design.confirmPasswordField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.confirmPasswordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Passwords_Empty_ExistingUser() throws Throwable {
    presenter.setValue(user);
    setFields();
    design.passwordField.setValue("");
    design.confirmPasswordField.setValue("");

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(any(), any());
  }

  @Test
  public void save_Passwords_Match() throws Throwable {
    setFields();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).register(any(), any(), any(), any());
  }

  @Test
  public void save_Passwords_DontMatch_ConfirmPasswordChanged() {
    setFields();
    design.confirmPasswordField.setValue("password2");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(PASSWORD, "notMatch"))),
        design.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Passwords_DontMatch_PasswordChanged() {
    setFields();
    design.passwordField.setValue("password2");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(PASSWORD, "notMatch"))),
        design.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Manager_Empty() {
    setFields();
    design.managerField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.managerField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Manager_Empty_NewLaboratory() {
    setFields();
    design.newLaboratoryField.setValue(true);
    design.managerField.setValue("");

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(userService).register(any(), any(), any(), any());
  }

  @Test
  public void save_Manager_Invalid() {
    setFields();
    design.managerField.setValue("abc");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_EMAIL)),
        design.managerField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Manager_NotManager() {
    setFields();
    when(userService.isManager(any())).thenReturn(false);
    design.managerField.setValue("not.manager@ircm.qc.ca");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(MANAGER, "notExists"))),
        design.managerField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Organization_Empty() {
    setFields();
    design.newLaboratoryField.setValue(true);
    design.organizationField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.organizationField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Organization_Empty_ExistingLaboratory() {
    setFields();
    design.organizationField.setValue("");

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(userService).register(any(), any(), any(), any());
  }

  @Test
  public void save_LaboratoryName_Empty() {
    setFields();
    design.newLaboratoryField.setValue(true);
    design.laboratoryNameField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.laboratoryNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_LaboratoryName_Empty_ExistingLaboratory() {
    setFields();
    design.laboratoryNameField.setValue("");

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(userService).register(any(), any(), any(), any());
  }

  @Test
  public void save_AddressLine_Empty() {
    setFields();
    design.addressLineField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.addressLineField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Town_Empty() {
    setFields();
    design.townField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.townField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_State_Empty() {
    setFields();
    design.stateField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.stateField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Country_Empty() {
    setFields();
    design.countryField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.countryField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_PostalCode_Empty() {
    setFields();
    design.postalCodeField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.postalCodeField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_PhoneNumberType_Empty_1() {
    setFields();
    typeField(0).setValue(null);

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        typeField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Number_Empty_1() {
    setFields();
    numberField(0).setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        numberField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Number_Invalid_1() {
    setFields();
    numberField(0).setValue("123-abc");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(PHONE, PHONE_NUMBER, "invalid"))),
        numberField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Extension_Invalid_1() {
    setFields();
    extensionField(0).setValue("1-a");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(PHONE, PHONE_EXTENSION, "invalid"))),
        extensionField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_PhoneNumberType_Empty_2() {
    setFields();
    typeField(1).setValue(null);

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        typeField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Number_Empty_2() {
    setFields();
    numberField(1).setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        numberField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Number_Invalid_2() {
    setFields();
    numberField(1).setValue("123-abc");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(PHONE, PHONE_NUMBER, "invalid"))),
        numberField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Extension_Invalid_2() {
    setFields();
    extensionField(1).setValue("1-a");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(property(PHONE, PHONE_EXTENSION, "invalid"))),
        extensionField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).register(any(), any(), any(), any());
  }

  @Test
  public void save_Insert() {
    setFields();
    String validationUrl = "validationUrl";
    when(view.getUrl(any())).thenReturn(validationUrl);

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService).register(userCaptor.capture(), eq(password), userCaptor.capture(),
        registerUserWebContextCaptor.capture());
    User user = userCaptor.getAllValues().get(0);
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    Address address = user.getAddress();
    assertEquals(addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertEquals(2, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(type1, phoneNumber.getType());
    assertEquals(number1, phoneNumber.getNumber());
    assertEquals(extension1, phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    assertEquals(type2, phoneNumber.getType());
    assertEquals(number2, phoneNumber.getNumber());
    assertEquals(extension2, phoneNumber.getExtension());
    User manager = userCaptor.getValue();
    assertEquals(this.manager, manager.getEmail());
    RegisterUserWebContext registerUserWebContext = registerUserWebContextCaptor.getValue();
    assertEquals(validationUrl, registerUserWebContext.getValidateUserUrl(locale));
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message("save.done", email), stringCaptor.getValue());
    verify(view).fireSaveEvent(user);
  }

  @Test
  public void save_Insert_NewLaboratory() {
    setFields();
    design.newLaboratoryField.setValue(true);
    String validationUrl = "validationUrl";
    when(view.getUrl(any())).thenReturn(validationUrl);

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService).register(userCaptor.capture(), eq(password), eq(null),
        registerUserWebContextCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    Laboratory laboratory = user.getLaboratory();
    assertEquals(organization, laboratory.getOrganization());
    assertEquals(laboratoryName, laboratory.getName());
    Address address = user.getAddress();
    assertEquals(addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertEquals(2, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(type1, phoneNumber.getType());
    assertEquals(number1, phoneNumber.getNumber());
    assertEquals(extension1, phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    assertEquals(type2, phoneNumber.getType());
    assertEquals(number2, phoneNumber.getNumber());
    assertEquals(extension2, phoneNumber.getExtension());
    RegisterUserWebContext registerUserWebContext = registerUserWebContextCaptor.getValue();
    assertEquals(validationUrl, registerUserWebContext.getValidateUserUrl(locale));
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message("save.done", email), stringCaptor.getValue());
    verify(view).fireSaveEvent(user);
  }

  @Test
  public void save_InsertAdmin() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(null);
    setFields();
    String validationUrl = "validationUrl";
    when(view.getUrl(any())).thenReturn(validationUrl);

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService).register(userCaptor.capture(), eq(password), any(), any());
    User user = userCaptor.getAllValues().get(0);
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(true, user.isAdmin());
    Address address = user.getAddress();
    assertEquals(addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertEquals(2, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(type1, phoneNumber.getType());
    assertEquals(number1, phoneNumber.getNumber());
    assertEquals(extension1, phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    assertEquals(type2, phoneNumber.getType());
    assertEquals(number2, phoneNumber.getNumber());
    assertEquals(extension2, phoneNumber.getExtension());
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message("save.done", email), stringCaptor.getValue());
    verify(view).fireSaveEvent(user);
  }

  @Test
  public void save_Update() {
    presenter.setValue(user);
    setFields();
    final int expectedPhoneNumberSize = this.user.getPhoneNumbers().size() + 1;

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService).update(userCaptor.capture(), eq(password));
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    Address address = user.getAddress();
    assertEquals(addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertEquals(expectedPhoneNumberSize, user.getPhoneNumbers().size());
    for (int i = 0; i < expectedPhoneNumberSize - 2; i++) {
      PhoneNumber expected = this.user.getPhoneNumbers().get(i);
      PhoneNumber phoneNumber = user.getPhoneNumbers().get(i);
      assertEquals(expected.getType(), phoneNumber.getType());
      assertEquals(expected.getNumber(), phoneNumber.getNumber());
      assertEquals(expected.getExtension(), phoneNumber.getExtension());
    }
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(expectedPhoneNumberSize - 2);
    assertEquals(type1, phoneNumber.getType());
    assertEquals(number1, phoneNumber.getNumber());
    assertEquals(extension1, phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(expectedPhoneNumberSize - 1);
    assertEquals(type2, phoneNumber.getType());
    assertEquals(number2, phoneNumber.getNumber());
    assertEquals(extension2, phoneNumber.getExtension());
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message("save.done", email), stringCaptor.getValue());
    verify(view).fireSaveEvent(user);
  }

  @Test
  public void save_UpdateAdmin() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.setValue(user);
    setFields();
    final int expectedPhoneNumberSize = this.user.getPhoneNumbers().size() + 1;

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService).update(userCaptor.capture(), eq(password));
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    Address address = user.getAddress();
    assertEquals(addressLine, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertEquals(expectedPhoneNumberSize, user.getPhoneNumbers().size());
    for (int i = 0; i < expectedPhoneNumberSize - 2; i++) {
      PhoneNumber expected = this.user.getPhoneNumbers().get(i);
      PhoneNumber phoneNumber = user.getPhoneNumbers().get(i);
      assertEquals(expected.getType(), phoneNumber.getType());
      assertEquals(expected.getNumber(), phoneNumber.getNumber());
      assertEquals(expected.getExtension(), phoneNumber.getExtension());
    }
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(expectedPhoneNumberSize - 2);
    assertEquals(type1, phoneNumber.getType());
    assertEquals(number1, phoneNumber.getNumber());
    assertEquals(extension1, phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(expectedPhoneNumberSize - 1);
    assertEquals(type2, phoneNumber.getType());
    assertEquals(number2, phoneNumber.getNumber());
    assertEquals(extension2, phoneNumber.getExtension());
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message("save.done", email), stringCaptor.getValue());
    verify(view).fireSaveEvent(user);
  }

  @Test
  public void save_Update_KeepPassword() {
    presenter.setValue(user);
    setFields();
    design.passwordField.setValue("");
    design.confirmPasswordField.setValue("");

    design.saveButton.click();

    verify(userService).update(userCaptor.capture(), eq(null));
  }

  @Test
  public void save_Update_RemovePhoneNumber() {
    PhoneNumber secondPhoneNumber = new PhoneNumber();
    user.getPhoneNumbers().add(secondPhoneNumber);
    presenter.setValue(user);
    setFields();
    removePhoneNumberButton(0).click();

    design.saveButton.click();

    verify(userService).update(userCaptor.capture(), eq(password));
    User user = userCaptor.getValue();
    assertEquals(2, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(type1, phoneNumber.getType());
    assertEquals(number1, phoneNumber.getNumber());
    assertEquals(extension1, phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    assertEquals(type2, phoneNumber.getType());
    assertEquals(number2, phoneNumber.getNumber());
    assertEquals(extension2, phoneNumber.getExtension());
  }
}
