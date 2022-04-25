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

import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Laboratory}.
 */
public class LaboratoryPermissionEvaluator extends AbstractPermissionEvaluator {
  private LaboratoryRepository repository;
  private RoleValidator roleValidator;

  LaboratoryPermissionEvaluator(LaboratoryRepository repository, UserRepository userRepository,
      RoleValidator roleValidator) {
    super(userRepository);
    this.repository = repository;
    this.roleValidator = roleValidator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof Laboratory)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Laboratory laboratory = (Laboratory) targetDomainObject;
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(laboratory, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if ((authentication == null) || !(targetId instanceof Long)
        || !targetType.equals(Laboratory.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Laboratory laboratory = repository.findById((Long) targetId).orElse(null);
    if (laboratory == null) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(laboratory, currentUser, realPermission);
  }

  private boolean hasPermission(Laboratory laboratory, User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (laboratory.getId() == null) {
      return false;
    }
    boolean authorized = false;
    authorized |= permission.equals(BasePermission.READ)
        && roleValidator.hasRole(UserAuthority.laboratoryMember(laboratory));
    authorized |= permission.equals(BasePermission.WRITE)
        && roleValidator.hasAllRoles(MANAGER, UserAuthority.laboratoryMember(laboratory));
    return authorized;
  }
}
