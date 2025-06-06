package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserAuthority;
import ca.qc.ircm.proview.user.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Submission}.
 */
@Component
public class SubmissionPermissionEvaluator extends AbstractPermissionEvaluator {

  private final SubmissionRepository repository;
  private final RoleValidator roleValidator;

  @Autowired
  SubmissionPermissionEvaluator(SubmissionRepository repository, UserRepository userRepository,
      RoleValidator roleValidator) {
    super(userRepository);
    this.repository = repository;
    this.roleValidator = roleValidator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if (!(targetDomainObject instanceof Submission submission)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(submission, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (!(targetId instanceof Long) || !targetType.equals(Submission.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Submission submission = repository.findById((Long) targetId).orElse(null);
    if (submission == null) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(submission, currentUser, realPermission);
  }

  boolean hasPermission(Submission submission, @Nullable User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (submission.getId() == 0) {
      return roleValidator.hasRole(USER);
    }
    User owner = submission.getUser();
    boolean authorized = false;
    boolean ownerOrManager = currentUser.getId() == owner.getId() || roleValidator
        .hasAllRoles(MANAGER, UserAuthority.laboratoryMember(owner.getLaboratory()));
    authorized |= permission.equals(Permission.READ) && ownerOrManager;
    authorized |= permission.equals(Permission.WRITE) && !submissionAfterWaiting(submission)
        && ownerOrManager;
    return authorized;
  }

  private boolean submissionAfterWaiting(Submission submissionParam) {
    BooleanExpression predicate = submission.id.eq(submissionParam.getId())
        .and(submission.samples.any().status.gt(SampleStatus.WAITING));
    return repository.count(predicate) > 0;
  }
}
