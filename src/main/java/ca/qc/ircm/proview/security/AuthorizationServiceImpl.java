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

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of authorization services.
 */
@Service
@Transactional
public class AuthorizationServiceImpl implements AuthorizationService {
  private static final String ADMIN = UserRole.ADMIN.name();
  private static final String MANAGER = UserRole.MANAGER.name();
  private static final String USER = UserRole.USER.name();

  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthenticationService authenticationService;

  protected AuthorizationServiceImpl() {
  }

  protected AuthorizationServiceImpl(EntityManager entityManager,
      AuthenticationService authenticationService) {
    this.entityManager = entityManager;
    this.authenticationService = authenticationService;
  }

  private Subject getSubject() {
    return SecurityUtils.getSubject();
  }

  private User getUser(Long id) {
    if (id == null) {
      return null;
    }

    return entityManager.find(User.class, id);
  }

  private String getRealmName() {
    return ShiroRealm.REALM_NAME;
  }

  @Override
  public User getCurrentUser() {
    return getUser((Long) getSubject().getPrincipal());
  }

  @Override
  public boolean isUser() {
    return getSubject().isAuthenticated() || getSubject().isRemembered();
  }

  @Override
  public boolean isRunAs() {
    return getSubject().isRunAs();
  }

  @Override
  public boolean hasAdminRole() {
    return getSubject().hasRole(ADMIN);
  }

  @Override
  public boolean hasManagerRole() {
    return getSubject().hasRole(MANAGER);
  }

  @Override
  public boolean hasManagerRole(User user) {
    if (user == null) {
      return false;
    }

    AuthorizationInfo authorizationInfo = authenticationService
        .getAuthorizationInfo(new SimplePrincipalCollection(user.getId(), getRealmName()));
    return authorizationInfo.getRoles() != null && authorizationInfo.getRoles().contains(MANAGER);
  }

  @Override
  public boolean hasUserRole() {
    return getSubject().hasRole(USER);
  }

  @Override
  public void checkAdminRole() {
    getSubject().checkRole(ADMIN);
  }

  @Override
  public void checkUserRole() {
    getSubject().checkRole(USER);
  }

  @Override
  public void checkRobotRole() {
    getSubject().checkPermission(new RobotPermission());
  }

  @Override
  public void checkLaboratoryReadPermission(Laboratory laboratory) {
    if (laboratory != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("laboratory:read:" + laboratory.getId());
      }
    }
  }

  @Override
  public boolean hasLaboratoryManagerPermission(Laboratory laboratory) {
    if (laboratory != null && getSubject().hasRole(USER)) {
      return getSubject().hasRole(ADMIN)
          || getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
    }
    return false;
  }

  @Override
  public void checkLaboratoryManagerPermission(Laboratory laboratory) {
    if (laboratory != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("laboratory:manager:" + laboratory.getId());
      }
    }
  }

  @Override
  public void checkUserReadPermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        if (!getSubject().isPermitted("laboratory:manager:" + user.getLaboratory().getId())) {
          getSubject().checkPermission("user:read:" + user.getId());
        }
      }
    }
  }

  @Override
  public boolean hasUserWritePermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      if (getSubject().hasRole(USER)) {
        boolean permitted = getSubject().hasRole(ADMIN);
        permitted |= getSubject().isPermitted("laboratory:manager:" + user.getLaboratory().getId());
        permitted |= getSubject().isPermitted("user:write:" + user.getId());
        return permitted;
      }
    }
    return false;
  }

  @Override
  public void checkUserWritePermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      getSubject().checkRole(USER);
      if (!hasUserWritePermission(user)) {
        getSubject().checkPermission("user:write:" + user.getId());
      }
    }
  }

  @Override
  public void checkUserWritePasswordPermission(User user) {
    if (user != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("user:write_password:" + user.getId());
      }
    }
  }
}
