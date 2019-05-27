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

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.plate.QWell.well;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSampleContainer.sampleContainer;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.user.UserAuthority.FORCE_CHANGE_PASSWORD;

import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.QAcquisition;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for authorization.
 */
@Service
@Transactional
public class AuthorizationService {
  private static final String ADMIN = UserRole.ADMIN;
  private static final String MANAGER = UserRole.MANAGER;
  private static final String USER = UserRole.USER;
  private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
  @Inject
  private UserRepository repository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private UserDetailsService userDetailsService;
  //TODO Replace once permissionEvaluator is available.
  //@Inject
  private PermissionEvaluator permissionEvaluator;

  protected AuthorizationService() {
  }

  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private UserDetails getUserDetails() {
    Authentication authentication = getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      return (UserDetails) authentication.getPrincipal();
    } else {
      return null;
    }
  }

  /**
   * Returns current user.
   *
   * @return current user
   */
  public User getCurrentUser() {
    UserDetails user = getUserDetails();
    if (user instanceof AuthenticatedUser) {
      Long userId = ((AuthenticatedUser) user).getId();
      if (userId == null) {
        return null;
      }

      return repository.findOne(userId);
    } else {
      return null;
    }
  }

  /**
   * Returns true if current user is anonymous, false otherwise.
   *
   * @return true if current user is anonymous, false otherwise
   */
  public boolean isAnonymous() {
    return getUserDetails() == null;
  }

  /**
   * Returns true if current user has specified role, false otherwise.
   *
   * @param role
   *          role
   * @return true if current user has specified role, false otherwise
   */
  public boolean hasRole(String role) {
    Authentication authentication = getAuthentication();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    boolean hasRole = false;
    for (GrantedAuthority authority : authorities) {
      hasRole |= authority.getAuthority().equals(role);
    }
    logger.trace("user {} hasRole {}? {}", authentication.getName(), role, hasRole);
    return hasRole;
  }

  /**
   * Returns true if current user has any of the specified roles, false otherwise.
   *
   * @param roles
   *          roles
   * @return true if current user has any of the specified roles, false otherwise
   */
  public boolean hasAnyRole(String... roles) {
    boolean hasAnyRole = false;
    for (String role : roles) {
      hasAnyRole |= hasRole(role);
    }
    return hasAnyRole;
  }

  /**
   * Returns true if current user has all of the specified roles, false otherwise.
   *
   * @param roles
   *          roles
   * @return true if current user has all of the specified roles, false otherwise
   */
  public boolean hasAllRoles(String... roles) {
    boolean hasAllRole = true;
    for (String role : roles) {
      hasAllRole &= hasRole(role);
    }
    return hasAllRole;
  }

  /**
   * Reload current user's authorities.
   */
  public void reloadAuthorities() {
    if (hasRole(FORCE_CHANGE_PASSWORD)) {
      Authentication oldAuthentication = getAuthentication();
      logger.debug("reload authorities from user {}", oldAuthentication.getName());
      UserDetails userDetails = userDetailsService.loadUserByUsername(oldAuthentication.getName());
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, oldAuthentication.getCredentials(), userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }

  /**
   * Returns true if current user is authorized to access class, false otherwise.
   *
   * @param type
   *          class
   * @return true if current user is authorized to access class, false otherwise
   */
  public boolean isAuthorized(Class<?> type) {
    RolesAllowed rolesAllowed = AnnotationUtils.findAnnotation(type, RolesAllowed.class);
    if (rolesAllowed != null) {
      String[] roles = rolesAllowed.value();
      return hasAnyRole(roles);
    } else {
      return true;
    }
  }

  /**
   * Returns true if current user has permission on object, false otherwise.
   *
   * @param object
   *          object
   * @param permission
   *          permission
   * @return true if current user has permission on object, false otherwise
   */
  public boolean hasPermission(Object object, Permission permission) {
    return permissionEvaluator.hasPermission(getAuthentication(), object, permission);
  }

  private Subject getSubject() {
    return SecurityUtils.getSubject();
  }

  private User getUser(Long id) {
    if (id == null) {
      return null;
    }

    return repository.findOne(id);
  }

  private Sample getSample(Long id) {
    if (id == null) {
      return null;
    }

    return sampleRepository.findOne(id);
  }

  private Submission getSubmission(Long id) {
    if (id == null) {
      return null;
    }

    return submissionRepository.findOne(id);
  }

  /**
   * Returns true if current user is authenticated or remembered, false otherwise.
   *
   * @return true if current user is authenticated or remembered, false otherwise
   */
  @Deprecated
  public boolean isUser() {
    return getSubject().isAuthenticated() || getSubject().isRemembered();
  }

  /**
   * Returns true if user is 'running as' another identity other than its original one, false
   * otherwise.
   *
   * @return true if user is 'running as' another identity other than its original one, false
   *         otherwise
   */
  @Deprecated
  public boolean isRunAs() {
    return getSubject().isRunAs();
  }

  /**
   * Returns true if user has admin role, false otherwise.
   *
   * @return true if user has admin role, false otherwise
   */
  @Deprecated
  public boolean hasAdminRole() {
    return getSubject().hasRole(ADMIN);
  }

  /**
   * Returns true if user has manager role, false otherwise.
   *
   * @return true if user has manager role, false otherwise
   */
  @Deprecated
  public boolean hasManagerRole() {
    return getSubject().hasRole(MANAGER);
  }

  /**
   * Returns true if user has user role, false otherwise.
   *
   * @return true if user has user role, false otherwise
   */
  @Deprecated
  public boolean hasUserRole() {
    return getSubject().hasRole(USER);
  }

  /**
   * Checks that current user has admin role.
   */
  @Deprecated
  public void checkAdminRole() {
    getSubject().checkRole(ADMIN);
  }

  /**
   * Checks that current user has user role.
   */
  public void checkUserRole() {
    getSubject().checkRole(USER);
  }

  /**
   * Checks that current user has robot role.
   */
  public void checkRobotRole() {
    getSubject().checkPermission(new RobotPermission());
  }

  /**
   * Checks that current user can read laboratory.
   *
   * @param laboratory
   *          laboratory
   */
  public void checkLaboratoryReadPermission(Laboratory laboratory) {
    if (laboratory != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("laboratory:read:" + laboratory.getId());
      }
    }
  }

  /**
   * Returns true if user is a manager for laboratory, false otherwise.
   *
   * @param laboratory
   *          laboratory
   * @return true if user is a manager for laboratory, false otherwise
   */
  public boolean hasLaboratoryManagerPermission(Laboratory laboratory) {
    if (laboratory != null && getSubject().hasRole(USER)) {
      return getSubject().hasRole(ADMIN)
          || getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
    }
    return false;
  }

  /**
   * Checks that current user is a manager of laboratory.
   *
   * @param laboratory
   *          laboratory
   */
  public void checkLaboratoryManagerPermission(Laboratory laboratory) {
    if (laboratory != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("laboratory:manager:" + laboratory.getId());
      }
    }
  }

  /**
   * Checks that current user can read user.
   *
   * @param user
   *          user
   */
  public void checkUserReadPermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        if (!getSubject().isPermitted("laboratory:manager:" + user.getLaboratory().getId())) {
          getSubject().checkPermission("user:read:" + user.getId());
        }
      }
    }
  }

  /**
   * Returns true if current user can write user, false otherwise.
   *
   * @param user
   *          user
   * @return true if current user can write user, false otherwise
   */
  public boolean hasUserWritePermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      if (getSubject().hasRole(USER)) {
        boolean permitted = getSubject().hasRole(ADMIN);
        permitted |= getSubject().isPermitted("laboratory:manager:" + user.getLaboratory().getId());
        permitted |= getSubject().isPermitted("user:write:" + user.getId());
        return permitted;
      }
    }
    return false;
  }

  /**
   * Checks that current user can write user.
   *
   * @param user
   *          user
   */
  public void checkUserWritePermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      getSubject().checkRole(USER);
      if (!hasUserWritePermission(user)) {
        getSubject().checkPermission("user:write:" + user.getId());
      }
    }
  }

  private boolean sampleOwnerForAnyMsAnalysisByControl(Sample sampleParam) {
    User user = getCurrentUser();
    if (user == null) {
      return false;
    }

    QAcquisition controlAcquisition = new QAcquisition("control");
    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, controlAcquisition, acquisition, submissionSample);
    query.join(acquisition.sample, sample);
    query.join(submissionSample.submission, submission);
    query.where(submissionSample.eq(sample));
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(controlAcquisition.in(msAnalysis.acquisitions));
    query.where(controlAcquisition.sample.eq(sampleParam));
    query.where(submission.user.eq(user));
    return query.fetchCount() > 0;
  }

  private boolean laboratoryManagerForAnyMsAnalysisByControl(Sample sampleParam) {
    User currentUser = getCurrentUser();
    if (currentUser == null) {
      return false;
    }

    QAcquisition controlAcquisition = new QAcquisition("control");
    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, controlAcquisition, acquisition, submissionSample, user);
    query.join(acquisition.sample, sample);
    query.join(submissionSample.submission, submission);
    query.where(submissionSample.eq(sample));
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(controlAcquisition.in(msAnalysis.acquisitions));
    query.where(controlAcquisition.sample.eq(sampleParam));
    query.where(user.laboratory.eq(submission.laboratory));
    query.where(user.eq(currentUser));
    query.where(user.manager.eq(true));
    return query.fetchCount() > 0;
  }

  /**
   * Checks that current user can read sample.
   *
   * @param sample
   *          sample
   */
  public void checkSampleReadPermission(Sample sample) {
    if (sample != null) {
      sample = getSample(sample.getId());
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        if (sample instanceof SubmissionSample) {
          SubmissionSample submissionSample = (SubmissionSample) sample;
          boolean permitted = false;
          User owner = submissionSample.getSubmission().getUser();
          permitted |= getSubject().getPrincipal().equals(owner.getId());
          Laboratory laboratory = submissionSample.getSubmission().getLaboratory();
          permitted |= getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
          if (!permitted) {
            getSubject().checkPermission("sample:owner:" + sample.getId());
          }
        } else if (sample instanceof Control) {
          boolean permitted = false;
          permitted |= sampleOwnerForAnyMsAnalysisByControl(sample);
          permitted |= laboratoryManagerForAnyMsAnalysisByControl(sample);
          if (!permitted) {
            getSubject().checkPermission("sample:owner:" + sample.getId());
          }
        } else {
          getSubject().checkPermission("sample:owner:" + sample.getId());
        }
      }
    }
  }

  /**
   * Checks that current user can read submission.
   *
   * @param submission
   *          submission
   */
  public void checkSubmissionReadPermission(Submission submission) {
    if (submission != null) {
      submission = getSubmission(submission.getId());
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        boolean permitted = false;
        User owner = submission.getUser();
        permitted |= getSubject().getPrincipal().equals(owner.getId());
        Laboratory laboratory = submission.getLaboratory();
        permitted |= getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
        if (!permitted) {
          getSubject().checkPermission("submission:owner:" + submission.getId());
        }
      }
    }
  }

  /**
   * Returns true if current user can write submission, false otherwise.
   *
   * @param submission
   *          submission
   * @return true if current user can write submission, false otherwise
   */
  public boolean hasSubmissionWritePermission(Submission submission) {
    if (submission != null) {
      submission = getSubmission(submission.getId());
      if (getSubject().hasRole(USER)) {
        if (getSubject().hasRole(ADMIN)) {
          return true;
        }
        User owner = submission.getUser();
        boolean permitted = getSubject().getPrincipal().equals(owner.getId());
        Laboratory laboratory = submission.getLaboratory();
        permitted |= getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
        permitted &= !submissionAfterApproved(submission);
        return permitted;
      }
    }
    return false;
  }

  /**
   * Checks that current user can write submission.
   *
   * @param submission
   *          submission
   */
  public void checkSubmissionWritePermission(Submission submission) {
    if (submission != null) {
      submission = getSubmission(submission.getId());
      getSubject().checkRole(USER);
      if (!hasSubmissionWritePermission(submission)) {
        getSubject().checkPermission("submission:owner:" + submission.getId());
      }
    }
  }

  private boolean submissionAfterApproved(Submission submissionParam) {
    BooleanExpression predicate = submission.id.eq(submissionParam.getId())
        .and(submission.samples.any().status.gt(SampleStatus.WAITING));
    return submissionRepository.count(predicate) > 0;
  }

  /**
   * Checks that current user can read plate.
   *
   * @param plate
   *          plate
   */
  public void checkPlateReadPermission(Plate plate) {
    if (plate != null) {
      if (!hasPlateReadPermission(plate)) {
        getSubject().checkPermission("plate:read:" + plate.getId());
      }
    }
  }

  private boolean hasPlateReadPermission(Plate plate) {
    if (plate != null) {
      if (getSubject().hasRole(ADMIN)) {
        return true;
      } else if (getSubject().hasRole(MANAGER)) {
        return isPlateOwner(plate) || isPlateLaboratoryManager(plate);
      } else if (getSubject().hasRole(USER)) {
        return isPlateOwner(plate);
      }
    }
    return false;
  }

  private boolean isPlateOwner(Plate plate) {
    JPAQuery<Long> query = queryFactory.select(submissionSample.id);
    query.from(submissionSample, well);
    query.join(submissionSample.originalContainer, sampleContainer);
    query.where(submissionSample.submission.user.eq(getCurrentUser()));
    query.where(well.id.eq(sampleContainer.id));
    query.where(well.plate.eq(plate));
    return query.fetchCount() > 0;
  }

  private boolean isPlateLaboratoryManager(Plate plate) {
    JPAQuery<Long> query = queryFactory.select(submissionSample.id);
    query.from(submissionSample, well, user);
    query.join(submissionSample.originalContainer, sampleContainer);
    query.where(user.laboratory.eq(submissionSample.submission.laboratory));
    query.where(user.eq(getCurrentUser()));
    query.where(user.manager.eq(true));
    query.where(well.id.eq(sampleContainer.id));
    query.where(well.plate.eq(plate));
    return query.fetchCount() > 0;
  }

  private boolean sampleOwnerByMsAnalysis(MsAnalysis msAnalysisParam) {
    User user = getCurrentUser();
    if (user == null) {
      return false;
    }

    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, acquisition, submissionSample);
    query.join(acquisition.sample, sample);
    query.join(submissionSample.submission, submission);
    query.where(submissionSample.eq(sample));
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(msAnalysis.eq(msAnalysisParam));
    query.where(submission.user.eq(user));
    return query.fetchCount() > 0;
  }

  private boolean laboratoryManagerByMsAnalysis(MsAnalysis msAnalysisParam) {
    User currentUser = getCurrentUser();
    if (currentUser == null) {
      return false;
    }

    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, acquisition, submissionSample, user);
    query.join(acquisition.sample, sample);
    query.join(submissionSample.submission, submission);
    query.where(submissionSample.eq(sample));
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(msAnalysis.eq(msAnalysisParam));
    query.where(user.laboratory.eq(submission.laboratory));
    query.where(user.eq(currentUser));
    query.where(user.manager.eq(true));
    return query.fetchCount() > 0;
  }

  /**
   * Checks that current user can read MS analysis.
   *
   * @param msAnalysis
   *          MS analysis
   */
  public void checkMsAnalysisReadPermission(MsAnalysis msAnalysis) {
    if (msAnalysis != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        boolean permitted = false;
        permitted |= sampleOwnerByMsAnalysis(msAnalysis);
        permitted |= laboratoryManagerByMsAnalysis(msAnalysis);
        if (!permitted) {
          getSubject().checkPermission("msAnalysis:read:" + msAnalysis.getId());
        }
      }
    }
  }

  void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
    this.permissionEvaluator = permissionEvaluator;
  }
}
