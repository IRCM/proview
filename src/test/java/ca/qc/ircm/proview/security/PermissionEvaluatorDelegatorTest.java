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

package ca.qc.ircm.proview.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PermissionEvaluatorDelegatorTest {
  private static final String LABORATORY_CLASS = Laboratory.class.getName();
  private static final String USER_CLASS = User.class.getName();
  private static final String SUBMISSION_CLASS = Submission.class.getName();
  private static final String SAMPLE_CLASS = Sample.class.getName();
  private static final String PLATE_CLASS = Plate.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = BasePermission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = BasePermission.WRITE;
  @Inject
  private PermissionEvaluatorDelegator permissionEvaluator;
  @Mock
  private LaboratoryPermissionEvaluator laboratoryPermissionEvaluator;
  @Mock
  private UserPermissionEvaluator userPermissionEvaluator;
  @Mock
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;
  @Mock
  private SamplePermissionEvaluator samplePermissionEvaluator;
  @Mock
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

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    permissionEvaluator.setLaboratoryPermissionEvaluator(laboratoryPermissionEvaluator);
    permissionEvaluator.setUserPermissionEvaluator(userPermissionEvaluator);
    permissionEvaluator.setSubmissionPermissionEvaluator(submissionPermissionEvaluator);
    permissionEvaluator.setSamplePermissionEvaluator(samplePermissionEvaluator);
    permissionEvaluator.setPlatePermissionEvaluator(platePermissionEvaluator);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Laboratory_False() throws Throwable {
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
  public void hasPermission_Laboratory_True() throws Throwable {
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
  public void hasPermission_User_False() throws Throwable {
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
  public void hasPermission_User_True() throws Throwable {
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
  public void hasPermission_Submission_False() throws Throwable {
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
  public void hasPermission_Submission_True() throws Throwable {
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
  public void hasPermission_Sample_False() throws Throwable {
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
  public void hasPermission_Sample_True() throws Throwable {
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
  public void hasPermission_Plate_False() throws Throwable {
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
  public void hasPermission_Plate_True() throws Throwable {
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
  public void hasPermission_Other() throws Throwable {
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
