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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.Rules;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

public class ShiroRealmTest {
  private ShiroRealm shiroRealm;
  @Mock
  private AuthenticationService authenticatingService;
  @Mock
  private CacheManager cacheManager;
  @Mock
  private Cache<Object, Object> cache;
  @Mock
  private PermissionResolver permissionResolver;
  @Mock
  private AuthenticationInfo authenticationInfo;
  @Mock
  private AuthorizationInfo authorizationInfo;
  @Captor
  private ArgumentCaptor<AuthenticationInfo> authenticationInfoCaptor;
  @Rule
  public RuleChain rules = Rules.defaultRules(this);
  private Long authenticationId;
  private List<Permission> permissions;
  private String realmName = "proteus";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(cacheManager.getCache(any(String.class))).thenReturn(cache);
    shiroRealm = new ShiroRealm(authenticatingService, cacheManager, permissionResolver, realmName);
    authenticationId = 1L;
    when(authenticatingService.getAuthenticationInfo(any(UsernamePasswordToken.class)))
        .thenReturn(authenticationInfo);
    permissions = new ArrayList<Permission>() {
      private static final long serialVersionUID = 3384781677869656083L;

      {
        add(new WildcardPermission("project:read:1"));
        add(new WildcardPermission("project:write:1"));
        add(new WildcardPermission("project:read:2"));
      }
    };
  }

  @Test
  public void getName() throws Throwable {
    assertEquals(realmName, shiroRealm.getName());
  }

  @Test
  public void cacheName() throws Throwable {
    assertEquals(ShiroRealm.CACHE_NAME, shiroRealm.getAuthorizationCacheName());
  }

  @Test
  public void getAuthenticationInfo() throws Throwable {
    UsernamePasswordToken token = new UsernamePasswordToken("poitrac", "test_password");

    AuthenticationInfo authentication = shiroRealm.getAuthenticationInfo(token);

    verify(authenticatingService).getAuthenticationInfo(token);
    assertSame(this.authenticationInfo, authentication);
  }

  @Test
  public void getAuthenticationInfo_Unknown() throws Throwable {
    when(authenticatingService.getAuthenticationInfo(any(UsernamePasswordToken.class)))
        .thenThrow(new UnknownAccountException());
    UsernamePasswordToken token = new UsernamePasswordToken("poitrac", "test_password");

    try {
      shiroRealm.getAuthenticationInfo(token);
      fail("Expected UnknownAccountException");
    } catch (UnknownAccountException e) {
      // Ignore.
    }
  }

  @Test
  public void getAuthenticationInfo_Invalid() throws Throwable {
    when(authenticatingService.getAuthenticationInfo(any(UsernamePasswordToken.class)))
        .thenThrow(new InvalidAccountException());
    UsernamePasswordToken token = new UsernamePasswordToken("poitrac", "test_password");

    try {
      shiroRealm.getAuthenticationInfo(token);
      fail("Expected InvalidAccountException");
    } catch (InvalidAccountException e) {
      // Ignore.
    }
  }

  @Test
  public void getAuthenticationInfo_Inactive() throws Throwable {
    when(authenticatingService.getAuthenticationInfo(any(UsernamePasswordToken.class)))
        .thenThrow(new DisabledAccountException());
    UsernamePasswordToken token = new UsernamePasswordToken("poitrac", "test_password");

    try {
      shiroRealm.getAuthenticationInfo(token);
      fail("Expected DisabledAccountException");
    } catch (DisabledAccountException e) {
      // Ignore.
    }
  }

  @Test
  public void getAuthenticationInfo_InvalidPassword() throws Throwable {
    when(authenticatingService.getAuthenticationInfo(any(UsernamePasswordToken.class)))
        .thenThrow(new IncorrectCredentialsException());
    UsernamePasswordToken token = new UsernamePasswordToken("poitrac", "test_password");

    try {
      shiroRealm.getAuthenticationInfo(token);
      fail("Expected IncorrectCredentialsException");
    } catch (IncorrectCredentialsException e) {
      // Ignore.
    }
  }

  @Test
  public void getAuthenticationInfo_Cache() throws Throwable {
    AuthenticationInfo cachedAuthenticationInfo = mock(AuthenticationInfo.class);
    when(cache.get(any(Long.class))).thenReturn(cachedAuthenticationInfo);
    UsernamePasswordToken token = new UsernamePasswordToken("poitrac", "test_password");

    AuthenticationInfo authenticationInfo = shiroRealm.getAuthenticationInfo(token);

    // Cache is not enabled for authentication since credentials are not hashed in token.
    // See http://shiro.apache.org/static/1.2.2/apidocs/org/apache/shiro/realm/AuthenticatingRealm.html
    verify(cache, never()).get(any(Long.class));
    assertNotEquals(cachedAuthenticationInfo, authenticationInfo);
  }

  @Test
  public void getAuthenticationInfo_Null() throws Throwable {
    when(authenticatingService.getAuthenticationInfo(any(UsernamePasswordToken.class)))
        .thenThrow(new UnknownAccountException());

    try {
      shiroRealm.getAuthenticationInfo(null);
      fail("Expected UnknownAccountException");
    } catch (UnknownAccountException e) {
      // Ignore.
    }
  }

  @Test
  public void getAuthorizationInfo() {
    when(authenticatingService.getAuthorizationInfo(any(PrincipalCollection.class)))
        .thenReturn(authorizationInfo);
    PrincipalCollection principals = new SimplePrincipalCollection(authenticationId, realmName);

    AuthorizationInfo authorizationInfo = shiroRealm.doGetAuthorizationInfo(principals);

    verify(authenticatingService).getAuthorizationInfo(principals);
    assertEquals(this.authorizationInfo, authorizationInfo);
  }

  @Test
  public void getAuthorizationInfo_Cache() {
    AuthorizationInfo cachedAuthorizationInfo = mock(AuthorizationInfo.class);
    when(cache.get(any(PrincipalCollection.class))).thenReturn(cachedAuthorizationInfo);
    when(cachedAuthorizationInfo.getObjectPermissions()).thenReturn(permissions);
    PrincipalCollection principals = new SimplePrincipalCollection(authenticationId, realmName);

    shiroRealm.isPermitted(principals, "project:read:1");

    verify(cacheManager).getCache(ShiroRealm.CACHE_NAME);
    verify(cache).get(principals);
    verify(cachedAuthorizationInfo).getRoles();
    verify(cachedAuthorizationInfo).getObjectPermissions();
  }

  @Test
  public void getAuthorizationInfo_Null() throws Throwable {
    when(authenticatingService.getAuthorizationInfo(any(PrincipalCollection.class)))
        .thenReturn(authorizationInfo);

    AuthorizationInfo authorizationInfo = shiroRealm.doGetAuthorizationInfo(null);

    assertEquals(this.authorizationInfo, authorizationInfo);
  }
}
