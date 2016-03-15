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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.test.config.DatabaseRule;
import ca.qc.ircm.proview.test.config.RollBack;
import ca.qc.ircm.proview.test.config.Rules;
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
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

@RollBack
public class AuthorizationServiceDefaultTest {
  private AuthorizationServiceDefault authorizationServiceDefault;
  @ClassRule
  public static DatabaseRule jpaDatabaseRule = new DatabaseRule();
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
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(jpaDatabaseRule);
  private Subject subject;
  private String realmName = "proview";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    authorizationServiceDefault = new AuthorizationServiceDefault(
        jpaDatabaseRule.getEntityManager(), authenticationService, applicationConfiguration);
    when(applicationConfiguration.getRealmName()).thenReturn(realmName);
    subject = SecurityUtils.getSubject();
  }

  @Test
  @WithSubject(userId = 3)
  public void getCurrentUser() {
    User user = authorizationServiceDefault.getCurrentUser();

    assertEquals((Long) 3L, user.getId());
    assertEquals("benoit.coulombe@ircm.qc.ca", user.getEmail());
    assertEquals("Benoit Coulombe", user.getName());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        user.getHashedPassword());
    assertEquals("4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8"
        + "ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535", user.getSalt());
    assertEquals((Integer) 1, user.getPasswordVersion());
    assertEquals(1, user.getAddresses().size());
    Address address = user.getAddresses().get(0);
    assertEquals("110, avenue des Pins Ouest", address.getAddress());
    assertEquals(null, address.getAddress2());
    assertEquals("Montréal", address.getTown());
    assertEquals("Québec", address.getState());
    assertEquals("H2W 1R7", address.getPostalCode());
    assertEquals("Canada", address.getCountry());
    assertEquals(true, address.isBilling());
    assertEquals(1, user.getPhoneNumbers().size());
    PhoneNumber phoneNumber = user.getPhoneNumbers().get(0);
    assertEquals(PhoneNumberType.WORK, phoneNumber.getType());
    assertEquals("514-555-5556", phoneNumber.getNumber());
    assertEquals(null, phoneNumber.getExtension());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isValid());
    assertEquals(false, user.isProteomic());
  }

  @Test
  public void isUser_Authenticated() {
    when(subject.isAuthenticated()).thenReturn(true);

    boolean value = authorizationServiceDefault.isUser();

    assertEquals(true, value);
  }

  @Test
  public void isUser_Remembered() {
    when(subject.isRemembered()).thenReturn(true);

    boolean value = authorizationServiceDefault.isUser();

    assertEquals(true, value);
  }

  @Test
  public void isUser_NotAuthenticatedOrRemembered() {
    boolean value = authorizationServiceDefault.isUser();

    assertEquals(false, value);
  }

  @Test
  public void isRunAs_True() {
    when(subject.isRunAs()).thenReturn(true);

    boolean value = authorizationServiceDefault.isRunAs();

    assertEquals(true, value);
  }

  @Test
  public void isRunAs_False() {
    when(subject.isRunAs()).thenReturn(false);

    boolean value = authorizationServiceDefault.isRunAs();

    assertEquals(false, value);
  }

  @Test
  public void hasProteomicRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationServiceDefault.hasProteomicRole();

    verify(subject).hasRole("PROTEOMIC");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasProteomicRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationServiceDefault.hasProteomicRole();

    verify(subject).hasRole("PROTEOMIC");
    assertEquals(true, hasRole);
  }

  @Test
  public void hasManagerRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationServiceDefault.hasManagerRole();

    verify(subject).hasRole("MANAGER");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasManagerRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationServiceDefault.hasManagerRole();

    verify(subject).hasRole("MANAGER");
    assertEquals(true, hasRole);
  }

  @Test
  public void hasManagerRole_User_False() {
    when(authenticationService.getAuthorizationInfo(any())).thenReturn(authorizationInfo);
    when(authorizationInfo.getRoles()).thenReturn(new HashSet<>());
    User user = new User(8L);

    boolean hasRole = authorizationServiceDefault.hasManagerRole(user);

    assertEquals(false, hasRole);
    verify(applicationConfiguration).getRealmName();
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

    boolean hasRole = authorizationServiceDefault.hasManagerRole(user);

    assertEquals(true, hasRole);
    verify(applicationConfiguration).getRealmName();
    verify(authenticationService).getAuthorizationInfo(principalCollectionCaptor.capture());
    PrincipalCollection principalCollection = principalCollectionCaptor.getValue();
    assertEquals(8L, principalCollection.getPrimaryPrincipal());
    assertEquals(1, principalCollection.fromRealm(realmName).size());
    assertEquals(8L, principalCollection.fromRealm(realmName).iterator().next());
  }

  @Test
  public void hasManagerRole_NullUser() {
    boolean hasRole = authorizationServiceDefault.hasManagerRole(null);

    assertEquals(false, hasRole);
  }

  @Test
  public void hasUserRole_False() {
    when(subject.hasRole(any(String.class))).thenReturn(false);

    boolean hasRole = authorizationServiceDefault.hasUserRole();

    verify(subject).hasRole("USER");
    assertEquals(false, hasRole);
  }

  @Test
  public void hasUserRole_True() {
    when(subject.hasRole(any(String.class))).thenReturn(true);

    boolean hasRole = authorizationServiceDefault.hasUserRole();

    verify(subject).hasRole("USER");
    assertEquals(true, hasRole);
  }

  @Test
  public void checkProteomicRole_Proteomic() {
    authorizationServiceDefault.checkProteomicRole();

    verify(subject).checkRole("PROTEOMIC");
  }

  @Test
  public void checkProteomicRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationServiceDefault.checkProteomicRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("PROTEOMIC");
  }

  @Test
  public void checkProteomicManagerRole_ProteomicManager() {
    authorizationServiceDefault.checkProteomicManagerRole();

    verify(subject).checkRole("PROTEOMIC");
    verify(subject).checkRole("MANAGER");
  }

  @Test
  public void checkProteomicManagerRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationServiceDefault.checkProteomicManagerRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole(roleCaptor.capture());
    String role = roleCaptor.getValue();
    assertEquals(true, role.equals("PROTEOMIC") || role.equals("MANAGER"));
  }

  @Test
  public void checkUserRole_User() {
    authorizationServiceDefault.checkUserRole();

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserRole_NonUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));

    try {
      authorizationServiceDefault.checkUserRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkRobotRole_Robot() {
    authorizationServiceDefault.checkRobotRole();

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  public void checkRobotRole_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(Permission.class));

    try {
      authorizationServiceDefault.checkRobotRole();
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkPermission(new RobotPermission());
  }

  @Test
  public void checkLaboratoryReadPermission_Proteomic() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceDefault.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
  }

  @Test
  public void checkLaboratoryReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceDefault.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkLaboratoryReadPermission_Member() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceDefault.checkLaboratoryReadPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  public void checkLaboratoryReadPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceDefault.checkLaboratoryReadPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("laboratory:read:2");
  }

  @Test
  public void checkLaboratoryReadPermission_Null() {
    authorizationServiceDefault.checkLaboratoryReadPermission(null);
  }

  @Test
  public void hasLaboratoryManagerPermission_Proteomic() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    boolean manager = authorizationServiceDefault.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.PROTEOMIC.name());
    assertEquals(true, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_LaboratoryManager() {
    when(subject.hasRole(UserRole.USER.name())).thenReturn(true);
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationServiceDefault.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.PROTEOMIC.name());
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(true, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_False() {
    when(subject.hasRole(UserRole.USER.name())).thenReturn(true);
    Laboratory laboratory = new Laboratory(1L);

    final boolean manager = authorizationServiceDefault.hasLaboratoryManagerPermission(laboratory);

    verify(subject).hasRole(UserRole.USER.name());
    verify(subject).hasRole(UserRole.PROTEOMIC.name());
    verify(subject).isPermitted("laboratory:manager:1");
    assertEquals(false, manager);
  }

  @Test
  public void hasLaboratoryManagerPermission_Null() {
    boolean manager = authorizationServiceDefault.hasLaboratoryManagerPermission(null);

    assertEquals(false, manager);
  }

  @Test
  public void checkLaboratoryManagerPermission_Proteomic() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceDefault.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
  }

  @Test
  public void checkLaboratoryManagerPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceDefault.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkLaboratoryManagerPermission_LaboratoryManager() {
    Laboratory laboratory = new Laboratory(2L);

    authorizationServiceDefault.checkLaboratoryManagerPermission(laboratory);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  public void checkLaboratoryManagerPermission_Other() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    Laboratory laboratory = new Laboratory(2L);

    try {
      authorizationServiceDefault.checkLaboratoryManagerPermission(laboratory);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("laboratory:manager:2");
  }

  @Test
  public void checkLaboratoryManagerPermission_Null() {
    authorizationServiceDefault.checkLaboratoryManagerPermission(null);
  }

  @Test
  public void checkUserReadPermission_Proteomic() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    authorizationServiceDefault.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
  }

  @Test
  public void checkUserReadPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(6L);
    user.setLaboratory(new Laboratory(1L));

    try {
      authorizationServiceDefault.checkUserReadPermission(user);
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

    authorizationServiceDefault.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).isPermitted("laboratory:manager:1");
    verify(subject).checkPermission("user:read:6");
  }

  @Test
  public void checkUserReadPermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceDefault.checkUserReadPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkUserReadPermission_CannotRead() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceDefault.checkUserReadPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:read:8");
  }

  @Test
  public void checkUserReadPermission_Null() {
    authorizationServiceDefault.checkUserReadPermission(null);
  }

  @Test
  public void checkUserWritePermission_Proteomic() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceDefault.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
  }

  @Test
  public void checkUserWritePermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceDefault.checkUserWritePermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
  }

  @Test
  public void checkUserWritePermission_LaboratoryManager() {
    when(subject.isPermitted(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceDefault.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).isPermitted("laboratory:manager:2");
  }

  @Test
  public void checkUserWritePermission_CanWrite() {
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceDefault.checkUserWritePermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:write:8");
  }

  @Test
  public void checkUserWritePermission_CannotWrite() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceDefault.checkUserWritePermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).isPermitted("laboratory:manager:2");
    verify(subject).checkPermission("user:write:8");
  }

  @Test
  public void checkUserWritePermission_Null() {
    authorizationServiceDefault.checkUserWritePermission(null);
  }

  @Test
  public void checkUserWritePasswordPermission_Proteomic() {
    when(subject.hasRole(any(String.class))).thenReturn(true);
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceDefault.checkUserWritePasswordPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
  }

  @Test
  public void checkUserWritePasswordPermission_NotUser() {
    doThrow(new AuthorizationException()).when(subject).checkRole(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceDefault.checkUserWritePasswordPermission(user);
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
      authorizationServiceDefault.checkUserWritePasswordPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("user:write_password:8");
  }

  @Test
  public void checkUserWritePasswordPermission_CanWrite() {
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    authorizationServiceDefault.checkUserWritePasswordPermission(user);

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("user:write_password:8");
  }

  @Test
  public void checkUserWritePasswordPermission_CannotWrite() {
    doThrow(new AuthorizationException()).when(subject).checkPermission(any(String.class));
    User user = new User(8L);
    user.setLaboratory(new Laboratory(2L));

    try {
      authorizationServiceDefault.checkUserWritePasswordPermission(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }

    verify(subject).checkRole("USER");
    verify(subject).hasRole("PROTEOMIC");
    verify(subject).checkPermission("user:write_password:8");
  }

  @Test
  public void checkUserWritePasswordPermission_Null() {
    authorizationServiceDefault.checkUserWritePasswordPermission(null);
  }
}
