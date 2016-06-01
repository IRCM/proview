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

package ca.qc.ircm.proview.user.web.integration;

import static ca.qc.ircm.proview.user.QUser.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.security.PasswordVersion;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class RegisterViewTest extends RegisterPageObject {
  @Inject
  private SecurityConfiguration securityConfiguration;
  @Inject
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @PersistenceContext
  private EntityManager entityManager;
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
  private String phoneNumber = "514-555-5555";
  private String phoneExtension = "234";

  private User getUser(String email) {
    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.email.eq(email));
    return query.fetchOne();
  }

  @Test
  public void title() throws Throwable {
    open();

    Set<Locale> locales = WebConstants.getLocales();
    Set<String> titles = new HashSet<>();
    for (Locale locale : locales) {
      titles.add(new MessageResource(RegisterView.class, locale).message("title"));
    }
    assertTrue(titles.contains(getDriver().getTitle()));
  }

  @Test
  public void fieldPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = headerLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = userPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = emailField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = nameField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = passwordField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = confirmPasswordField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = laboratoryPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = newLaboratoryField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = managerEmailField().getLocation().y;
    assertTrue(previous < current);
    setNewLaboratory(true);
    current = organizationField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = laboratoryNameField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = addressPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = addressLineField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = townField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = stateField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = countryField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = postalCodeField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = clearAddressButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = phoneNumberPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = phoneNumberField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = phoneExtensionField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = registerHeaderLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = registerButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = requiredLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
  }

  @Test
  public void defaultAddress() throws Throwable {
    open();

    assertEquals(defaultAddressConfiguration.getAddress(), getAddressLine());
    assertEquals(defaultAddressConfiguration.getTown(), getTown());
    assertEquals(defaultAddressConfiguration.getState(), getState());
    assertEquals(defaultAddressConfiguration.getPostalCode(), getPostalCode());
    assertEquals(defaultAddressConfiguration.getCountry(), getCountry());
  }

  private void setFields() {
    setEmail(email);
    setName(name);
    setPassword(password);
    setConfirmPassword(password);
    if (isNewLaboratory()) {
      setLaboratoryName(laboratoryName);
      setOrganization(organization);
    } else {
      setManagerEmail(managerEmail);
    }
    setAddressLine(addressLine);
    setTown(town);
    setState(state);
    setCountry(country);
    setPostalCode(postalCode);
    setPhoneNumber(phoneNumber);
    setPhoneExtension(phoneExtension);
  }

  @Test
  public void register_Error() throws Throwable {
    open();

    clickRegister();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void register_ExistingLaboratory() throws Throwable {
    open();
    setFields();

    clickRegister();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
    User user = getUser(email);
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
    assertEquals(PhoneNumberType.WORK, userPhoneNumber.getType());
    assertEquals(phoneNumber, userPhoneNumber.getNumber());
    assertEquals(phoneExtension, userPhoneNumber.getExtension());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertTrue(notification.getCaption().contains(email));
  }

  @Test
  public void register_NewLaboratory() throws Throwable {
    open();
    setNewLaboratory(true);
    setFields();

    clickRegister();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
    User user = getUser(email);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    Laboratory laboratory = user.getLaboratory();
    assertNotNull(laboratory.getId());
    assertEquals(laboratoryName, laboratory.getName());
    assertEquals(organization, laboratory.getOrganization());
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
    assertEquals(PhoneNumberType.WORK, userPhoneNumber.getType());
    assertEquals(phoneNumber, userPhoneNumber.getNumber());
    assertEquals(phoneExtension, userPhoneNumber.getExtension());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertTrue(notification.getCaption().contains(email));
  }
}
