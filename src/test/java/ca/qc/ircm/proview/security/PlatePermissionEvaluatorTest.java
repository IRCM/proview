/*
 * Copyright (c) 2016 Institut de recherches cliniques de Montreal (IRCM)
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRepository;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlatePermissionEvaluatorTest {
  private static final String PLATE_CLASS = Plate.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = BasePermission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = BasePermission.WRITE;
  private PlatePermissionEvaluator permissionEvaluator;
  @Inject
  private PlateRepository plateRepository;
  @Inject
  private UserRepository userRepository;
  @Inject
  private AuthorizationService authorizationService;
  @Mock
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;
  @Mock
  private SubmissionService submissionService;
  @Mock
  private Submission submission;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    permissionEvaluator = new PlatePermissionEvaluator(plateRepository, userRepository,
        authorizationService, submissionService, submissionPermissionEvaluator);
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    when(submissionService.get(any(Plate.class))).thenReturn(submission);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous_Submission() throws Throwable {
    Plate plate = plateRepository.findOne(123L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_SubmissionTrue() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    Plate plate = plateRepository.findOne(123L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(submission), any(),
        eq(BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_SubmissionFalse() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(false);
    Plate plate = plateRepository.findOne(123L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(submission), any(),
        eq(BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() throws Throwable {
    Plate plate = new Plate();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous_Submission() throws Throwable {
    Plate plate = new Plate();
    plate.setSubmission(true);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() throws Throwable {
    Plate plate = new Plate();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User_Submission() throws Throwable {
    Plate plate = new Plate();
    plate.setSubmission(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() throws Throwable {
    Plate plate = new Plate();
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager_Submission() throws Throwable {
    Plate plate = new Plate();
    plate.setSubmission(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() throws Throwable {
    Plate plate = new Plate();
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin_Submission() throws Throwable {
    Plate plate = new Plate();
    plate.setSubmission(true);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous_Submission() throws Throwable {
    Plate plate = plateRepository.findOne(123L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_SubmissionTrue() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    Plate plate = plateRepository.findOne(123L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(submission), any(),
        eq(BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_SubmissionFalse() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(false);
    Plate plate = plateRepository.findOne(123L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(submission), any(),
        eq(BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Admin() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), plate.getId(), PLATE_CLASS,
        BASE_WRITE));
  }

  @Test
  public void hasPermission_NullAuthentication() throws Throwable {
    Plate plate = plateRepository.findOne(26L);
    assertFalse(permissionEvaluator.hasPermission(null, plate, READ));
    assertFalse(permissionEvaluator.hasPermission(null, plate, WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, plate, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(null, plate, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, plate.getId(), PLATE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(null, plate.getId(), PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, plate.getId(), PLATE_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(null, plate.getId(), PLATE_CLASS, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Null_Anonymous() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Null() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, PLATE_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotPlate() throws Throwable {
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
        permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", PLATE_CLASS,
        BASE_WRITE));
  }
}
