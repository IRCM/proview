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
import org.springframework.lang.Nullable;
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
    if (!(targetDomainObject instanceof User user)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(user, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (!(targetId instanceof Long) || !targetType.equals(User.class.getName())
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

  private boolean hasPermission(User user, @Nullable User currentUser, Permission permission) {
    logger.debug("hasPermission: {}, {}, {}", user, currentUser, permission);
    if (currentUser == null) {
      return false;
    }
    if (user.getId() != 0 && user.getId() == ROBOT_ID && permission.equals(Permission.WRITE)
        && !user.isActive()) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (user.getId() == 0) {
      return roleValidator.hasRole(MANAGER) && user.getLaboratory().getId() != 0
          && user.getLaboratory().getId() == currentUser.getLaboratory().getId();
    }
    if (user.getId() == currentUser.getId()) {
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
