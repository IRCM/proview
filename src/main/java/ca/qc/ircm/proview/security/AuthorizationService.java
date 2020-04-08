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

import static ca.qc.ircm.proview.user.UserAuthority.FORCE_CHANGE_PASSWORD;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.util.Collection;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for authorization.
 */
@Service
@Transactional
public class AuthorizationService {
  private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
  @Autowired
  private UserRepository repository;
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private PermissionEvaluator permissionEvaluator;

  protected AuthorizationService() {
  }

  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private UserDetails getUserDetails() {
    Authentication authentication = getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      return (UserDetails) authentication.getPrincipal();
    } else {
      return null;
    }
  }

  /**
   * Returns current user.
   *
   * @return current user
   */
  public User getCurrentUser() {
    UserDetails user = getUserDetails();
    if (user instanceof AuthenticatedUser) {
      Long userId = ((AuthenticatedUser) user).getId();
      if (userId == null) {
        return null;
      }

      return repository.findById(userId).orElse(null);
    } else {
      return null;
    }
  }

  /**
   * Returns true if current user is anonymous, false otherwise.
   *
   * @return true if current user is anonymous, false otherwise
   */
  public boolean isAnonymous() {
    return getUserDetails() == null;
  }

  /**
   * Returns true if current user has specified role, false otherwise.
   *
   * @param role
   *          role
   * @return true if current user has specified role, false otherwise
   */
  public boolean hasRole(String role) {
    Authentication authentication = getAuthentication();
    if (authentication == null) {
      return false;
    }

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    boolean hasRole = false;
    for (GrantedAuthority authority : authorities) {
      hasRole |= authority.getAuthority().equals(role);
    }
    logger.trace("user {} hasRole {}? {}", authentication.getName(), role, hasRole);
    return hasRole;
  }

  /**
   * Returns true if current user has any of the specified roles, false otherwise.
   *
   * @param roles
   *          roles
   * @return true if current user has any of the specified roles, false otherwise
   */
  public boolean hasAnyRole(String... roles) {
    boolean hasAnyRole = false;
    for (String role : roles) {
      hasAnyRole |= hasRole(role);
    }
    return hasAnyRole;
  }

  /**
   * Returns true if current user has all of the specified roles, false otherwise.
   *
   * @param roles
   *          roles
   * @return true if current user has all of the specified roles, false otherwise
   */
  public boolean hasAllRoles(String... roles) {
    boolean hasAllRole = true;
    for (String role : roles) {
      hasAllRole &= hasRole(role);
    }
    return hasAllRole;
  }

  /**
   * Reload current user's authorities.
   */
  public void reloadAuthorities() {
    if (hasRole(FORCE_CHANGE_PASSWORD)) {
      Authentication oldAuthentication = getAuthentication();
      logger.debug("reload authorities from user {}", oldAuthentication.getName());
      UserDetails userDetails = userDetailsService.loadUserByUsername(oldAuthentication.getName());
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, oldAuthentication.getCredentials(), userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }

  /**
   * Returns true if current user is authorized to access class, false otherwise.
   *
   * @param type
   *          class
   * @return true if current user is authorized to access class, false otherwise
   */
  public boolean isAuthorized(Class<?> type) {
    RolesAllowed rolesAllowed = AnnotationUtils.findAnnotation(type, RolesAllowed.class);
    if (rolesAllowed != null) {
      String[] roles = rolesAllowed.value();
      return hasAnyRole(roles);
    } else {
      return true;
    }
  }

  /**
   * Returns true if current user has permission on object, false otherwise.
   *
   * @param object
   *          object
   * @param permission
   *          permission
   * @return true if current user has permission on object, false otherwise
   */
  public boolean hasPermission(Object object, Permission permission) {
    return permissionEvaluator.hasPermission(getAuthentication(), object, permission);
  }

  void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
    this.permissionEvaluator = permissionEvaluator;
  }
}
