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
 * Tests for {@link LaboratoryPermissionEvaluator}.
 */
@ServiceTestAnnotations
public class LaboratoryPermissionEvaluatorTest {

  private static final String LABORATORY_CLASS = Laboratory.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = Permission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = Permission.WRITE;
  private LaboratoryPermissionEvaluator permissionEvaluator;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleValidator roleValidator;

  @BeforeEach
  public void beforeTest() {
    permissionEvaluator =
        new LaboratoryPermissionEvaluator(laboratoryRepository, userRepository, roleValidator);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_Member() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Read_NotMember() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() {
    Laboratory laboratory = new Laboratory("new lab");
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() {
    Laboratory laboratory = new Laboratory("new lab");
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() {
    Laboratory laboratory = new Laboratory("new lab");
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() {
    Laboratory laboratory = new Laboratory("new lab");
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_MemberManager() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_MemberNotManager() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Write_NotMember() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Admin() {
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotLaboratory() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), new User(1L), BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, User.class.getName(), BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotLongId() {
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS,
        WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS,
        BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", LABORATORY_CLASS,
        BASE_WRITE));
  }
}
