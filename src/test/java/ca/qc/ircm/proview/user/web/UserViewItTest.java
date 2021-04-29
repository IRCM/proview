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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.user.web.UserView.ID;
import static ca.qc.ircm.proview.user.web.UserView.SAVED;
import static ca.qc.ircm.proview.user.web.UserView.VIEW_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.web.AccessDeniedError;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserViewItTest extends AbstractTestBenchTestCase {
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
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

  private void open(Long id) {
    openView(VIEW_NAME, Objects.toString(id, ""));
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(SigninView.class, locale).message(TITLE,
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(AccessDeniedError.class, locale).message(TITLE,
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() throws Throwable {
    open();

    assertEquals(resources(UserView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(UserView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UserViewElement view = $(UserViewElement.class).id(ID);
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.userForm()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void update() throws Throwable {
    open(2L);
    UserViewElement view = $(UserViewElement.class).id(ID);
    final Locale locale = currentLocale();

    view.userForm().email().setValue(email);
    view.userForm().name().setValue(name);
    view.userForm().password().setValue(password);
    view.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    view.userForm().laboratory().selectByText(laboratory.getName());
    view.userForm().address().setValue(addressLine);
    view.userForm().town().setValue(town);
    view.userForm().state().setValue(state);
    view.userForm().country().setValue(country);
    view.userForm().postalCode().setValue(postalCode);
    view.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    view.userForm().number().setValue(number);
    view.userForm().extension().setValue(extension);
    view.save().click();
    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(UserView.class);
    assertEquals(resources.message(SAVED, name), notification.getText());
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
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(viewUrl(UsersView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void add() throws Throwable {
    open();
    UserViewElement view = $(UserViewElement.class).id(ID);
    final Locale locale = currentLocale();

    view.userForm().email().setValue(email);
    view.userForm().name().setValue(name);
    view.userForm().password().setValue(password);
    view.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    view.userForm().laboratory().selectByText(laboratory.getName());
    view.userForm().address().setValue(addressLine);
    view.userForm().town().setValue(town);
    view.userForm().state().setValue(state);
    view.userForm().country().setValue(country);
    view.userForm().postalCode().setValue(postalCode);
    view.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    view.userForm().number().setValue(number);
    view.userForm().extension().setValue(extension);
    view.save().click();
    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(UserView.class);
    assertEquals(resources.message(SAVED, name), notification.getText());
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
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    assertEquals(viewUrl(UsersView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
