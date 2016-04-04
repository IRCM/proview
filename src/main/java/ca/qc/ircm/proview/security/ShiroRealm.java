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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom authenticating realm for JSecurity.
 */
public class ShiroRealm extends org.apache.shiro.realm.AuthorizingRealm {
  public static final String CACHE_NAME = "Shiro";

  @SuppressWarnings("unused")
  private Logger logger = LoggerFactory.getLogger(ShiroRealm.class);
  private final AuthenticationService authenticationService;

  /**
   * Creates Shiro's realm.
   *
   * @param authenticationService
   *          authentication service
   * @param permissionResolver
   *          permission resolver
   * @param realmName
   *          realm name
   */
  public ShiroRealm(AuthenticationService authenticationService,
      PermissionResolver permissionResolver, String realmName) {
    super(new AllowAllCredentialsMatcher());
    this.authenticationService = authenticationService;
    this.setPermissionResolver(permissionResolver);
    this.setAuthorizationCachingEnabled(true);
    this.setAuthorizationCacheName(CACHE_NAME);
    this.setName(realmName);
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    UsernamePasswordToken upToken = (UsernamePasswordToken) token;
    return authenticationService.getAuthenticationInfo(upToken);
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    return authenticationService.getAuthorizationInfo(principals);
  }
}