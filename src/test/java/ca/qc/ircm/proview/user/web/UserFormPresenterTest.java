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

import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.user.web.UserFormPresenter.LaboratoryContainer;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserFormPresenterTest extends AbstractViewTestCase {
  private UserFormPresenter presenter;
  @Mock
  private UserForm form;
  @Mock
  private UserService userService;
  @Mock
  private LaboratoryService laboratoryService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private BinderValidationStatus<Passwords> passwordsValidationStatus;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Autowired
  private EntityManager entityManager;
  private Locale locale = ENGLISH;
  private AppResources webResources = new AppResources(WebConstants.class, locale);
  private String email = "test@ircm.qc.ca";
  private String name = "Test User";
  private String password = "test_password";
  private String laboratoryName = "Test Laboratory";
  private String addressLine = "200 My Street";
  private String town = "My Town";
  private String state = "My State";
  private String country = "My Country";
  private String postalCode = "12345";
  private PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private String number = "514-555-1234";
  private String extension = "443";
  private User currentUser;
  private List<Laboratory> laboratories;
  private Laboratory laboratory;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new UserFormPresenter(laboratoryService, authorizationService, defaultAddressConfiguration);
    form.email = new TextField();
    form.name = new TextField();
    form.admin = new Checkbox();
    form.manager = new Checkbox();
    form.createNewLaboratory = new Checkbox();
    form.passwords = mock(PasswordsForm.class);
    form.laboratory = new ComboBox<>();
    form.laboratoryName = new TextField();
    form.addressLine = new TextField();
    form.town = new TextField();
    form.state = new TextField();
    form.country = new TextField();
    form.postalCode = new TextField();
    form.phoneType = new ComboBox<>();
    form.phoneType.setItems(PhoneNumberType.values());
    form.number = new TextField();
    form.extension = new TextField();
    currentUser = userRepository.findById(3L).orElse(null);
    when(authorizationService.getCurrentUser()).thenReturn(currentUser);
    laboratories = laboratoryRepository.findAll();
    laboratories.forEach(lab -> entityManager.detach(lab));
    when(laboratoryService.all()).thenReturn(laboratories);
    when(laboratoryService.get(any()))
        .thenAnswer(i -> laboratoryRepository.findById(i.getArgument(0)).orElse(null));
    laboratory = laboratoryRepository.findById(2L).orElse(null);
    when(form.passwords.validate()).thenReturn(passwordsValidationStatus);
    when(passwordsValidationStatus.isOk()).thenReturn(true);
  }

  private void fillForm() {
    form.email.setValue(email);
    form.name.setValue(name);
    if (!items(form.laboratory).isEmpty()) {
      form.laboratory.setValue(laboratory);
    }
    form.laboratoryName.setValue(laboratoryName);
    form.addressLine.setValue(addressLine);
    form.town.setValue(town);
    form.state.setValue(state);
    form.country.setValue(country);
    form.postalCode.setValue(postalCode);
    form.phoneType.setValue(phoneType);
    form.number.setValue(number);
    form.extension.setValue(extension);
  }

  @Test
  public void currentUser_User() {
    presenter.init(form);
    presenter.localeChange(locale);
    assertFalse(form.admin.isVisible());
    assertFalse(form.manager.isVisible());
    assertFalse(form.createNewLaboratory.isVisible());
    assertFalse(form.laboratory.isVisible());
    assertTrue(form.laboratoryName.isReadOnly());
  }

  @Test
  public void currentUser_Manager() {
    when(authorizationService.hasAnyRole(any()))
        .thenAnswer(i -> Stream.of(i.getArguments()).filter(MANAGER::equals).findAny().isPresent());
    presenter.init(form);
    presenter.localeChange(locale);
    assertFalse(form.admin.isVisible());
    assertTrue(form.manager.isVisible());
    assertFalse(form.createNewLaboratory.isVisible());
    assertFalse(form.laboratory.isVisible());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void currentUser_Admin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    assertTrue(form.admin.isVisible());
    assertTrue(form.manager.isVisible());
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void laboratory() {
    presenter.init(form);
    presenter.localeChange(locale);
    assertFalse(form.laboratory.isAllowCustomValue());
    assertTrue(form.laboratory.isRequiredIndicatorVisible());
    List<Laboratory> values = items(form.laboratory);
    assertEquals(1, values.size());
    assertEquals(currentUser.getLaboratory(), values.get(0));
    assertEquals(laboratory.getName(), form.laboratory.getItemLabelGenerator().apply(laboratory));
  }

  @Test
  public void laboratory_Admin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    assertFalse(form.laboratory.isAllowCustomValue());
    assertTrue(form.laboratory.isRequiredIndicatorVisible());
    List<Laboratory> values = items(form.laboratory);
    assertEquals(laboratories.size(), values.size());
    for (Laboratory laboratory : laboratories) {
      assertTrue(values.contains(laboratory));
      assertEquals(laboratory.getName(), form.laboratory.getItemLabelGenerator().apply(laboratory));
    }
  }

  @Test
  public void checkAdmin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    form.admin.setValue(true);
    assertTrue(form.manager.isVisible());
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void uncheckAdmin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    form.admin.setValue(true);
    form.admin.setValue(false);
    assertTrue(form.manager.isVisible());
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void checkManager_Manager() {
    when(authorizationService.hasAnyRole(any()))
        .thenAnswer(i -> Stream.of(i.getArguments()).filter(MANAGER::equals).findAny().isPresent());
    presenter.init(form);
    presenter.localeChange(locale);
    form.manager.setValue(true);
    assertFalse(form.createNewLaboratory.isVisible());
    assertFalse(form.laboratory.isVisible());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void checkManager_Admin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    form.manager.setValue(true);
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void checkManagerAndCheckCreateNewLaboratory_Admin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    assertTrue(form.createNewLaboratory.isVisible());
    assertTrue(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertFalse(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void uncheckManager_Admin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    form.manager.setValue(true);
    form.manager.setValue(false);
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void uncheckManagerAndCheckCreateNewLaboratory_Admin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    form.manager.setValue(false);
    assertTrue(form.createNewLaboratory.isVisible());
    assertFalse(form.createNewLaboratory.isEnabled());
    assertTrue(form.laboratory.isVisible());
    assertTrue(form.laboratory.isEnabled());
    assertFalse(form.laboratoryName.isReadOnly());
  }

  @Test
  public void checkThenUncheckCreateNewLaboratory() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    User user = userRepository.findById(10L).get();
    Laboratory laboratory = laboratoryRepository.findById(4L).get();
    presenter.setUser(user);
    form.laboratory.setValue(laboratory);
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    form.laboratoryName.setValue(laboratoryName);
    form.createNewLaboratory.setValue(false);
    assertEquals(laboratory, form.laboratory.getValue());
    assertEquals(laboratory.getId(), form.laboratory.getValue().getId());
    assertEquals(laboratory.getName(), form.laboratoryName.getValue());
  }

  @Test
  public void getPassword() {
    String password = "test_password";
    when(form.passwords.getPassword()).thenReturn(password);
    presenter.init(form);
    assertEquals(password, presenter.getPassword());
  }

  @Test
  public void getUser() {
    presenter.init(form);
    User user = new User();
    presenter.setUser(user);
    assertEquals(user, presenter.getUser());
  }

  @Test
  public void setUser_NewUser() {
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    User user = new User();

    presenter.localeChange(locale);
    presenter.setUser(user);

    assertEquals("", form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals("", form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertFalse(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertEquals(laboratory.getId(), form.laboratory.getValue().getId());
    assertEquals(laboratory.getName(), form.laboratoryName.getValue());
    assertTrue(form.laboratoryName.isReadOnly());
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
  public void setUser_NewUserAdmin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    User user = new User();

    presenter.localeChange(locale);
    presenter.setUser(user);

    assertEquals("", form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals("", form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertFalse(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertEquals((Long) 1L, form.laboratory.getValue().getId());
    assertEquals("Admin", form.laboratoryName.getValue());
    assertFalse(form.laboratoryName.isReadOnly());
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
  public void setUser_User() {
    presenter.init(form);
    User user = userRepository.findById(3L).get();

    presenter.localeChange(locale);
    presenter.setUser(user);

    assertEquals(user.getEmail(), form.email.getValue());
    assertTrue(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertTrue(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertTrue(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertTrue(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertEquals(user.getLaboratory().getName(), form.laboratoryName.getValue());
    assertTrue(form.laboratoryName.isReadOnly());
    Address address = user.getAddress();
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
  public void setUser_UserCanWrite() {
    presenter.init(form);
    User user = userRepository.findById(3L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);

    presenter.localeChange(locale);
    presenter.setUser(user);

    assertEquals(user.getEmail(), form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertEquals(user.getLaboratory().getName(), form.laboratoryName.getValue());
    assertTrue(form.laboratoryName.isReadOnly());
    Address address = user.getAddress();
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
  public void setUser_UserAdmin() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    User user = userRepository.findById(3L).get();

    presenter.localeChange(locale);
    presenter.setUser(user);

    assertEquals(user.getEmail(), form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertEquals(user.getLaboratory().getName(), form.laboratoryName.getValue());
    assertFalse(form.laboratoryName.isReadOnly());
    Address address = user.getAddress();
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
  public void setUser_UserBeforeLocaleChange() {
    presenter.init(form);
    User user = userRepository.findById(3L).get();

    presenter.setUser(user);
    presenter.localeChange(locale);

    assertEquals(user.getEmail(), form.email.getValue());
    assertTrue(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertTrue(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertTrue(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertTrue(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertEquals(user.getLaboratory().getName(), form.laboratoryName.getValue());
    assertTrue(form.laboratoryName.isReadOnly());
    Address address = user.getAddress();
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
  public void setUser_UserCanWriteBeforeLocaleChange() {
    presenter.init(form);
    User user = userRepository.findById(3L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);

    presenter.setUser(user);
    presenter.localeChange(locale);

    assertEquals(user.getEmail(), form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertEquals(user.getLaboratory().getName(), form.laboratoryName.getValue());
    assertTrue(form.laboratoryName.isReadOnly());
    Address address = user.getAddress();
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
  public void setUser_UserAdminBeforeLocaleChange() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.init(form);
    User user = userRepository.findById(3L).get();

    presenter.setUser(user);
    presenter.localeChange(locale);

    assertEquals(user.getEmail(), form.email.getValue());
    assertFalse(form.email.isReadOnly());
    assertEquals(user.getName(), form.name.getValue());
    assertFalse(form.name.isReadOnly());
    assertFalse(form.admin.getValue());
    assertFalse(form.admin.isReadOnly());
    assertTrue(form.manager.getValue());
    assertFalse(form.manager.isReadOnly());
    verify(form.passwords, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertEquals(user.getLaboratory().getId(), form.laboratory.getValue().getId());
    assertEquals(user.getLaboratory().getName(), form.laboratoryName.getValue());
    assertFalse(form.laboratoryName.isReadOnly());
    Address address = user.getAddress();
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
    presenter.init(form);
    presenter.localeChange(locale);
    presenter.setUser(null);

    assertEquals("", form.email.getValue());
    assertEquals("", form.name.getValue());
    assertFalse(form.admin.getValue());
    assertFalse(form.manager.getValue());
    verify(form.passwords, atLeastOnce()).setRequired(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertEquals(laboratory.getId(), form.laboratory.getValue().getId());
    assertEquals(laboratory.getName(), form.laboratoryName.getValue());
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
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.email.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<User> status = presenter.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_EmailInvalid() {
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.email.setValue("test");

    assertFalse(presenter.isValid());

    BinderValidationStatus<User> status = presenter.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_EMAIL)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_NameEmpty() {
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.name.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<User> status = presenter.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.name);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_PasswordValidationFailed() {
    when(passwordsValidationStatus.isOk()).thenReturn(false);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();

    assertFalse(presenter.isValid());

    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_LaboratoryEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(laboratoryService.all()).thenReturn(new ArrayList<>());
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();

    assertFalse(presenter.isValid());

    BinderValidationStatus<LaboratoryContainer> status = presenter.validateLaboratoryContainer();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.laboratory);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_AdminLaboratoryEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(laboratoryService.all()).thenReturn(new ArrayList<>());
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.admin.setValue(true);

    assertFalse(presenter.isValid());

    BinderValidationStatus<LaboratoryContainer> status = presenter.validateLaboratoryContainer();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.laboratory);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_NewLaboratoryNameEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);
    form.laboratoryName.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Laboratory> status = presenter.validateLaboratory();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.laboratoryName);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_LaboratoryNameEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.manager.setValue(true);
    form.laboratoryName.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Laboratory> status = presenter.validateLaboratory();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.laboratoryName);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_AddressLineEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.addressLine.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Address> status = presenter.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.addressLine);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_TownEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.town.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Address> status = presenter.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.town);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_StateEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.state.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Address> status = presenter.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.state);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_CountryEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.country.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Address> status = presenter.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.country);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_PostalCodeEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.postalCode.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<Address> status = presenter.validateAddress();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.postalCode);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_NumberEmpty() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.number.setValue("");

    assertFalse(presenter.isValid());

    BinderValidationStatus<PhoneNumber> status = presenter.validatePhoneNumber();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.number);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(userService, never()).save(any(), any());
  }

  @Test
  public void isValid_NewUser() {
    when(form.passwords.getPassword()).thenReturn(password);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();

    assertTrue(presenter.isValid());

    User user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertFalse(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_NewManager() {
    when(form.passwords.getPassword()).thenReturn(password);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.manager.setValue(true);

    assertTrue(presenter.isValid());

    User user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_NewManagerNewLaboratory() {
    when(form.passwords.getPassword()).thenReturn(password);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.manager.setValue(true);
    form.createNewLaboratory.setValue(true);

    assertTrue(presenter.isValid());

    User user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertNull(user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_UpdateUser() {
    when(form.passwords.getPassword()).thenReturn(password);
    presenter.init(form);
    User user = userRepository.findById(3L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.setUser(user);
    presenter.localeChange(locale);
    fillForm();

    assertTrue(presenter.isValid());

    user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_UpdateUserLaboratory() {
    when(form.passwords.getPassword()).thenReturn(password);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    User user = userRepository.findById(26L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.setUser(user);
    presenter.localeChange(locale);
    fillForm();

    assertTrue(presenter.isValid());

    user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertFalse(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_UpdateUserNoPassword() {
    presenter.init(form);
    User user = userRepository.findById(3L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.setUser(user);
    presenter.localeChange(locale);
    fillForm();

    assertTrue(presenter.isValid());

    user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_NewAdmin() {
    when(form.passwords.getPassword()).thenReturn(password);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.localeChange(locale);
    fillForm();
    form.admin.setValue(true);

    assertTrue(presenter.isValid());

    User user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(user.isAdmin());
    assertFalse(user.isManager());
    assertNotNull(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_UpdateAdmin() {
    when(form.passwords.getPassword()).thenReturn(password);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    User user = userRepository.findById(1L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.setUser(user);
    presenter.localeChange(locale);
    fillForm();

    assertTrue(presenter.isValid());

    user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(user.isAdmin());
    assertTrue(user.isManager());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_UpdateAdminNoPassword() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    User user = userRepository.findById(1L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.setUser(user);
    presenter.localeChange(locale);
    fillForm();

    assertTrue(presenter.isValid());

    user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(user.isAdmin());
    assertTrue(user.isManager());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void isValid_UpdateAdmin_RemoveAdminAddManager() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    User user = userRepository.findById(1L).get();
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);
    presenter.setUser(user);
    presenter.localeChange(locale);
    fillForm();
    form.admin.setValue(false);
    form.manager.setValue(true);

    assertTrue(presenter.isValid());

    user = presenter.getUser();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertFalse(user.isAdmin());
    assertTrue(user.isManager());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(laboratoryName, user.getLaboratory().getName());
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
  public void setUser_Multiple_ChangeLaboratoryName() {
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    when(authorizationService.hasRole(any())).thenReturn(true);
    presenter.init(form);
    presenter.setUser(new User());
    presenter.localeChange(locale);
    final String expectedLaboratoryName = form.laboratory.getValue().getName();
    form.laboratoryName.setValue(laboratoryName);

    presenter.setUser(new User());

    assertEquals(expectedLaboratoryName, form.laboratory.getValue().getName());
  }
}
