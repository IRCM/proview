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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Locale;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AuthorizationServiceTest {
  private AuthorizationService authorizationService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  private Subject subject;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    authorizationService = new AuthorizationService(entityManager, queryFactory);
    subject = SecurityUtils.getSubject();
  }

  @Test
  @WithSubject(userId = 3)
  public void getCurrentUser() {
    User user = authorizationService.getCurrentUser();

    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    Address address = user.getAddress();
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
    assertEquals(true, user.isValid());
    assertEquals(false, user.isAdmin());
  }

  @Test
  @WithSubject(anonymous = true)
  public void getCurrentUser_Anonymous() {
    User user = authorizationService.getCurrentUser();

    assertNull(user);
  }

  @Test
  public void isUser_Authenticated() {
    when(subject.isAuthenticated()).thenReturn(true);

    boolean value = authorizationService.isUser();

    assertEquals(true, value);
  }

  @Test
  public void isUser_Remembered() {
    when(subject.isRemembered()).thenReturn(true);

    boolean value = authorizationService.isUser();

    assertEquals(true, value);
  }

  @Test
  public void isUser_NotAuthenticatedOrRemembered() {
    boolean value = authorizationService.isUser();

    assertEquals(false, value);
  }

  @Test
  public void isRunAs_True() {
    when(subject.isRunAs()).thenReturn(true);

    boolean value = authorizationService.isRunAs();

    assertEquals(true, value);
  }

  @Test
  public void isRunAs_False() {
    when(subject.isRunAs()).thenReturn(false);

    boolean value = authorizationService.isRunAs();

    assertEquals(false, value);
  }

  @Test
  public void hasAdminRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationService.hasAdminRole();

    verify(subject).hasRole("ADMIN");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasAdminRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationService.hasAdminRole();

    verify(subject).hasRole("ADMIN");
    assertEquals(true, hasRole);
  }

  @Test
  public void hasManagerRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationService.hasManagerRole();

    verify(subject).hasRole("MANAGER");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasManagerRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationService.hasManagerRole();

    verify(subject).hasRole("MANAGER");
    assertEquals(true, hasRole);
  }

  @Test
  public void hasUserRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationService.hasUserRole();

    verify(subject).hasRole("USER");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasUserRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationService.hasUserRole();

    verify(subject).hasRole("USER");
    assertEquals(true, hasRole);
  }

  @Test
  public void checkAdminRole_Admin() {
    authorizationService.checkAdminRole();

    verify(subject).checkRole("ADMIN");
  }

  @Test
  public void checkAdminRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationService.checkAdminRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("ADMIN");
  }

  @Test
  public void checkUserRole_User() {
    authorizationService.checkUserRole();

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserRole_NonUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationService.checkUserRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkRobotRole_Robot() {
    authorizationService.checkRobotRole();

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  public void checkRobotRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(Permission.class));

    try {
      authorizationService.checkRobotRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  public void checkLaboratoryReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkLaboratoryReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkLaboratoryReadPermission_Member() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  public void checkLaboratoryReadPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  public void checkLaboratoryReadPermission_Null() {
    authorizationService.checkLaboratoryReadPermission(null);
  }

  @Test
  public void hasLaboratoryManagerPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    boolean manager = authorizationService.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.ADMIN.name());
    assertEquals(true, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_LaboratoryManager() {
    when(subject.hasRole(UserRole.USER.name())).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationService.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.ADMIN.name());
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(true, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_False() {
    when(subject.hasRole(UserRole.USER.name())).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationService.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.ADMIN.name());
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(false, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_Null() {
    boolean manager = authorizationService.hasLaboratoryManagerPermission(null);

    assertEquals(false, manager);
  }

  @Test
  public void checkLaboratoryManagerPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkLaboratoryManagerPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkLaboratoryManagerPermission_LaboratoryManager() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  public void checkLaboratoryManagerPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  public void checkLaboratoryManagerPermission_Null() {
    authorizationService.checkLaboratoryManagerPermission(null);
  }

  @Test
  public void checkUserReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    authorizationService.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkUserReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    try {
      authorizationService.checkUserReadPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserReadPermission_CanRead() {
    User user = new User(5L);
    user.setLaboratory(new Laboratory(1L));

    authorizationService.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:1");
    verify(subject).checkPermission("user:read:5");
  }

  @Test
  public void checkUserReadPermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkUserReadPermission_CannotRead() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationService.checkUserReadPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:read:10");
  }

  @Test
  public void checkUserReadPermission_Null() {
    authorizationService.checkUserReadPermission(null);
  }

  @Test
  public void hasUserWritePermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void hasUserWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertFalse(value);
    verify(subject).hasRole("USER");
  }

  @Test
  public void hasUserWritePermission_LaboratoryManager() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasUserWritePermission_CanWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("user:write:10")).thenReturn(true);
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:10");
  }

  @Test
  public void hasUserWritePermission_CannotWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:10");
  }

  @Test
  public void hasUserWritePermission_Null() {
    boolean value = authorizationService.hasUserWritePermission(null);

    assertFalse(value);
  }

  @Test
  public void checkUserWritePermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkUserWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationService.checkUserWritePermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserWritePermission_LaboratoryManager() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkUserWritePermission_CanWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("user:write:10")).thenReturn(true);
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:10");
  }

  @Test
  public void checkUserWritePermission_CannotWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationService.checkUserWritePermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:write:10");
  }

  @Test
  public void checkUserWritePermission_Null() {
    authorizationService.checkUserWritePermission(null);
  }

  @Test
  public void checkSampleReadPermission_SubmissionSample_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSampleReadPermission_SubmissionSample_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkSampleReadPermission_SubmissionSample_SampleOwner() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSampleReadPermission_SubmissionSample_LaboratoryManager() throws Exception {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkSampleReadPermission_SubmissionSample_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("sample:owner:446");
  }

  @Test
  public void checkSampleReadPermission_Control_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Control sample = new Control(444L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSampleReadPermission_Control_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Control sample = new Control(444L);

    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkSampleReadPermission_Control_SampleOwnerForAnalysis() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Control sample = new Control(444L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 3)
  public void checkSampleReadPermission_Control_LaboratoryManagerForAnalysis() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Control sample = new Control(444L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 6)
  public void checkSampleReadPermission_Control_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));

    Control sample = new Control(444L);
    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("sample:owner:444");
  }

  @Test
  public void checkSampleReadPermission_Null() throws Exception {
    authorizationService.checkSampleReadPermission(null);
  }

  @Test
  public void checkSubmissionReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(35L);

    authorizationService.checkSubmissionReadPermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSubmissionReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Submission submission = new Submission(35L);

    try {
      authorizationService.checkSubmissionReadPermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkSubmissionReadPermission_SubmissionOwner() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(35L);

    authorizationService.checkSubmissionReadPermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSubmissionReadPermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(35L);

    authorizationService.checkSubmissionReadPermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkSubmissionReadPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));

    Submission submission = new Submission(35L);
    try {
      authorizationService.checkSubmissionReadPermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("submission:owner:35");
  }

  @Test
  public void checkSubmissionReadPermission_Null() {
    authorizationService.checkSubmissionReadPermission(null);
  }

  @Test
  public void hasSubmissionWritePermission_Admin_ToApprove() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void hasSubmissionWritePermission_Admin_Analysed() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void hasSubmissionWritePermission_NotUser() {
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void hasSubmissionWritePermission_SubmissionOwner_ToApprove() {
    when(subject.hasRole("USER")).thenReturn(true);
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 10)
  public void hasSubmissionWritePermission_SubmissionOwner_Approved() {
    when(subject.hasRole("USER")).thenReturn(true);
    Submission submission = new Submission(164L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 10)
  public void hasSubmissionWritePermission_SubmissionOwner_Received() {
    when(subject.hasRole("USER")).thenReturn(true);
    Submission submission = new Submission(161L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 10)
  public void hasSubmissionWritePermission_SubmissionOwner_Analysed() {
    when(subject.hasRole("USER")).thenReturn(true);
    Submission submission = new Submission(156L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void hasSubmissionWritePermission_LaboratoryManager_ToApprove() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasSubmissionWritePermission_LaboratoryManager_Approved() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    Submission submission = new Submission(164L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasSubmissionWritePermission_LaboratoryManager_Received() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    Submission submission = new Submission(161L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasSubmissionWritePermission_LaboratoryManager_Analysed() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasSubmissionWritePermission_Other() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(false);
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasSubmissionWritePermission_Null() {
    boolean value = authorizationService.hasSubmissionWritePermission(null);

    assertFalse(value);
  }

  @Test
  public void checkSubmissionWritePermission_Admin_ToApprove() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSubmissionWritePermission_Admin_Analysed() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSubmissionWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Submission submission = new Submission(36L);

    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkSubmissionWritePermission_SubmissionOwner_ToApprove() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkSubmissionWritePermission_SubmissionOwner_Analysed() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkSubmissionWritePermission_LaboratoryManager_ToApprove() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkSubmissionWritePermission_LaboratoryManager_Analysed() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkSubmissionWritePermission_Other() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));

    Submission submission = new Submission(36L);
    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("submission:owner:36");
  }

  @Test
  public void checkSubmissionWritePermission_Null() {
    authorizationService.checkSubmissionWritePermission(null);
  }

  @Test
  public void checkPlateReadPermission_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(26L);

    authorizationService.checkPlateReadPermission(plate);

    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkPlateReadPermission_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(26L);

    try {
      authorizationService.checkPlateReadPermission(plate);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }
  }

  @Test
  @WithSubject(userId = 10)
  public void checkPlateReadPermission_UserOwner() throws Exception {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(123L);

    authorizationService.checkPlateReadPermission(plate);
  }

  @Test
  @WithSubject(userId = 10)
  public void checkPlateReadPermission_UserNotOwner() throws Exception {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(26L);

    try {
      authorizationService.checkPlateReadPermission(plate);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }
  }

  @Test
  @WithSubject(userId = 3)
  public void checkPlateReadPermission_LaboratoryManagerOwner() throws Exception {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.hasRole("MANAGER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(123L);

    authorizationService.checkPlateReadPermission(plate);

    verify(subject).hasRole("MANAGER");
  }

  @Test
  @WithSubject(userId = 3)
  public void checkPlateReadPermission_LaboratoryManagerNotOwner() throws Exception {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.hasRole("MANAGER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(26L);

    try {
      authorizationService.checkPlateReadPermission(plate);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }
  }

  @Test
  @WithSubject(userId = 6)
  public void checkPlateReadPermission_Other() throws Exception {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(123L);

    try {
      authorizationService.checkPlateReadPermission(plate);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }
  }

  @Test
  public void checkPlateReadPermission_Null() throws Exception {
    authorizationService.checkPlateReadPermission(null);
  }

  @Test
  public void checkMsAnalysisReadPermission_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkMsAnalysisReadPermission_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    try {
      authorizationService.checkMsAnalysisReadPermission(msAnalysis);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkMsAnalysisReadPermission_SampleOwner() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 3)
  public void checkMsAnalysisReadPermission_LaboratoryManager() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 6)
  public void checkMsAnalysisReadPermission_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    try {
      authorizationService.checkMsAnalysisReadPermission(msAnalysis);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("msAnalysis:read:13");
  }

  @Test
  public void checkMsAnalysisReadPermission_Null() throws Exception {
    authorizationService.checkMsAnalysisReadPermission(null);
  }

  @Test
  public void checkDataAnalysisReadPermission_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    DataAnalysis dataAnalysis = new DataAnalysis(5L);

    authorizationService.checkDataAnalysisReadPermission(dataAnalysis);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkDataAnalysisReadPermission_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    DataAnalysis dataAnalysis = new DataAnalysis(5L);

    try {
      authorizationService.checkDataAnalysisReadPermission(dataAnalysis);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  @WithSubject(userId = 10)
  public void checkDataAnalysisReadPermission_SampleOwner() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    DataAnalysis dataAnalysis = new DataAnalysis(5L);

    authorizationService.checkDataAnalysisReadPermission(dataAnalysis);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 3)
  public void checkDataAnalysisReadPermission_LaboratoryManager() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    DataAnalysis dataAnalysis = new DataAnalysis(5L);

    authorizationService.checkDataAnalysisReadPermission(dataAnalysis);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  @WithSubject(userId = 6)
  public void checkDataAnalysisReadPermission_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    DataAnalysis dataAnalysis = new DataAnalysis(5L);

    try {
      authorizationService.checkDataAnalysisReadPermission(dataAnalysis);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("dataAnalysis:read:5");
  }

  @Test
  public void checkDataAnalysisReadPermission_Null() throws Exception {
    authorizationService.checkDataAnalysisReadPermission(null);
  }
}
