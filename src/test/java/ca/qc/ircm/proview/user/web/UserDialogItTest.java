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

import static ca.qc.ircm.proview.user.web.UsersView.ID;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserDialogItTest extends AbstractTestBenchTestCase {
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private String email = "test@ircm.qc.ca";
  private String name = "Test User";
  private String password = "test_password";
  private String addressLine = "200 My Street";
  private String town = "My Town";
  private String state = "My State";
  private String country = "My Country";
  private String postalCode = "12345";
  private PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private String number = "514-555-1234";
  private String extension = "443";

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).id(ID);
    view.doubleClickUser(0);
    UserDialogElement dialog = view.dialog();
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertTrue(optional(() -> dialog.userForm().email()).isPresent());
    assertTrue(optional(() -> dialog.userForm().name()).isPresent());
    assertTrue(optional(() -> dialog.userForm().admin()).isPresent());
    assertTrue(optional(() -> dialog.userForm().manager()).isPresent());
    assertTrue(optional(() -> dialog.userForm().password()).isPresent());
    assertTrue(optional(() -> dialog.userForm().passwordConfirm()).isPresent());
    assertTrue(optional(() -> dialog.userForm().laboratory()).isPresent());
    assertTrue(optional(() -> dialog.userForm().createNewLaboratory()).isPresent());
    assertTrue(optional(() -> dialog.userForm().address()).isPresent());
    assertTrue(optional(() -> dialog.userForm().town()).isPresent());
    assertTrue(optional(() -> dialog.userForm().state()).isPresent());
    assertTrue(optional(() -> dialog.userForm().country()).isPresent());
    assertTrue(optional(() -> dialog.userForm().postalCode()).isPresent());
    assertTrue(optional(() -> dialog.userForm().phoneType()).isPresent());
    assertTrue(optional(() -> dialog.userForm().number()).isPresent());
    assertTrue(optional(() -> dialog.userForm().extension()).isPresent());
    assertTrue(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.cancel()).isPresent());
  }

  @Test
  public void update() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).id(ID);
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.doubleClickUser(0);

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.save().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email).get();
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertEquals(LocalDateTime.of(2019, 5, 11, 13, 43, 51), user.getLastSignAttempt());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    assertEquals(LocalDateTime.of(2008, 8, 11, 13, 43, 51), user.getRegisterTime());
    entityManager.refresh(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(rows, view.users().getRowCount());
    assertEquals(email, view.email(0));
  }

  @Test
  public void update_Cancel() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).id(ID);
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.doubleClickUser(0);

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.cancel().click();
    waitUntil(driver -> !dialog.isOpen());
    assertFalse(repository.findByEmail(email).isPresent());
    assertEquals(rows, view.users().getRowCount());
  }

  @Test
  public void add() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).id(ID);
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.add().click();

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.save().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email).get();
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertNull(user.getLastSignAttempt());
    assertNull(user.getLocale());
    assertTrue(user.getRegisterTime().isAfter(LocalDateTime.now().minusSeconds(60)));
    assertTrue(user.getRegisterTime().isBefore(LocalDateTime.now().plusSeconds(60)));
    entityManager.refresh(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(rows + 1, view.users().getRowCount());
  }

  @Test
  public void add_Cancel() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).id(ID);
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.add().click();

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.cancel().click();
    waitUntil(driver -> !dialog.isOpen());
    assertFalse(repository.findByEmail(email).isPresent());
    assertEquals(rows, view.users().getRowCount());
  }
}
