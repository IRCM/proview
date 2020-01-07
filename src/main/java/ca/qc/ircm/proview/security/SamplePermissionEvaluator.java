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

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Sample}.
 */
public class SamplePermissionEvaluator extends AbstractPermissionEvaluator {
  private SampleRepository repository;
  private AuthorizationService authorizationService;
  private SubmissionPermissionEvaluator submissionPermissionEvaluator;

  SamplePermissionEvaluator(SampleRepository repository, UserRepository userRepository,
      AuthorizationService authorizationService,
      SubmissionPermissionEvaluator submissionPermissionEvaluator) {
    super(userRepository);
    this.repository = repository;
    this.authorizationService = authorizationService;
    this.submissionPermissionEvaluator = submissionPermissionEvaluator;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof Sample)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Sample sample = (Sample) targetDomainObject;
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(sample, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if ((authentication == null) || !(targetId instanceof Long)
        || !targetType.equals(Sample.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Sample sample = repository.findById((Long) targetId).orElse(null);
    if (sample == null) {
      return false;
    }
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(sample, currentUser, realPermission);
  }

  private boolean hasPermission(Sample sample, User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (authorizationService.hasRole(ADMIN)) {
      return true;
    }
    if (sample instanceof SubmissionSample) {
      if (sample.getId() == null) {
        return authorizationService.hasRole(USER);
      }
      Submission submission = ((SubmissionSample) sample).getSubmission();
      return submissionPermissionEvaluator.hasPermission(submission, currentUser, permission);
    }
    return false;
  }
}
