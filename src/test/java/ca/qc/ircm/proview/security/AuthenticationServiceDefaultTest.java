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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.test.config.DatabaseRule;
import ca.qc.ircm.proview.test.config.RollBack;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.test.config.SubjectRule;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Collections;

@RollBack
public class AuthenticationServiceDefaultTest {
  private AuthenticationServiceDefault authenticationServiceDefault;
  @ClassRule
  public static DatabaseRule databaseRule = new DatabaseRule();
  @Mock
  private ApplicationConfiguration applicationConfiguration;
  @Captor
  private ArgumentCaptor<PrincipalCollection> principalCollectionCaptor;
  @Captor
  private ArgumentCaptor<AuthenticationToken> tokenCaptor;
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(databaseRule);
  private String realmName = "unit_realm";
  private Subject subject;
  private PasswordVersion passwordVersion;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    authenticationServiceDefault =
        new AuthenticationServiceDefault(databaseRule.getEntityManager(), applicationConfiguration);
    when(applicationConfiguration.getRealmName()).thenReturn(realmName);
    passwordVersion = SubjectRule.getPasswordVersion();
    when(applicationConfiguration.getPasswordVersions())
        .thenReturn(Collections.nCopies(1, passwordVersion));
    when(applicationConfiguration.getPasswordVersion()).thenReturn(passwordVersion);
    subject = SecurityUtils.getSubject();
  }

  @Test
  public void sign() throws Throwable {
    authenticationServiceDefault.sign("christian.poitras@ircm.qc.ca", "password", false);

    verify(subject).login(tokenCaptor.capture());
    assertEquals(true, tokenCaptor.getValue() instanceof UsernamePasswordToken);
    UsernamePasswordToken token = (UsernamePasswordToken) tokenCaptor.getValue();
    assertEquals("christian.poitras@ircm.qc.ca", token.getUsername());
    assertArrayEquals("password".toCharArray(), token.getPassword());
    assertEquals(false, token.isRememberMe());
  }

  @Test
  public void sign_Remember() throws Throwable {
    authenticationServiceDefault.sign("christian.poitras@ircm.qc.ca", "password", true);

    verify(subject).login(tokenCaptor.capture());
    assertEquals(true, tokenCaptor.getValue() instanceof UsernamePasswordToken);
    UsernamePasswordToken token = (UsernamePasswordToken) tokenCaptor.getValue();
    assertEquals("christian.poitras@ircm.qc.ca", token.getUsername());
    assertArrayEquals("password".toCharArray(), token.getPassword());
    assertEquals(true, token.isRememberMe());
  }

  @Test
  public void sign_AuthenticationException() throws Throwable {
    doThrow(new AuthenticationException("test")).when(subject).login(tokenCaptor.capture());

    try {
      authenticationServiceDefault.sign("christian.poitras@ircm.qc.ca", "password", true);
      fail("Expected AuthenticationException");
    } catch (AuthenticationException e) {
      // Ignore.
    }

    verify(subject).login(tokenCaptor.capture());
    assertEquals(true, tokenCaptor.getValue() instanceof UsernamePasswordToken);
    UsernamePasswordToken token = (UsernamePasswordToken) tokenCaptor.getValue();
    assertEquals("christian.poitras@ircm.qc.ca", token.getUsername());
    assertArrayEquals("password".toCharArray(), token.getPassword());
    assertEquals(true, token.isRememberMe());
  }

  @Test
  public void sign_NullUsername() throws Throwable {
    try {
      authenticationServiceDefault.sign(null, "password", false);
      fail("Expected AuthenticationException");
    } catch (AuthenticationException e) {
      // Ignore.
    }
  }

  @Test
  public void sign_NullPassword() throws Throwable {
    try {
      authenticationServiceDefault.sign("christian.poitras@ircm.qc.ca", null, false);
      fail("Expected AuthenticationException");
    } catch (AuthenticationException e) {
      // Ignore.
    }
  }

  @Test
  public void signout() throws Throwable {
    authenticationServiceDefault.signout();

    verify(subject).logout();
  }

  @Test
  public void runAs() throws Throwable {
    User user = new User(3L);

    authenticationServiceDefault.runAs(user);

    verify(subject).checkRole(UserRole.PROTEOMIC.name());
    verify(subject).runAs(principalCollectionCaptor.capture());
    PrincipalCollection principalCollection = principalCollectionCaptor.getValue();
    assertEquals(user.getId(), principalCollection.getPrimaryPrincipal());
    assertEquals(1, principalCollection.fromRealm(realmName).size());
    assertEquals(user.getId(), principalCollection.fromRealm(realmName).iterator().next());
  }

  @Test
  public void runAs_Proteomic() throws Throwable {
    User user = new User(2L);
    user.setProteomic(true);

    try {
      authenticationServiceDefault.runAs(user);
      fail("Expected AuthorizationException");
    } catch (AuthorizationException e) {
      // Ignore.
    }
  }

  @Test
  public void runAs_Null() throws Throwable {
    try {
      authenticationServiceDefault.runAs(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      // Ignore.
    }
  }

  @Test
  public void stopRunAs() throws Throwable {
    when(subject.releaseRunAs()).thenReturn(new SimplePrincipalCollection(2L, realmName));

    Long userId = authenticationServiceDefault.stopRunAs();

    verify(subject).releaseRunAs();
    assertEquals((Long) 2L, userId);
  }

  @Test
  public void getAuthenticationInfo_2() throws Throwable {
    UsernamePasswordToken token =
        new UsernamePasswordToken("christian.poitras@ircm.qc.ca", "password");

    AuthenticationInfo authentication = authenticationServiceDefault.getAuthenticationInfo(token);

    assertEquals(2L, authentication.getPrincipals().getPrimaryPrincipal());
    assertEquals(1, authentication.getPrincipals().fromRealm(realmName).size());
    assertEquals(2L, authentication.getPrincipals().fromRealm(realmName).iterator().next());
    assertEquals("b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7",
        authentication.getCredentials());
    assertTrue(authentication instanceof SaltedAuthenticationInfo);
    SaltedAuthenticationInfo saltedAuthentication = (SaltedAuthenticationInfo) authentication;
    assertEquals(
        "d04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d"
            + "58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d",
        saltedAuthentication.getCredentialsSalt().toHex());
  }

  @Test
  public void getAuthenticationInfo_3() throws Throwable {
    UsernamePasswordToken token =
        new UsernamePasswordToken("benoit.coulombe@ircm.qc.ca", "password");

    AuthenticationInfo authentication = authenticationServiceDefault.getAuthenticationInfo(token);

    assertEquals(3L, authentication.getPrincipals().getPrimaryPrincipal());
    assertEquals(1, authentication.getPrincipals().fromRealm(realmName).size());
    assertEquals(3L, authentication.getPrincipals().fromRealm(realmName).iterator().next());
    assertEquals("da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d",
        authentication.getCredentials());
    assertTrue(authentication instanceof SaltedAuthenticationInfo);
    SaltedAuthenticationInfo saltedAuthentication = (SaltedAuthenticationInfo) authentication;
    assertEquals(
        "4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3f"
            + "c86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535",
        saltedAuthentication.getCredentialsSalt().toHex());
  }

  @Test(expected = InvalidAccountException.class)
  public void getAuthenticationInfo_Invalid() throws Throwable {
    UsernamePasswordToken token =
        new UsernamePasswordToken("robert.stlouis@ircm.qc.ca", "password");

    authenticationServiceDefault.getAuthenticationInfo(token);
  }

  @Test(expected = DisabledAccountException.class)
  public void getAuthenticationInfo_Inactive() throws Throwable {
    UsernamePasswordToken token =
        new UsernamePasswordToken("michel.tremblay@ircm.qc.ca", "password");

    authenticationServiceDefault.getAuthenticationInfo(token);
  }

  @Test(expected = UnknownAccountException.class)
  public void getAuthenticationInfo_NotExists() throws Throwable {
    UsernamePasswordToken token = new UsernamePasswordToken("non.user@ircm.qc.ca", "password");

    authenticationServiceDefault.getAuthenticationInfo(token);
  }

  @Test(expected = UnknownAccountException.class)
  public void getAuthenticationInfo_NullUsername() throws Throwable {
    UsernamePasswordToken token = new UsernamePasswordToken(null, "password");

    authenticationServiceDefault.getAuthenticationInfo(token);
  }

  @Test(expected = UnknownAccountException.class)
  public void getAuthenticationInfo_NullPassword() throws Throwable {
    UsernamePasswordToken token =
        new UsernamePasswordToken("christian.poitras@ircm.qc.ca", (String) null);

    authenticationServiceDefault.getAuthenticationInfo(token);
  }

  @Test(expected = IncorrectCredentialsException.class)
  public void getAuthenticationInfo_InvalidPassword() throws Throwable {
    UsernamePasswordToken token =
        new UsernamePasswordToken("christian.poitras@ircm.qc.ca", "password2");

    authenticationServiceDefault.getAuthenticationInfo(token);
  }

  @Test
  public void isRobot() throws Throwable {
    assertEquals(true, authenticationServiceDefault.isRobot(1L));
    assertEquals(false, authenticationServiceDefault.isRobot(2L));
  }

  @Test
  public void isRobot_Null() throws Throwable {
    assertEquals(false, authenticationServiceDefault.isRobot(null));
  }

  @Test
  public void getAuthorizationInfo_3() {
    AuthorizationInfo authorization = authenticationServiceDefault
        .getAuthorizationInfo(new SimplePrincipalCollection(3L, realmName));

    assertEquals(true, authorization.getRoles().contains("USER"));
    assertEquals(true, authorization.getRoles().contains("MANAGER"));
    assertEquals(false, authorization.getRoles().contains("PROTEOMIC"));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:3")));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:write:3")));
    assertEquals(true, implies(authorization.getObjectPermissions(),
        new WildcardPermission("user:write_password:3")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:7")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:1")));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:2")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:3")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:1")));
    assertEquals(true, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:2")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:3")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("abc:read:1")));
  }

  @Test
  public void getAuthorizationInfo_2() {
    AuthorizationInfo authorization = authenticationServiceDefault
        .getAuthorizationInfo(new SimplePrincipalCollection(2L, realmName));

    assertEquals(true, authorization.getRoles().contains("USER"));
    assertEquals(true, authorization.getRoles().contains("MANAGER"));
    assertEquals(true, authorization.getRoles().contains("PROTEOMIC"));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:2")));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:write:2")));
    assertEquals(true, implies(authorization.getObjectPermissions(),
        new WildcardPermission("user:write_password:2")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:7")));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:1")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:2")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:3")));
    assertEquals(true, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:1")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:2")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:3")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("abc:read:1")));
  }

  @Test
  public void getAuthorizationInfo_6() {
    AuthorizationInfo authorization = authenticationServiceDefault
        .getAuthorizationInfo(new SimplePrincipalCollection(6L, realmName));

    assertEquals(true, authorization.getRoles().contains("USER"));
    assertEquals(false, authorization.getRoles().contains("MANAGER"));
    assertEquals(true, authorization.getRoles().contains("PROTEOMIC"));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:6")));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:write:6")));
    assertEquals(true, implies(authorization.getObjectPermissions(),
        new WildcardPermission("user:write_password:6")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:7")));
    assertEquals(true,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:1")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:2")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:3")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:1")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:2")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:3")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("abc:read:1")));
  }

  @Test
  public void getAuthorizationInfo_Invalid() {
    AuthorizationInfo authorization = authenticationServiceDefault
        .getAuthorizationInfo(new SimplePrincipalCollection(4L, realmName));

    assertEquals(false, authorization.getRoles().contains("USER"));
    assertEquals(false, authorization.getRoles().contains("MANAGER"));
    assertEquals(false, authorization.getRoles().contains("PROTEOMIC"));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:4")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:write:4")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("user:write_password:4")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:7")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:1")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:2")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:3")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:1")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:2")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:3")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("abc:read:1")));
  }

  @Test
  public void getAuthorizationInfo_Inactive() {
    AuthorizationInfo authorization = authenticationServiceDefault
        .getAuthorizationInfo(new SimplePrincipalCollection(5L, realmName));

    assertEquals(false, authorization.getRoles().contains("USER"));
    assertEquals(false, authorization.getRoles().contains("MANAGER"));
    assertEquals(false, authorization.getRoles().contains("PROTEOMIC"));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:5")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:write:5")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("user:write_password:5")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("user:read:7")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:1")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:2")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("laboratory:read:3")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:1")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:2")));
    assertEquals(false, implies(authorization.getObjectPermissions(),
        new WildcardPermission("laboratory:manager:3")));
    assertEquals(false,
        implies(authorization.getObjectPermissions(), new WildcardPermission("abc:read:1")));
  }

  @Test
  public void getAuthorizationInfo_1() {
    AuthorizationInfo authorization = authenticationServiceDefault
        .getAuthorizationInfo(new SimplePrincipalCollection(1L, realmName));

    assertEquals(true, authorization.getRoles().contains("USER"));
    assertEquals(true, authorization.getRoles().contains("MANAGER"));
    assertEquals(true, authorization.getRoles().contains("PROTEOMIC"));
    assertEquals(true, implies(authorization.getObjectPermissions(), new RobotPermission()));
    assertEquals(true, implies(authorization.getObjectPermissions(), new WildcardPermission("*")));
  }

  @Test
  public void getAuthorizationInfo_Null() {
    AuthorizationInfo authorization = authenticationServiceDefault.getAuthorizationInfo(null);
    assertNull(authorization.getRoles());
    assertNull(authorization.getStringPermissions());
    assertNull(authorization.getObjectPermissions());
  }

  private boolean implies(Iterable<Permission> permissions, Permission permission) {
    for (Permission test : permissions) {
      if (test.implies(permission)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void hashPassword() throws Throwable {
    HashedPassword hashedPassword = authenticationServiceDefault.hashPassword("password");
    assertNotNull(hashedPassword.getPassword());
    assertNotNull(hashedPassword.getSalt());
    SimpleHash hash = new SimpleHash(passwordVersion.getAlgorithm(), "password",
        Hex.decode(hashedPassword.getSalt().toCharArray()), passwordVersion.getIterations());
    assertEquals(hash.toHex(), hashedPassword.getPassword());

    hashedPassword = authenticationServiceDefault.hashPassword("unit_test");
    assertNotNull(hashedPassword.getPassword());
    assertNotNull(hashedPassword.getSalt());
    hash = new SimpleHash(passwordVersion.getAlgorithm(), "unit_test",
        Hex.decode(hashedPassword.getSalt().toCharArray()), passwordVersion.getIterations());
    assertEquals(hash.toHex(), hashedPassword.getPassword());
  }

  @Test
  public void hashPassword_Null() throws Throwable {
    assertNull(authenticationServiceDefault.hashPassword(null));
  }
}
