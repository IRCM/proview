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
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

/**
 * {@link PermissionEvaluator} that can evaluate permission for {@link Submission}.
 */
public class SubmissionPermissionEvaluator extends AbstractPermissionEvaluator {
  private SubmissionRepository repository;
  private AuthorizationService authorizationService;

  SubmissionPermissionEvaluator(SubmissionRepository repository, UserRepository userRepository,
      AuthorizationService authorizationService) {
    super(userRepository);
    this.repository = repository;
    this.authorizationService = authorizationService;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if ((authentication == null) || !(targetDomainObject instanceof Submission)
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Submission submission = (Submission) targetDomainObject;
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(submission, currentUser, realPermission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if ((authentication == null) || !(targetId instanceof Long)
        || !targetType.equals(Submission.class.getName())
        || (!(permission instanceof String) && !(permission instanceof Permission))) {
      return false;
    }
    Submission submission = repository.findById((Long) targetId).orElse(null);
    if (submission == null) {
      return false;
    }
    User currentUser = getUser(authentication);
    Permission realPermission = resolvePermission(permission);
    return hasPermission(submission, currentUser, realPermission);
  }

  boolean hasPermission(Submission submission, User currentUser, Permission permission) {
    if (currentUser == null) {
      return false;
    }
    if (authorizationService.hasRole(ADMIN)) {
      return true;
    }
    if (submission.getId() == null) {
      return authorizationService.hasRole(USER);
    }
    User owner = submission.getUser();
    boolean authorized = false;
    boolean ownerOrManager = currentUser.getId().equals(owner.getId()) || authorizationService
        .hasAllRoles(MANAGER, UserAuthority.laboratoryMember(owner.getLaboratory()));
    authorized |= permission.equals(BasePermission.READ) && ownerOrManager;
    authorized |= permission.equals(BasePermission.WRITE) && submissionAfterApproved(submission)
        && ownerOrManager;
    return authorized;
  }

  private boolean submissionAfterApproved(Submission submissionParam) {
    BooleanExpression predicate = submission.id.eq(submissionParam.getId())
        .and(submission.samples.any().status.gt(SampleStatus.WAITING));
    return repository.count(predicate) > 0;
  }
}
