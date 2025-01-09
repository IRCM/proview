package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UserPermissionEvaluator}.
 */
@ServiceTestAnnotations
public class UserPermissionEvaluatorTest {
  private static final String USER_CLASS = User.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = Permission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = Permission.WRITE;
  private UserPermissionEvaluator permissionEvaluator;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private RoleValidator roleValidator;

  @BeforeEach
  public void beforeTest() {
    permissionEvaluator = new UserPermissionEvaluator(userRepository, roleValidator);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_Self() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Read_NotSelf() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Read_Manager() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("marie.trudel@ircm.qc.ca")
  public void hasPermission_Read_OtherManager() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() throws Throwable {
    User user = new User("new.user@ircm.qc.ca");
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() throws Throwable {
    User user = new User("new.user@ircm.qc.ca");
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager_OwnLab() throws Throwable {
    User user = new User("new.user@ircm.qc.ca");
    user.setLaboratory(laboratoryRepository.findById(2L).get());
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager_NotOwnLab() throws Throwable {
    User user = new User("new.user@ircm.qc.ca");
    user.setLaboratory(laboratoryRepository.findById(4L).get());
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() throws Throwable {
    User user = new User("new.user@ircm.qc.ca");
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_Self() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Write_NotSelf() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_Manager() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("marie.trudel@ircm.qc.ca")
  public void hasPermission_Write_OtherManager() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Admin() throws Throwable {
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Robot() throws Throwable {
    User user = userRepository.findById(1L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Robot_Deactivate() throws Throwable {
    User user = userRepository.findById(1L).orElseThrow();
    user.setActive(false);
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotUser() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), new Laboratory(1L), READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new Laboratory(1L), WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new Laboratory(1L), BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), new Laboratory(1L), BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, Laboratory.class.getName(), READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, Laboratory.class.getName(), WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), 1L, Laboratory.class.getName(),
        BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), 1L, Laboratory.class.getName(),
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotLongId() throws Throwable {
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", USER_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", USER_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", USER_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", USER_CLASS, BASE_WRITE));
  }
}
