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

  private LaboratoryPermissionEvaluator laboratoryPermissionEvaluator;
  private UserPermissionEvaluator userPermissionEvaluator;
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;
  private SamplePermissionEvaluator samplePermissionEvaluator;
  private PlatePermissionEvaluator platePermissionEvaluator;

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

  @Autowired
  @UsedBy(SPRING)
  void setLaboratoryPermissionEvaluator(
      LaboratoryPermissionEvaluator laboratoryPermissionEvaluator) {
    this.laboratoryPermissionEvaluator = laboratoryPermissionEvaluator;
  }

  @Autowired
  @UsedBy(SPRING)
  void setUserPermissionEvaluator(UserPermissionEvaluator userPermissionEvaluator) {
    this.userPermissionEvaluator = userPermissionEvaluator;
  }

  @Autowired
  @UsedBy(SPRING)
  void setSubmissionPermissionEvaluator(
      SubmissionPermissionEvaluator submissionPermissionEvaluator) {
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
  }

  @Autowired
  @UsedBy(SPRING)
  void setSamplePermissionEvaluator(SamplePermissionEvaluator samplePermissionEvaluator) {
    this.samplePermissionEvaluator = samplePermissionEvaluator;
  }

  @Autowired
  @UsedBy(SPRING)
  void setPlatePermissionEvaluator(PlatePermissionEvaluator platePermissionEvaluator) {
    this.platePermissionEvaluator = platePermissionEvaluator;
  }
}
