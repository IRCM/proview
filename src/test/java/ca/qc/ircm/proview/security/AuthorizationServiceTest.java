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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Tests for {@link AuthorizationService}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AuthorizationServiceTest {
  private static final String ADMIN = UserRole.ADMIN;
  private static final String MANAGER = UserRole.MANAGER;
  private static final String USER = UserRole.USER;
  private static final String DEFAULT_ROLE = USER;
  @Autowired
  private AuthorizationService authorizationService;
  @Autowired
  private UserDetailsService userDetailsService;
  @Mock
  private PermissionEvaluator permissionEvaluator;
  @Mock
  private Object object;
  @Mock
  private Permission permission;

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
    assertFalse(authorizationService.getCurrentUser().isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void getCurrentUser() throws Throwable {
    User user = authorizationService.getCurrentUser().orElse(null);
    assertNotNull(user);
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
  @WithAnonymousUser
  public void hasRole_Anonymous() throws Throwable {
    assertFalse(authorizationService.hasRole(ADMIN));
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
  @WithAnonymousUser
  public void hasAnyRole_Anonymous() throws Throwable {
    assertFalse(authorizationService.hasAnyRole(ADMIN, MANAGER));
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

  /**
   * Class that requires no role.
   */
  public static final class NoRoleTest {
  }

  /**
   * Class that requires USER role.
   */
  @RolesAllowed(USER)
  public static final class UserRoleTest {
  }

  /**
   * Class that requires MANAGER role.
   */
  @RolesAllowed(MANAGER)
  public static final class ManagerRoleTest {
  }

  /**
   * Class that requires ADMIN role.
   */
  @RolesAllowed(ADMIN)
  public static final class AdminRoleTest {
  }

  /**
   * Class that requires MANAGER or ADMIN role.
   */
  @RolesAllowed({ MANAGER, ADMIN })
  public static final class ManagerOrAdminRoleTest {
  }
}
