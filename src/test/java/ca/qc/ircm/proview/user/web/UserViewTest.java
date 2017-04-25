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

import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.user.web.UserViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.security.PasswordVersion;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class UserViewTest extends UserPageObject {
  @Inject
  private SecurityConfiguration securityConfiguration;
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Value("${spring.application.name}")
  private String applicationName;
  private String email = "unit.test@ircm.qc.ca";
  private String name = "Unit Test";
  private String password = "unittestpassword";
  private String addressLine = "123 Papineau";
  private String town = "Laval";
  private String state = "Ontario";
  private String country = "USA";
  private String postalCode = "12345";
  private PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private String phoneNumber = "514-555-5555";
  private String phoneExtension = "234";

  private User getUser(Long id) {
    JPAQuery<User> query = jpaQueryFactory.select(user);
    query.from(user);
    query.where(user.id.eq(id));
    return query.fetchOne();
  }

  @Test
  @WithSubject(anonymous = true)
  public void security_Anonymous() throws Throwable {
    openView(MainView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(
        resources(UserView.class).message(TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> headerLabel()).isPresent());
    assertTrue(optional(() -> userPanel()).isPresent());
    assertTrue(optional(() -> emailField()).isPresent());
    assertTrue(optional(() -> nameField()).isPresent());
    assertTrue(optional(() -> passwordField()).isPresent());
    assertTrue(optional(() -> confirmPasswordField()).isPresent());
    assertTrue(optional(() -> laboratoryPanel()).isPresent());
    assertFalse(optional(() -> newLaboratoryField()).isPresent());
    assertFalse(optional(() -> managerEmailField()).isPresent());
    assertTrue(optional(() -> organizationField()).isPresent());
    assertTrue(optional(() -> laboratoryNameField()).isPresent());
    assertTrue(optional(() -> addressPanel()).isPresent());
    assertTrue(optional(() -> addressLineField()).isPresent());
    assertTrue(optional(() -> townField()).isPresent());
    assertTrue(optional(() -> stateField()).isPresent());
    assertTrue(optional(() -> countryField()).isPresent());
    assertTrue(optional(() -> postalCodeField()).isPresent());
    assertTrue(optional(() -> clearAddressButton()).isPresent());
    assertTrue(optional(() -> phoneNumberPanel()).isPresent());
    assertTrue(optional(() -> phoneTypeField()).isPresent());
    assertTrue(optional(() -> phoneNumberField()).isPresent());
    assertTrue(optional(() -> phoneExtensionField()).isPresent());
    assertTrue(optional(() -> removePhoneNumberButton()).isPresent());
    assertTrue(optional(() -> addPhoneNumberButton()).isPresent());
    assertTrue(optional(() -> saveButton()).isPresent());
  }

  @Test
  public void defaultValues() throws Throwable {
    open();
    User user = getUser(10L);

    assertEquals(user.getEmail(), getEmail());
    assertEquals(user.getName(), getName());
    assertEquals("", getPassword());
    assertEquals("", getConfirmPassword());
    assertEquals(user.getLaboratory().getOrganization(), getOrganization());
    assertEquals(user.getLaboratory().getName(), getLaboratoryName());
    Address address = user.getAddress();
    assertEquals(address.getLine(), getAddressLine());
    assertEquals(address.getTown(), getTown());
    assertEquals(address.getState(), getState());
    assertEquals(address.getCountry(), getCountry());
    assertEquals(address.getPostalCode(), getPostalCode());
    PhoneNumber phone = user.getPhoneNumbers().get(0);
    assertEquals(phone.getType(), getPhoneType());
    assertEquals(phone.getNumber(), getPhoneNumber());
    assertTrue(getPhoneExtension().isEmpty());
  }

  private void setFields() {
    setEmail(email);
    setName(name);
    setPassword(password);
    setConfirmPassword(password);
    setAddressLine(addressLine);
    setTown(town);
    setState(state);
    setCountry(country);
    setPostalCode(postalCode);
    setPhoneType(phoneType);
    setPhoneNumber(phoneNumber);
    setPhoneExtension(phoneExtension);
  }

  @Test
  public void save_Error() throws Throwable {
    open();
    setEmail("");

    clickSave();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void save() throws Throwable {
    open();
    setFields();

    clickSave();

    assertEquals(viewUrl(UserView.VIEW_NAME), getDriver().getCurrentUrl());
    User user = getUser(10L);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    Laboratory laboratory = user.getLaboratory();
    assertEquals((Long) 2L, laboratory.getId());
    PasswordVersion passwordVersion = securityConfiguration.getPasswordVersion();
    assertNotNull(user.getSalt());
    SimpleHash hash = new SimpleHash(passwordVersion.getAlgorithm(), password,
        Hex.decode(user.getSalt()), passwordVersion.getIterations());
    assertEquals(hash.toHex(), user.getHashedPassword());
    assertEquals((Integer) passwordVersion.getVersion(), user.getPasswordVersion());
    assertNotNull(user.getLocale());
    Address userAddress = user.getAddress();
    assertEquals(addressLine, userAddress.getLine());
    assertEquals(town, userAddress.getTown());
    assertEquals(state, userAddress.getState());
    assertEquals(postalCode, userAddress.getPostalCode());
    assertEquals(country, userAddress.getCountry());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber userPhoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(phoneType, userPhoneNumber.getType());
    assertEquals(phoneNumber, userPhoneNumber.getNumber());
    assertEquals(phoneExtension, userPhoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertTrue(notification.getCaption().contains(email));
  }
}
