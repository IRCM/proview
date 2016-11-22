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

import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_COUNTRY;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_LINE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_POSTAL_CODE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_STATE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_TOWN;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADD_PHONE_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.CLEAR_ADDRESS;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.LABORATORY;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.LABORATORY_ORGANIZATION;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.NAME;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PASSWORD;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBERS;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER_EXTENSION;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER_TYPE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.REMOVE_PHONE_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.SAVE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.USER;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
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

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class NewUserFormPresenterTest {
  private NewUserFormPresenter presenter;
  @Mock
  private NewUserForm view;
  @Mock
  private UserService userService;
  @Mock
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Component> componentCaptor;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(NewUserForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private User user;
  private String email = "unit.test@ircm.qc.ca";
  private String name = "Unit Test";
  private String password = "unittestpassword";
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
    presenter = new NewUserFormPresenter(userService, defaultAddressConfiguration);
    view.userPanel = new Panel();
    view.emailField = new TextField();
    view.nameField = new TextField();
    view.passwordField = new PasswordField();
    view.confirmPasswordField = new PasswordField();
    view.laboratoryPanel = new Panel();
    view.organizationField = new TextField();
    view.laboratoryNameField = new TextField();
    view.addressPanel = new Panel();
    view.addressLineField = new TextField();
    view.townField = new TextField();
    view.stateField = new TextField();
    view.countryField = new TextField();
    view.postalCodeField = new TextField();
    view.clearAddressButton = new Button();
    view.phoneNumbersPanel = new Panel();
    view.phoneNumbersLayout = new VerticalLayout();
    view.phoneNumberTypeField = new ComboBox();
    view.numberField = new TextField();
    view.extensionField = new TextField();
    view.removePhoneNumberButton = new Button();
    view.addPhoneNumberButton = new Button();
    view.buttonsLayout = new HorizontalLayout();
    view.saveButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(defaultAddressConfiguration.getAddress()).thenReturn(defaultAddressLine);
    when(defaultAddressConfiguration.getTown()).thenReturn(defaultTown);
    when(defaultAddressConfiguration.getState()).thenReturn(defaultState);
    when(defaultAddressConfiguration.getCountry()).thenReturn(defaultCountry);
    when(defaultAddressConfiguration.getPostalCode()).thenReturn(defaultPostalCode);
    presenter.init(view);
    user = entityManager.find(User.class, 10L);
  }

  private void addFirstPhoneNumber() {
    view.addPhoneNumberButton.click();
  }

  private void setFields() {
    view.emailField.setValue(email);
    view.nameField.setValue(name);
    view.passwordField.setValue(password);
    view.confirmPasswordField.setValue(password);
    view.addressLineField.setValue(addressLine);
    view.townField.setValue(town);
    view.stateField.setValue(state);
    view.countryField.setValue(country);
    view.postalCodeField.setValue(postalCode);
    int phoneNumberIndex = view.phoneNumbersLayout.getComponentCount() - 1;
    view.addPhoneNumberButton.click();
    phoneNumberIndex++;
    typeField(phoneNumberIndex).setValue(type1);
    numberField(phoneNumberIndex).setValue(number1);
    extensionField(phoneNumberIndex).setValue(extension1);
    view.addPhoneNumberButton.click();
    phoneNumberIndex++;
    typeField(phoneNumberIndex).setValue(type2);
    numberField(phoneNumberIndex).setValue(number2);
    extensionField(phoneNumberIndex).setValue(extension2);
  }

  private ComboBox typeField(int index) {
    FormLayout layout = (FormLayout) view.phoneNumbersLayout.getComponent(index);
    return (ComboBox) layout.getComponent(0);
  }

  private TextField numberField(int index) {
    FormLayout layout = (FormLayout) view.phoneNumbersLayout.getComponent(index);
    return (TextField) layout.getComponent(1);
  }

  private TextField extensionField(int index) {
    FormLayout layout = (FormLayout) view.phoneNumbersLayout.getComponent(index);
    return (TextField) layout.getComponent(2);
  }

  private Button removePhoneNumberButton(int index) {
    FormLayout layout = (FormLayout) view.phoneNumbersLayout.getComponent(index);
    return (Button) layout.getComponent(3);
  }

  private String errorMessage(String message) {
    return new CompositeErrorMessage(new UserError(message)).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    assertTrue(view.userPanel.getStyleName().contains(USER));
    assertTrue(view.emailField.getStyleName().contains(EMAIL));
    assertTrue(view.nameField.getStyleName().contains(NAME));
    assertTrue(view.passwordField.getStyleName().contains(PASSWORD));
    assertTrue(view.confirmPasswordField.getStyleName().contains(CONFIRM_PASSWORD));
    assertTrue(view.laboratoryPanel.getStyleName().contains(LABORATORY));
    assertTrue(view.organizationField.getStyleName().contains(LABORATORY_ORGANIZATION));
    assertTrue(view.laboratoryNameField.getStyleName().contains(LABORATORY_NAME));
    assertTrue(view.addressPanel.getStyleName().contains(ADDRESS));
    assertTrue(view.addressLineField.getStyleName().contains(ADDRESS_LINE));
    assertTrue(view.townField.getStyleName().contains(ADDRESS_TOWN));
    assertTrue(view.stateField.getStyleName().contains(ADDRESS_STATE));
    assertTrue(view.countryField.getStyleName().contains(ADDRESS_COUNTRY));
    assertTrue(view.postalCodeField.getStyleName().contains(ADDRESS_POSTAL_CODE));
    assertTrue(view.clearAddressButton.getStyleName().contains(CLEAR_ADDRESS));
    assertTrue(view.phoneNumbersPanel.getStyleName().contains(PHONE_NUMBERS));
    addFirstPhoneNumber();
    assertTrue(typeField(0).getStyleName().contains(PHONE_NUMBER_TYPE));
    assertTrue(numberField(0).getStyleName().contains(PHONE_NUMBER_NUMBER));
    assertTrue(extensionField(0).getStyleName().contains(PHONE_NUMBER_EXTENSION));
    assertTrue(removePhoneNumberButton(0).getStyleName().contains(REMOVE_PHONE_NUMBER));
    assertTrue(view.addPhoneNumberButton.getStyleName().contains(ADD_PHONE_NUMBER));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(USER), view.userPanel.getCaption());
    assertEquals(resources.message(EMAIL), view.emailField.getCaption());
    assertEquals(resources.message(NAME), view.nameField.getCaption());
    assertEquals(resources.message(PASSWORD), view.passwordField.getCaption());
    assertEquals(resources.message(CONFIRM_PASSWORD), view.confirmPasswordField.getCaption());
    assertEquals(resources.message(LABORATORY + "." + LABORATORY_ORGANIZATION),
        view.organizationField.getCaption());
    assertEquals(resources.message(LABORATORY + "." + LABORATORY_NAME),
        view.laboratoryNameField.getCaption());
    assertEquals(resources.message(ADDRESS), view.addressPanel.getCaption());
    assertEquals(resources.message(ADDRESS + "." + ADDRESS_LINE),
        view.addressLineField.getCaption());
    assertEquals(resources.message(ADDRESS + "." + ADDRESS_TOWN), view.townField.getCaption());
    assertEquals(resources.message(ADDRESS + "." + ADDRESS_STATE), view.stateField.getCaption());
    assertEquals(resources.message(ADDRESS + "." + ADDRESS_COUNTRY),
        view.countryField.getCaption());
    assertEquals(resources.message(ADDRESS + "." + ADDRESS_POSTAL_CODE),
        view.postalCodeField.getCaption());
    assertEquals(resources.message(CLEAR_ADDRESS), view.clearAddressButton.getCaption());
    assertEquals(resources.message(PHONE_NUMBERS), view.phoneNumbersPanel.getCaption());
    addFirstPhoneNumber();
    assertEquals(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_TYPE),
        typeField(0).getCaption());
    assertEquals(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER),
        numberField(0).getCaption());
    assertEquals(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION),
        extensionField(0).getCaption());
    assertEquals(resources.message(REMOVE_PHONE_NUMBER), removePhoneNumberButton(0).getCaption());
    assertEquals(resources.message(ADD_PHONE_NUMBER), view.addPhoneNumberButton.getCaption());
    assertEquals(resources.message(SAVE), view.saveButton.getCaption());
  }

  @Test
  public void required_Default() {
    assertTrue(view.emailField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.emailField.getRequiredError());
    assertTrue(view.nameField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.nameField.getRequiredError());
    assertTrue(view.passwordField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.passwordField.getRequiredError());
    assertTrue(view.confirmPasswordField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.confirmPasswordField.getRequiredError());
    assertTrue(view.addressLineField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.addressLineField.getRequiredError());
    assertTrue(view.townField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.townField.getRequiredError());
    assertTrue(view.stateField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.stateField.getRequiredError());
    assertTrue(view.countryField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.countryField.getRequiredError());
    assertTrue(view.postalCodeField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.postalCodeField.getRequiredError());
    addFirstPhoneNumber();
    assertTrue(typeField(0).isRequired());
    assertEquals(generalResources.message(REQUIRED), typeField(0).getRequiredError());
    assertTrue(numberField(0).isRequired());
    assertEquals(generalResources.message(REQUIRED), numberField(0).getRequiredError());
    assertFalse(extensionField(0).isRequired());
  }

  @Test
  public void required_ExistingUser() {
    presenter.setItemDataSource(new BeanItem<>(user));

    assertTrue(view.emailField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.emailField.getRequiredError());
    assertTrue(view.nameField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.nameField.getRequiredError());
    assertFalse(view.passwordField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.passwordField.getRequiredError());
    assertFalse(view.confirmPasswordField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.confirmPasswordField.getRequiredError());
    assertTrue(view.addressLineField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.addressLineField.getRequiredError());
    assertTrue(view.townField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.townField.getRequiredError());
    assertTrue(view.stateField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.stateField.getRequiredError());
    assertTrue(view.countryField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.countryField.getRequiredError());
    assertTrue(view.postalCodeField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.postalCodeField.getRequiredError());
    addFirstPhoneNumber();
    assertTrue(typeField(0).isRequired());
    assertEquals(generalResources.message(REQUIRED), typeField(0).getRequiredError());
    assertTrue(numberField(0).isRequired());
    assertEquals(generalResources.message(REQUIRED), numberField(0).getRequiredError());
    assertFalse(extensionField(0).isRequired());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.emailField.isReadOnly());
    assertTrue(view.nameField.isReadOnly());
    assertFalse(view.passwordField.isVisible());
    assertFalse(view.confirmPasswordField.isVisible());
    assertTrue(view.addressLineField.isReadOnly());
    assertTrue(view.townField.isReadOnly());
    assertTrue(view.stateField.isReadOnly());
    assertTrue(view.countryField.isReadOnly());
    assertTrue(view.postalCodeField.isReadOnly());
    addFirstPhoneNumber();
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.emailField.isReadOnly());
    assertTrue(view.nameField.isReadOnly());
    assertFalse(view.passwordField.isVisible());
    assertFalse(view.confirmPasswordField.isVisible());
    assertTrue(view.addressLineField.isReadOnly());
    assertTrue(view.townField.isReadOnly());
    assertTrue(view.stateField.isReadOnly());
    assertTrue(view.countryField.isReadOnly());
    assertTrue(view.postalCodeField.isReadOnly());
    addFirstPhoneNumber();
    assertTrue(typeField(0).isReadOnly());
    assertTrue(numberField(0).isReadOnly());
    assertTrue(extensionField(0).isReadOnly());
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.emailField.isReadOnly());
    assertFalse(view.nameField.isReadOnly());
    assertTrue(view.passwordField.isVisible());
    assertFalse(view.passwordField.isReadOnly());
    assertTrue(view.confirmPasswordField.isVisible());
    assertFalse(view.confirmPasswordField.isReadOnly());
    assertFalse(view.addressLineField.isReadOnly());
    assertFalse(view.townField.isReadOnly());
    assertFalse(view.stateField.isReadOnly());
    assertFalse(view.countryField.isReadOnly());
    assertFalse(view.postalCodeField.isReadOnly());
    addFirstPhoneNumber();
    assertFalse(typeField(0).isReadOnly());
    assertFalse(numberField(0).isReadOnly());
    assertFalse(extensionField(0).isReadOnly());
  }

  @Test
  public void visible() {
    assertTrue(view.userPanel.isVisible());
    assertTrue(view.emailField.isVisible());
    assertTrue(view.nameField.isVisible());
    assertFalse(view.passwordField.isVisible());
    assertFalse(view.confirmPasswordField.isVisible());
    assertTrue(view.organizationField.isVisible());
    assertTrue(view.laboratoryNameField.isVisible());
    assertTrue(view.addressPanel.isVisible());
    assertTrue(view.addressLineField.isVisible());
    assertTrue(view.townField.isVisible());
    assertTrue(view.stateField.isVisible());
    assertTrue(view.countryField.isVisible());
    assertTrue(view.postalCodeField.isVisible());
    assertFalse(view.clearAddressButton.isVisible());
    assertTrue(view.phoneNumbersPanel.isVisible());
    addFirstPhoneNumber();
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertFalse(removePhoneNumberButton(0).isVisible());
    assertFalse(view.addPhoneNumberButton.isVisible());
    assertFalse(view.buttonsLayout.isVisible());
    assertFalse(view.saveButton.isVisible());
  }

  @Test
  public void visible_Editable() {
    presenter.setEditable(true);

    assertTrue(view.userPanel.isVisible());
    assertTrue(view.emailField.isVisible());
    assertTrue(view.nameField.isVisible());
    assertTrue(view.passwordField.isVisible());
    assertTrue(view.confirmPasswordField.isVisible());
    assertTrue(view.organizationField.isVisible());
    assertTrue(view.laboratoryNameField.isVisible());
    assertTrue(view.addressPanel.isVisible());
    assertTrue(view.addressLineField.isVisible());
    assertTrue(view.townField.isVisible());
    assertTrue(view.stateField.isVisible());
    assertTrue(view.countryField.isVisible());
    assertTrue(view.postalCodeField.isVisible());
    assertTrue(view.clearAddressButton.isVisible());
    assertTrue(view.phoneNumbersPanel.isVisible());
    addFirstPhoneNumber();
    assertTrue(typeField(0).isVisible());
    assertTrue(numberField(0).isVisible());
    assertTrue(extensionField(0).isVisible());
    assertTrue(removePhoneNumberButton(0).isVisible());
    assertTrue(view.addPhoneNumberButton.isVisible());
    assertTrue(view.buttonsLayout.isVisible());
    assertTrue(view.saveButton.isVisible());
  }

  @Test
  public void defaultAddress() {
    assertEquals(defaultAddressLine, view.addressLineField.getValue());
    assertEquals(defaultTown, view.townField.getValue());
    assertEquals(defaultState, view.stateField.getValue());
    assertEquals(defaultCountry, view.countryField.getValue());
    assertEquals(defaultPostalCode, view.postalCodeField.getValue());
  }

  @Test
  public void clearAddress() {
    presenter.setEditable(true);

    view.clearAddressButton.click();

    assertTrue(view.addressLineField.getValue().isEmpty());
    assertTrue(view.townField.getValue().isEmpty());
    assertTrue(view.stateField.getValue().isEmpty());
    assertTrue(view.countryField.getValue().isEmpty());
    assertTrue(view.postalCodeField.getValue().isEmpty());
  }

  @Test
  public void defaultPhoneType() {
    addFirstPhoneNumber();
    assertEquals(PhoneNumberType.WORK, typeField(0).getValue());
  }

  @Test
  public void addPhoneNumber() {
    addFirstPhoneNumber();
    assertEquals(1, view.phoneNumbersLayout.getComponentCount());
  }

  @Test
  public void removePhoneNumber() {
    addFirstPhoneNumber();
    removePhoneNumberButton(0).click();
    assertEquals(0, view.phoneNumbersLayout.getComponentCount());
  }

  @Test
  public void save_Email_Empty() {
    presenter.setEditable(true);
    setFields();
    view.emailField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Email_Invalid() {
    presenter.setEditable(true);
    setFields();
    view.emailField.setValue("abc");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_EMAIL)),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Email_AlreadyExists_NewUser() {
    when(userService.exists(any())).thenReturn(true);
    presenter.setEditable(true);
    setFields();

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Email_AlreadyExists_ExistingUser() {
    Long userId = user.getId();
    presenter.setItemDataSource(new BeanItem<>(user));
    User databaseUser = new User(userId);
    databaseUser.setEmail("other@email.com");
    when(userService.exists(any())).thenReturn(true);
    when(userService.get(any(Long.class))).thenReturn(databaseUser);
    presenter.setEditable(true);
    setFields();

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService, atLeastOnce()).get(userId);
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Email_AlreadyExists_ExistingUser_DatabaseEmail() throws Throwable {
    Long userId = user.getId();
    presenter.setItemDataSource(new BeanItem<>(user));
    User databaseUser = new User(userId);
    databaseUser.setEmail(email);
    when(userService.exists(any())).thenReturn(true);
    when(userService.get(any(Long.class))).thenReturn(databaseUser);
    presenter.setEditable(true);
    setFields();

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService, atLeastOnce()).exists(email);
    verify(userService, atLeastOnce()).get(userId);
    verify(userService).update(any(), any());
  }

  @Test
  public void save_Name_Empty() throws Throwable {
    presenter.setEditable(true);
    setFields();
    view.nameField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Password_Empty_NewUser() throws Throwable {
    presenter.setEditable(true);
    setFields();
    view.passwordField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_ConfirmPassword_Empty_NewUser() throws Throwable {
    presenter.setEditable(true);
    setFields();
    view.confirmPasswordField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.confirmPasswordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Passwords_Empty_ExistingUser() throws Throwable {
    presenter.setItemDataSource(new BeanItem<>(user));
    presenter.setEditable(true);
    setFields();
    view.passwordField.setValue("");
    view.confirmPasswordField.setValue("");

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(any(), any());
  }

  @Test
  public void save_Passwords_Match() throws Throwable {
    presenter.setEditable(true);
    setFields();

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(any(), any());
  }

  @Test
  public void save_Passwords_DontMatch_ConfirmPasswordChanged() {
    presenter.setEditable(true);
    setFields();
    view.confirmPasswordField.setValue("password2");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(PASSWORD + ".notMatch")),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Passwords_DontMatch_PasswordChanged() {
    presenter.setEditable(true);
    setFields();
    view.passwordField.setValue("password2");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(PASSWORD + ".notMatch")),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_AddressLine_Empty() {
    presenter.setEditable(true);
    setFields();
    view.addressLineField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.addressLineField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Town_Empty() {
    presenter.setEditable(true);
    setFields();
    view.townField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.townField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_State_Empty() {
    presenter.setEditable(true);
    setFields();
    view.stateField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.stateField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Country_Empty() {
    presenter.setEditable(true);
    setFields();
    view.countryField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.countryField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_PostalCode_Empty() {
    presenter.setEditable(true);
    setFields();
    view.postalCodeField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.postalCodeField.getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_PhoneNumberType_Empty_1() {
    presenter.setEditable(true);
    setFields();
    typeField(0).setValue("");

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(userCaptor.capture(), any());
    User user = userCaptor.getValue();
    assertEquals(type1, user.getPhoneNumbers().get(0).getType());
  }

  @Test
  public void save_PhoneNumberType_Invalid_1() {
    presenter.setEditable(true);
    setFields();
    typeField(0).setValue("abc");

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(userCaptor.capture(), any());
    User user = userCaptor.getValue();
    assertEquals(type1, user.getPhoneNumbers().get(0).getType());
  }

  @Test
  public void save_Number_Empty_1() {
    presenter.setEditable(true);
    setFields();
    numberField(0).setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        numberField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Number_Invalid_1() {
    presenter.setEditable(true);
    setFields();
    numberField(0).setValue("123-abc");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(
        errorMessage(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER + ".invalid")),
        numberField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Extension_Invalid_1() {
    presenter.setEditable(true);
    setFields();
    extensionField(0).setValue("1-a");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(
        errorMessage(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION + ".invalid")),
        extensionField(0).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_PhoneNumberType_Empty_2() {
    presenter.setEditable(true);
    setFields();
    typeField(1).setValue("");

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(userCaptor.capture(), any());
    User user = userCaptor.getValue();
    assertEquals(type2, user.getPhoneNumbers().get(1).getType());
  }

  @Test
  public void save_PhoneNumberType_Invalid_2() {
    presenter.setEditable(true);
    setFields();
    typeField(1).setValue("abc");

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).update(userCaptor.capture(), any());
    User user = userCaptor.getValue();
    assertEquals(type2, user.getPhoneNumbers().get(1).getType());
  }

  @Test
  public void save_Number_Empty_2() {
    presenter.setEditable(true);
    setFields();
    numberField(1).setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        numberField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Number_Invalid_2() {
    presenter.setEditable(true);
    setFields();
    numberField(1).setValue("123-abc");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(
        errorMessage(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER + ".invalid")),
        numberField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  public void save_Extension_Invalid_2() {
    presenter.setEditable(true);
    setFields();
    extensionField(1).setValue("1-a");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(
        errorMessage(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION + ".invalid")),
        extensionField(1).getErrorMessage().getFormattedHtmlMessage());
    verify(userService, never()).update(any(), any());
  }

  @Test
  @Ignore("Not supported yet")
  public void save_Insert() {
    presenter.setEditable(true);
    setFields();

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).exists(email);
    verify(userService).update(userCaptor.capture(), password);
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    // Undetermined laboratory.
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
  }

  @Test
  public void save_Update() {
    presenter.setItemDataSource(new BeanItem<>(user));
    presenter.setEditable(true);
    setFields();
    final int expectedPhoneNumberSize = this.user.getPhoneNumbers().size() + 2;

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(userService).exists(email);
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
  }

  @Test
  public void save_Update_KeepPassword() {
    presenter.setItemDataSource(new BeanItem<>(user));
    presenter.setEditable(true);
    setFields();
    view.passwordField.setValue("");
    view.confirmPasswordField.setValue("");

    view.saveButton.click();

    verify(userService).update(userCaptor.capture(), eq(null));
  }

  @Test
  public void save_Update_RemovePhoneNumber() {
    presenter.setItemDataSource(new BeanItem<>(user));
    presenter.setEditable(true);
    setFields();
    final int expectedPhoneNumberSize = this.user.getPhoneNumbers().size() + 1;
    removePhoneNumberButton(0).click();

    view.saveButton.click();

    verify(userService).update(userCaptor.capture(), eq(password));
    User user = userCaptor.getValue();
    assertEquals(expectedPhoneNumberSize, user.getPhoneNumbers().size());
    for (int i = 1; i < expectedPhoneNumberSize - 2; i++) {
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
  }
}
