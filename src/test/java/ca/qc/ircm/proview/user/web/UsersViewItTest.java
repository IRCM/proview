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

import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.security.web.AccessDeniedError;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.time.TimeConverter;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewItTest extends AbstractTestBenchTestCase {
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Value("${spring.application.name}")
  private String applicationName;
  private String email = "test@ircm.qc.ca";
  private String name = "Test User";
  private String password = "test_password";
  private String laboratoryName = "Test Laboratory";
  private String addressLine = "200 My Street";
  private String town = "My Town";
  private String state = "My State";
  private String country = "My Country";
  private String postalCode = "12345";

  private void open() {
    openView(UsersView.VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new MessageResource(SigninView.class, locale).message(TITLE,
            new MessageResource(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new MessageResource(AccessDeniedError.class, locale).message(TITLE,
            new MessageResource(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(UsersView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).first();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.switchFailed()).isPresent());
    assertTrue(optional(() -> view.addButton()).isPresent());
    assertTrue(optional(() -> view.switchUserButton()).isPresent());
  }

  @Test
  public void update() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).first();
    final int rows = view.users().getRowCount();

    view.doubleClickUser(0);

    assertTrue(optional(() -> $(UserDialogElement.class).first()).isPresent());
    UserDialogElement dialog = $(UserDialogElement.class).first();
    dialog.email().setValue(email);
    dialog.name().setValue(name);
    dialog.password().setValue(password);
    dialog.passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.laboratory().selectByText(laboratory.getName());
    dialog.laboratoryName().setValue(laboratoryName);
    dialog.address().setValue(addressLine);
    dialog.town().setValue(town);
    dialog.state().setValue(state);
    dialog.country().setValue(country);
    dialog.postalCode().setValue(postalCode);
    dialog.save().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertEquals(TimeConverter.toInstant(LocalDateTime.of(2019, 5, 11, 13, 43, 51)),
        user.getLastSignAttempt());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    assertEquals(TimeConverter.toInstant(LocalDateTime.of(2008, 8, 11, 13, 43, 51)),
        user.getRegisterTime());
    assertEquals(laboratory, user.getLaboratory());
    assertEquals(laboratoryName, user.getLaboratory().getName());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(PhoneNumberType.WORK, user.getPhoneNumbers().get(0).getType());
    assertEquals("514-555-5555", user.getPhoneNumbers().get(0).getNumber());
    assertNull(user.getPhoneNumbers().get(0).getExtension());
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
    UsersViewElement view = $(UsersViewElement.class).first();
    final int rows = view.users().getRowCount();

    view.doubleClickUser(0);

    assertTrue(optional(() -> $(UserDialogElement.class).first()).isPresent());
    UserDialogElement dialog = $(UserDialogElement.class).first();
    dialog.email().setValue(email);
    dialog.name().setValue(name);
    dialog.password().setValue(password);
    dialog.passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.laboratory().selectByText(laboratory.getName());
    dialog.laboratoryName().setValue(laboratoryName);
    dialog.address().setValue(addressLine);
    dialog.town().setValue(town);
    dialog.state().setValue(state);
    dialog.country().setValue(country);
    dialog.postalCode().setValue(postalCode);
    dialog.cancel().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email);
    assertNull(user);
    assertEquals(rows, view.users().getRowCount());
  }

  @Test
  public void add() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).first();
    final int rows = view.users().getRowCount();

    view.clickAdd();

    assertTrue(optional(() -> $(UserDialogElement.class).first()).isPresent());
    UserDialogElement dialog = $(UserDialogElement.class).first();
    dialog.email().setValue(email);
    dialog.name().setValue(name);
    dialog.password().setValue(password);
    dialog.passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.laboratory().selectByText(laboratory.getName());
    dialog.laboratoryName().setValue(laboratoryName);
    dialog.address().setValue(addressLine);
    dialog.town().setValue(town);
    dialog.state().setValue(state);
    dialog.country().setValue(country);
    dialog.postalCode().setValue(postalCode);
    dialog.save().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertNull(user.getLastSignAttempt());
    assertNull(user.getLocale());
    assertTrue(user.getRegisterTime().isAfter(Instant.now().minusSeconds(60)));
    assertTrue(user.getRegisterTime().isBefore(Instant.now().plusSeconds(60)));
    assertEquals(laboratory, user.getLaboratory());
    assertEquals(laboratoryName, user.getLaboratory().getName());
    assertTrue(user.getPhoneNumbers().isEmpty());
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
    UsersViewElement view = $(UsersViewElement.class).first();
    final int rows = view.users().getRowCount();

    view.clickAdd();

    assertTrue(optional(() -> $(UserDialogElement.class).first()).isPresent());
    UserDialogElement dialog = $(UserDialogElement.class).first();
    dialog.email().setValue(email);
    dialog.name().setValue(name);
    dialog.password().setValue(password);
    dialog.passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    dialog.laboratory().selectByText(laboratory.getName());
    dialog.laboratoryName().setValue(laboratoryName);
    dialog.address().setValue(addressLine);
    dialog.town().setValue(town);
    dialog.state().setValue(state);
    dialog.country().setValue(country);
    dialog.postalCode().setValue(postalCode);
    dialog.cancel().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email);
    assertNull(user);
    assertEquals(rows, view.users().getRowCount());
  }

  @Test
  public void switchUser() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).first();
    view.clickUser(1);

    view.clickSwitchUser();

    Locale locale = currentLocale();
    assertEquals(
        new MessageResource(MainView.class, locale).message(TITLE,
            new MessageResource(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @Ignore("Admins are allowed to switch to another admin right now")
  public void switchUser_Fail() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).first();
    view.clickUser(0);

    view.clickSwitchUser();

    assertTrue(optional(() -> view.switchFailed()).isPresent());
  }
}