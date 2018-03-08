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

package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.SpringConfiguration;
import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.HashedPassword;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserServiceTest {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
  private UserService userService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private SpringConfiguration springConfiguration;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private ApplicationConfiguration applicationConfiguration;
  @Mock
  private EmailService emailService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private CacheFlusher cacheFlusher;
  @Mock
  private MimeMessageHelper email;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private HashedPassword hashedPassword;
  private String validateUserUrl = "/validate/user";
  private String homeUrl = "/";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    TemplateEngine templateEngine = springConfiguration.templateEngine();
    userService = new UserService(entityManager, authenticationService, templateEngine,
        emailService, cacheFlusher, applicationConfiguration, authorizationService);
    when(applicationConfiguration.getUrl(any(String.class))).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return "http://proview.ircm.qc.ca/proview" + invocation.getArguments()[0];
      }
    });
    hashedPassword = new HashedPassword("da78f3a74658706", "4ae8470fc73a83f369fed012", 1);
    when(authenticationService.hashPassword(any(String.class))).thenReturn(hashedPassword);
    when(emailService.htmlEmail()).thenReturn(email);
  }

  private <D extends PhoneNumber> D findPhoneNumber(Collection<D> datas, PhoneNumberType type) {
    for (D data : datas) {
      if (data.getType() == type) {
        return data;
      }
    }
    return null;
  }

  private RegisterUserWebContext registerUserWebContext() {
    return locale -> validateUserUrl;
  }

  private HomeWebContext homeWebContext() {
    return locale -> homeUrl;
  }

  @Test
  public void get_Id() throws Throwable {
    User user = userService.get(3L);

    verify(authorizationService).checkUserReadPermission(user);
    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    Laboratory laboratory = user.getLaboratory();
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals("Translational Proteomics", laboratory.getName());
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    Address address = user.getAddress();
    assertEquals("110, avenue des Pins Ouest", address.getLine());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5556", phoneNumber.getNumber());
    assertEquals(null, phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void get_NullId() throws Throwable {
    User user = userService.get((Long) null);

    assertNull(user);
  }

  @Test
  public void get_Email() throws Throwable {
    User user = userService.get("benoit.coulombe@ircm.qc.ca");

    verify(authorizationService).checkUserReadPermission(user);
    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    Laboratory laboratory = user.getLaboratory();
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals("Translational Proteomics", laboratory.getName());
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    Address address = user.getAddress();
    assertEquals("110, avenue des Pins Ouest", address.getLine());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5556", phoneNumber.getNumber());
    assertEquals(null, phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void get_NullEmail() throws Throwable {
    User user = userService.get((String) null);

    assertNull(user);
  }

  @Test
  public void exists_Email_True() throws Throwable {
    boolean exists = userService.exists("christian.poitras@ircm.qc.ca");

    assertEquals(true, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_False() throws Throwable {
    boolean exists = userService.exists("abc@ircm.qc.ca");

    assertEquals(false, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_Null() throws Throwable {
    boolean exists = userService.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void isManager_Robot() throws Throwable {
    boolean manager = userService.isManager("proview@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Admin() throws Throwable {
    boolean manager = userService.isManager("christian.poitras@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Manager() throws Throwable {
    boolean manager = userService.isManager("benoit.coulombe@ircm.qc.ca");

    assertEquals(true, manager);
  }

  @Test
  public void isManager_NonManager() throws Throwable {
    boolean manager = userService.isManager("james.johnson@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Invalid() throws Throwable {
    boolean manager = userService.isManager("nicole.francis@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Inactive() throws Throwable {
    boolean manager = userService.isManager("marie.trudel@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Null() throws Throwable {
    boolean manager = userService.isManager(null);

    assertEquals(false, manager);
  }

  @Test
  public void all_InvalidInLaboratory() throws Throwable {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    UserFilter parameters = new UserFilter();
    parameters.valid = false;
    parameters.laboratory = laboratory;

    List<User> users = userService.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(1, users.size());
    assertTrue(find(users, 7).isPresent());
  }

  @Test
  public void all_Invalid() throws Throwable {
    UserFilter parameters = new UserFilter();
    parameters.valid = false;

    List<User> users = userService.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, users.size());
    assertTrue(find(users, 6).isPresent());
    assertTrue(find(users, 7).isPresent());
  }

  @Test
  public void all_ValidInLaboratory() throws Throwable {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    UserFilter parameters = new UserFilter();
    parameters.valid = true;
    parameters.laboratory = laboratory;

    List<User> users = userService.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(4, users.size());
    assertTrue(find(users, 3).isPresent());
    assertTrue(find(users, 10).isPresent());
    assertTrue(find(users, 12).isPresent());
    assertTrue(find(users, 27).isPresent());
  }

  @Test
  public void all_Valid() throws Throwable {
    UserFilter parameters = new UserFilter();
    parameters.valid = true;

    List<User> users = userService.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(12, users.size());
    assertTrue(find(users, 2).isPresent());
    assertTrue(find(users, 3).isPresent());
    assertTrue(find(users, 4).isPresent());
    assertTrue(find(users, 5).isPresent());
    assertTrue(find(users, 10).isPresent());
    assertTrue(find(users, 11).isPresent());
    assertTrue(find(users, 12).isPresent());
    assertTrue(find(users, 19).isPresent());
    assertTrue(find(users, 24).isPresent());
    assertTrue(find(users, 25).isPresent());
    assertTrue(find(users, 26).isPresent());
    assertTrue(find(users, 27).isPresent());
  }

  @Test
  public void all_NonAdmin() throws Throwable {
    UserFilter parameters = new UserFilter();
    parameters.admin = false;

    List<User> users = userService.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(10, users.size());
    assertTrue(find(users, 3).isPresent());
    assertTrue(find(users, 6).isPresent());
    assertTrue(find(users, 7).isPresent());
    assertTrue(find(users, 10).isPresent());
    assertTrue(find(users, 12).isPresent());
    assertTrue(find(users, 19).isPresent());
    assertTrue(find(users, 24).isPresent());
    assertTrue(find(users, 25).isPresent());
    assertTrue(find(users, 26).isPresent());
    assertTrue(find(users, 27).isPresent());
  }

  @Test
  public void all_Null() throws Throwable {
    List<User> users = userService.all(null);

    authorizationService.checkAdminRole();
    assertEquals(14, users.size());
    assertTrue(find(users, 2).isPresent());
    assertTrue(find(users, 3).isPresent());
    assertTrue(find(users, 4).isPresent());
    assertTrue(find(users, 5).isPresent());
    assertTrue(find(users, 6).isPresent());
    assertTrue(find(users, 7).isPresent());
    assertTrue(find(users, 10).isPresent());
    assertTrue(find(users, 11).isPresent());
    assertTrue(find(users, 12).isPresent());
    assertTrue(find(users, 19).isPresent());
    assertTrue(find(users, 24).isPresent());
    assertTrue(find(users, 25).isPresent());
    assertTrue(find(users, 26).isPresent());
    assertTrue(find(users, 27).isPresent());
  }

  @Test
  public void register_Admin() throws Throwable {
    final User manager = entityManager.find(User.class, 1L);
    when(authorizationService.getCurrentUser()).thenReturn(manager);
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    user.setAdmin(true);
    Address address = new Address();
    address.setLine("110 av des Pins Ouest");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);

    userService.register(user, "password", null, registerUserWebContext());

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(authorizationService).getCurrentUser();
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = manager.getLaboratory();
    entityManager.refresh(laboratory);
    assertFalse(find(laboratory.getManagers(), user.getId()).isPresent());
    assertNotNull(user.getId());
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals((Long) 1L, user.getLaboratory().getId());
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    address = user.getAddress();
    assertEquals("110 av des Pins Ouest", address.getLine());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(1, user.getPhoneNumbers().size());
    phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5500", phoneNumber.getNumber());
    assertEquals("3228", phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(true, user.isAdmin());

    verifyZeroInteractions(emailService);
  }

  @Test
  public void register_ExistingLaboratory() throws Throwable {
    final User manager = new User();
    manager.setEmail("benoit.coulombe@ircm.qc.ca");
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    Address address = new Address();
    address.setLine("110 av des Pins Ouest");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);

    userService.register(user, "password", manager, registerUserWebContext());

    entityManager.flush();
    verifyZeroInteractions(authorizationService);
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.refresh(laboratory);
    assertEquals(2, laboratory.getManagers().size());
    assertTrue(find(laboratory.getManagers(), 3L).isPresent());
    assertTrue(find(laboratory.getManagers(), 27L).isPresent());
    assertNotNull(user.getId());
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    address = user.getAddress();
    assertEquals("110 av des Pins Ouest", address.getLine());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(1, user.getPhoneNumbers().size());
    phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5500", phoneNumber.getNumber());
    assertEquals("3228", phoneNumber.getExtension());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    verify(email).addTo("benoit.coulombe@ircm.qc.ca");
    MessageResource resources =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.CANADA_FRENCH);
    verify(email).setSubject(resources.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains(resources.message("header", user.getName())));
    assertTrue(
        htmlContent.contains(StringUtils.escapeXml(resources.message("header", user.getName()))));
    assertTrue(textContent.contains(resources.message("header2")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("header2"))));
    assertTrue(textContent.contains(resources.message("message")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("message"))));
    assertTrue(textContent.contains(resources.message("footer")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("footer"))));
    String url = applicationConfiguration.getUrl(validateUserUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void register_ExistingLaboratory_EnglishEmail() throws Throwable {
    User updateLocale = entityManager.find(User.class, 3L);
    updateLocale.setLocale(Locale.CANADA);
    entityManager.flush();
    final User manager = new User();
    manager.setEmail("benoit.coulombe@ircm.qc.ca");
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    Address address = new Address();
    address.setLine("110 av des Pins Ouest");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);

    userService.register(user, "password", manager, registerUserWebContext());

    entityManager.flush();
    MessageResource resources =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.CANADA);
    verify(email).setSubject(resources.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains(resources.message("header", user.getName())));
    assertTrue(
        htmlContent.contains(StringUtils.escapeXml(resources.message("header", user.getName()))));
    assertTrue(textContent.contains(resources.message("header2")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("header2"))));
    assertTrue(textContent.contains(resources.message("message")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("message"))));
    assertTrue(textContent.contains(resources.message("footer")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("footer"))));
    String url = applicationConfiguration.getUrl(validateUserUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void register_ExistingLaboratory_Invalid() throws Throwable {
    final User manager = entityManager.find(User.class, 6L);
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    Address address = new Address();
    address.setLine("110 av des Pins Ouest");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);

    try {
      userService.register(user, "password", manager, registerUserWebContext());
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void register_NewLaboratory() throws Throwable {
    Laboratory laboratory = new Laboratory();
    laboratory.setOrganization("IRCM");
    laboratory.setName("Ribonucleoprotein Biochemistry");
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLaboratory(laboratory);
    user.setLocale(Locale.CANADA);
    Address address = new Address();
    address.setLine("110 av des Pins Ouest");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);

    userService.register(user, "password", null, registerUserWebContext());

    entityManager.flush();
    verifyZeroInteractions(authorizationService);
    verify(authenticationService).hashPassword("password");
    assertNotNull(laboratory.getId());
    assertNotNull(user.getId());
    laboratory = entityManager.find(Laboratory.class, laboratory.getId());
    entityManager.refresh(laboratory);
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    assertEquals(1, laboratory.getManagers().size());
    assertEquals(user.getId(), laboratory.getManagers().get(0).getId());
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    laboratory = user.getLaboratory();
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    assertEquals("Christian Poitras", laboratory.getDirector());
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
    assertEquals(Locale.CANADA, user.getLocale());
    address = user.getAddress();
    assertEquals("110 av des Pins Ouest", address.getLine());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(1, user.getPhoneNumbers().size());
    phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5500", phoneNumber.getNumber());
    assertEquals("3228", phoneNumber.getExtension());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    assertEquals(false, user.isAdmin());
    verify(emailService, times(3)).htmlEmail();
    verify(emailService, times(3)).send(email);
    Set<String> subjects = new HashSet<>();
    MessageResource frenchResources =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.CANADA_FRENCH);
    subjects.add(frenchResources.message("newLaboratory.email.subject"));
    MessageResource englishResources =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.ENGLISH);
    subjects.add(englishResources.message("newLaboratory.email.subject"));
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, times(3)).setSubject(stringCaptor.capture());
    assertEquals(1,
        stringCaptor.getAllValues().stream()
            .filter(
                subject -> subject.equals(frenchResources.message("newLaboratory.email.subject")))
            .count());
    assertEquals(2,
        stringCaptor.getAllValues().stream()
            .filter(
                subject -> subject.equals(englishResources.message("newLaboratory.email.subject")))
            .count());
    stringCaptor.getAllValues().clear();
    verify(email, times(3)).setText(stringCaptor.capture(), stringCaptor.capture());
    for (int i = 0; i < 6; i += 2) {
      String textContent = stringCaptor.getAllValues().get(i);
      String htmlContent = stringCaptor.getAllValues().get(i + 1);
      Locale locale = Locale.ENGLISH;
      if (textContent.contains(frenchResources.message("header2"))) {
        locale = Locale.CANADA_FRENCH;
      }
      MessageResource resources =
          new MessageResource(UserService.class.getName() + "_RegisterEmail", locale);
      assertTrue(textContent.contains(
          resources.message("newLaboratory.header", user.getName(), laboratory.getName())));
      assertTrue(htmlContent.contains(StringUtils.escapeXml(
          resources.message("newLaboratory.header", user.getName(), laboratory.getName()))));
      assertTrue(textContent.contains(resources.message("header2")));
      assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("header2"))));
      assertTrue(textContent.contains(resources.message("message")));
      assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("message"))));
      assertTrue(textContent.contains(resources.message("footer")));
      assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("footer"))));
      String url = applicationConfiguration.getUrl(validateUserUrl);
      assertTrue(textContent.contains(url));
      assertTrue(htmlContent.contains(url));
      assertFalse(textContent.contains("???"));
      assertFalse(htmlContent.contains("???"));
    }
  }

  @Test
  public void update() throws Throwable {
    User user = entityManager.find(User.class, 12L);
    entityManager.detach(user);
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.US);
    Address address = new Address();
    address.setLine("110 av des Pins West");
    address.setTown("Montreal");
    address.setState("Quebec");
    address.setPostalCode("H2W 1R8");
    address.setCountry("USA");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-987-5501");
    phoneNumber.setExtension("3218");
    PhoneNumber phoneNumber2 = new PhoneNumber();
    phoneNumber2.setType(PhoneNumberType.FAX);
    phoneNumber2.setNumber("514-987-5502");
    phoneNumber2.setExtension("1234");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    phoneNumbers.add(phoneNumber2);
    user.setPhoneNumbers(phoneNumbers);

    userService.update(user, null);

    entityManager.flush();
    verify(authorizationService).checkUserWritePermission(user);
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    Laboratory laboratory = entityManager.find(Laboratory.class, user.getLaboratory().getId());
    entityManager.refresh(laboratory);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
    assertEquals("IRCM", user.getLaboratory().getOrganization());
    assertEquals("Benoit Coulombe", user.getLaboratory().getDirector());
    assertEquals(Locale.US, user.getLocale());
    address = user.getAddress();
    assertEquals("110 av des Pins West", address.getLine());
    assertEquals("Montreal", address.getTown());
    assertEquals("Quebec", address.getState());
    assertEquals("H2W 1R8", address.getPostalCode());
    assertEquals("USA", address.getCountry());
    assertEquals(2, user.getPhoneNumbers().size());
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.WORK);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-987-5501", phoneNumber.getNumber());
    assertEquals("3218", phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.FAX);
    assertEquals("514-987-5502", phoneNumber.getNumber());
    assertEquals("1234", phoneNumber.getExtension());
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void update_Director() throws Throwable {
    when(authorizationService.hasManagerRole()).thenReturn(true);
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.US);
    Address address = new Address();
    address.setLine("110 av des Pins West");
    address.setTown("Montreal");
    address.setState("Quebec");
    address.setPostalCode("H2W 1R8");
    address.setCountry("USA");
    user.setAddress(address);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-987-5501");
    phoneNumber.setExtension("3218");
    PhoneNumber phoneNumber2 = new PhoneNumber();
    phoneNumber2.setType(PhoneNumberType.FAX);
    phoneNumber2.setNumber("514-987-5502");
    phoneNumber2.setExtension("1234");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    phoneNumbers.add(phoneNumber2);
    user.setPhoneNumbers(phoneNumbers);

    userService.update(user, null);

    entityManager.flush();
    verify(authorizationService).checkUserWritePermission(user);
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    Laboratory laboratory = entityManager.find(Laboratory.class, user.getLaboratory().getId());
    entityManager.refresh(laboratory);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
    assertEquals("IRCM", user.getLaboratory().getOrganization());
    assertEquals("Christian Poitras", user.getLaboratory().getDirector());
    assertEquals(Locale.US, user.getLocale());
    address = user.getAddress();
    assertEquals("110 av des Pins West", address.getLine());
    assertEquals("Montreal", address.getTown());
    assertEquals("Quebec", address.getState());
    assertEquals("H2W 1R8", address.getPostalCode());
    assertEquals("USA", address.getCountry());
    assertEquals(2, user.getPhoneNumbers().size());
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.WORK);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-987-5501", phoneNumber.getNumber());
    assertEquals("3218", phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.FAX);
    assertEquals("514-987-5502", phoneNumber.getNumber());
    assertEquals("1234", phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void updatePassword() throws Throwable {
    User user = entityManager.find(User.class, 4L);
    entityManager.detach(user);

    userService.update(user, "unit_test_password");

    entityManager.flush();
    verify(authorizationService).checkUserWritePermission(user);
    verify(authenticationService).hashPassword("unit_test_password");
    user = entityManager.find(User.class, 4L);
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
  }

  @Test
  public void update_Lab() throws Throwable {
    when(authorizationService.hasManagerRole()).thenReturn(true);
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);

    user.setEmail("unit_test@ircm.qc.ca");
    user.getLaboratory().setName("lab test");
    user.getLaboratory().setOrganization("organization test");

    userService.update(user, null);

    entityManager.flush();
    verify(authorizationService).checkUserWritePermission(user);
    verify(authorizationService).hasManagerRole();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    assertEquals(user.getId(), user.getId());
    assertEquals("lab test", user.getLaboratory().getName());
    assertEquals("organization test", user.getLaboratory().getOrganization());
  }

  @Test
  public void validate() throws Throwable {
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.validate(users, homeWebContext());

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 7L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
    verify(emailService).htmlEmail();
    verify(emailService).send(email);
    verify(email).addTo(user.getEmail());
    MessageResource resources =
        new MessageResource(UserService.class.getName() + "_ValidateEmail", Locale.CANADA_FRENCH);
    verify(email).setSubject(resources.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains(resources.message("header", user.getEmail())));
    assertTrue(
        htmlContent.contains(StringUtils.escapeXml(resources.message("header", user.getEmail()))));
    assertTrue(textContent.contains(resources.message("message")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("message"))));
    assertTrue(textContent.contains(resources.message("footer")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("footer"))));
    String url = applicationConfiguration.getUrl(homeUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void validate_EnglishEmail() throws Throwable {
    User updateLocale = entityManager.find(User.class, 7L);
    updateLocale.setLocale(Locale.CANADA);
    entityManager.flush();
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.validate(users, homeWebContext());

    entityManager.flush();
    MessageResource resources =
        new MessageResource(UserService.class.getName() + "_ValidateEmail", Locale.CANADA);
    verify(email).setSubject(resources.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains(resources.message("header", user.getEmail())));
    assertTrue(
        htmlContent.contains(StringUtils.escapeXml(resources.message("header", user.getEmail()))));
    assertTrue(textContent.contains(resources.message("message")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("message"))));
    assertTrue(textContent.contains(resources.message("footer")));
    assertTrue(htmlContent.contains(StringUtils.escapeXml(resources.message("footer"))));
    String url = applicationConfiguration.getUrl(homeUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void activate() throws Throwable {
    User user = entityManager.find(User.class, 12L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.activate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 12L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate() throws Throwable {
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.deactivate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 10L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_Manager() throws Throwable {
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.deactivate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 3L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_Admin() throws Throwable {
    User user = entityManager.find(User.class, 4L);
    entityManager.detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.deactivate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 4L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(true, user.isAdmin());
  }

  @Test(expected = IllegalArgumentException.class)
  public void deactivate_Robot() throws Throwable {
    User user = entityManager.find(User.class, 1L);
    entityManager.detach(user);
    assertEquals(true, user.isActive());

    Collection<User> users = new LinkedList<>();
    users.add(user);
    userService.deactivate(users);
  }

  @Test
  public void addManagerAdmin() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 1L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 5L);

    userService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = entityManager.find(Laboratory.class, 1L);
    List<User> testManagers = laboratory.getManagers();
    assertEquals(true, testManagers.contains(user));
  }

  @Test
  public void addManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    userService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void addManager_InactivatedUser() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 12L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));
    assertEquals(false, user.isActive());

    userService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    User testUser = entityManager.find(User.class, 12L);
    assertTrue(find(testManagers, user.getId()).isPresent());
    assertEquals(true, testUser.isActive());
  }

  @Test
  public void addManager_AlreadyManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertTrue(find(managers, user.getId()).isPresent());

    userService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void addManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    userService.addManager(laboratory, user);
  }

  @Test(expected = InvalidUserException.class)
  public void addManager_Invalid() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);

    userService.addManager(laboratory, user);
  }

  @Test
  public void addManager_DirectorChange() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    laboratory.setDirector("Test");
    entityManager.merge(laboratory);
    entityManager.flush();
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);

    userService.addManager(laboratory, user);

    entityManager.flush();
    laboratory = entityManager.find(Laboratory.class, 2L);
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void removeManagerAdmin() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 1L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    userService.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 1L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void removeManager_UnmanagedLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 3L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 6L);
    entityManager.detach(user);

    try {
      userService.removeManager(laboratory, user);
      fail("Expected UnmanagedLaboratoryException");
    } catch (UnmanagedLaboratoryException e) {
      // Ignore.
    }
  }

  @Test
  public void removeManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 27L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertTrue(find(managers, user.getId()).isPresent());

    userService.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void removeManager_AlreadyNotManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    userService.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void removeManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    userService.removeManager(laboratory, user);
  }

  @Test
  public void removeManager_DirectorChange() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    laboratory.setDirector("Test");
    entityManager.merge(laboratory);
    entityManager.flush();
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 27L);
    entityManager.detach(user);

    userService.removeManager(laboratory, user);

    entityManager.flush();
    laboratory = entityManager.find(Laboratory.class, 2L);
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void deleteValid() throws Throwable {
    User user = entityManager.find(User.class, 5L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    try {
      userService.delete(users);
      fail("Expected DeleteValidUserException");
    } catch (DeleteValidUserException e) {
      // Ignore.
    }
  }

  @Test
  public void delete() throws Throwable {
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.delete(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = entityManager.find(User.class, 7L);
    assertNull(user);
  }

  @Test
  public void delete_NewLaboratory() throws Throwable {
    User user = entityManager.find(User.class, 6L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userService.delete(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = entityManager.find(User.class, 6L);
    assertNull(user);
    Laboratory laboratory = entityManager.find(Laboratory.class, 3L);
    assertNull(laboratory);
  }
}
