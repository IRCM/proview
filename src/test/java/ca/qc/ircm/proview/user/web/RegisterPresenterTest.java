package ca.qc.ircm.proview.user.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.RegisterUserWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collection;
import java.util.Locale;

public class RegisterPresenterTest {
  @Rule
  public RuleChain rules = Rules.defaultRules(this);
  @InjectMocks
  private RegisterPresenter registerPresenter = new RegisterPresenter();
  @Mock
  private RegisterView view;
  @Mock
  private ApplicationConfiguration applicationConfiguration;
  @Mock
  private UserService userService;
  @Mock
  private VaadinUtils vaadinUtils;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<User> managerCaptor;
  @Captor
  private ArgumentCaptor<RegisterUserWebContext> registerUserWebContextCaptor;
  private Label headerLabel = new Label();
  private TextField emailField = new TextField();
  private TextField nameField = new TextField();
  private PasswordField passwordField = new PasswordField();
  private PasswordField confirmPasswordField = new PasswordField();
  private Label laboratoryHeaderLabel = new Label();
  private CheckBox newLaboratoryField = new CheckBox();
  private TextField organizationField = new TextField();
  private TextField laboratoryNameField = new TextField();
  private TextField managerEmailField = new TextField();
  private Label addressHeaderLabel = new Label();
  private TextField addressField = new TextField();
  private TextField addressSecondField = new TextField();
  private TextField townField = new TextField();
  private TextField stateField = new TextField();
  private ComboBox countryField = new ComboBox();
  private TextField postalCodeField = new TextField();
  private Button clearAddressButton = new Button();
  private Label phoneNumberHeaderLabel = new Label();
  private TextField phoneNumberField = new TextField();
  private TextField phoneExtensionField = new TextField();
  private Label registerHeaderLabel = new Label();
  private Button registerButton = new Button();
  private Label requiredLabel = new Label();
  private String[] countries = new String[] { "Canada", "USA" };
  private String defaultAddress = "110 avenue des Pins Ouest";
  private String defaultTown = "Montreal";
  private String defaultState = "Quebec";
  private String defaultPostalCode = "H2W 1R7";
  private String email = "unit.test@ircm.qc.ca";
  private String name = "Unit Test";
  private String password = "unittestpassword";
  private String managerEmail = "benoit.coulombe@ircm.qc.ca";
  private String laboratoryName = "Test lab";
  private String organization = "IRCM";
  private String address = "123 Papineau";
  private String addressSecond = "2640";
  private String town = "Laval";
  private String state = "Ontario";
  private String country = "USA";
  private String postalCode = "12345";
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
    when(applicationConfiguration.getCountries()).thenReturn(countries);
    when(applicationConfiguration.getAddress()).thenReturn(defaultAddress);
    when(applicationConfiguration.getTown()).thenReturn(defaultTown);
    when(applicationConfiguration.getState()).thenReturn(defaultState);
    when(applicationConfiguration.getPostalCode()).thenReturn(defaultPostalCode);
    when(view.getHeaderLabel()).thenReturn(headerLabel);
    when(view.getEmailField()).thenReturn(emailField);
    when(view.getNameField()).thenReturn(nameField);
    when(view.getPasswordField()).thenReturn(passwordField);
    when(view.getConfirmPasswordField()).thenReturn(confirmPasswordField);
    when(view.getLaboratoryHeaderLabel()).thenReturn(laboratoryHeaderLabel);
    when(view.getNewLaboratoryField()).thenReturn(newLaboratoryField);
    when(view.getOrganizationField()).thenReturn(organizationField);
    when(view.getLaboratoryNameField()).thenReturn(laboratoryNameField);
    when(view.getManagerEmailField()).thenReturn(managerEmailField);
    when(view.getAddressHeaderLabel()).thenReturn(addressHeaderLabel);
    when(view.getAddressField()).thenReturn(addressField);
    when(view.getAddressSecondField()).thenReturn(addressSecondField);
    when(view.getTownField()).thenReturn(townField);
    when(view.getStateField()).thenReturn(stateField);
    when(view.getCountryField()).thenReturn(countryField);
    when(view.getPostalCodeField()).thenReturn(postalCodeField);
    when(view.getClearAddressButton()).thenReturn(clearAddressButton);
    when(view.getPhoneNumberHeaderLabel()).thenReturn(phoneNumberHeaderLabel);
    when(view.getPhoneNumberField()).thenReturn(phoneNumberField);
    when(view.getPhoneExtensionField()).thenReturn(phoneExtensionField);
    when(view.getRegisterHeaderLabel()).thenReturn(registerHeaderLabel);
    when(view.getRegisterButton()).thenReturn(registerButton);
    when(view.getRequiredLabel()).thenReturn(requiredLabel);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    registerPresenter.init(view);
  }

  private void setFields() {
    emailField.focus();
    emailField.setValue(email);
    nameField.focus();
    nameField.setValue(name);
    passwordField.focus();
    passwordField.setValue(password);
    confirmPasswordField.focus();
    confirmPasswordField.setValue(password);
    managerEmailField.focus();
    managerEmailField.setValue(managerEmail);
    laboratoryNameField.focus();
    laboratoryNameField.setValue(laboratoryName);
    organizationField.focus();
    organizationField.setValue(organization);
    addressField.focus();
    addressField.setValue(address);
    addressSecondField.focus();
    addressSecondField.setValue(addressSecond);
    townField.focus();
    townField.setValue(town);
    stateField.focus();
    stateField.setValue(state);
    countryField.focus();
    countryField.setValue(country);
    postalCodeField.focus();
    postalCodeField.setValue(postalCode);
    phoneNumberField.focus();
    phoneNumberField.setValue(phoneNumber);
    phoneExtensionField.focus();
    phoneExtensionField.setValue(phoneExtension);
    registerButton.focus();
  }

  @Test
  public void countryValues() {
    ComboBox comboBox = view.getCountryField();

    Collection<?> items = comboBox.getItemIds();
    assertEquals(countries.length, items.size());
    for (String country : countries) {
      assertTrue(items.contains(country));
    }
  }

  @Test
  public void newLaboratoryVisibleFields_True() {
    newLaboratoryField.setValue(true);
    newLaboratoryField.valueChange(new ValueChangeEvent(newLaboratoryField));

    assertFalse(managerEmailField.isVisible());
    assertTrue(organizationField.isVisible());
    assertTrue(laboratoryNameField.isVisible());
  }

  @Test
  public void newLaboratoryVisibleFields_False() {
    newLaboratoryField.setValue(false);
    newLaboratoryField.valueChange(new ValueChangeEvent(newLaboratoryField));

    assertTrue(managerEmailField.isVisible());
    assertFalse(organizationField.isVisible());
    assertFalse(laboratoryNameField.isVisible());
  }

  @Test
  public void defaultAddress() {
    assertEquals(defaultAddress, addressField.getValue());
    assertEquals(defaultTown, townField.getValue());
    assertEquals(defaultState, stateField.getValue());
    assertEquals(defaultPostalCode, postalCodeField.getValue());
    assertEquals(countries[0], countryField.getValue());
  }

  @Test
  public void clearAddress() {
    setFields();

    clearAddressButton.click();

    assertEquals("", addressField.getValue());
    assertEquals("", townField.getValue());
    assertEquals("", stateField.getValue());
    assertEquals("", postalCodeField.getValue());
    assertEquals(countries[0], countryField.getValue());
  }

  @Test
  public void passwordsDontMatch_Password() {
    setFields();
    passwordField.valueChange(new ValueChangeEvent(passwordField));
    confirmPasswordField.valueChange(new ValueChangeEvent(confirmPasswordField));
    assertTrue(passwordField.isValid());
    assertTrue(confirmPasswordField.isValid());
    assertNull(passwordField.getComponentError());

    passwordField.setValue(password + "a");
    passwordField.valueChange(new ValueChangeEvent(passwordField));

    assertTrue(passwordField.getComponentError() instanceof UserError);
    UserError userError = (UserError) passwordField.getComponentError();
    assertEquals(resources.message("password.notMatch"), userError.getMessage());
  }

  @Test
  public void passwordsDontMatch_ConfirmPassword() {
    setFields();
    passwordField.valueChange(new ValueChangeEvent(passwordField));
    confirmPasswordField.valueChange(new ValueChangeEvent(confirmPasswordField));
    assertTrue(passwordField.isValid());
    assertTrue(confirmPasswordField.isValid());
    assertNull(passwordField.getComponentError());

    confirmPasswordField.setValue(password + "a");
    confirmPasswordField.valueChange(new ValueChangeEvent(confirmPasswordField));

    assertTrue(passwordField.getComponentError() instanceof UserError);
    UserError userError = (UserError) passwordField.getComponentError();
    assertEquals(resources.message("password.notMatch"), userError.getMessage());
  }

  @Test
  public void title() {
    verify(view).setTitle(resources.message("title"));
  }

  @Test
  public void captions() {
    assertEquals(resources.message("header"), headerLabel.getValue());
    assertEquals(resources.message("email"), emailField.getCaption());
    assertEquals(resources.message("name"), nameField.getCaption());
    assertEquals(resources.message("password"), passwordField.getCaption());
    assertEquals(resources.message("confirmPassword"), confirmPasswordField.getCaption());
    assertEquals(resources.message("laboratoryHeader"), laboratoryHeaderLabel.getValue());
    assertEquals(resources.message("newLaboratory"), newLaboratoryField.getCaption());
    assertEquals(resources.message("organization"), organizationField.getCaption());
    assertEquals(resources.message("laboratoryName"), laboratoryNameField.getCaption());
    assertEquals(resources.message("manager"), managerEmailField.getCaption());
    assertEquals(resources.message("addressHeader"), addressHeaderLabel.getValue());
    assertEquals(resources.message("address"), addressField.getCaption());
    assertEquals(resources.message("addressSecond"), addressSecondField.getCaption());
    assertEquals(resources.message("town"), townField.getCaption());
    assertEquals(resources.message("state"), stateField.getCaption());
    assertEquals(resources.message("country"), countryField.getCaption());
    assertEquals(resources.message("postalCode"), postalCodeField.getCaption());
    assertEquals(resources.message("clearAddress"), clearAddressButton.getCaption());
    assertEquals(resources.message("phoneNumberHeader"), phoneNumberHeaderLabel.getValue());
    assertEquals(resources.message("phoneNumber"), phoneNumberField.getCaption());
    assertEquals(resources.message("phoneExtension"), phoneExtensionField.getCaption());
    assertEquals(resources.message("registerHeader"), registerHeaderLabel.getValue());
    assertEquals(resources.message("register"), registerButton.getCaption());
    assertEquals(resources.message("required"), requiredLabel.getValue());
  }

  private String requiredError(String caption) {
    return generalResources.message("required", caption);
  }

  @Test
  public void requiredFields() {
    assertTrue(emailField.isRequired());
    assertEquals(requiredError(resources.message("email")), emailField.getRequiredError());
    assertTrue(nameField.isRequired());
    assertEquals(requiredError(resources.message("name")), nameField.getRequiredError());
    assertTrue(passwordField.isRequired());
    assertEquals(requiredError(resources.message("password")), passwordField.getRequiredError());
    assertTrue(confirmPasswordField.isRequired());
    assertEquals(requiredError(resources.message("confirmPassword")),
        confirmPasswordField.getRequiredError());
    assertTrue(organizationField.isRequired());
    assertEquals(requiredError(resources.message("organization")),
        organizationField.getRequiredError());
    assertTrue(laboratoryNameField.isRequired());
    assertEquals(requiredError(resources.message("laboratoryName")),
        laboratoryNameField.getRequiredError());
    assertTrue(managerEmailField.isRequired());
    assertEquals(requiredError(resources.message("manager")), managerEmailField.getRequiredError());
    assertTrue(addressField.isRequired());
    assertEquals(requiredError(resources.message("address")), addressField.getRequiredError());
    assertFalse(addressSecondField.isRequired());
    assertTrue(townField.isRequired());
    assertEquals(requiredError(resources.message("town")), townField.getRequiredError());
    assertTrue(stateField.isRequired());
    assertEquals(requiredError(resources.message("state")), stateField.getRequiredError());
    assertTrue(countryField.isRequired());
    assertEquals(requiredError(resources.message("country")), countryField.getRequiredError());
    assertTrue(postalCodeField.isRequired());
    assertEquals(requiredError(resources.message("postalCode")),
        postalCodeField.getRequiredError());
    assertTrue(phoneNumberField.isRequired());
    assertEquals(requiredError(resources.message("phoneNumber")),
        phoneNumberField.getRequiredError());
    assertFalse(phoneExtensionField.isRequired());
  }

  @Test
  public void emailEmailValidator() {
    emailField.setValue("aaa");

    assertFalse(emailField.isValid());
  }

  @Test
  public void emailExistsValidtor_Exists() {
    when(userService.exists(any())).thenReturn(true);

    emailField.setValue(email);

    assertFalse(emailField.isValid());
    verify(userService).exists(email);
  }

  @Test
  public void emailExistsValidtor_NotExists() {
    when(userService.exists(any())).thenReturn(false);

    emailField.setValue(email);

    assertTrue(emailField.isValid());
    verify(userService).exists(email);
  }

  @Test
  public void managerEmailEmailValidator() {
    managerEmailField.setValue("aaa");

    assertFalse(emailField.isValid());
  }

  @Test
  public void managerEmailManagerValidtor_Manager() {
    when(userService.isManager(any())).thenReturn(true);

    managerEmailField.setValue(managerEmail);

    assertTrue(managerEmailField.isValid());
    verify(userService).isManager(managerEmail);
  }

  @Test
  public void managerEmailManagerValidtor_NotManager() {
    when(userService.isManager(any())).thenReturn(false);

    managerEmailField.setValue(managerEmail);

    assertFalse(managerEmailField.isValid());
    verify(userService).isManager(managerEmail);
  }

  @Test
  public void phoneNumberPatternValidtor_Matches() {
    phoneNumberField.setValue(phoneNumber);

    assertTrue(phoneNumberField.isValid());
  }

  @Test
  public void phoneNumberPatternValidtor_Fail() {
    phoneNumberField.setValue("ABC");

    assertFalse(phoneNumberField.isValid());
  }

  @Test
  public void phoneExtensionPatternValidtor_Matches() {
    phoneExtensionField.setValue(phoneExtension);

    assertTrue(phoneExtensionField.isValid());
  }

  @Test
  public void phoneExtensionPatternValidtor_Fail() {
    phoneExtensionField.setValue("ABC");

    assertFalse(phoneExtensionField.isValid());
  }

  @Test
  public void register_NewLaboratory() {
    setFields();
    newLaboratoryField.setValue(true);
    String validationUrl = "validationUrl";
    when(vaadinUtils.getUrl(any())).thenReturn(validationUrl);

    registerButton.click();

    verify(userService).register(userCaptor.capture(), eq(password), managerCaptor.capture(),
        registerUserWebContextCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isProteomic());
    assertNull(user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
    assertEquals(organization, user.getLaboratory().getOrganization());
    assertNotNull(user.getAddress());
    Address address = user.getAddress();
    assertEquals(this.address, address.getAddress());
    assertEquals(addressSecond, address.getAddressSecond());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertNotNull(user.getPhoneNumbers());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(this.phoneNumber, phoneNumber.getNumber());
    assertEquals(phoneExtension, phoneNumber.getExtension());
    User manager = managerCaptor.getValue();
    assertNull(manager);
    RegisterUserWebContext registerUserWebContext = registerUserWebContextCaptor.getValue();
    assertEquals(validationUrl, registerUserWebContext.getValidateUserUrl(locale));
    verify(view).afterSuccessfulRegister();
  }

  @Test
  public void register_ExistingLaboratory() {
    setFields();
    newLaboratoryField.setValue(false);
    String validationUrl = "validationUrl";
    when(userService.isManager(any())).thenReturn(true);
    when(vaadinUtils.getUrl(any())).thenReturn(validationUrl);

    registerButton.click();

    verify(userService).register(userCaptor.capture(), eq(password), managerCaptor.capture(),
        registerUserWebContextCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(locale, user.getLocale());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isProteomic());
    assertNull(user.getLaboratory());
    assertNotNull(user.getAddress());
    Address address = user.getAddress();
    assertEquals(this.address, address.getAddress());
    assertEquals(addressSecond, address.getAddressSecond());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
    assertNotNull(user.getPhoneNumbers());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(this.phoneNumber, phoneNumber.getNumber());
    assertEquals(phoneExtension, phoneNumber.getExtension());
    User manager = managerCaptor.getValue();
    assertNotNull(manager);
    assertEquals(managerEmail, manager.getEmail());
    RegisterUserWebContext registerUserWebContext = registerUserWebContextCaptor.getValue();
    assertEquals(validationUrl, registerUserWebContext.getValidateUserUrl(locale));
    verify(view).afterSuccessfulRegister();
  }

  @Test
  public void register_EmailEmpty() {
    setFields();
    emailField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_EmailInvalid() {
    setFields();
    emailField.setValue("aaa");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_EmailExists() {
    when(userService.exists(any())).thenReturn(true);
    setFields();
    emailField.setValue("aaa@ircm.qc.ca");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PasswordEmpty() {
    setFields();
    passwordField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ConfirmPasswordEmpty() {
    setFields();
    confirmPasswordField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PasswordsDontMatch() {
    setFields();
    confirmPasswordField.setValue(this.password + "a");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_OrganizationEmpty() {
    setFields();
    newLaboratoryField.setValue(true);
    organizationField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_LaboratoryNameEmpty() {
    setFields();
    newLaboratoryField.setValue(true);
    laboratoryNameField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailEmpty() {
    setFields();
    newLaboratoryField.setValue(false);
    managerEmailField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailInvalid() {
    setFields();
    newLaboratoryField.setValue(false);
    managerEmailField.setValue("aaa");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_ManagerEmailNotManager() {
    when(userService.isManager(any())).thenReturn(false);
    setFields();
    newLaboratoryField.setValue(false);
    managerEmailField.setValue("aaa@ircm.qc.ca");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_AddressEmpty() {
    setFields();
    addressField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_TownEmpty() {
    setFields();
    townField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_StateEmpty() {
    setFields();
    stateField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_CountryInvalid() {
    setFields();
    countryField.setValue("AAA");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PhoneNumberEmpty() {
    setFields();
    phoneNumberField.setValue("");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PhoneNumberPatternFail() {
    setFields();
    phoneNumberField.setValue("AAA");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }

  @Test
  public void register_PhoneExtensionPatternFail() {
    setFields();
    phoneExtensionField.setValue("AAA");

    registerButton.click();

    verify(userService, never()).register(any(), any(), any(), any());
    verify(view).showError(any());
  }
}
