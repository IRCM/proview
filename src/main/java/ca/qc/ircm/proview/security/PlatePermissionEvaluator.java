package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Plate}.
 */
@Component
public class PlatePermissionEvaluator extends AbstractPermissionEvaluator {
  private PlateRepository repository;
  private RoleValidator roleValidator;
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;

  @Autowired
  PlatePermissionEvaluator(PlateRepository repository, UserRepository userRepository,
      RoleValidator roleValidator, SubmissionPermissionEvaluator submissionPermissionEvaluator) {
    super(userRepository);
    this.repository = repository;
    this.roleValidator = roleValidator;
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if (!(targetDomainObject instanceof Plate plate)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(plate, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (!(targetId instanceof Long) || !targetType.equals(Plate.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Plate plate = repository.findById((Long) targetId).orElse(null);
    if (plate == null) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(plate, currentUser, realPermission);
  }

  private boolean hasPermission(Plate plate, @Nullable User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (plate.getSubmission() != null) {
      if (plate.getId() == 0) {
        return roleValidator.hasRole(USER);
      }
      Submission submission = plate.getSubmission();
      return submissionPermissionEvaluator.hasPermission(submission, currentUser, permission);
    }
    return false;
  }
}
