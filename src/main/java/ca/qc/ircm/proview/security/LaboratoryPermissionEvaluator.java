package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Laboratory}.
 */
@Component
public class LaboratoryPermissionEvaluator extends AbstractPermissionEvaluator {

  private LaboratoryRepository repository;
  private RoleValidator roleValidator;

  @Autowired
  LaboratoryPermissionEvaluator(LaboratoryRepository repository, UserRepository userRepository,
      RoleValidator roleValidator) {
    super(userRepository);
    this.repository = repository;
    this.roleValidator = roleValidator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if (!(targetDomainObject instanceof Laboratory laboratory)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(laboratory, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (!(targetId instanceof Long) || !targetType.equals(Laboratory.class.getName())
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

  private boolean hasPermission(Laboratory laboratory, @Nullable User currentUser,
      Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (laboratory.getId() == 0) {
      return false;
    }
    boolean authorized = false;
    authorized |= permission.equals(Permission.READ)
        && roleValidator.hasRole(UserAuthority.laboratoryMember(laboratory));
    authorized |= permission.equals(Permission.WRITE)
        && roleValidator.hasAllRoles(MANAGER, UserAuthority.laboratoryMember(laboratory));
    return authorized;
  }
}
