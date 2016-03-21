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
import ca.qc.ircm.proview.mail.HtmlEmail;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.HashedPassword;
import ca.qc.ircm.proview.security.HashedPasswordDefault;
import ca.qc.ircm.proview.test.config.DatabaseRule;
import ca.qc.ircm.proview.test.config.RollBack;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;

@RollBack
public class UserServiceDefaultTest {
  private static class RegisterUserWebContextDefault implements RegisterUserWebContext {
    @Override
    public String getValidateUserUrl(Locale locale) {
      return "/validate/user";
    }
  }

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(UserServiceDefaultTest.class);
  private UserServiceDefault userServiceDefault;
  @ClassRule
  public static DatabaseRule jpaDatabaseRule = new DatabaseRule();
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
  @Captor
  private ArgumentCaptor<HtmlEmail> emailCaptor;
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(jpaDatabaseRule);
  private EntityManager entityManager;
  private HashedPassword hashedPassword;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    SpringConfiguration springConfiguration = new SpringConfiguration();
    TemplateEngine templateEngine = springConfiguration.templateEngine();
    userServiceDefault = new UserServiceDefault(jpaDatabaseRule.getEntityManager(),
        authenticationService, templateEngine, emailService, cacheFlusher, applicationConfiguration,
        authorizationService);
    entityManager = jpaDatabaseRule.getEntityManager();
    when(applicationConfiguration.getUrl(any(String.class))).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return "http://proview.ircm.qc.ca/proview" + invocation.getArguments()[0];
      }
    });
    hashedPassword = new HashedPasswordDefault("da78f3a74658706", "4ae8470fc73a83f369fed012", 1);
    when(authenticationService.hashPassword(any(String.class))).thenReturn(hashedPassword);
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

  @Test
  public void get_Id() throws Throwable {
    User user = userServiceDefault.get(3L);

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
    assertEquals(1, user.getAddresses().size());
    Address address = user.getAddresses().get(0);
    assertEquals("110, avenue des Pins Ouest", address.getAddress());
    assertEquals(null, address.getAddressSecond());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(true, address.isBilling());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5556", phoneNumber.getNumber());
    assertEquals(null, phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void get_NullId() throws Throwable {
    User user = userServiceDefault.get((Long) null);

    assertNull(user);
  }

  @Test
  public void get_Email() throws Throwable {
    User user = userServiceDefault.get("benoit.coulombe@ircm.qc.ca");

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
    assertEquals(1, user.getAddresses().size());
    Address address = user.getAddresses().get(0);
    assertEquals("110, avenue des Pins Ouest", address.getAddress());
    assertEquals(null, address.getAddressSecond());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(true, address.isBilling());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5556", phoneNumber.getNumber());
    assertEquals(null, phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void get_NullEmail() throws Throwable {
    User user = userServiceDefault.get((String) null);

    assertNull(user);
  }

  @Test
  public void exists_Email_True() throws Throwable {
    boolean exists = userServiceDefault.exists("christian.poitras@ircm.qc.ca");

    assertEquals(true, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_False() throws Throwable {
    boolean exists = userServiceDefault.exists("abc@ircm.qc.ca");

    assertEquals(false, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_Null() throws Throwable {
    boolean exists = userServiceDefault.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void isManager_Robot() throws Throwable {
    boolean manager = userServiceDefault.isManager("proview@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Proteomic() throws Throwable {
    boolean manager = userServiceDefault.isManager("christian.poitras@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Manager() throws Throwable {
    boolean manager = userServiceDefault.isManager("benoit.coulombe@ircm.qc.ca");

    assertEquals(true, manager);
  }

  @Test
  public void isManager_NonManager() throws Throwable {
    boolean manager = userServiceDefault.isManager("james.johnson@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Invalid() throws Throwable {
    boolean manager = userServiceDefault.isManager("nicole.francis@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Inactive() throws Throwable {
    boolean manager = userServiceDefault.isManager("marie.trudel@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Null() throws Throwable {
    boolean manager = userServiceDefault.isManager(null);

    assertEquals(false, manager);
  }

  @Test
  public void all_InvalidInLaboratory() throws Throwable {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyInvalid().inLaboratory(laboratory);

    List<User> users = userServiceDefault.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(1, users.size());
    assertNotNull(find(users, 4));
  }

  @Test
  public void all_Invalid() throws Throwable {
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyInvalid();

    List<User> users = userServiceDefault.all(parameters);

    verify(authorizationService).checkProteomicRole();
    assertEquals(2, users.size());
    assertNotNull(find(users, 4));
    assertNotNull(find(users, 10));
  }

  @Test
  public void all_ValidInLaboratory() throws Throwable {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyValid().inLaboratory(laboratory);

    List<User> users = userServiceDefault.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(4, users.size());
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 5));
    assertNotNull(find(users, 8));
    assertNotNull(find(users, 9));
  }

  @Test
  public void all_Valid() throws Throwable {
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyValid();

    List<User> users = userServiceDefault.all(parameters);

    verify(authorizationService).checkProteomicRole();
    assertEquals(8, users.size());
    assertNotNull(find(users, 2));
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 5));
    assertNotNull(find(users, 6));
    assertNotNull(find(users, 7));
    assertNotNull(find(users, 8));
    assertNotNull(find(users, 9));
    assertNotNull(find(users, 11));
  }

  @Test
  public void all_NonProteomic() throws Throwable {
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyNonProteomic();

    List<User> users = userServiceDefault.all(parameters);

    verify(authorizationService).checkProteomicRole();
    assertEquals(8, users.size());
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 4));
    assertNotNull(find(users, 5));
    assertNotNull(find(users, 7));
    assertNotNull(find(users, 8));
    assertNotNull(find(users, 9));
    assertNotNull(find(users, 10));
    assertNotNull(find(users, 11));
  }

  @Test
  public void all_Null() throws Throwable {
    List<User> users = userServiceDefault.all(null);

    authorizationService.checkProteomicRole();
    assertEquals(10, users.size());
    assertNotNull(find(users, 2));
    assertNotNull(find(users, 3));
    assertNotNull(find(users, 4));
    assertNotNull(find(users, 5));
    assertNotNull(find(users, 6));
    assertNotNull(find(users, 7));
    assertNotNull(find(users, 8));
    assertNotNull(find(users, 9));
    assertNotNull(find(users, 10));
    assertNotNull(find(users, 11));
  }

  @Test
  public void register_Proteomic() throws Throwable {
    final User manager = entityManager.find(User.class, 1L);
    when(authorizationService.getCurrentUser()).thenReturn(manager);
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    user.setProteomic(true);
    Address address = new Address();
    address.setAddress("110 av des Pins Ouest");
    address.setAddressSecond("2640");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    List<Address> addresses = new ArrayList<>();
    addresses.add(address);
    user.setAddresses(addresses);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);
    RegisterUserWebContext webContext = new RegisterUserWebContextDefault();

    userServiceDefault.register(user, "password", null, webContext);

    entityManager.flush();
    verify(authorizationService).checkProteomicManagerRole();
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
    assertEquals(1, user.getAddresses().size());
    address = user.getAddresses().get(0);
    assertEquals("110 av des Pins Ouest", address.getAddress());
    assertEquals("2640", address.getAddressSecond());
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
    assertEquals(true, user.isProteomic());

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
    address.setAddress("110 av des Pins Ouest");
    address.setAddressSecond("2640");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    List<Address> addresses = new ArrayList<>();
    addresses.add(address);
    user.setAddresses(addresses);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);
    RegisterUserWebContext webContext = new RegisterUserWebContextDefault();

    userServiceDefault.register(user, "password", manager, webContext);

    entityManager.flush();
    verifyZeroInteractions(authorizationService);
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.refresh(laboratory);
    assertEquals(2, laboratory.getManagers().size());
    assertNotNull(find(laboratory.getManagers(), 3L));
    assertNotNull(find(laboratory.getManagers(), 9L));
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
    assertEquals(1, user.getAddresses().size());
    address = user.getAddresses().get(0);
    assertEquals("110 av des Pins Ouest", address.getAddress());
    assertEquals("2640", address.getAddressSecond());
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
    assertEquals(false, user.isProteomic());
    verify(emailService).sendHtmlEmail(emailCaptor.capture());
    HtmlEmail email = emailCaptor.getValue();
    email.getReceivers().contains("benoit.coulombe@ircm.qc.ca");
    MessageResource messageResource =
        new MessageResource(UserServiceDefault.class.getName() + "_Email", Locale.CANADA_FRENCH);
    assertEquals(messageResource.message("email.subject"), email.getSubject());
    email.getHtmlMessage().contains("Christian");
    email.getHtmlMessage().contains("Poitras");
    email.getTextMessage().contains("Christian");
    email.getTextMessage().contains("Poitras");
    String url =
        applicationConfiguration.getUrl(webContext.getValidateUserUrl(Locale.CANADA_FRENCH));
    assertTrue(email.getTextMessage().contains(url));
    assertTrue(email.getHtmlMessage().contains(url));
    assertFalse(email.getTextMessage().contains("???"));
    assertFalse(email.getHtmlMessage().contains("???"));
    assertFalse(email.getTextMessage().contains("$resourceTool"));
    assertFalse(email.getHtmlMessage().contains("$resourceTool"));
  }

  @Test
  public void register_ExistingLaboratory_Invalid() throws Throwable {
    final User manager = entityManager.find(User.class, 10L);
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    Address address = new Address();
    address.setAddress("110 av des Pins Ouest");
    address.setAddressSecond("2640");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    List<Address> addresses = new ArrayList<>();
    addresses.add(address);
    user.setAddresses(addresses);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);
    RegisterUserWebContext webContext = new RegisterUserWebContextDefault();

    try {
      userServiceDefault.register(user, "password", manager, webContext);
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
    address.setAddress("110 av des Pins Ouest");
    address.setAddressSecond("2640");
    address.setTown("Montréal");
    address.setState("Québec");
    address.setPostalCode("H2W 1R7");
    address.setCountry("Canada");
    List<Address> addresses = new ArrayList<>();
    addresses.add(address);
    user.setAddresses(addresses);
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.WORK);
    phoneNumber.setNumber("514-555-5500");
    phoneNumber.setExtension("3228");
    List<PhoneNumber> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(phoneNumber);
    user.setPhoneNumbers(phoneNumbers);
    RegisterUserWebContext webContext = new RegisterUserWebContextDefault();

    userServiceDefault.register(user, "password", null, webContext);

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
    assertEquals(1, user.getAddresses().size());
    address = user.getAddresses().get(0);
    assertEquals("110 av des Pins Ouest", address.getAddress());
    assertEquals("2640", address.getAddressSecond());
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
    assertEquals(false, user.isProteomic());
    verify(emailService, times(2)).sendHtmlEmail(emailCaptor.capture());
    Set<String> receivers = new HashSet<>();
    Set<String> subjects = new HashSet<>();
    MessageResource frenchMessageResource =
        new MessageResource(UserServiceDefault.class.getName() + "_Email", Locale.CANADA_FRENCH);
    subjects.add(frenchMessageResource.message("newLaboratory.email.subject"));
    MessageResource englishMessageResource =
        new MessageResource(UserServiceDefault.class.getName() + "_Email", Locale.CANADA_FRENCH);
    subjects.add(englishMessageResource.message("newLaboratory.email.subject"));
    for (HtmlEmail email : emailCaptor.getAllValues()) {
      receivers.addAll(email.getReceivers());
      assertTrue(subjects.contains(email.getSubject()));
      email.getHtmlMessage().contains("Christian");
      email.getHtmlMessage().contains("Poitras");
      email.getTextMessage().contains("Christian");
      email.getTextMessage().contains("Poitras");
      String url = applicationConfiguration.getUrl(webContext.getValidateUserUrl(Locale.CANADA));
      assertTrue(email.getTextMessage().contains(url));
      assertTrue(email.getHtmlMessage().contains(url));
      assertFalse(email.getTextMessage().contains("???"));
      assertFalse(email.getHtmlMessage().contains("???"));
      assertFalse(email.getTextMessage().contains("$resourceTool"));
      assertFalse(email.getHtmlMessage().contains("$resourceTool"));
    }
    receivers.contains("christian.poitras@ircm.qc.ca");
    receivers.contains("christopher.anderson@ircm.qc.ca");
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
    assertEquals(1, user.getAddresses().size());
    Address address = user.getAddresses().get(0);
    assertEquals("110, avenue des Pins Ouest", address.getAddress());
    assertEquals(null, address.getAddressSecond());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(true, address.isBilling());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5556", phoneNumber.getNumber());
    assertEquals(null, phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());

    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.US);
    address = new Address();
    address.setAddress("110 av des Pins West");
    address.setAddressSecond("2640");
    address.setTown("Montreal");
    address.setState("Quebec");
    address.setPostalCode("H2W 1R8");
    address.setCountry("USA");
    List<Address> addresses = new ArrayList<>();
    addresses.add(address);
    user.setAddresses(addresses);
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

    userServiceDefault.update(user, null);

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
    assertEquals(1, user.getAddresses().size());
    address = user.getAddresses().get(0);
    assertEquals("110 av des Pins West", address.getAddress());
    assertEquals("2640", address.getAddressSecond());
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
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void updatePassword() throws Throwable {
    User user = entityManager.find(User.class, 4L);
    entityManager.detach(user);

    userServiceDefault.update(user, "unit_test_password");

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
    User user = entityManager.find(User.class, 4L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    Collection<User> users = new LinkedList<User>();
    users.add(user);

    userServiceDefault.validate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 4L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void activate() throws Throwable {
    User user = entityManager.find(User.class, 5L);
    entityManager.detach(user);
    assertEquals(false, user.isActive());
    Collection<User> users = new LinkedList<User>();
    users.add(user);

    userServiceDefault.activate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 5L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void deactivate_Manager() throws Throwable {
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    Collection<User> users = new LinkedList<User>();
    users.add(user);
    when(authorizationService.hasManagerRole(any(User.class))).thenReturn(true);

    try {
      userServiceDefault.deactivate(users);
      fail("Expected DeactivateManagerException");
    } catch (DeactivateManagerException e) {
      // Ignore.
    }
  }

  @Test
  public void deactivate() throws Throwable {
    User user = entityManager.find(User.class, 8L);
    entityManager.detach(user);
    assertEquals(true, user.isActive());

    Collection<User> users = new LinkedList<User>();
    users.add(user);
    userServiceDefault.deactivate(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(authorizationService).hasManagerRole(user);
    verify(cacheFlusher).flushShiroCache();
    user = entityManager.find(User.class, 8L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void deleteValid() throws Throwable {
    User user = entityManager.find(User.class, 5L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<User>();
    users.add(user);

    try {
      userServiceDefault.delete(users);
      fail("Expected DeleteValidUserException");
    } catch (DeleteValidUserException e) {
      // Ignore.
    }
  }

  @Test
  public void delete() throws Throwable {
    User user = entityManager.find(User.class, 4L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<User>();
    users.add(user);

    userServiceDefault.delete(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = entityManager.find(User.class, 4L);
    assertNull(user);
  }

  @Test
  public void delete_NewLaboratory() throws Throwable {
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<User>();
    users.add(user);

    userServiceDefault.delete(users);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = entityManager.find(User.class, 10L);
    assertNull(user);
    Laboratory laboratory = entityManager.find(Laboratory.class, 4L);
    assertNull(laboratory);
  }
}
