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
import static ca.qc.ircm.proview.user.QUser.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@WithMockUser
public class UserServiceTest extends AbstractServiceTestCase {
  private static final String READ = "read";
  private static final String WRITE = "write";
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
  @Inject
  private UserService service;
  @Inject
  private UserRepository repository;
  @Inject
  private LaboratoryRepository laboratoryRepository;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private PermissionEvaluator permissionEvaluator;
  private String hashedPassword;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    hashedPassword = "da78f3a74658706/4ae8470fc73a83f369fed012";
    when(passwordEncoder.encode(any(String.class))).thenReturn(hashedPassword);
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
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
    User user = service.get(3L);

    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(READ));
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
    assertEquals(false, user.isAdmin());
    assertEquals(true, user.isManager());
  }

  @Test
  public void get_NullId() throws Throwable {
    User user = service.get((Long) null);

    assertNull(user);
  }

  @Test
  public void get_Email() throws Throwable {
    User user = service.get("benoit.coulombe@ircm.qc.ca");

    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(READ));
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
    assertEquals(false, user.isAdmin());
    assertEquals(true, user.isManager());
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
  @WithMockUser(authorities = UserRole.ADMIN)
  public void all_Filter() throws Throwable {
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    List<User> users = service.all(filter);

    verify(filter).predicate();
    assertEquals(12, users.size());
    assertTrue(find(users, 2).isPresent());
    assertFalse(find(users, 1).isPresent());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void all_Null() throws Throwable {
    List<User> users = service.all(null);

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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void all_Filter_AccessDenied_Anonymous() throws Throwable {
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    service.all(filter);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void all_Filter_AccessDenied() throws Throwable {
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    service.all(filter);
  }

  @Test
  public void all_Laboratory_Filter() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    List<User> users = service.all(filter, laboratory);

    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
    verify(filter).predicate();
    assertEquals(4, users.size());
    assertTrue(find(users, 3).isPresent());
    assertFalse(find(users, 2).isPresent());
  }

  @Test
  public void all_Laboratory_NullFilter() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);

    List<User> users = service.all(null, laboratory);

    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
    assertEquals(4, users.size());
    assertTrue(find(users, 3).isPresent());
    assertFalse(find(users, 2).isPresent());
  }

  @Test
  public void all_Laboratory_NullLaboratory() throws Throwable {
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    List<User> users = service.all(filter, null);

    assertTrue(users.isEmpty());
  }

  @Test
  public void save_Insert_Admin() throws Throwable {
    final User manager = repository.findById(1L).orElse(null);
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

    service.save(user, "password");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authorizationService).getCurrentUser();
    verify(passwordEncoder).encode("password");
    assertNotNull(user.getId());
    user = repository.findById(user.getId()).orElse(null);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals((Long) 1L, user.getLaboratory().getId());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
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
    assertEquals(true, user.isAdmin());
    assertEquals(false, user.isManager());
  }

  @Test
  public void save_Insert_ExistingLaboratory() throws Throwable {
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
    user.setLaboratory(laboratoryRepository.findById(2L).get());
    when(authorizationService.getCurrentUser()).thenReturn(repository.findById(3L).get());

    service.save(user, "password");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verifyZeroInteractions(authorizationService);
    verify(passwordEncoder).encode("password");
    assertNotNull(user.getId());
    user = repository.findById(user.getId()).orElse(null);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
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
    assertEquals(false, user.isAdmin());
    assertEquals(false, user.isManager());
  }

  @Test
  public void save_Insert_NewLaboratory() throws Throwable {
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

    service.save(user, "password");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verifyZeroInteractions(authorizationService);
    verify(passwordEncoder).encode("password");
    assertNotNull(laboratory.getId());
    assertNotNull(user.getId());
    laboratory = laboratoryRepository.findById(laboratory.getId()).orElse(null);
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    user = repository.findById(user.getId()).orElse(null);
    assertEquals(user.getId(), user.getId());
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    laboratory = user.getLaboratory();
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    assertEquals("Christian Poitras", laboratory.getDirector());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
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
    assertEquals(true, user.isActive());
    assertEquals(false, user.isAdmin());
    assertEquals(true, user.isManager());
  }

  @Test
  public void save_Update() throws Throwable {
    User user = repository.findById(12L).orElse(null);
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

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElse(null);
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
    assertEquals(false, user.isAdmin());
    assertEquals(false, user.isManager());
  }

  @Test
  public void save_Update_CurrentManager() throws Throwable {
    when(authorizationService.hasRole(UserRole.MANAGER)).thenReturn(true);
    User user = repository.findById(3L).orElse(null);
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

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElse(null);
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
    assertEquals(false, user.isAdmin());
    assertEquals(true, user.isManager());
  }

  @Test
  public void save_Update_AddManager() throws Throwable {
    User user = repository.findById(10L).orElse(null);
    detach(user);
    user.setManager(true);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElse(null);
    assertEquals(true, user.isManager());
  }

  @Test
  public void save_Update_AddManagerDirectorChange() throws Throwable {
    User user = repository.findById(10L).orElse(null);
    detach(user);
    user.setManager(true);

    service.save(user, null);

    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void save_Update_RemoveManager() throws Throwable {
    User user = repository.findById(27L).orElse(null);
    detach(user);
    user.setManager(false);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElse(null);
    assertEquals(false, user.isManager());
  }

  @Test(expected = UnmanagedLaboratoryException.class)
  public void save_Update_RemoveManagerUnmanagedLaboratory() throws Throwable {
    User user = repository.findById(25L).orElse(null);
    detach(user);
    user.setManager(false);

    service.save(user, null);
  }

  @Test
  public void save_Update_RemoveManagerDirectorChange() throws Throwable {
    User user = repository.findById(27L).orElse(null);
    detach(user);
    user.setManager(false);

    service.save(user, null);

    Laboratory laboratory = laboratoryRepository.findById(2L).orElse(null);
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void save_UpdatePassword() throws Throwable {
    User user = repository.findById(4L).orElse(null);
    detach(user);

    service.save(user, "unit_test_password");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(passwordEncoder).encode("unit_test_password");
    user = repository.findById(4L).orElse(null);
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
  }

  @Test
  public void save_Update_Lab() throws Throwable {
    when(authorizationService.hasRole(UserRole.MANAGER)).thenReturn(true);
    User user = repository.findById(3L).orElse(null);
    detach(user);

    user.setEmail("unit_test@ircm.qc.ca");
    user.getLaboratory().setName("lab test");
    user.getLaboratory().setOrganization("organization test");

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authorizationService).hasRole(UserRole.MANAGER);
    verify(authorizationService).hasPermission(eq(user.getLaboratory()), eq(BasePermission.WRITE));
    user = repository.findById(user.getId()).orElse(null);
    assertEquals(user.getId(), user.getId());
    assertEquals("lab test", user.getLaboratory().getName());
    assertEquals("organization test", user.getLaboratory().getOrganization());
  }

  @Test
  public void save_Update_Activate() throws Throwable {
    User user = repository.findById(12L).orElse(null);
    detach(user);
    assertFalse(user.isActive());
    user.setActive(true);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElse(null);
    assertTrue(user.isActive());
  }

  @Test
  public void save_Update_Deactivate() throws Throwable {
    User user = repository.findById(10L).orElse(null);
    detach(user);
    user.setActive(false);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElse(null);
    assertFalse(user.isActive());
  }
}
