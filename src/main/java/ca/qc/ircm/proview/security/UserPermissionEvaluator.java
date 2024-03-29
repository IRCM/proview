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

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link User}.
 */
@Component
public class UserPermissionEvaluator extends AbstractPermissionEvaluator {
  private static final long ROBOT_ID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(UserPermissionEvaluator.class);
  private UserRepository repository;
  private RoleValidator roleValidator;

  @Autowired
  UserPermissionEvaluator(UserRepository repository, RoleValidator roleValidator) {
    super(repository);
    this.repository = repository;
    this.roleValidator = roleValidator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof User)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User user = (User) targetDomainObject;
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(user, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if ((authentication == null) || !(targetId instanceof Long)
        || !targetType.equals(User.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User user = repository.findById((Long) targetId).orElse(null);
    if (user == null) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(user, currentUser, realPermission);
  }

  private boolean hasPermission(User user, User currentUser, Permission permission) {
    logger.debug("hasPermission: {}, {}, {}", user, currentUser, permission);
    if (currentUser == null) {
      return false;
    }
    if (user.getId() != null && user.getId() == ROBOT_ID && permission.equals(Permission.WRITE)
        && !user.isActive()) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (user.getId() == null) {
      if (roleValidator.hasRole(MANAGER) && user.getLaboratory() != null
          && user.getLaboratory().getId() != null && currentUser.getLaboratory() != null
          && user.getLaboratory().getId().equals(currentUser.getLaboratory().getId())) {
        return true;
      }
      return false;
    }
    if (user.getId().equals(currentUser.getId())) {
      return true;
    }
    boolean authorized = false;
    authorized |= permission.equals(Permission.READ)
        && roleValidator.hasRole(UserAuthority.laboratoryMember(user.getLaboratory()));
    authorized |= permission.equals(Permission.WRITE)
        && roleValidator.hasAllRoles(MANAGER, UserAuthority.laboratoryMember(user.getLaboratory()));
    return authorized;
  }
}
