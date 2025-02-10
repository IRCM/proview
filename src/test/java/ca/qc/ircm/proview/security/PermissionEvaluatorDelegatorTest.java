package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link PermissionEvaluatorDelegator}.
 */
@ServiceTestAnnotations
public class PermissionEvaluatorDelegatorTest {

  private static final String LABORATORY_CLASS = Laboratory.class.getName();
  private static final String USER_CLASS = User.class.getName();
  private static final String SUBMISSION_CLASS = Submission.class.getName();
  private static final String SAMPLE_CLASS = Sample.class.getName();
  private static final String PLATE_CLASS = Plate.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = Permission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = Permission.WRITE;
  @Autowired
  private PermissionEvaluatorDelegator permissionEvaluator;
  @MockitoBean
  private LaboratoryPermissionEvaluator laboratoryPermissionEvaluator;
  @MockitoBean
  private UserPermissionEvaluator userPermissionEvaluator;
  @MockitoBean
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;
  @MockitoBean
  private SamplePermissionEvaluator samplePermissionEvaluator;
  @MockitoBean
  private PlatePermissionEvaluator platePermissionEvaluator;
  @Mock
  private Laboratory laboratory;
  @Mock
  private User user;
  @Mock
  private Submission submission;
  @Mock
  private Sample sample;
  @Mock
  private Plate plate;

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Laboratory_False() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, BASE_READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, WRITE);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, BASE_WRITE);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Laboratory_True() {
    when(laboratoryPermissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    when(laboratoryPermissionEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE));
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, BASE_READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, WRITE);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory, BASE_WRITE);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_READ);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, WRITE);
    verify(laboratoryPermissionEvaluator).hasPermission(authentication(), laboratory.getId(),
        LABORATORY_CLASS, BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_User_False() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
    verify(userPermissionEvaluator).hasPermission(authentication(), user, READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user, BASE_READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user, WRITE);
    verify(userPermissionEvaluator).hasPermission(authentication(), user, BASE_WRITE);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS, READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS,
        BASE_READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS,
        WRITE);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS,
        BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_User_True() {
    when(userPermissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    when(userPermissionEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), user.getId(), USER_CLASS, BASE_WRITE));
    verify(userPermissionEvaluator).hasPermission(authentication(), user, READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user, BASE_READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user, WRITE);
    verify(userPermissionEvaluator).hasPermission(authentication(), user, BASE_WRITE);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS, READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS,
        BASE_READ);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS,
        WRITE);
    verify(userPermissionEvaluator).hasPermission(authentication(), user.getId(), USER_CLASS,
        BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Submission_False() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, BASE_READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, WRITE);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, BASE_WRITE);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Submission_True() {
    when(submissionPermissionEvaluator.hasPermission(any(Authentication.class), any(), any()))
        .thenReturn(true);
    when(submissionPermissionEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, BASE_READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, WRITE);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission, BASE_WRITE);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE);
    verify(submissionPermissionEvaluator).hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Sample_False() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, BASE_READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, WRITE);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, BASE_WRITE);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        WRITE);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Sample_True() {
    when(samplePermissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    when(samplePermissionEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, BASE_READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, WRITE);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample, BASE_WRITE);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        WRITE);
    verify(samplePermissionEvaluator).hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Plate_False() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, BASE_READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, WRITE);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, BASE_WRITE);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        WRITE);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Plate_True() {
    when(platePermissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    when(platePermissionEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, BASE_READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, WRITE);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate, BASE_WRITE);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_READ);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        WRITE);
    verify(platePermissionEvaluator).hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Other() {
    assertFalse(permissionEvaluator.hasPermission(authentication(), "test", READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "test", BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "test", WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "test", BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, String.class.getName(), READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, String.class.getName(), BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), 1L, String.class.getName(), WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), 1L, String.class.getName(),
        BASE_WRITE));
  }
}
