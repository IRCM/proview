package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.user.QUser.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.Permission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link UserService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class UserServiceTest extends AbstractServiceTestCase {
  private static final String READ = "read";
  private static final String WRITE = "write";
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
  @Autowired
  private UserService service;
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @MockBean
  private AuthenticatedUser authenticatedUser;
  @MockBean
  private PermissionEvaluator permissionEvaluator;
  private String hashedPassword;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() throws Throwable {
    hashedPassword = "da78f3a74658706/4ae8470fc73a83f369fed012";
    when(passwordEncoder.encode(any(String.class))).thenReturn(hashedPassword);
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(true);
  }

  private Optional<PhoneNumber> findPhoneNumber(Collection<PhoneNumber> datas,
      PhoneNumberType type) {
    for (PhoneNumber data : datas) {
      if (data.getType() == type) {
        return Optional.of(data);
      }
    }
    return Optional.empty();
  }

  @Test
  public void get_Id() throws Throwable {
    User user = service.get(3L).orElseThrow();

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
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    Address address = user.getAddress();
    assertNotNull(address);
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
  public void get_Email() throws Throwable {
    User user = service.get("benoit.coulombe@ircm.qc.ca").orElseThrow();

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
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    Address address = user.getAddress();
    assertNotNull(address);
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
  public void exists_Email_True() throws Throwable {
    boolean exists = service.exists("christian.poitras@ircm.qc.ca");

    assertEquals(true, exists);

    verifyNoInteractions(authenticatedUser);
  }

  @Test
  public void exists_Email_False() throws Throwable {
    boolean exists = service.exists("abc@ircm.qc.ca");

    assertEquals(false, exists);

    verifyNoInteractions(authenticatedUser);
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
  public void all_EmptyFilter() throws Throwable {
    List<User> users = service.all(new UserFilter());

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
  @WithAnonymousUser
  public void all_Filter_AccessDenied_Anonymous() throws Throwable {
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    assertThrows(AccessDeniedException.class, () -> {
      service.all(filter);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void all_Filter_AccessDenied() throws Throwable {
    UserFilter filter = mock(UserFilter.class);
    when(filter.predicate()).thenReturn(user.isNotNull());

    assertThrows(AccessDeniedException.class, () -> {
      service.all(filter);
    });
  }

  @Test
  public void all_Laboratory_Filter() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
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
  public void all_Laboratory_EmptyFilter() throws Throwable {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();

    List<User> users = service.all(new UserFilter(), laboratory);

    verify(permissionEvaluator).hasPermission(any(), eq(laboratory), eq(WRITE));
    assertEquals(4, users.size());
    assertTrue(find(users, 3).isPresent());
    assertFalse(find(users, 2).isPresent());
  }

  @Test
  public void save_Insert_Admin() throws Throwable {
    final User manager = repository.findById(1L).orElseThrow();
    when(authenticatedUser.getUser()).thenReturn(Optional.of(manager));
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLocale(Locale.CANADA_FRENCH);
    user.setAdmin(true);
    user.setLaboratory(laboratoryRepository.findById(1L).orElseThrow());
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
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    verify(passwordEncoder).encode("password");
    assertNotEquals(0, user.getId());
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals((Long) 1L, user.getLaboratory().getId());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    address = user.getAddress();
    assertNotNull(address);
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
    Laboratory laboratory = user.getLaboratory();
    assertEquals((Long) 1L, laboratory.getId());
    assertEquals("Admin", laboratory.getName());
    assertEquals("Robot", laboratory.getDirector());
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
    user.setLaboratory(laboratoryRepository.findById(2L).orElseThrow());
    user.getLaboratory().setName("Ribonucleoprotein Biochemistry");
    when(authenticatedUser.getUser()).thenReturn(repository.findById(3L));

    service.save(user, "password");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    verify(passwordEncoder).encode("password");
    assertNotEquals(0, user.getId());
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals("Ribonucleoprotein Biochemistry", user.getLaboratory().getName());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    address = user.getAddress();
    assertNotNull(address);
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
    Laboratory laboratory = user.getLaboratory();
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void save_Insert_NewLaboratory() throws Throwable {
    Laboratory laboratory = new Laboratory();
    laboratory.setName("Ribonucleoprotein Biochemistry");
    User user = new User();
    user.setEmail("unit_test@ircm.qc.ca");
    user.setName("Christian Poitras");
    user.setLaboratory(laboratory);
    user.setLocale(Locale.CANADA);
    user.setManager(true);
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
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    verify(passwordEncoder).encode("password");
    assertNotEquals(0, laboratory.getId());
    assertNotEquals(0, user.getId());
    laboratory = laboratoryRepository.findById(laboratory.getId()).orElseThrow();
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    laboratory = user.getLaboratory();
    assertEquals("Ribonucleoprotein Biochemistry", laboratory.getName());
    assertEquals("Christian Poitras", laboratory.getDirector());
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
    assertEquals(Locale.CANADA, user.getLocale());
    address = user.getAddress();
    assertNotNull(address);
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
  public void save_Insert_NewLaboratoryNotManager() throws Throwable {
    Laboratory laboratory = new Laboratory();
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

    assertThrows(IllegalArgumentException.class, () -> {
      service.save(user, "password");
    });
  }

  @Test
  public void save_Update() throws Throwable {
    User user = repository.findById(12L).orElseThrow();
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
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
    assertEquals("Benoit Coulombe", user.getLaboratory().getDirector());
    assertEquals(Locale.US, user.getLocale());
    address = user.getAddress();
    assertNotNull(address);
    assertEquals("110 av des Pins West", address.getLine());
    assertEquals("Montreal", address.getTown());
    assertEquals("Quebec", address.getState());
    assertEquals("H2W 1R8", address.getPostalCode());
    assertEquals("USA", address.getCountry());
    assertEquals(2, user.getPhoneNumbers().size());
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.WORK).orElseThrow();
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-987-5501", phoneNumber.getNumber());
    assertEquals("3218", phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.FAX).orElseThrow();
    assertEquals("514-987-5502", phoneNumber.getNumber());
    assertEquals("1234", phoneNumber.getExtension());
    assertEquals(false, user.isActive());
    assertEquals(false, user.isAdmin());
    assertEquals(false, user.isManager());
  }

  @Test
  public void save_Update_CurrentManager() throws Throwable {
    User user = repository.findById(3L).orElseThrow();
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
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals("unit_test@ircm.qc.ca", user.getEmail());
    assertEquals("Christian Poitras", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
    assertEquals("Christian Poitras", user.getLaboratory().getDirector());
    assertEquals(Locale.US, user.getLocale());
    address = user.getAddress();
    assertNotNull(address);
    assertEquals("110 av des Pins West", address.getLine());
    assertEquals("Montreal", address.getTown());
    assertEquals("Quebec", address.getState());
    assertEquals("H2W 1R8", address.getPostalCode());
    assertEquals("USA", address.getCountry());
    assertEquals(2, user.getPhoneNumbers().size());
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.WORK).orElseThrow();
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-987-5501", phoneNumber.getNumber());
    assertEquals("3218", phoneNumber.getExtension());
    phoneNumber = user.getPhoneNumbers().get(1);
    phoneNumber = findPhoneNumber(user.getPhoneNumbers(), PhoneNumberType.FAX).orElseThrow();
    assertEquals("514-987-5502", phoneNumber.getNumber());
    assertEquals("1234", phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(false, user.isAdmin());
    assertEquals(true, user.isManager());
  }

  @Test
  public void save_Update_AddManager() throws Throwable {
    User user = repository.findById(10L).orElseThrow();
    detach(user);
    user.setManager(true);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals(true, user.isManager());
  }

  @Test
  public void save_Update_AddManagerDirectorChange() throws Throwable {
    User user = repository.findById(10L).orElseThrow();
    detach(user);
    user.setManager(true);

    service.save(user, null);

    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void save_Update_RemoveManager() throws Throwable {
    User user = repository.findById(27L).orElseThrow();
    detach(user);
    user.setManager(false);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals(false, user.isManager());
  }

  @Test
  public void save_Update_RemoveManagerUnmanagedLaboratory() throws Throwable {
    User user = repository.findById(25L).orElseThrow();
    detach(user);
    user.setManager(false);

    assertThrows(UnmanagedLaboratoryException.class, () -> {
      service.save(user, null);
    });
  }

  @Test
  public void save_Update_RemoveManagerDirectorChange() throws Throwable {
    User user = repository.findById(27L).orElseThrow();
    detach(user);
    user.setManager(false);

    service.save(user, null);

    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertEquals("Benoit Coulombe", laboratory.getDirector());
  }

  @Test
  public void save_UpdatePassword() throws Throwable {
    User user = repository.findById(4L).orElseThrow();
    detach(user);

    service.save(user, "unit_test_password");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    verify(passwordEncoder).encode("unit_test_password");
    user = repository.findById(4L).orElseThrow();
    assertEquals(hashedPassword, user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
  }

  @Test
  public void save_Update_Lab() throws Throwable {
    User user = repository.findById(3L).orElseThrow();
    detach(user);

    user.setEmail("unit_test@ircm.qc.ca");
    user.getLaboratory().setName("lab test");

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals("lab test", user.getLaboratory().getName());
  }

  @Test
  public void save_Update_CreateLab() throws Throwable {
    User user = repository.findById(10L).orElseThrow();
    detach(user);
    user.setManager(true);
    user.setEmail("unit_test@ircm.qc.ca");
    user.setLaboratory(new Laboratory("lab test"));

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertNotEquals(0, user.getLaboratory().getId());
    assertEquals("lab test", user.getLaboratory().getName());
  }

  @Test
  public void save_Update_CreateLabNotManager() throws Throwable {
    User user = repository.findById(10L).orElseThrow();
    detach(user);
    user.setEmail("unit_test@ircm.qc.ca");
    user.setLaboratory(new Laboratory("lab test"));

    assertThrows(IllegalArgumentException.class, () -> {
      service.save(user, null);
    });
  }

  @Test
  public void save_Update_ChangeLab() throws Throwable {
    User user = repository.findById(10L).orElseThrow();
    detach(user);

    user.setEmail("unit_test@ircm.qc.ca");
    user.setLaboratory(laboratoryRepository.findById(4L).orElseThrow());

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    verify(authenticatedUser).hasPermission(eq(user.getLaboratory()), eq(Permission.WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertEquals((Long) 4L, user.getLaboratory().getId());
    assertEquals("Biochemistry of Epigenetic Inheritance", user.getLaboratory().getName());
  }

  @Test
  public void save_Update_Activate() throws Throwable {
    User user = repository.findById(12L).orElseThrow();
    detach(user);
    assertFalse(user.isActive());
    user.setActive(true);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertTrue(user.isActive());
  }

  @Test
  public void save_Update_Deactivate() throws Throwable {
    User user = repository.findById(10L).orElseThrow();
    detach(user);
    user.setActive(false);

    service.save(user, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(user), eq(WRITE));
    user = repository.findById(user.getId()).orElseThrow();
    assertFalse(user.isActive());
  }
}
