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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@SuppressWarnings("deprecation")
public class AuthorizationServiceTest {
  private static final String ADMIN = UserRole.ADMIN;
  private static final String MANAGER = UserRole.MANAGER;
  private static final String USER = UserRole.USER;
  private static final String DEFAULT_ROLE = USER;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private UserDetailsService userDetailsService;
  @Mock
  private PermissionEvaluator permissionEvaluator;
  @Mock
  private Object object;
  @Mock
  private Permission permission;
  private Subject subject;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    //subject = SecurityUtils.getSubject();
    authorizationService.setPermissionEvaluator(permissionEvaluator);
  }

  private void switchToUser(String username) {
    Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
    authorities.add(new SwitchUserGrantedAuthority(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR,
        previousAuthentication));
    TestingAuthenticationToken authentication =
        new TestingAuthenticationToken(userDetails, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @WithAnonymousUser
  public void getCurrentUser_Anonymous() throws Throwable {
    assertNull(authorizationService.getCurrentUser());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void getCurrentUser() throws Throwable {
    User user = authorizationService.getCurrentUser();
    assertNotNull(authorizationService.getCurrentUser());
    assertEquals((Long) 1L, user.getId());
  }

  @Test
  @WithAnonymousUser
  public void isAnonymous_True() throws Throwable {
    assertTrue(authorizationService.isAnonymous());
  }

  @Test
  @WithMockUser
  public void isAnonymous_False() throws Throwable {
    assertFalse(authorizationService.isAnonymous());
  }

  @Test
  @WithMockUser
  public void hasRole_False() throws Throwable {
    assertFalse(authorizationService.hasRole(ADMIN));
  }

  @Test
  @WithMockUser
  public void hasRole_True() throws Throwable {
    assertTrue(authorizationService.hasRole(DEFAULT_ROLE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasRole_SwitchedUser() throws Throwable {
    switchToUser("benoit.coulombe@ircm.qc.ca");
    assertTrue(authorizationService.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
  }

  @Test
  @WithMockUser
  public void hasAnyRole_False() throws Throwable {
    assertFalse(authorizationService.hasAnyRole(ADMIN, MANAGER));
  }

  @Test
  @WithMockUser
  public void hasAnyRole_TrueFirst() throws Throwable {
    assertTrue(authorizationService.hasAnyRole(DEFAULT_ROLE, MANAGER));
  }

  @Test
  @WithMockUser
  public void hasAnyRole_TrueLast() throws Throwable {
    assertTrue(authorizationService.hasAnyRole(ADMIN, DEFAULT_ROLE));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hasAnyRole_SwitchedUser() throws Throwable {
    switchToUser("benoit.coulombe@ircm.qc.ca");
    assertTrue(authorizationService.hasAnyRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  @Ignore("User does not have expired password")
  public void removeForceChangePasswordRole() throws Throwable {
    fail("Program test");
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  @Ignore("User does not have expired password")
  public void removeForceChangePasswordRole_NoForceChangePasswordRole() throws Throwable {
    fail("Program test");
  }

  @Test
  @WithMockUser
  public void isAuthorized_NoRole() throws Throwable {
    assertTrue(authorizationService.isAuthorized(NoRoleTest.class));
  }

  @Test
  @WithMockUser
  public void isAuthorized_UserRole_True() throws Throwable {
    assertTrue(authorizationService.isAuthorized(UserRoleTest.class));
  }

  @Test
  @WithMockUser(roles = {})
  public void isAuthorized_UserRole_False() throws Throwable {
    assertFalse(authorizationService.isAuthorized(UserRoleTest.class));
  }

  @Test
  @WithMockUser(authorities = { MANAGER })
  public void isAuthorized_ManagerRole_True() throws Throwable {
    assertTrue(authorizationService.isAuthorized(ManagerRoleTest.class));
  }

  @Test
  @WithMockUser
  public void isAuthorized_ManagerRole_False() throws Throwable {
    assertFalse(authorizationService.isAuthorized(ManagerRoleTest.class));
  }

  @Test
  @WithMockUser(authorities = { ADMIN })
  public void isAuthorized_AdminRole_True() throws Throwable {
    assertTrue(authorizationService.isAuthorized(AdminRoleTest.class));
  }

  @Test
  @WithMockUser
  public void isAuthorized_AdminRole_False() throws Throwable {
    assertFalse(authorizationService.isAuthorized(AdminRoleTest.class));
  }

  @Test
  @WithMockUser(authorities = { MANAGER })
  public void isAuthorized_ManagerOrAdminRole_Manager() throws Throwable {
    assertTrue(authorizationService.isAuthorized(ManagerOrAdminRoleTest.class));
  }

  @Test
  @WithMockUser(authorities = { ADMIN })
  public void isAuthorized_ManagerOrAdminRole_Admin() throws Throwable {
    assertTrue(authorizationService.isAuthorized(ManagerOrAdminRoleTest.class));
  }

  @Test
  @WithMockUser
  public void isAuthorized_ManagerOrAdminRole_False() throws Throwable {
    assertFalse(authorizationService.isAuthorized(ManagerOrAdminRoleTest.class));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isAuthorized_SwitchedUser() throws Throwable {
    switchToUser("benoit.coulombe@ircm.qc.ca");
    assertTrue(authorizationService.isAuthorized(UserRoleTest.class));
    assertTrue(authorizationService.isAuthorized(ManagerRoleTest.class));
    assertFalse(authorizationService.isAuthorized(AdminRoleTest.class));
    assertTrue(authorizationService.isAuthorized(ManagerOrAdminRoleTest.class));
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_False() throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertFalse(authorizationService.hasPermission(object, permission));
    verify(permissionEvaluator).hasPermission(authentication, object, permission);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_True() throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    assertTrue(authorizationService.hasPermission(object, permission));
    verify(permissionEvaluator).hasPermission(authentication, object, permission);
  }

  public static final class NoRoleTest {
  }

  @RolesAllowed(USER)
  public static final class UserRoleTest {
  }

  @RolesAllowed(MANAGER)
  public static final class ManagerRoleTest {
  }

  @RolesAllowed(ADMIN)
  public static final class AdminRoleTest {
  }

  @RolesAllowed({ MANAGER, ADMIN })
  public static final class ManagerOrAdminRoleTest {
  }

  @Test
  @Ignore
  public void isUser_Authenticated() {
    when(subject.isAuthenticated()).thenReturn(true);

    boolean value = authorizationService.isUser();

    assertEquals(true, value);
  }

  @Test
  @Ignore
  public void isUser_Remembered() {
    when(subject.isRemembered()).thenReturn(true);

    boolean value = authorizationService.isUser();

    assertEquals(true, value);
  }

  @Test
  @Ignore
  public void isUser_NotAuthenticatedOrRemembered() {
    boolean value = authorizationService.isUser();

    assertEquals(false, value);
  }

  @Test
  @Ignore
  public void isRunAs_True() {
    when(subject.isRunAs()).thenReturn(true);

    boolean value = authorizationService.isRunAs();

    assertEquals(true, value);
  }

  @Test
  @Ignore
  public void isRunAs_False() {
    when(subject.isRunAs()).thenReturn(false);

    boolean value = authorizationService.isRunAs();

    assertEquals(false, value);
  }

  @Test
  @Ignore
  public void hasAdminRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationService.hasAdminRole();

    verify(subject).hasRole(ADMIN);
    assertEquals(false, hasRole);
  }

  @Test
  @Ignore
  public void hasAdminRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationService.hasAdminRole();

    verify(subject).hasRole(ADMIN);
    assertEquals(true, hasRole);
  }

  @Test
  @Ignore
  public void hasManagerRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationService.hasManagerRole();

    verify(subject).hasRole(MANAGER);
    assertEquals(false, hasRole);
  }

  @Test
  @Ignore
  public void hasManagerRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationService.hasManagerRole();

    verify(subject).hasRole(MANAGER);
    assertEquals(true, hasRole);
  }

  @Test
  @Ignore
  public void hasUserRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationService.hasUserRole();

    verify(subject).hasRole(USER);
    assertEquals(false, hasRole);
  }

  @Test
  @Ignore
  public void hasUserRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationService.hasUserRole();

    verify(subject).hasRole(USER);
    assertEquals(true, hasRole);
  }

  @Test
  @Ignore
  public void checkAdminRole_Admin() {
    authorizationService.checkAdminRole();

    verify(subject).checkRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkAdminRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationService.checkAdminRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkUserRole_User() {
    authorizationService.checkUserRole();

    verify(subject).checkRole(USER);
  }

  @Test
  @Ignore
  public void checkUserRole_NonUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationService.checkUserRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @Ignore
  public void checkRobotRole_Robot() {
    authorizationService.checkRobotRole();

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  @Ignore
  public void checkRobotRole_Other() {
    doThrow(new AuthorizationException()).when(subject)
        .checkPermission(any(org.apache.shiro.authz.Permission.class));

    try {
      authorizationService.checkRobotRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  @Ignore
  public void checkLaboratoryReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkLaboratoryReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @Ignore
  public void checkLaboratoryReadPermission_Member() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  @Ignore
  public void checkLaboratoryReadPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  @Ignore
  public void checkLaboratoryReadPermission_Null() {
    authorizationService.checkLaboratoryReadPermission(null);
  }

  @Test
  @Ignore
  public void hasLaboratoryManagerPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    boolean manager = authorizationService.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER);
    verify(subject).hasRole(UserRole.ADMIN);
    assertEquals(true, manager);
  }

  @Test
  @Ignore
  public void hasLaboratoryManagerPermission_LaboratoryManager() {
    when(subject.hasRole(UserRole.USER)).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationService.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER);
    verify(subject).hasRole(UserRole.ADMIN);
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(true, manager);
  }

  @Test
  @Ignore
  public void hasLaboratoryManagerPermission_False() {
    when(subject.hasRole(UserRole.USER)).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationService.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER);
    verify(subject).hasRole(UserRole.ADMIN);
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(false, manager);
  }

  @Test
  @Ignore
  public void hasLaboratoryManagerPermission_Null() {
    boolean manager = authorizationService.hasLaboratoryManagerPermission(null);

    assertEquals(false, manager);
  }

  @Test
  @Ignore
  public void checkLaboratoryManagerPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkLaboratoryManagerPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @Ignore
  public void checkLaboratoryManagerPermission_LaboratoryManager() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationService.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkLaboratoryManagerPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationService.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkLaboratoryManagerPermission_Null() {
    authorizationService.checkLaboratoryManagerPermission(null);
  }

  @Test
  @Ignore
  public void checkUserReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    authorizationService.checkUserReadPermission(user);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
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

    verify(subject).checkRole(USER);
  }

  @Test
  @Ignore
  public void checkUserReadPermission_CanRead() {
    User user = new User(5L);
    user.setLaboratory(new Laboratory(1L));

    authorizationService.checkUserReadPermission(user);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:1");
    verify(subject).checkPermission("user:read:5");
  }

  @Test
  @Ignore
  public void checkUserReadPermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserReadPermission(user);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
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

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:read:10");
  }

  @Test
  @Ignore
  public void checkUserReadPermission_Null() {
    authorizationService.checkUserReadPermission(null);
  }

  @Test
  @Ignore
  public void hasUserWritePermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void hasUserWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertFalse(value);
    verify(subject).hasRole(USER);
  }

  @Test
  @Ignore
  public void hasUserWritePermission_LaboratoryManager() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void hasUserWritePermission_CanWrite() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted("user:write:10")).thenReturn(true);
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:10");
  }

  @Test
  @Ignore
  public void hasUserWritePermission_CannotWrite() {
    when(subject.hasRole(USER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationService.hasUserWritePermission(user);

    assertFalse(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:10");
  }

  @Test
  @Ignore
  public void hasUserWritePermission_Null() {
    boolean value = authorizationService.hasUserWritePermission(null);

    assertFalse(value);
  }

  @Test
  @Ignore
  public void checkUserWritePermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserWritePermission(user);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
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

    verify(subject).checkRole(USER);
  }

  @Test
  @Ignore
  public void checkUserWritePermission_LaboratoryManager() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserWritePermission(user);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkUserWritePermission_CanWrite() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted("user:write:10")).thenReturn(true);
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    authorizationService.checkUserWritePermission(user);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:10");
  }

  @Test
  @Ignore
  public void checkUserWritePermission_CannotWrite() {
    when(subject.hasRole(USER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationService.checkUserWritePermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:write:10");
  }

  @Test
  @Ignore
  public void checkUserWritePermission_Null() {
    authorizationService.checkUserWritePermission(null);
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_SubmissionSample_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_SubmissionSample_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkSampleReadPermission_SubmissionSample_SampleOwner() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_SubmissionSample_LaboratoryManager() throws Exception {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_SubmissionSample_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    SubmissionSample sample = new SubmissionSample(446L);

    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("sample:owner:446");
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_Control_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Control sample = new Control(444L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_Control_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Control sample = new Control(444L);

    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkSampleReadPermission_Control_SampleOwnerForAnalysis() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Control sample = new Control(444L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 3)
  @Ignore
  public void checkSampleReadPermission_Control_LaboratoryManagerForAnalysis() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Control sample = new Control(444L);

    authorizationService.checkSampleReadPermission(sample);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 6)
  @Ignore
  public void checkSampleReadPermission_Control_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));

    Control sample = new Control(444L);
    try {
      authorizationService.checkSampleReadPermission(sample);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).checkPermission("sample:owner:444");
  }

  @Test
  @Ignore
  public void checkSampleReadPermission_Null() throws Exception {
    authorizationService.checkSampleReadPermission(null);
  }

  @Test
  @Ignore
  public void checkSubmissionReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(35L);

    authorizationService.checkSubmissionReadPermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSubmissionReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Submission submission = new Submission(35L);

    try {
      authorizationService.checkSubmissionReadPermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkSubmissionReadPermission_SubmissionOwner() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(35L);

    authorizationService.checkSubmissionReadPermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSubmissionReadPermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(35L);

    authorizationService.checkSubmissionReadPermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkSubmissionReadPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));

    Submission submission = new Submission(35L);
    try {
      authorizationService.checkSubmissionReadPermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("submission:owner:35");
  }

  @Test
  @Ignore
  public void checkSubmissionReadPermission_Null() {
    authorizationService.checkSubmissionReadPermission(null);
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_Admin_ToApprove() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_Admin_Analysed() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_NotUser() {
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole(USER);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void hasSubmissionWritePermission_SubmissionOwner_ToApprove() {
    when(subject.hasRole(USER)).thenReturn(true);
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void hasSubmissionWritePermission_SubmissionOwner_Approved() {
    when(subject.hasRole(USER)).thenReturn(true);
    Submission submission = new Submission(164L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void hasSubmissionWritePermission_SubmissionOwner_Received() {
    when(subject.hasRole(USER)).thenReturn(true);
    Submission submission = new Submission(161L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void hasSubmissionWritePermission_SubmissionOwner_Analysed() {
    when(subject.hasRole(USER)).thenReturn(true);
    Submission submission = new Submission(156L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_LaboratoryManager_ToApprove() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_LaboratoryManager_Approved() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    Submission submission = new Submission(164L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertTrue(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_LaboratoryManager_Received() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    Submission submission = new Submission(161L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_LaboratoryManager_Analysed() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted("laboratory:manager:2")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_Other() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(false);
    Submission submission = new Submission(36L);

    boolean value = authorizationService.hasSubmissionWritePermission(submission);

    assertFalse(value);
    verify(subject).hasRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void hasSubmissionWritePermission_Null() {
    boolean value = authorizationService.hasSubmissionWritePermission(null);

    assertFalse(value);
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_Admin_ToApprove() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_Admin_Analysed() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Submission submission = new Submission(36L);

    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkSubmissionWritePermission_SubmissionOwner_ToApprove() {
    when(subject.hasRole(USER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkSubmissionWritePermission_SubmissionOwner_Analysed() {
    when(subject.hasRole(USER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_LaboratoryManager_ToApprove() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(36L);

    authorizationService.checkSubmissionWritePermission(submission);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_LaboratoryManager_Analysed() {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Submission submission = new Submission(156L);

    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_Other() {
    when(subject.hasRole(USER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));

    Submission submission = new Submission(36L);
    try {
      authorizationService.checkSubmissionWritePermission(submission);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("submission:owner:36");
  }

  @Test
  @Ignore
  public void checkSubmissionWritePermission_Null() {
    authorizationService.checkSubmissionWritePermission(null);
  }

  @Test
  @Ignore
  public void checkPlateReadPermission_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(26L);

    authorizationService.checkPlateReadPermission(plate);

    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
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
  @Ignore
  public void checkPlateReadPermission_UserOwner() throws Exception {
    when(subject.hasRole(USER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(123L);

    authorizationService.checkPlateReadPermission(plate);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkPlateReadPermission_UserNotOwner() throws Exception {
    when(subject.hasRole(USER)).thenReturn(true);
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
  @Ignore
  public void checkPlateReadPermission_LaboratoryManagerOwner() throws Exception {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.hasRole(MANAGER)).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Plate plate = new Plate(123L);

    authorizationService.checkPlateReadPermission(plate);

    verify(subject).hasRole(MANAGER);
  }

  @Test
  @WithSubject(userId = 3)
  @Ignore
  public void checkPlateReadPermission_LaboratoryManagerNotOwner() throws Exception {
    when(subject.hasRole(USER)).thenReturn(true);
    when(subject.hasRole(MANAGER)).thenReturn(true);
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
  @Ignore
  public void checkPlateReadPermission_Other() throws Exception {
    when(subject.hasRole(USER)).thenReturn(true);
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
  @Ignore
  public void checkPlateReadPermission_Null() throws Exception {
    authorizationService.checkPlateReadPermission(null);
  }

  @Test
  @Ignore
  public void checkMsAnalysisReadPermission_Admin() throws Exception {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @Ignore
  public void checkMsAnalysisReadPermission_NotUser() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    try {
      authorizationService.checkMsAnalysisReadPermission(msAnalysis);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
  }

  @Test
  @WithSubject(userId = 10)
  @Ignore
  public void checkMsAnalysisReadPermission_SampleOwner() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 3)
  @Ignore
  public void checkMsAnalysisReadPermission_LaboratoryManager() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
  }

  @Test
  @WithSubject(userId = 6)
  @Ignore
  public void checkMsAnalysisReadPermission_Other() throws Exception {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    MsAnalysis msAnalysis = new MsAnalysis(13L);

    try {
      authorizationService.checkMsAnalysisReadPermission(msAnalysis);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(USER);
    verify(subject).hasRole(ADMIN);
    verify(subject).checkPermission("msAnalysis:read:13");
  }

  @Test
  @Ignore
  public void checkMsAnalysisReadPermission_Null() throws Exception {
    authorizationService.checkMsAnalysisReadPermission(null);
  }
}
