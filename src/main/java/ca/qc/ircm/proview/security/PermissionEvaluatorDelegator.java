/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.security;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private UserRepository userRepository;
  @Inject
  private LaboratoryRepository laboratoryRepository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private PlateRepository plateRepository;
  private LaboratoryPermissionEvaluator laboratoryPermissionEvaluator;
  private UserPermissionEvaluator userPermissionEvaluator;
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;
  private SamplePermissionEvaluator samplePermissionEvaluator;
  private PlatePermissionEvaluator platePermissionEvaluator;

  @PostConstruct
  protected void init() {
    laboratoryPermissionEvaluator = new LaboratoryPermissionEvaluator(laboratoryRepository,
        userRepository, authorizationService);
    userPermissionEvaluator = new UserPermissionEvaluator(userRepository, authorizationService);
    submissionPermissionEvaluator = new SubmissionPermissionEvaluator(submissionRepository,
        userRepository, authorizationService);
    samplePermissionEvaluator = new SamplePermissionEvaluator(sampleRepository, userRepository,
        authorizationService, submissionPermissionEvaluator);
    platePermissionEvaluator = new PlatePermissionEvaluator(plateRepository, userRepository,
        authorizationService, submissionPermissionEvaluator);
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

  void setLaboratoryPermissionEvaluator(
      LaboratoryPermissionEvaluator laboratoryPermissionEvaluator) {
    this.laboratoryPermissionEvaluator = laboratoryPermissionEvaluator;
  }

  void setUserPermissionEvaluator(UserPermissionEvaluator userPermissionEvaluator) {
    this.userPermissionEvaluator = userPermissionEvaluator;
  }

  void setSubmissionPermissionEvaluator(
      SubmissionPermissionEvaluator submissionPermissionEvaluator) {
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
  }

  void setSamplePermissionEvaluator(SamplePermissionEvaluator samplePermissionEvaluator) {
    this.samplePermissionEvaluator = samplePermissionEvaluator;
  }

  void setPlatePermissionEvaluator(PlatePermissionEvaluator platePermissionEvaluator) {
    this.platePermissionEvaluator = platePermissionEvaluator;
  }
}
