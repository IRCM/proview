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
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.SpringConfiguration;
import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.laboratory.Laboratory;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserServiceTest {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
  private UserService userServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
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
    SpringConfiguration springConfiguration = new SpringConfiguration();
    TemplateEngine templateEngine = springConfiguration.templateEngine();
    userServiceImpl = new UserService(entityManager, authenticationService, templateEngine,
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

  private <D extends Data> D find(Collection<D> datas, long id) {
    for (D data : datas) {
      if (data.getId() == id) {
        return data;
      }
    }
    return null;
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
    User user = userServiceImpl.get(3L);

    verify(authorizationService).checkUserReadPermission(user);
    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
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
    User user = userServiceImpl.get((Long) null);

    assertNull(user);
  }

  @Test
  public void get_Email() throws Throwable {
    User user = userServiceImpl.get("benoit.coulombe@ircm.qc.ca");

    verify(authorizationService).checkUserReadPermission(user);
    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
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
    User user = userServiceImpl.get((String) null);

    assertNull(user);
  }

  @Test
  public void exists_Email_True() throws Throwable {
    boolean exists = userServiceImpl.exists("christian.poitras@ircm.qc.ca");

    assertEquals(true, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_False() throws Throwable {
    boolean exists = userServiceImpl.exists("abc@ircm.qc.ca");

    assertEquals(false, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_Null() throws Throwable {
    boolean exists = userServiceImpl.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void isManager_Robot() throws Throwable {
    boolean manager = userServiceImpl.isManager("proview@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Admin() throws Throwable {
    boolean manager = userServiceImpl.isManager("christian.poitras@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Manager() throws Throwable {
    boolean manager = userServiceImpl.isManager("benoit.coulombe@ircm.qc.ca");

    assertEquals(true, manager);
  }

  @Test
  public void isManager_NonManager() throws Throwable {
    boolean manager = userServiceImpl.isManager("james.johnson@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Invalid() throws Throwable {
    boolean manager = userServiceImpl.isManager("nicole.francis@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Inactive() throws Throwable {
    boolean manager = userServiceImpl.isManager("marie.trudel@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Null() throws Throwable {
    boolean manager = userServiceImpl.isManager(null);

    assertEquals(false, manager);
  }

  @Test
  public void all_InvalidInLaboratory() throws Throwable {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyInvalid().inLaboratory(laboratory);

    List<User> users = userServiceImpl.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(1, users.size());
    assertNotNull(find(users, 7));
  }

  @Test
  public void all_Invalid() throws Throwable {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyInvalid();

    List<User> users = userServiceImpl.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, users.size());
    assertNotNull(find(users, 6));
    assertNotNull(find(users, 7));
  }

  @Test
  public void all_ValidInLaboratory() throws Throwable {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyValid().inLaboratory(laboratory);

    List<User> users = userServiceImpl.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(4, users.size());
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 10));
    assertNotNull(find(users, 12));
    assertNotNull(find(users, 27));
  }

  @Test
  public void all_Valid() throws Throwable {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyValid();

    List<User> users = userServiceImpl.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(12, users.size());
    assertNotNull(find(users, 2));
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 4));
    assertNotNull(find(users, 5));
    assertNotNull(find(users, 10));
    assertNotNull(find(users, 11));
    assertNotNull(find(users, 12));
    assertNotNull(find(users, 19));
    assertNotNull(find(users, 24));
    assertNotNull(find(users, 25));
    assertNotNull(find(users, 26));
    assertNotNull(find(users, 27));
  }

  @Test
  public void all_NonAdmin() throws Throwable {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyNonAdmin();

    List<User> users = userServiceImpl.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(10, users.size());
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 6));
    assertNotNull(find(users, 7));
    assertNotNull(find(users, 10));
    assertNotNull(find(users, 12));
    assertNotNull(find(users, 19));
    assertNotNull(find(users, 24));
    assertNotNull(find(users, 25));
    assertNotNull(find(users, 26));
    assertNotNull(find(users, 27));
  }

  @Test
  public void all_Null() throws Throwable {
    List<User> users = userServiceImpl.all(null);

    authorizationService.checkAdminRole();
    assertEquals(14, users.size());
    assertNotNull(find(users, 2));
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 4));
    assertNotNull(find(users, 5));
    assertNotNull(find(users, 6));
    assertNotNull(find(users, 7));
    assertNotNull(find(users, 10));
    assertNotNull(find(users, 11));
    assertNotNull(find(users, 12));
    assertNotNull(find(users, 19));
    assertNotNull(find(users, 24));
    assertNotNull(find(users, 25));
    assertNotNull(find(users, 26));
    assertNotNull(find(users, 27));
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

    userServiceImpl.register(user, "password", null, registerUserWebContext());

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(authorizationService).getCurrentUser();
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = manager.getLaboratory();
    entityManager.refresh(laboratory);
    assertNull(find(laboratory.getManagers(), user.getId()));
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

    userServiceImpl.register(user, "password", manager, registerUserWebContext());

    entityManager.flush();
    verifyZeroInteractions(authorizationService);
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.refresh(laboratory);
    assertEquals(2, laboratory.getManagers().size());
    assertNotNull(find(laboratory.getManagers(), 3L));
    assertNotNull(find(laboratory.getManagers(), 27L));
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
    MessageResource messageResource =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.CANADA_FRENCH);
    verify(email).setSubject(messageResource.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    htmlContent.contains("Christian");
    htmlContent.contains("Poitras");
    textContent.contains("Christian");
    textContent.contains("Poitras");
    String url = applicationConfiguration.getUrl(validateUserUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
    assertFalse(textContent.contains("$resourceTool"));
    assertFalse(htmlContent.contains("$resourceTool"));
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
      userServiceImpl.register(user, "password", manager, registerUserWebContext());
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

    userServiceImpl.register(user, "password", null, registerUserWebContext());

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
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
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
    MessageResource frenchMessageResource =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.CANADA_FRENCH);
    subjects.add(frenchMessageResource.message("newLaboratory.email.subject"));
    MessageResource englishMessageResource =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", Locale.ENGLISH);
    subjects.add(englishMessageResource.message("newLaboratory.email.subject"));
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, times(3)).setSubject(stringCaptor.capture());
    for (String subject : stringCaptor.getAllValues()) {
      assertTrue(subjects.contains(subject));
    }
    stringCaptor.getAllValues().clear();
    verify(email, times(3)).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    String url = applicationConfiguration.getUrl(validateUserUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void update() throws Throwable {
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
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

    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.US);
    address = new Address();
    address.setLine("110 av des Pins West");
    address.setTown("Montreal");
    address.setState("Quebec");
    address.setPostalCode("H2W 1R8");
    address.setCountry("USA");
    user.setAddress(address);
    phoneNumber = new PhoneNumber();
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

    userServiceImpl.update(user, null);

    entityManager.flush();
    verify(authorizationService).checkUserWritePermission(user);
    user = entityManager.find(User.class, user.getId());
    entityManager.refresh(user);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
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

    userServiceImpl.update(user, "unit_test_password");

    entityManager.flush();
    verify(authorizationService).checkUserWritePasswordPermission(user);
    verify(authenticationService).hashPassword("unit_test_password");
    user = entityManager.find(User.class, 4L);
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
  }

  @Test
  public void validate() throws Throwable {
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userServiceImpl.validate(users, homeWebContext());

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
    MessageResource messageResource =
        new MessageResource(UserService.class.getName() + "_ValidateEmail", Locale.CANADA_FRENCH);
    verify(email).setSubject(messageResource.message("email.subject"));
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    htmlContent.contains(user.getEmail());
    textContent.contains(user.getEmail());
    String url = applicationConfiguration.getUrl(homeUrl);
    assertTrue(textContent.contains(url));
    assertTrue(htmlContent.contains(url));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
    assertFalse(textContent.contains("$resourceTool"));
    assertFalse(htmlContent.contains("$resourceTool"));
  }

  @Test
  public void activate() throws Throwable {
    User user = entityManager.find(User.class, 12L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    userServiceImpl.activate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 12L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_Manager() throws Throwable {
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);
    when(authorizationService.hasManagerRole(any(User.class))).thenReturn(true);

    try {
      userServiceImpl.deactivate(users);
      fail("Expected DeactivateManagerException");
    } catch (DeactivateManagerException e) {
      // Ignore.
    }
  }

  @Test
  public void deactivate() throws Throwable {
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    assertEquals(true, user.isActive());

    Collection<User> users = new LinkedList<>();
    users.add(user);
    userServiceImpl.deactivate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(authorizationService).hasManagerRole(user);
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 10L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deleteValid() throws Throwable {
    User user = entityManager.find(User.class, 5L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    try {
      userServiceImpl.delete(users);
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

    userServiceImpl.delete(users);

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

    userServiceImpl.delete(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = entityManager.find(User.class, 6L);
    assertNull(user);
    Laboratory laboratory = entityManager.find(Laboratory.class, 3L);
    assertNull(laboratory);
  }
}
