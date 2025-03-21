package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link PlatePermissionEvaluator}.
 */
@ServiceTestAnnotations
public class PlatePermissionEvaluatorTest {

  private static final String PLATE_CLASS = Plate.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = Permission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = Permission.WRITE;
  private PlatePermissionEvaluator permissionEvaluator;
  @Autowired
  private PlateRepository plateRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleValidator roleValidator;
  @Mock
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;
  @Mock
  private Submission submission;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    permissionEvaluator = new PlatePermissionEvaluator(plateRepository, userRepository,
        roleValidator, submissionPermissionEvaluator);
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() {
    Plate plate = plateRepository.findById(26L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous_Submission() {
    Plate plate = plateRepository.findById(123L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read() {
    Plate plate = plateRepository.findById(26L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_SubmissionTrue() {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    Plate plate = plateRepository.findById(123L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
    assertNotNull(plate.getSubmission());
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(plate.getSubmission()), any(),
        eq(BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_SubmissionFalse() {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(false);
    Plate plate = plateRepository.findById(123L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
    assertNotNull(plate.getSubmission());
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(plate.getSubmission()), any(),
        eq(BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() {
    Plate plate = plateRepository.findById(26L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() {
    Plate plate = new Plate();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous_Submission() {
    Plate plate = new Plate();
    plate.setSubmission(submission);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() {
    Plate plate = new Plate();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User_Submission() {
    Plate plate = new Plate();
    plate.setSubmission(new Submission(1L));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() {
    Plate plate = new Plate();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager_Submission() {
    Plate plate = new Plate();
    plate.setSubmission(new Submission(1L));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() {
    Plate plate = new Plate();
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin_Submission() {
    Plate plate = new Plate();
    plate.setSubmission(new Submission(1L));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() {
    Plate plate = plateRepository.findById(26L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous_Submission() {
    Plate plate = plateRepository.findById(123L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write() {
    Plate plate = plateRepository.findById(26L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_SubmissionTrue() {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    Plate plate = plateRepository.findById(123L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
    assertNotNull(plate.getSubmission());
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(plate.getSubmission()), any(),
        eq(BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_SubmissionFalse() {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(false);
    Plate plate = plateRepository.findById(123L).orElseThrow();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
    assertNotNull(plate.getSubmission());
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(plate.getSubmission()), any(),
        eq(BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Admin() {
    Plate plate = plateRepository.findById(26L).orElseThrow();
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotPlate() {
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
        permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS,
        BASE_WRITE));
  }
}
