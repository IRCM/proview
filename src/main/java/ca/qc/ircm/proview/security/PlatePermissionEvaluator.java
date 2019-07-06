package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Plate}.
 */
public class PlatePermissionEvaluator extends AbstractPermissionEvaluator {
  private PlateRepository repository;
  private AuthorizationService authorizationService;
  private SubmissionService submissionService;
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;

  PlatePermissionEvaluator(PlateRepository repository, UserRepository userRepository,
      AuthorizationService authorizationService, SubmissionService submissionService,
      SubmissionPermissionEvaluator submissionPermissionEvaluator) {
    super(userRepository);
    this.repository = repository;
    this.authorizationService = authorizationService;
    this.submissionService = submissionService;
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof Plate)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Plate plate = (Plate) targetDomainObject;
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(plate, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if ((authentication == null) || !(targetId instanceof Long)
        || !targetType.equals(Plate.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Plate plate = repository.findById((Long) targetId).orElse(null);
    if (plate == null) {
      return false;
    }
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(plate, currentUser, realPermission);
  }

  private boolean hasPermission(Plate plate, User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (authorizationService.hasRole(ADMIN)) {
      return true;
    }
    if (plate.getSubmission() != null) {
      if (plate.getId() == null) {
        return authorizationService.hasRole(USER);
      }
      Submission submission = submissionService.get(plate);
      return submissionPermissionEvaluator.hasPermission(submission, currentUser, permission);
    }
    return false;
  }
}
