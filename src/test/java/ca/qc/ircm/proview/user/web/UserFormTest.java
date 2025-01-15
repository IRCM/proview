package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.INVALID_EMAIL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
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
import static ca.qc.ircm.proview.user.web.UserForm.CREATE_NEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.UserForm.EMAIL_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.ID;
import static ca.qc.ircm.proview.user.web.UserForm.LABORATORY_NAME_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.NAME_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.NEW_LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.UserForm.NUMBER_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.UserPermissionEvaluator;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Tests for {@link UserForm}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class UserFormTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(UserForm.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String ADDRESS_PREFIX = messagePrefix(Address.class);
  private static final String PHONE_NUMBER_PREFIX = messagePrefix(PhoneNumber.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  private UserForm form;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private EntityManager entityManager;
  @MockitoSpyBean
  private UserPermissionEvaluator userPermissionEvaluator;
  private Locale locale = ENGLISH;
  private List<Laboratory> laboratories;
  private String email = "test@ircm.qc.ca";
  private String name = "Test User";
  private String password = "test_password";
  private String newLaboratoryName = "Test Laboratory";
  private String addressLine = "200 My Street";
  private String town = "My Town";
  private String state = "My State";
  private String country = "My Country";
  private String postalCode = "12345";
  private PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private String number = "514-555-1234";
  private String extension = "443";

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    laboratories = laboratoryRepository.findAll();
    navigate(ProfileView.class);
    form = $(UserForm.class).first();
    entityManager.detach(form.getUser());
  }

  private void fillForm() {
    form.email.setValue(email);
    form.name.setValue(name);
    if (!items(form.laboratory).isEmpty()) {
      form.laboratory.setValue(authenticatedUser.getUser().orElseThrow().getLaboratory());
    }
    form.newLaboratoryName.setValue(newLaboratoryName);
    form.addressLine.setValue(addressLine);
    form.town.setValue(town);
    form.state.setValue(state);
    form.country.setValue(country);
    form.postalCode.setValue(postalCode);
    form.phoneType.setValue(phoneType);
    form.number.setValue(number);
    form.extension.setValue(extension);
    form.passwords.password.setValue(password);
    form.passwords.passwordConfirm.setValue(password);
  }

  @Test
  public void styles() {
    assertEquals(ID, form.getId().orElse(""));
    assertEquals(id(EMAIL), form.email.getId().orElse(""));
    assertEquals(id(NAME), form.name.getId().orElse(""));
    assertEquals(id(ADMIN), form.admin.getId().orElse(""));
    assertEquals(id(MANAGER), form.manager.getId().orElse(""));
    assertEquals(id(LABORATORY), form.laboratory.getId().orElse(""));
    assertEquals(id(CREATE_NEW_LABORATORY), form.createNewLaboratory.getId().orElse(""));
    assertEquals(id(NEW_LABORATORY_NAME), form.newLaboratoryName.getId().orElse(""));
    assertEquals(id(LINE), form.addressLine.getId().orElse(""));
    assertEquals(id(TOWN), form.town.getId().orElse(""));
    assertEquals(id(STATE), form.state.getId().orElse(""));
    assertEquals(id(COUNTRY), form.country.getId().orElse(""));
    assertEquals(id(POSTAL_CODE), form.postalCode.getId().orElse(""));
    assertEquals(id(TYPE), form.phoneType.getId().orElse(""));
    assertEquals(id(NUMBER), form.number.getId().orElse(""));
    assertEquals(id(EXTENSION), form.extension.getId().orElse(""));
  }

  @Test
  public void placeholder() {
    assertEquals(EMAIL_PLACEHOLDER, form.email.getPlaceholder());
    assertEquals(NAME_PLACEHOLDER, form.name.getPlaceholder());
    assertEquals(LABORATORY_NAME_PLACEHOLDER, form.newLaboratoryName.getPlaceholder());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals(address.getLine(), form.addressLine.getPlaceholder());
    assertEquals(address.getTown(), form.town.getPlaceholder());
    assertEquals(address.getState(), form.state.getPlaceholder());
    assertEquals(address.getCountry(), form.country.getPlaceholder());
    assertEquals(address.getPostalCode(), form.postalCode.getPlaceholder());
    assertEquals(NUMBER_PLACEHOLDER, form.number.getPlaceholder());
  }

  @Test
  public void labels() {
    assertEquals(form.getTranslation(USER_PREFIX + EMAIL), form.email.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + NAME), form.name.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + ADMIN), form.admin.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + MANAGER), form.manager.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + LABORATORY), form.laboratory.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + CREATE_NEW_LABORATORY),
        form.createNewLaboratory.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + NEW_LABORATORY_NAME),
        form.newLaboratoryName.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + LINE), form.addressLine.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + TOWN), form.town.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + STATE), form.state.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + COUNTRY), form.country.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + POSTAL_CODE), form.postalCode.getLabel());
    assertEquals(form.getTranslation(PHONE_NUMBER_PREFIX + TYPE), form.phoneType.getLabel());
    for (PhoneNumberType type : PhoneNumberType.values()) {
      assertEquals(form.getTranslation(PHONE_NUMBER_TYPE_PREFIX + type.name()),
          form.phoneType.getItemLabelGenerator().apply(type));
    }
    assertEquals(form.getTranslation(PHONE_NUMBER_PREFIX + NUMBER), form.number.getLabel());
    assertEquals(form.getTranslation(PHONE_NUMBER_PREFIX + EXTENSION), form.extension.getLabel());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(form.getTranslation(USER_PREFIX + EMAIL), form.email.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + NAME), form.name.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + ADMIN), form.admin.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + MANAGER), form.manager.getLabel());
    assertEquals(form.getTranslation(USER_PREFIX + LABORATORY), form.laboratory.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + CREATE_NEW_LABORATORY),
        form.createNewLaboratory.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + NEW_LABORATORY_NAME),
        form.newLaboratoryName.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + LINE), form.addressLine.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + TOWN), form.town.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + STATE), form.state.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + COUNTRY), form.country.getLabel());
    assertEquals(form.getTranslation(ADDRESS_PREFIX + POSTAL_CODE), form.postalCode.getLabel());
    assertEquals(form.getTranslation(PHONE_NUMBER_PREFIX + TYPE), form.phoneType.getLabel());
    for (PhoneNumberType type : PhoneNumberType.values()) {
      assertEquals(form.getTranslation(PHONE_NUMBER_TYPE_PREFIX + type.name()),
          form.phoneType.getItemLabelGenerator().apply(type));
    }
    assertEquals(form.getTranslation(PHONE_NUMBER_PREFIX + NUMBER), form.number.getLabel());
    assertEquals(form.getTranslation(PHONE_NUMBER_PREFIX + EXTENSION), form.extension.getLabel());
  }

  @Test
  public void currentUser_User() {
    assertFalse(form.admin.isVisible());
    assertFalse(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.createNewLaboratory.isVisible());
    assertFalse(form.newLaboratoryName.isVisible());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void currentUser_Manager() {
    assertFalse(form.admin.isVisible());
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.createNewLaboratory.isVisible());
    assertFalse(form.newLaboratoryName.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void currentUser_Admin() {
    assertTrue(form.admin.isVisible());
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  public void laboratory() {
    assertFalse(form.laboratory.isAllowCustomValue());
    assertTrue(form.laboratory.isRequiredIndicatorVisible());
    List<Laboratory> values = items(form.laboratory);
    assertEquals(1, values.size());
    Laboratory laboratory = authenticatedUser.getUser().orElseThrow().getLaboratory();
    assertEquals(laboratory.getId(), values.get(0).getId());
    assertEquals(laboratory.getName(), form.laboratory.getItemLabelGenerator().apply(laboratory));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void laboratory_Manager() {
    assertFalse(form.laboratory.isAllowCustomValue());
    assertTrue(form.laboratory.isRequiredIndicatorVisible());
    List<Laboratory> values = items(form.laboratory);
    assertEquals(1, values.size());
    Laboratory laboratory = authenticatedUser.getUser().orElseThrow().getLaboratory();
    assertEquals(laboratory.getId(), values.get(0).getId());
    assertEquals(laboratory.getName(), form.laboratory.getItemLabelGenerator().apply(laboratory));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void laboratory_Admin() {
    assertFalse(form.laboratory.isAllowCustomValue());
    assertTrue(form.laboratory.isRequiredIndicatorVisible());
    List<Laboratory> values = items(form.laboratory);
    assertEquals(laboratories.size(), values.size());
    for (Laboratory laboratory : laboratories) {
      assertTrue(
          values.stream().filter(lab -> lab.getId() == laboratory.getId()).findAny().isPresent());
      assertEquals(laboratory.getName(), form.laboratory.getItemLabelGenerator().apply(laboratory));
    }
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void checkAdmin() {
    form.admin.setValue(true);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void uncheckAdmin() {
    form.admin.setValue(true);
    form.admin.setValue(false);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void checkManager_Manager() {
    form.manager.setValue(true);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertFalse(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void checkManager_Admin() {
    form.manager.setValue(true);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void checkManagerAndCheckCreateNewLaboratory_Admin() {
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isReadOnly());
    assertFalse(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.newLaboratoryName.isVisible());
    assertTrue(form.newLaboratoryName.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void uncheckManager_Admin() {
    form.manager.setValue(true);
    form.manager.setValue(false);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isReadOnly());
    assertTrue(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertTrue(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void uncheckManagerAndCheckCreateNewLaboratory_Admin() {
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    form.manager.setValue(false);
    assertTrue(form.manager.isVisible());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertFalse(form.createNewLaboratory.getValue());
    assertTrue(form.newLaboratoryName.isVisible());
    assertFalse(form.newLaboratoryName.isEnabled());
  }

  @Test
  public void getPassword() {
    String password = "test_password";
    form.passwords = mock(PasswordsForm.class);
    when(form.passwords.getPassword()).thenReturn(password);
    assertEquals(password, form.getPassword());
  }

  @Test
  public void getUser() {
    assertEquals(10, form.getUser().getId());
  }

  @Test
  public void setUser_NewUser() {
    User user = new User();
    user.setLaboratory(new Laboratory());
    user.setPhoneNumbers(new ArrayList<>());

    form.setUser(user);

    assertEquals("", form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals("", form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertFalse(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    assertTrue(form.passwords.isVisible());
    assertTrue(form.passwords.isRequired());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        form.laboratory.getValue().getId());
    assertTrue(form.laboratory.isReadOnly());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals(address.getLine(), form.addressLine.getValue());
    assertFalse(form.addressLine.isReadOnly());
    assertEquals(address.getTown(), form.town.getValue());
    assertFalse(form.town.isReadOnly());
    assertEquals(address.getState(), form.state.getValue());
    assertFalse(form.state.isReadOnly());
    assertEquals(address.getCountry(), form.country.getValue());
    assertFalse(form.country.isReadOnly());
    assertEquals(address.getPostalCode(), form.postalCode.getValue());
    assertFalse(form.postalCode.isReadOnly());
    assertEquals(PhoneNumberType.WORK, form.phoneType.getValue());
    assertFalse(form.phoneType.isReadOnly());
    assertEquals("", form.number.getValue());
    assertFalse(form.number.isReadOnly());
    assertEquals("", form.extension.getValue());
    assertFalse(form.extension.isReadOnly());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void setUser_NewUserAdmin() {
    User user = new User();
    user.setLaboratory(new Laboratory());
    user.setPhoneNumbers(new ArrayList<>());

    form.setUser(user);

    assertEquals("", form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals("", form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertFalse(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    assertTrue(form.passwords.isVisible());
    assertTrue(form.passwords.isRequired());
    assertEquals((Long) 1L, form.laboratory.getValue().getId());
    assertFalse(form.laboratory.isReadOnly());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals(address.getLine(), form.addressLine.getValue());
    assertFalse(form.addressLine.isReadOnly());
    assertEquals(address.getTown(), form.town.getValue());
    assertFalse(form.town.isReadOnly());
    assertEquals(address.getState(), form.state.getValue());
    assertFalse(form.state.isReadOnly());
    assertEquals(address.getCountry(), form.country.getValue());
    assertFalse(form.country.isReadOnly());
    assertEquals(address.getPostalCode(), form.postalCode.getValue());
    assertFalse(form.postalCode.isReadOnly());
    assertEquals(PhoneNumberType.WORK, form.phoneType.getValue());
    assertFalse(form.phoneType.isReadOnly());
    assertEquals("", form.number.getValue());
    assertFalse(form.number.isReadOnly());
    assertEquals("", form.extension.getValue());
    assertFalse(form.extension.isReadOnly());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void setUser_User() {
    User user = userRepository.findById(10L).orElseThrow();
    when(userPermissionEvaluator.hasPermission(any(), eq(user), eq(WRITE))).thenReturn(false);

    form.setUser(user);

    assertEquals(user.getEmail(), form.email.getValue());
    assertTrue(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertTrue(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertTrue(form.admin.isReadOnly());
    assertFalse(form.manager.getValue());
    assertTrue(form.manager.isReadOnly());
    assertFalse(form.passwords.isVisible());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertTrue(form.laboratory.isReadOnly());
    Address address = user.getAddress();
    assertNotNull(address);
    assertEquals(address.getLine(), form.addressLine.getValue());
    assertTrue(form.addressLine.isReadOnly());
    assertEquals(address.getTown(), form.town.getValue());
    assertTrue(form.town.isReadOnly());
    assertEquals(address.getState(), form.state.getValue());
    assertTrue(form.state.isReadOnly());
    assertEquals(address.getCountry(), form.country.getValue());
    assertTrue(form.country.isReadOnly());
    assertEquals(address.getPostalCode(), form.postalCode.getValue());
    assertTrue(form.postalCode.isReadOnly());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(phoneNumber.getType(), form.phoneType.getValue());
    assertTrue(form.phoneType.isReadOnly());
    assertEquals(phoneNumber.getNumber(), form.number.getValue());
    assertTrue(form.number.isReadOnly());
    assertEquals(Objects.toString(phoneNumber.getExtension(), ""), form.extension.getValue());
    assertTrue(form.extension.isReadOnly());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void setUser_UserCanWrite() {
    User user = userRepository.findById(3L).orElseThrow();

    form.setUser(user);

    assertEquals(user.getEmail(), form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    assertTrue(form.passwords.isVisible());
    assertFalse(form.passwords.isRequired());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertTrue(form.laboratory.isReadOnly());
    Address address = user.getAddress();
    assertNotNull(address);
    assertEquals(address.getLine(), form.addressLine.getValue());
    assertFalse(form.addressLine.isReadOnly());
    assertEquals(address.getTown(), form.town.getValue());
    assertFalse(form.town.isReadOnly());
    assertEquals(address.getState(), form.state.getValue());
    assertFalse(form.state.isReadOnly());
    assertEquals(address.getCountry(), form.country.getValue());
    assertFalse(form.country.isReadOnly());
    assertEquals(address.getPostalCode(), form.postalCode.getValue());
    assertFalse(form.postalCode.isReadOnly());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(phoneNumber.getType(), form.phoneType.getValue());
    assertFalse(form.phoneType.isReadOnly());
    assertEquals(phoneNumber.getNumber(), form.number.getValue());
    assertFalse(form.number.isReadOnly());
    assertEquals(Objects.toString(phoneNumber.getExtension(), ""), form.extension.getValue());
    assertFalse(form.extension.isReadOnly());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void setUser_UserAdmin() {
    User user = userRepository.findById(3L).orElseThrow();

    form.setUser(user);

    assertEquals(user.getEmail(), form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    assertTrue(form.passwords.isVisible());
    assertFalse(form.passwords.isRequired());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertFalse(form.laboratory.isReadOnly());
    Address address = user.getAddress();
    assertNotNull(address);
    assertEquals(address.getLine(), form.addressLine.getValue());
    assertFalse(form.addressLine.isReadOnly());
    assertEquals(address.getTown(), form.town.getValue());
    assertFalse(form.town.isReadOnly());
    assertEquals(address.getState(), form.state.getValue());
    assertFalse(form.state.isReadOnly());
    assertEquals(address.getCountry(), form.country.getValue());
    assertFalse(form.country.isReadOnly());
    assertEquals(address.getPostalCode(), form.postalCode.getValue());
    assertFalse(form.postalCode.isReadOnly());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(phoneNumber.getType(), form.phoneType.getValue());
    assertFalse(form.phoneType.isReadOnly());
    assertEquals(phoneNumber.getNumber(), form.number.getValue());
    assertFalse(form.number.isReadOnly());
    assertEquals(Objects.toString(phoneNumber.getExtension(), ""), form.extension.getValue());
    assertFalse(form.extension.isReadOnly());
  }

  @Test
  public void setUser_Null() {
    form.setUser(null);

    assertEquals("", form.email.getValue());
    assertEquals("", form.name.getValue());
    assertFalse(form.admin.getValue());
    assertFalse(form.manager.getValue());
    assertTrue(form.passwords.isVisible());
    assertTrue(form.passwords.isRequired());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        form.laboratory.getValue().getId());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals(address.getLine(), form.addressLine.getValue());
    assertEquals(address.getTown(), form.town.getValue());
    assertEquals(address.getState(), form.state.getValue());
    assertEquals(address.getCountry(), form.country.getValue());
    assertEquals(address.getPostalCode(), form.postalCode.getValue());
    assertEquals(PhoneNumberType.WORK, form.phoneType.getValue());
    assertFalse(form.phoneType.isReadOnly());
    assertEquals("", form.number.getValue());
    assertFalse(form.number.isReadOnly());
    assertEquals("", form.extension.getValue());
    assertFalse(form.extension.isReadOnly());
  }

  @Test
  public void isValid_EmailEmpty() {
    form.email.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<User> status = form.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmailInvalid() {
    form.email.setValue("test");

    assertFalse(form.isValid());

    BinderValidationStatus<User> status = form.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + INVALID_EMAIL)),
        error.getMessage());
  }

  @Test
  public void isValid_NameEmpty() {
    form.name.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<User> status = form.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.name);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_PasswordValidationFailed() {
    form.passwords = mock(PasswordsForm.class);

    assertFalse(form.isValid());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_LaboratoryEmpty() {
    form.laboratory.setItems(new ArrayList<>());

    assertFalse(form.isValid());

    BinderValidationStatus<User> status = form.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.laboratory);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_AdminLaboratoryEmpty() {
    form.laboratory.setItems(new ArrayList<>());
    form.admin.setValue(true);

    assertFalse(form.isValid());

    BinderValidationStatus<User> status = form.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.laboratory);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_NewLaboratoryNameEmpty() {
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    form.newLaboratoryName.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<Laboratory> status = form.validateLaboratory();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.newLaboratoryName);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_AddressLineEmpty() {
    form.addressLine.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<Address> status = form.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.addressLine);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_TownEmpty() {
    form.town.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<Address> status = form.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.town);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_StateEmpty() {
    form.state.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<Address> status = form.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.state);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_CountryEmpty() {
    form.country.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<Address> status = form.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.country);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_PostalCodeEmpty() {
    form.postalCode.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<Address> status = form.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.postalCode);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_NumberEmpty() {
    form.number.setValue("");

    assertFalse(form.isValid());

    BinderValidationStatus<PhoneNumber> status = form.validatePhoneNumber();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.number);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_NewUser() {
    form.setUser(null);
    fillForm();

    assertTrue(form.isValid());

    User user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertFalse(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_NewManager() {
    form.setUser(null);
    fillForm();
    form.manager.setValue(true);

    assertTrue(form.isValid());

    User user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(authenticatedUser.getUser().orElseThrow().getId(), user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_NewManagerNewLaboratory() {
    form.setUser(null);
    fillForm();
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);

    assertTrue(form.isValid());

    User user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(0, user.getLaboratory().getId());
    assertEquals(newLaboratoryName, user.getLaboratory().getName());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void isValid_UpdateUser() {
    fillForm();

    assertTrue(form.isValid());

    User user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_UpdateUserLaboratory() {
    User user = userRepository.findById(26L).orElseThrow();
    form.setUser(user);
    fillForm();

    assertTrue(form.isValid());

    user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertFalse(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void isValid_UpdateUserNoPassword() {
    fillForm();
    form.passwords.password.setValue("");
    form.passwords.passwordConfirm.setValue("");

    assertTrue(form.isValid());

    User user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_NewAdmin() {
    form.setUser(null);
    fillForm();
    form.admin.setValue(true);

    assertTrue(form.isValid());

    User user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(user.isAdmin());
    assertFalse(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_UpdateAdmin() {
    User user = userRepository.findById(1L).orElseThrow();
    entityManager.detach(user);
    form.setUser(user);
    fillForm();

    assertTrue(form.isValid());

    user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(user.isAdmin());
    assertTrue(user.isManager());
    assertEquals((Long) 1L, user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_UpdateAdminNoPassword() {
    User user = userRepository.findById(1L).orElseThrow();
    entityManager.detach(user);
    form.setUser(user);
    fillForm();
    form.passwords.password.setValue("");
    form.passwords.passwordConfirm.setValue("");

    assertTrue(form.isValid());

    user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(user.isAdmin());
    assertTrue(user.isManager());
    assertEquals((Long) 1L, user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isValid_UpdateAdmin_RemoveAdminAddManager() {
    User user = userRepository.findById(1L).orElseThrow();
    entityManager.detach(user);
    form.setUser(user);
    fillForm();
    form.admin.setValue(false);
    form.manager.setValue(true);

    assertTrue(form.isValid());

    user = form.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertEquals(authenticatedUser.getUser().orElseThrow().getLaboratory().getId(),
        user.getLaboratory().getId());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
  }
}
