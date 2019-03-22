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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.HashedPassword;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.utils.MessageResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.util.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserServiceTest extends AbstractServiceTestCase {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
  @Inject
  private UserService service;
  @Inject
  private UserRepository repository;
  @Inject
  private LaboratoryRepository laboratoryRepository;
  @MockBean
  private AuthenticationService authenticationService;
  @MockBean
  private ApplicationConfiguration applicationConfiguration;
  @MockBean
  private EmailService emailService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
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
    User user = service.get(3L);

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
    User user = service.get((Long) null);

    assertNull(user);
  }

  @Test
  public void get_Email() throws Throwable {
    User user = service.get("benoit.coulombe@ircm.qc.ca");

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
    User user = service.get((String) null);

    assertNull(user);
  }

  @Test
  public void exists_Email_True() throws Throwable {
    boolean exists = service.exists("christian.poitras@ircm.qc.ca");

    assertEquals(true, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_False() throws Throwable {
    boolean exists = service.exists("abc@ircm.qc.ca");

    assertEquals(false, exists);

    verifyZeroInteractions(authorizationService);
  }

  @Test
  public void exists_Email_Null() throws Throwable {
    boolean exists = service.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void isManager_Robot() throws Throwable {
    boolean manager = service.isManager("proview@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Admin() throws Throwable {
    boolean manager = service.isManager("christian.poitras@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Manager() throws Throwable {
    boolean manager = service.isManager("benoit.coulombe@ircm.qc.ca");

    assertEquals(true, manager);
  }

  @Test
  public void isManager_NonManager() throws Throwable {
    boolean manager = service.isManager("james.johnson@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Invalid() throws Throwable {
    boolean manager = service.isManager("nicole.francis@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Inactive() throws Throwable {
    boolean manager = service.isManager("marie.trudel@ircm.qc.ca");

    assertEquals(false, manager);
  }

  @Test
  public void isManager_Null() throws Throwable {
    boolean manager = service.isManager(null);

    assertEquals(false, manager);
  }

  @Test
  public void hasInvalid_True() throws Throwable {
    boolean hasInvalid = service.hasInvalid(laboratoryRepository.findOne(3L));

    assertEquals(true, hasInvalid);
  }

  @Test
  public void hasInvalid_False() throws Throwable {
    boolean hasInvalid = service.hasInvalid(laboratoryRepository.findOne(4L));

    assertEquals(false, hasInvalid);
  }

  @Test
  public void hasInvalid_AnyLaboratory() throws Throwable {
    boolean hasInvalid = service.hasInvalid(null);

    assertEquals(true, hasInvalid);
  }

  @Test
  public void all_InvalidInLaboratory() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    UserFilter parameters = new UserFilter();
    parameters.valid = false;
    parameters.laboratory = laboratory;

    List<User> users = service.all(parameters);

    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    assertEquals(1, users.size());
    assertTrue(find(users, 7).isPresent());
  }

  @Test
  public void all_Invalid() throws Throwable {
    UserFilter parameters = new UserFilter();
    parameters.valid = false;

    List<User> users = service.all(parameters);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, users.size());
    assertTrue(find(users, 6).isPresent());
    assertTrue(find(users, 7).isPresent());
  }

  @Test
  public void all_ValidInLaboratory() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    UserFilter parameters = new UserFilter();
    parameters.valid = true;
    parameters.laboratory = laboratory;

    List<User> users = service.all(parameters);

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

    List<User> users = service.all(parameters);

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

    List<User> users = service.all(parameters);

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
    List<User> users = service.all(null);

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
    final User manager = repository.findOne(1L);
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

    service.register(user, "password", null, registerUserWebContext());

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(authorizationService).getCurrentUser();
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = laboratoryRepository.findOne(manager.getLaboratory().getId());
    assertFalse(find(laboratory.getManagers(), user.getId()).isPresent());
    assertNotNull(user.getId());
    user = repository.findOne(user.getId());
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

    service.register(user, "password", manager, registerUserWebContext());

    repository.flush();
    verifyZeroInteractions(authorizationService);
    verify(authenticationService).hashPassword("password");
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    assertEquals(2, laboratory.getManagers().size());
    assertTrue(find(laboratory.getManagers(), 3L).isPresent());
    assertTrue(find(laboratory.getManagers(), 27L).isPresent());
    assertNotNull(user.getId());
    user = repository.findOne(user.getId());
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
    User updateLocale = repository.findOne(3L);
    updateLocale.setLocale(Locale.CANADA);
    repository.save(updateLocale);
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

    service.register(user, "password", manager, registerUserWebContext());

    repository.flush();
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
    final User manager = repository.findOne(6L);
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
      service.register(user, "password", manager, registerUserWebContext());
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

    service.register(user, "password", null, registerUserWebContext());

    repository.flush();
    verifyZeroInteractions(authorizationService);
    verify(authenticationService).hashPassword("password");
    assertNotNull(laboratory.getId());
    assertNotNull(user.getId());
    laboratory = laboratoryRepository.findOne(laboratory.getId());
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    assertEquals(1, laboratory.getManagers().size());
    assertEquals(user.getId(), laboratory.getManagers().get(0).getId());
    user = repository.findOne(user.getId());
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
    verify(email, never()).addTo("robert.stlouis@ircm.qc.ca");
    verify(email, never()).addTo("benoit.coulombe@ircm.qc.ca");
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
    User user = repository.findOne(12L);
    detach(user);
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

    service.update(user, null);

    repository.flush();
    verify(authorizationService).checkUserWritePermission(user);
    user = repository.findOne(user.getId());
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
  public void update_Manager() throws Throwable {
    when(authorizationService.hasManagerRole()).thenReturn(true);
    User user = repository.findOne(3L);
    detach(user);
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

    service.update(user, null);

    repository.flush();
    verify(authorizationService).checkUserWritePermission(user);
    user = repository.findOne(user.getId());
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
    User user = repository.findOne(4L);
    detach(user);

    service.update(user, "unit_test_password");

    repository.flush();
    verify(authorizationService).checkUserWritePermission(user);
    verify(authenticationService).hashPassword("unit_test_password");
    user = repository.findOne(4L);
    assertEquals(hashedPassword.getPassword(), user.getHashedPassword());
    assertEquals(hashedPassword.getSalt(), user.getSalt());
    assertEquals((Integer) hashedPassword.getPasswordVersion(), user.getPasswordVersion());
  }

  @Test
  public void update_Lab() throws Throwable {
    when(authorizationService.hasManagerRole()).thenReturn(true);
    User user = repository.findOne(3L);
    detach(user);

    user.setEmail("unit_test@ircm.qc.ca");
    user.getLaboratory().setName("lab test");
    user.getLaboratory().setOrganization("organization test");

    service.update(user, null);

    repository.flush();
    verify(authorizationService).checkUserWritePermission(user);
    verify(authorizationService).hasManagerRole();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = repository.findOne(user.getId());
    assertEquals(user.getId(), user.getId());
    assertEquals("lab test", user.getLaboratory().getName());
    assertEquals("organization test", user.getLaboratory().getOrganization());
  }

  @Test
  public void validate_Many() throws Throwable {
    User user = repository.findOne(7L);
    detach(user);
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.validate(users, homeWebContext());

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(7L);
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
  public void validate_Many_EnglishEmail() throws Throwable {
    User updateLocale = repository.findOne(7L);
    updateLocale.setLocale(Locale.CANADA);
    repository.save(updateLocale);
    User user = repository.findOne(7L);
    detach(user);
    assertEquals(false, user.isActive());
    assertEquals(false, user.isValid());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.validate(users, homeWebContext());

    repository.flush();
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
    User user = repository.findOne(12L);
    detach(user);
    assertEquals(false, user.isActive());

    service.activate(user);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(12L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void activate_Many() throws Throwable {
    User user = repository.findOne(12L);
    detach(user);
    assertEquals(false, user.isActive());
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.activate(users);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(12L);
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate() throws Throwable {
    User user = repository.findOne(10L);
    detach(user);

    service.deactivate(user);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(10L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_Many() throws Throwable {
    User user = repository.findOne(10L);
    detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.deactivate(users);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(10L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_Manager() throws Throwable {
    User user = repository.findOne(3L);
    detach(user);

    service.deactivate(user);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(3L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_Admin() throws Throwable {
    User user = repository.findOne(4L);
    detach(user);

    service.deactivate(user);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(4L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(true, user.isAdmin());
  }

  @Test(expected = IllegalArgumentException.class)
  public void deactivate_Robot() throws Throwable {
    User user = repository.findOne(1L);
    detach(user);
    assertEquals(true, user.isActive());

    service.deactivate(user);
  }

  @Test
  public void deactivate_ManyManager() throws Throwable {
    User user = repository.findOne(3L);
    detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.deactivate(users);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(3L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  public void deactivate_ManyAdmin() throws Throwable {
    User user = repository.findOne(4L);
    detach(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.deactivate(users);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    verify(cacheFlusher).flushShiroCache();
    user = repository.findOne(4L);
    assertEquals(false, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(true, user.isAdmin());
  }

  @Test(expected = IllegalArgumentException.class)
  public void deactivate_ManyRobot() throws Throwable {
    User user = repository.findOne(1L);
    detach(user);
    assertEquals(true, user.isActive());

    Collection<User> users = new LinkedList<>();
    users.add(user);
    service.deactivate(users);
  }

  @Test
  public void addManagerAdmin() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(1L);
    User user = repository.findOne(5L);
    detach(laboratory, user);

    service.addManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = laboratoryRepository.findOne(1L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void addManager() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(10L);
    detach(laboratory, user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    service.addManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = laboratoryRepository.findOne(2L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void addManager_InactivatedUser() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(12L);
    detach(laboratory, user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));
    assertEquals(false, user.isActive());

    service.addManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = laboratoryRepository.findOne(2L);
    List<User> testManagers = laboratory.getManagers();
    User testUser = repository.findOne(12L);
    assertTrue(find(testManagers, user.getId()).isPresent());
    assertEquals(true, testUser.isActive());
  }

  @Test
  public void addManager_AlreadyManager() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(3L);
    detach(laboratory, user);
    List<User> managers = laboratory.getManagers();
    assertTrue(find(managers, user.getId()).isPresent());

    service.addManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = laboratoryRepository.findOne(2L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void addManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(2L);
    detach(laboratory, user);

    service.addManager(laboratory, user);
  }

  @Test(expected = InvalidUserException.class)
  public void addManager_Invalid() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(7L);
    detach(laboratory, user);

    service.addManager(laboratory, user);
  }

  @Test
  public void addManager_DirectorChange() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    laboratory.setDirector("Test");
    laboratoryRepository.save(laboratory);
    User user = repository.findOne(10L);
    detach(laboratory, user);

    service.addManager(laboratory, user);

    laboratoryRepository.flush();
    laboratory = laboratoryRepository.findOne(2L);
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void removeManagerAdmin() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(1L);
    User user = repository.findOne(2L);
    detach(laboratory, user);

    service.removeManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = laboratoryRepository.findOne(1L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void removeManager_UnmanagedLaboratory() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(3L);
    User user = repository.findOne(6L);
    detach(laboratory, user);

    try {
      service.removeManager(laboratory, user);
      fail("Expected UnmanagedLaboratoryException");
    } catch (UnmanagedLaboratoryException e) {
      // Ignore.
    }
  }

  @Test
  public void removeManager() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(27L);
    detach(laboratory, user);
    List<User> managers = laboratory.getManagers();
    assertTrue(find(managers, user.getId()).isPresent());

    service.removeManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = laboratoryRepository.findOne(2L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void removeManager_AlreadyNotManager() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(10L);
    detach(laboratory, user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    service.removeManager(laboratory, user);

    laboratoryRepository.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = laboratoryRepository.findOne(2L);
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void removeManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    User user = repository.findOne(2L);
    detach(laboratory, user);

    service.removeManager(laboratory, user);
  }

  @Test
  public void removeManager_DirectorChange() throws Exception {
    Laboratory laboratory = laboratoryRepository.findOne(2L);
    laboratory.setDirector("Test");
    laboratoryRepository.saveAndFlush(laboratory);
    User user = repository.findOne(27L);
    detach(laboratory, user);

    service.removeManager(laboratory, user);

    laboratoryRepository.flush();
    laboratory = laboratoryRepository.findOne(2L);
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void deleteValid() throws Throwable {
    User user = repository.findOne(5L);
    detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    try {
      service.delete(users);
      fail("Expected DeleteValidUserException");
    } catch (DeleteValidUserException e) {
      // Ignore.
    }
  }

  @Test
  public void delete() throws Throwable {
    User user = repository.findOne(7L);
    detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.delete(users);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = repository.findOne(7L);
    assertNull(user);
  }

  @Test
  public void delete_NewLaboratory() throws Throwable {
    User user = repository.findOne(6L);
    detach(user);
    assertNotNull(user);
    Collection<User> users = new LinkedList<>();
    users.add(user);

    service.delete(users);

    repository.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(user.getLaboratory());
    user = repository.findOne(6L);
    assertNull(user);
    Laboratory laboratory = laboratoryRepository.findOne(3L);
    assertNull(laboratory);
  }
}
