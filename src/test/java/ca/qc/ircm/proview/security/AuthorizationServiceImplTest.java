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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AuthorizationServiceImplTest {
  private AuthorizationServiceImpl authorizationServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private ApplicationConfiguration applicationConfiguration;
  @Mock
  private AuthorizationInfo authorizationInfo;
  @Captor
  private ArgumentCaptor<String> roleCaptor;
  @Captor
  private ArgumentCaptor<PrincipalCollection> principalCollectionCaptor;
  private Subject subject;
  private String realmName = ShiroRealm.REALM_NAME;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    authorizationServiceImpl = new AuthorizationServiceImpl(entityManager, authenticationService);
    subject = SecurityUtils.getSubject();
  }

  @Test
  @WithSubject(userId = 3)
  public void getCurrentUser() {
    User user = authorizationServiceImpl.getCurrentUser();

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
    assertEquals(null, address.getSecondLine());
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
  public void isUser_Authenticated() {
    when(subject.isAuthenticated()).thenReturn(true);

    boolean value = authorizationServiceImpl.isUser();

    assertEquals(true, value);
  }

  @Test
  public void isUser_Remembered() {
    when(subject.isRemembered()).thenReturn(true);

    boolean value = authorizationServiceImpl.isUser();

    assertEquals(true, value);
  }

  @Test
  public void isUser_NotAuthenticatedOrRemembered() {
    boolean value = authorizationServiceImpl.isUser();

    assertEquals(false, value);
  }

  @Test
  public void isRunAs_True() {
    when(subject.isRunAs()).thenReturn(true);

    boolean value = authorizationServiceImpl.isRunAs();

    assertEquals(true, value);
  }

  @Test
  public void isRunAs_False() {
    when(subject.isRunAs()).thenReturn(false);

    boolean value = authorizationServiceImpl.isRunAs();

    assertEquals(false, value);
  }

  @Test
  public void hasAdminRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationServiceImpl.hasAdminRole();

    verify(subject).hasRole("ADMIN");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasAdminRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationServiceImpl.hasAdminRole();

    verify(subject).hasRole("ADMIN");
    assertEquals(true, hasRole);
  }

  @Test
  public void hasManagerRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationServiceImpl.hasManagerRole();

    verify(subject).hasRole("MANAGER");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasManagerRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationServiceImpl.hasManagerRole();

    verify(subject).hasRole("MANAGER");
    assertEquals(true, hasRole);
  }

  @Test
  public void hasManagerRole_User_False() {
    when(authenticationService.getAuthorizationInfo(any())).thenReturn(authorizationInfo);
    when(authorizationInfo.getRoles()).thenReturn(new HashSet<>());
    User user = new User(8L);

    boolean hasRole = authorizationServiceImpl.hasManagerRole(user);

    assertEquals(false, hasRole);
    verify(authenticationService).getAuthorizationInfo(principalCollectionCaptor.capture());
    PrincipalCollection principalCollection = principalCollectionCaptor.getValue();
    assertEquals(8L, principalCollection.getPrimaryPrincipal());
    assertEquals(1, principalCollection.fromRealm(realmName).size());
    assertEquals(8L, principalCollection.fromRealm(realmName).iterator().next());
  }

  @Test
  public void hasManagerRole_User_True() {
    when(authenticationService.getAuthorizationInfo(any())).thenReturn(authorizationInfo);
    Set<String> roles = new HashSet<>();
    roles.add(UserRole.MANAGER.name());
    when(authorizationInfo.getRoles()).thenReturn(roles);
    User user = new User(8L);

    boolean hasRole = authorizationServiceImpl.hasManagerRole(user);

    assertEquals(true, hasRole);
    verify(authenticationService).getAuthorizationInfo(principalCollectionCaptor.capture());
    PrincipalCollection principalCollection = principalCollectionCaptor.getValue();
    assertEquals(8L, principalCollection.getPrimaryPrincipal());
    assertEquals(1, principalCollection.fromRealm(realmName).size());
    assertEquals(8L, principalCollection.fromRealm(realmName).iterator().next());
  }

  @Test
  public void hasManagerRole_NullUser() {
    boolean hasRole = authorizationServiceImpl.hasManagerRole(null);

    assertEquals(false, hasRole);
  }

  @Test
  public void hasUserRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationServiceImpl.hasUserRole();

    verify(subject).hasRole("USER");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasUserRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationServiceImpl.hasUserRole();

    verify(subject).hasRole("USER");
    assertEquals(true, hasRole);
  }

  @Test
  public void checkAdminRole_Admin() {
    authorizationServiceImpl.checkAdminRole();

    verify(subject).checkRole("ADMIN");
  }

  @Test
  public void checkAdminRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationServiceImpl.checkAdminRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("ADMIN");
  }

  @Test
  public void checkUserRole_User() {
    authorizationServiceImpl.checkUserRole();

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserRole_NonUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationServiceImpl.checkUserRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkRobotRole_Robot() {
    authorizationServiceImpl.checkRobotRole();

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  public void checkRobotRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(Permission.class));

    try {
      authorizationServiceImpl.checkRobotRole();
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

    authorizationServiceImpl.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkLaboratoryReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceImpl.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkLaboratoryReadPermission_Member() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceImpl.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  public void checkLaboratoryReadPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceImpl.checkLaboratoryReadPermission(laboratory);
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
    authorizationServiceImpl.checkLaboratoryReadPermission(null);
  }

  @Test
  public void hasLaboratoryManagerPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    boolean manager = authorizationServiceImpl.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.ADMIN.name());
    assertEquals(true, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_LaboratoryManager() {
    when(subject.hasRole(UserRole.USER.name())).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationServiceImpl.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.ADMIN.name());
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(true, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_False() {
    when(subject.hasRole(UserRole.USER.name())).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationServiceImpl.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.ADMIN.name());
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(false, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_Null() {
    boolean manager = authorizationServiceImpl.hasLaboratoryManagerPermission(null);

    assertEquals(false, manager);
  }

  @Test
  public void checkLaboratoryManagerPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceImpl.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkLaboratoryManagerPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceImpl.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkLaboratoryManagerPermission_LaboratoryManager() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceImpl.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  public void checkLaboratoryManagerPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceImpl.checkLaboratoryManagerPermission(laboratory);
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
    authorizationServiceImpl.checkLaboratoryManagerPermission(null);
  }

  @Test
  public void checkUserReadPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    authorizationServiceImpl.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkUserReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    try {
      authorizationServiceImpl.checkUserReadPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserReadPermission_CanRead() {
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    authorizationServiceImpl.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:1");
    verify(subject).checkPermission("user:read:6");
  }

  @Test
  public void checkUserReadPermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceImpl.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkUserReadPermission_CannotRead() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceImpl.checkUserReadPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:read:8");
  }

  @Test
  public void checkUserReadPermission_Null() {
    authorizationServiceImpl.checkUserReadPermission(null);
  }

  @Test
  public void hasUserWritePermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationServiceImpl.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void hasUserWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationServiceImpl.hasUserWritePermission(user);

    assertFalse(value);
    verify(subject).hasRole("USER");
  }

  @Test
  public void hasUserWritePermission_LaboratoryManager() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationServiceImpl.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void hasUserWritePermission_CanWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("user:write:8")).thenReturn(true);
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationServiceImpl.hasUserWritePermission(user);

    assertTrue(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:8");
  }

  @Test
  public void hasUserWritePermission_CannotWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    boolean value = authorizationServiceImpl.hasUserWritePermission(user);

    assertFalse(value);
    verify(subject).hasRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:8");
  }

  @Test
  public void hasUserWritePermission_Null() {
    boolean value = authorizationServiceImpl.hasUserWritePermission(null);

    assertFalse(value);
  }

  @Test
  public void checkUserWritePermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceImpl.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkUserWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceImpl.checkUserWritePermission(user);
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
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceImpl.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkUserWritePermission_CanWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    when(subject.isPermitted("user:write:8")).thenReturn(true);
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceImpl.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).isPermitted("user:write:8");
  }

  @Test
  public void checkUserWritePermission_CannotWrite() {
    when(subject.hasRole("USER")).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceImpl.checkUserWritePermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:write:8");
  }

  @Test
  public void checkUserWritePermission_Null() {
    authorizationServiceImpl.checkUserWritePermission(null);
  }

  @Test
  public void checkUserWritePasswordPermission_Admin() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceImpl.checkUserWritePasswordPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
  }

  @Test
  public void checkUserWritePasswordPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceImpl.checkUserWritePasswordPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserWritePasswordPermission_LaboratoryManager() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceImpl.checkUserWritePasswordPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("user:write_password:8");
  }

  @Test
  public void checkUserWritePasswordPermission_CanWrite() {
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceImpl.checkUserWritePasswordPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("user:write_password:8");
  }

  @Test
  public void checkUserWritePasswordPermission_CannotWrite() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceImpl.checkUserWritePasswordPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("ADMIN");
    verify(subject).checkPermission("user:write_password:8");
  }

  @Test
  public void checkUserWritePasswordPermission_Null() {
    authorizationServiceImpl.checkUserWritePasswordPermission(null);
  }
}
