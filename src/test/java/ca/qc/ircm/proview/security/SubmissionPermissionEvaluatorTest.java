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

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRepository;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionPermissionEvaluatorTest {
  private static final String SUBMISSION_CLASS = Submission.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = BasePermission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = BasePermission.WRITE;
  private SubmissionPermissionEvaluator permissionEvaluator;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private UserRepository userRepository;
  @Inject
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    permissionEvaluator = new SubmissionPermissionEvaluator(submissionRepository, userRepository,
        authorizationService);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_Owner() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Read_NotOwner() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Read_Manager() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("marie.trudel@ircm.qc.ca")
  public void hasPermission_Read_OtherManager() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Admin() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() throws Throwable {
    Submission submission = new Submission();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() throws Throwable {
    Submission submission = new Submission();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() throws Throwable {
    Submission submission = new Submission();
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() throws Throwable {
    Submission submission = new Submission();
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_Owner() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("patricia.jones@ircm.qc.ca")
  public void hasPermission_Write_NotOwner() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_Manager() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("marie.trudel@ircm.qc.ca")
  public void hasPermission_Write_OtherManager() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Admin() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission, BASE_WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), submission.getId(),
        SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  public void hasPermission_NullAuthentication() throws Throwable {
    Submission submission = submissionRepository.findOne(35L);
    assertFalse(permissionEvaluator.hasPermission(null, submission, READ));
    assertFalse(permissionEvaluator.hasPermission(null, submission, WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, submission, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(null, submission, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(null, submission.getId(), SUBMISSION_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(null, submission.getId(), SUBMISSION_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(null, submission.getId(), SUBMISSION_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(null, submission.getId(), SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Null_Anonymous() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Null() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, SUBMISSION_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotSubmission() throws Throwable {
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
        permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS,
        WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS,
        BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SUBMISSION_CLASS,
        BASE_WRITE));
  }
}
