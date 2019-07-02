package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link User}.
 */
public class UserPermissionEvaluator extends AbstractPermissionEvaluator {
  private static final Logger logger = LoggerFactory.getLogger(UserPermissionEvaluator.class);
  private UserRepository repository;
  private AuthorizationService authorizationService;

  UserPermissionEvaluator(UserRepository repository, AuthorizationService authorizationService) {
    super(repository);
    this.repository = repository;
    this.authorizationService = authorizationService;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof User)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User user = (User) targetDomainObject;
    User currentUser = getUser(authentication);
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
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(user, currentUser, realPermission);
  }

  private boolean hasPermission(User user, User currentUser, Permission permission) {
    logger.debug("hasPermission: {}, {}, {}", user, currentUser, permission);
    if (currentUser == null) {
      return false;
    }
    if (authorizationService.hasRole(ADMIN)) {
      return true;
    }
    if (user.getId() == null) {
      return false;
    }
    if (user.getId().equals(currentUser.getId())) {
      return true;
    }
    boolean authorized = false;
    authorized |= permission.equals(BasePermission.READ)
        && authorizationService.hasRole(UserAuthority.laboratoryMember(user.getLaboratory()));
    authorized |= permission.equals(BasePermission.WRITE) && authorizationService
        .hasAllRoles(MANAGER, UserAuthority.laboratoryMember(user.getLaboratory()));
    return authorized;
  }
}
