package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link PermissionEvaluator}.
 */
@Component
@Primary
public class PermissionEvaluatorDelegator implements PermissionEvaluator {

  private final LaboratoryPermissionEvaluator laboratoryPermissionEvaluator;
  private final UserPermissionEvaluator userPermissionEvaluator;
  private final SubmissionPermissionEvaluator submissionPermissionEvaluator;
  private final SamplePermissionEvaluator samplePermissionEvaluator;
  private final PlatePermissionEvaluator platePermissionEvaluator;

  /**
   * Create an instance of PermissionEvaluatorDelegator.
   *
   * @param laboratoryPermissionEvaluator laboratory permission delegator
   * @param userPermissionEvaluator       user permission delegator
   * @param submissionPermissionEvaluator submission permission delegator
   * @param samplePermissionEvaluator     sample permission delegator
   * @param platePermissionEvaluator      plate permission delegator
   */
  @Autowired
  @UsedBy(SPRING)
  public PermissionEvaluatorDelegator(LaboratoryPermissionEvaluator laboratoryPermissionEvaluator,
      UserPermissionEvaluator userPermissionEvaluator,
      SubmissionPermissionEvaluator submissionPermissionEvaluator,
      SamplePermissionEvaluator samplePermissionEvaluator,
      PlatePermissionEvaluator platePermissionEvaluator) {
    this.laboratoryPermissionEvaluator = laboratoryPermissionEvaluator;
    this.userPermissionEvaluator = userPermissionEvaluator;
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
    this.samplePermissionEvaluator = samplePermissionEvaluator;
    this.platePermissionEvaluator = platePermissionEvaluator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if (targetDomainObject instanceof Laboratory) {
      return laboratoryPermissionEvaluator.hasPermission(authentication, targetDomainObject,
          permission);
    } else if (targetDomainObject instanceof User) {
      return userPermissionEvaluator.hasPermission(authentication, targetDomainObject, permission);
    } else if (targetDomainObject instanceof Submission) {
      return submissionPermissionEvaluator.hasPermission(authentication, targetDomainObject,
          permission);
    } else if (targetDomainObject instanceof Sample) {
      return samplePermissionEvaluator.hasPermission(authentication, targetDomainObject,
          permission);
    } else if (targetDomainObject instanceof Plate) {
      return platePermissionEvaluator.hasPermission(authentication, targetDomainObject, permission);
    }
    return false;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (targetType.equals(Laboratory.class.getName())) {
      return laboratoryPermissionEvaluator.hasPermission(authentication, targetId, targetType,
          permission);
    } else if (targetType.equals(User.class.getName())) {
      return userPermissionEvaluator.hasPermission(authentication, targetId, targetType,
          permission);
    } else if (targetType.equals(Submission.class.getName())) {
      return submissionPermissionEvaluator.hasPermission(authentication, targetId, targetType,
          permission);
    } else if (targetType.equals(Sample.class.getName())) {
      return samplePermissionEvaluator.hasPermission(authentication, targetId, targetType,
          permission);
    } else if (targetType.equals(Plate.class.getName())) {
      return platePermissionEvaluator.hasPermission(authentication, targetId, targetType,
          permission);
    }
    return false;
  }
}
