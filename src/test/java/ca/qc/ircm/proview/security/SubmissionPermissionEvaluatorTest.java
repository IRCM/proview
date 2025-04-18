package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link SubmissionPermissionEvaluator}.
 */
@ServiceTestAnnotations
public class SubmissionPermissionEvaluatorTest {

  private static final String SUBMISSION_CLASS = Submission.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = Permission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = Permission.WRITE;
  private SubmissionPermissionEvaluator permissionEvaluator;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleValidator roleValidator;

  @BeforeEach
  public void beforeTest() {
    permissionEvaluator =
        new SubmissionPermissionEvaluator(submissionRepository, userRepository, roleValidator);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_Owner() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Read_NotOwner() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Read_Manager() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("marie.trudel@ircm.qc.ca")
  public void hasPermission_Read_OtherManager() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() {
    Submission submission = new Submission();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() {
    Submission submission = new Submission();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() {
    Submission submission = new Submission();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() {
    Submission submission = new Submission();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_OwnerWaiting() {
    Submission submission = submissionRepository.findById(36L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_OwnerReceived() {
    Submission submission = submissionRepository.findById(149L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_OwnerAfterReceived() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Write_NotOwner() {
    Submission submission = submissionRepository.findById(36L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_ManagerWaiting() {
    Submission submission = submissionRepository.findById(36L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_ManagerReceived() {
    Submission submission = submissionRepository.findById(149L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_ManagerAfterReceived() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("marie.trudel@ircm.qc.ca")
  public void hasPermission_Write_OtherManager() {
    Submission submission = submissionRepository.findById(36L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_AdminWaiting() {
    Submission submission = submissionRepository.findById(36L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_AdminReceived() {
    Submission submission = submissionRepository.findById(149L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_AdminAfterReceived() {
    Submission submission = submissionRepository.findById(35L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotSubmission() {
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
  public void hasPermission_NotLongId() {
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS,
        WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS,
        BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS,
        BASE_WRITE));
  }
}
