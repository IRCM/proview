package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import jakarta.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link AuthenticatedUser}.
 */
@ServiceTestAnnotations
public class AuthenticatedUserTest {
  private static final String ADMIN = UserRole.ADMIN;
  private static final String MANAGER = UserRole.MANAGER;
  private static final String USER = UserRole.USER;
  private static final String DEFAULT_ROLE = USER;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private UserDetailsService userDetailsService;
  @MockitoBean
  private RoleValidator roleValidator;
  @MockitoBean
  private PermissionEvaluator permissionEvaluator;
  @Autowired
  private UserRepository repository;
  @Mock
  private Object object;

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
  public void getUser_Anonymous() throws Throwable {
    assertFalse(authenticatedUser.getUser().isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void getUser() throws Throwable {
    User user = authenticatedUser.getUser().orElseThrow();
    assertEquals((Long) 1L, user.getId());
  }

  @Test
  @WithMockUser("proview@ircm.qc.ca")
  public void getUser_NoId() throws Throwable {
    User user = authenticatedUser.getUser().orElseThrow();
    assertEquals((Long) 1L, user.getId());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void getUser_UsernameMissmatchId() throws Throwable {
    User user = repository.findById(1L).orElseThrow();
    user.setEmail("other_email@ircm.qc.ca");
    repository.save(user);
    user = authenticatedUser.getUser().orElseThrow();
    assertEquals((Long) 1L, user.getId());
  }

  @Test
  @WithAnonymousUser
  public void isAnonymous_True() throws Throwable {
    assertTrue(authenticatedUser.isAnonymous());
  }

  @Test
  @WithMockUser
  public void isAnonymous_False() throws Throwable {
    assertFalse(authenticatedUser.isAnonymous());
  }

  @Test
  @WithMockUser
  public void hasRole_False() {
    assertFalse(authenticatedUser.hasRole(DEFAULT_ROLE));
    verify(roleValidator).hasRole(DEFAULT_ROLE);
  }

  @Test
  @WithMockUser
  public void hasRole_True() {
    when(roleValidator.hasRole(any())).thenReturn(true);
    assertTrue(authenticatedUser.hasRole(DEFAULT_ROLE));
    verify(roleValidator).hasRole(DEFAULT_ROLE);
  }

  @Test
  @WithMockUser
  public void hasAnyRole_False() throws Throwable {
    assertFalse(authenticatedUser.hasAnyRole(DEFAULT_ROLE, MANAGER));
    verify(roleValidator).hasAnyRole(DEFAULT_ROLE, MANAGER);
  }

  @Test
  @WithMockUser
  public void hasAnyRole_True() throws Throwable {
    when(roleValidator.hasAnyRole(any(String[].class))).thenReturn(true);
    assertTrue(authenticatedUser.hasAnyRole(DEFAULT_ROLE, MANAGER));
    verify(roleValidator).hasAnyRole(DEFAULT_ROLE, MANAGER);
  }

  @Test
  @WithMockUser
  public void hasAllRoles_False() throws Throwable {
    assertFalse(authenticatedUser.hasAllRoles(DEFAULT_ROLE, MANAGER));
    verify(roleValidator).hasAllRoles(DEFAULT_ROLE, MANAGER);
  }

  @Test
  @WithMockUser
  public void hasAllRoles_True() throws Throwable {
    when(roleValidator.hasAllRoles(any(String[].class))).thenReturn(true);
    assertTrue(authenticatedUser.hasAllRoles(DEFAULT_ROLE, MANAGER));
    verify(roleValidator).hasAllRoles(DEFAULT_ROLE, MANAGER);
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  @Disabled("User does not have expired password")
  public void removeForceChangePasswordRole() throws Throwable {
    fail("Program test");
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  @Disabled("User does not have expired password")
  public void removeForceChangePasswordRole_NoForceChangePasswordRole() throws Throwable {
    fail("Program test");
  }

  @Test
  @WithMockUser
  public void isAuthorized_NoRole() throws Throwable {
    assertTrue(authenticatedUser.isAuthorized(NoRoleTest.class));
    verify(roleValidator, never()).hasAnyRole(any());
  }

  @Test
  @WithMockUser
  public void isAuthorized_UserRole_True() throws Throwable {
    when(roleValidator.hasAnyRole(any())).thenReturn(true);
    assertTrue(authenticatedUser.isAuthorized(UserRoleTest.class));
    verify(roleValidator).hasAnyRole(USER);
  }

  @Test
  @WithMockUser(roles = {})
  public void isAuthorized_UserRole_False() throws Throwable {
    assertFalse(authenticatedUser.isAuthorized(UserRoleTest.class));
    verify(roleValidator).hasAnyRole(USER);
  }

  @Test
  @WithMockUser(authorities = { MANAGER })
  public void isAuthorized_ManagerRole_True() throws Throwable {
    when(roleValidator.hasAnyRole(any())).thenReturn(true);
    assertTrue(authenticatedUser.isAuthorized(ManagerRoleTest.class));
    verify(roleValidator).hasAnyRole(MANAGER);
  }

  @Test
  @WithMockUser
  public void isAuthorized_ManagerRole_False() throws Throwable {
    assertFalse(authenticatedUser.isAuthorized(ManagerRoleTest.class));
    verify(roleValidator).hasAnyRole(MANAGER);
  }

  @Test
  @WithMockUser(authorities = { ADMIN })
  public void isAuthorized_AdminRole_True() throws Throwable {
    when(roleValidator.hasAnyRole(any())).thenReturn(true);
    assertTrue(authenticatedUser.isAuthorized(AdminRoleTest.class));
    verify(roleValidator).hasAnyRole(ADMIN);
  }

  @Test
  @WithMockUser
  public void isAuthorized_AdminRole_False() throws Throwable {
    assertFalse(authenticatedUser.isAuthorized(AdminRoleTest.class));
    verify(roleValidator).hasAnyRole(ADMIN);
  }

  @Test
  @WithMockUser(authorities = { MANAGER })
  public void isAuthorized_ManagerOrAdminRole_True() throws Throwable {
    when(roleValidator.hasAnyRole(any(String[].class))).thenReturn(true);
    assertTrue(authenticatedUser.isAuthorized(ManagerOrAdminRoleTest.class));
    verify(roleValidator).hasAnyRole(MANAGER, ADMIN);
  }

  @Test
  @WithMockUser
  public void isAuthorized_ManagerOrAdminRole_False() throws Throwable {
    assertFalse(authenticatedUser.isAuthorized(ManagerOrAdminRoleTest.class));
    verify(roleValidator).hasAnyRole(MANAGER, ADMIN);
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void isAuthorized_SwitchedUser() throws Throwable {
    when(roleValidator.hasAnyRole(USER)).thenReturn(true);
    when(roleValidator.hasAnyRole(MANAGER)).thenReturn(true);
    when(roleValidator.hasAnyRole(MANAGER, ADMIN)).thenReturn(true);
    switchToUser("benoit.coulombe@ircm.qc.ca");
    assertTrue(authenticatedUser.isAuthorized(UserRoleTest.class));
    verify(roleValidator).hasAnyRole(USER);
    assertTrue(authenticatedUser.isAuthorized(ManagerRoleTest.class));
    verify(roleValidator).hasAnyRole(MANAGER);
    assertFalse(authenticatedUser.isAuthorized(AdminRoleTest.class));
    verify(roleValidator).hasAnyRole(ADMIN);
    assertTrue(authenticatedUser.isAuthorized(ManagerOrAdminRoleTest.class));
    verify(roleValidator).hasAnyRole(MANAGER, ADMIN);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_False() throws Throwable {
    Permission permission = Permission.READ;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertFalse(authenticatedUser.hasPermission(object, permission));
    verify(permissionEvaluator).hasPermission(authentication, object, permission);
  }

  @Test
  @WithAnonymousUser
  public void hasPermission_True() throws Throwable {
    Permission permission = Permission.READ;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
    assertTrue(authenticatedUser.hasPermission(object, permission));
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
