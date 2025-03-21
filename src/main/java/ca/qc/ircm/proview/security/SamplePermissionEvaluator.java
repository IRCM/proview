package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSample;
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
 * {@link PermissionEvaluator} that can evaluate permission for {@link Sample}.
 */
@Component
public class SamplePermissionEvaluator extends AbstractPermissionEvaluator {

  private final SampleRepository repository;
  private final RoleValidator roleValidator;
  private final SubmissionPermissionEvaluator submissionPermissionEvaluator;

  @Autowired
  SamplePermissionEvaluator(SampleRepository repository, UserRepository userRepository,
      RoleValidator roleValidator, SubmissionPermissionEvaluator submissionPermissionEvaluator) {
    super(userRepository);
    this.repository = repository;
    this.roleValidator = roleValidator;
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if (!(targetDomainObject instanceof Sample sample)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(sample, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (!(targetId instanceof Long) || !targetType.equals(Sample.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Sample sample = repository.findById((Long) targetId).orElse(null);
    if (sample == null) {
      return false;
    }
    User currentUser = getUser(authentication).orElse(null);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(sample, currentUser, realPermission);
  }

  private boolean hasPermission(Sample sample, @Nullable User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (roleValidator.hasRole(ADMIN)) {
      return true;
    }
    if (sample instanceof SubmissionSample) {
      if (sample.getId() == 0) {
        return roleValidator.hasRole(USER);
      }
      Submission submission = ((SubmissionSample) sample).getSubmission();
      return submissionPermissionEvaluator.hasPermission(submission, currentUser, permission);
    }
    return false;
  }
}
