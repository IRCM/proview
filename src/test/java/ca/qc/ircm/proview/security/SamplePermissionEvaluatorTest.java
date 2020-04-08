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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SamplePermissionEvaluatorTest {
  private static final String SAMPLE_CLASS = Sample.class.getName();
  private static final String READ = "read";
  private static final Permission BASE_READ = BasePermission.READ;
  private static final String WRITE = "write";
  private static final Permission BASE_WRITE = BasePermission.WRITE;
  private SamplePermissionEvaluator permissionEvaluator;
  @Autowired
  private SampleRepository sampleRepository;
  @Autowired
  private SubmissionSampleRepository submissionSampleRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AuthorizationService authorizationService;
  @Mock
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    permissionEvaluator = new SamplePermissionEvaluator(sampleRepository, userRepository,
        authorizationService, submissionPermissionEvaluator);
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
  }

  private Authentication authentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous() throws Throwable {
    Sample sample = sampleRepository.findById(446L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Read_Anonymous_Control() throws Throwable {
    Sample sample = sampleRepository.findById(444L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_SubmissionTrue() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    SubmissionSample sample = submissionSampleRepository.findById(446L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(sample.getSubmission()), any(),
        eq(BASE_READ));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Read_SubmissionFalse() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(false);
    SubmissionSample sample = submissionSampleRepository.findById(446L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(sample.getSubmission()), any(),
        eq(BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Read_Control_Admin() throws Throwable {
    Sample sample = sampleRepository.findById(444L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Read_Control_NotAdmin() throws Throwable {
    Sample sample = sampleRepository.findById(444L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_READ));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous() throws Throwable {
    Sample sample = new SubmissionSample();
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_WriteNew_Anonymous_Control() throws Throwable {
    Sample sample = new Control();
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User() throws Throwable {
    Sample sample = new SubmissionSample();
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_WriteNew_User_Control() throws Throwable {
    Sample sample = new Control();
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager() throws Throwable {
    Sample sample = new SubmissionSample();
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_WriteNew_Manager_Control() throws Throwable {
    Sample sample = new Control();
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin() throws Throwable {
    Sample sample = new SubmissionSample();
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_WriteNew_Admin_Control() throws Throwable {
    Sample sample = new Control();
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous() throws Throwable {
    Sample sample = sampleRepository.findById(446L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Write_Anonymous_Control() throws Throwable {
    Sample sample = sampleRepository.findById(444L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_SubmissionTrue() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(true);
    SubmissionSample sample = submissionSampleRepository.findById(446L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(sample.getSubmission()), any(),
        eq(BASE_WRITE));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void hasPermission_Write_SubmissionFalse() throws Throwable {
    when(submissionPermissionEvaluator.hasPermission(any(Submission.class), any(), any()))
        .thenReturn(false);
    SubmissionSample sample = submissionSampleRepository.findById(446L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
    verify(submissionPermissionEvaluator, times(4)).hasPermission(eq(sample.getSubmission()), any(),
        eq(BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Write_Control_Admin() throws Throwable {
    Sample sample = sampleRepository.findById(444L).orElse(null);
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertTrue(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertTrue(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hasPermission_Write_Control_NotAdmin() throws Throwable {
    Sample sample = sampleRepository.findById(444L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample, BASE_WRITE));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), sample.getId(), SAMPLE_CLASS,
        BASE_WRITE));
  }

  @Test
  public void hasPermission_NullAuthentication() throws Throwable {
    Sample sample = sampleRepository.findById(446L).orElse(null);
    assertFalse(permissionEvaluator.hasPermission(null, sample, READ));
    assertFalse(permissionEvaluator.hasPermission(null, sample, WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, sample, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(null, sample, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, sample.getId(), SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(null, sample.getId(), SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(null, sample.getId(), SAMPLE_CLASS, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(null, sample.getId(), SAMPLE_CLASS, BASE_WRITE));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_Null_Anonymous() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, BASE_READ));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_Null() throws Throwable {
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, BASE_WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, BASE_READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), null, SAMPLE_CLASS, BASE_WRITE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasPermission_NotSample() throws Throwable {
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
        permissionEvaluator.hasPermission(authentication(), "Informatics", SAMPLE_CLASS, READ));
    assertFalse(
        permissionEvaluator.hasPermission(authentication(), "Informatics", SAMPLE_CLASS, WRITE));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SAMPLE_CLASS,
        BASE_READ));
    assertFalse(permissionEvaluator.hasPermission(authentication(), "Informatics", SAMPLE_CLASS,
        BASE_WRITE));
  }
}
