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
  private AuthorizationService authorizationService;

  LaboratoryPermissionEvaluator(LaboratoryRepository repository, UserRepository userRepository,
      AuthorizationService authorizationService) {
    super(userRepository);
    this.repository = repository;
    this.authorizationService = authorizationService;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof Laboratory)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Laboratory laboratory = (Laboratory) targetDomainObject;
    User currentUser = getUser(authentication);
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
    Laboratory laboratory = repository.findOne((Long) targetId);
    if (laboratory == null) {
      return false;
    }
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(laboratory, currentUser, realPermission);
  }

  private boolean hasPermission(Laboratory laboratory, User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (authorizationService.hasRole(ADMIN)) {
      return true;
    }
    if (laboratory.getId() == null) {
      return false;
    }
    boolean authorized = false;
    authorized |= permission.equals(BasePermission.READ)
        && authorizationService.hasRole(UserAuthority.laboratoryMember(laboratory));
    authorized |= permission.equals(BasePermission.WRITE)
        && authorizationService.hasAllRoles(MANAGER, UserAuthority.laboratoryMember(laboratory));
    return authorized;
  }
}
