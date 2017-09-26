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

import static ca.qc.ircm.proview.dataanalysis.QDataAnalysis.dataAnalysis;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QLaboratory.laboratory;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.QAcquisition;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for authorization.
 */
@Service
@Transactional
public class AuthorizationService {
  private static final String ADMIN = UserRole.ADMIN.name();
  private static final String MANAGER = UserRole.MANAGER.name();
  private static final String USER = UserRole.USER.name();
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private SecurityConfiguration securityConfiguration;

  protected AuthorizationService() {
  }

  protected AuthorizationService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthenticationService authenticationService, SecurityConfiguration securityConfiguration) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authenticationService = authenticationService;
    this.securityConfiguration = securityConfiguration;
  }

  private Subject getSubject() {
    return SecurityUtils.getSubject();
  }

  private User getUser(Long id) {
    if (id == null) {
      return null;
    }

    return entityManager.find(User.class, id);
  }

  private String realmName() {
    return securityConfiguration.realmName();
  }

  private Sample getSample(Long id) {
    if (id == null) {
      return null;
    }

    return entityManager.find(Sample.class, id);
  }

  private Submission getSubmission(Long id) {
    if (id == null) {
      return null;
    }

    return entityManager.find(Submission.class, id);
  }

  /**
   * Returns current user.
   *
   * @return current user
   */
  public User getCurrentUser() {
    return getUser((Long) getSubject().getPrincipal());
  }

  /**
   * Returns true if current user is authenticated or remembered, false otherwise.
   *
   * @return true if current user is authenticated or remembered, false otherwise
   */
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
  public boolean isRunAs() {
    return getSubject().isRunAs();
  }

  /**
   * Returns true if user has admin role, false otherwise.
   *
   * @return true if user has admin role, false otherwise
   */
  public boolean hasAdminRole() {
    return getSubject().hasRole(ADMIN);
  }

  /**
   * Returns true if user has manager role, false otherwise.
   *
   * @return true if user has manager role, false otherwise
   */
  public boolean hasManagerRole() {
    return getSubject().hasRole(MANAGER);
  }

  /**
   * Returns true if user has manager role, false otherwise.
   *
   * @param user
   *          user
   * @return true if user has manager role, false otherwise
   */
  public boolean hasManagerRole(User user) {
    if (user == null) {
      return false;
    }

    AuthorizationInfo authorizationInfo = authenticationService
        .getAuthorizationInfo(new SimplePrincipalCollection(user.getId(), realmName()));
    return authorizationInfo.getRoles() != null && authorizationInfo.getRoles().contains(MANAGER);
  }

  /**
   * Returns true if user has user role, false otherwise.
   *
   * @return true if user has user role, false otherwise
   */
  public boolean hasUserRole() {
    return getSubject().hasRole(USER);
  }

  /**
   * Checks that current user has admin role.
   */
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

  /**
   * Checks that current user can write user's password.
   *
   * @param user
   *          user
   */
  public void checkUserWritePasswordPermission(User user) {
    if (user != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("user:write_password:" + user.getId());
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
    User user = getCurrentUser();
    if (user == null) {
      return false;
    }

    QAcquisition controlAcquisition = new QAcquisition("control");
    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, controlAcquisition, acquisition, submissionSample);
    query.join(acquisition.sample, sample);
    query.join(submissionSample.submission, submission);
    query.join(submission.laboratory, laboratory);
    query.where(submissionSample.eq(sample));
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(controlAcquisition.in(msAnalysis.acquisitions));
    query.where(controlAcquisition.sample.eq(sampleParam));
    query.where(laboratory.managers.contains(user));
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
        boolean permitted = getSubject().hasRole(ADMIN);
        User owner = submission.getUser();
        permitted |= getSubject().getPrincipal().equals(owner.getId());
        Laboratory laboratory = submission.getLaboratory();
        permitted |= getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
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
    User user = getCurrentUser();
    if (user == null) {
      return false;
    }

    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, acquisition, submissionSample);
    query.join(acquisition.sample, sample);
    query.join(submissionSample.submission, submission);
    query.join(submission.laboratory, laboratory);
    query.where(submissionSample.eq(sample));
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(msAnalysis.eq(msAnalysisParam));
    query.where(laboratory.managers.contains(user));
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

  private boolean sampleOwnerByDataAnalysis(DataAnalysis dataAnalysisParam) {
    User user = getCurrentUser();
    if (user == null) {
      return false;
    }

    JPAQuery<Long> query = queryFactory.select(dataAnalysis.id);
    query.from(dataAnalysis);
    query.join(dataAnalysis.sample, submissionSample);
    query.join(submissionSample.submission, submission);
    query.where(dataAnalysis.eq(dataAnalysisParam));
    query.where(submission.user.eq(user));
    return query.fetchCount() > 0;
  }

  private boolean laboratoryManagerByDataAnalysis(DataAnalysis dataAnalysisParam) {
    User user = getCurrentUser();
    if (user == null) {
      return false;
    }

    JPAQuery<Long> query = queryFactory.select(dataAnalysis.id);
    query.from(dataAnalysis);
    query.join(dataAnalysis.sample, submissionSample);
    query.join(submissionSample.submission, submission);
    query.join(submission.laboratory, laboratory);
    query.where(dataAnalysis.eq(dataAnalysisParam));
    query.where(laboratory.managers.contains(user));
    return query.fetchCount() > 0;
  }

  /**
   * Checks that current user can read data analysis.
   *
   * @param dataAnalysis
   *          data analysis
   */
  public void checkDataAnalysisReadPermission(DataAnalysis dataAnalysis) {
    if (dataAnalysis != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        boolean permitted = false;
        permitted |= sampleOwnerByDataAnalysis(dataAnalysis);
        permitted |= laboratoryManagerByDataAnalysis(dataAnalysis);
        if (!permitted) {
          getSubject().checkPermission("dataAnalysis:read:" + dataAnalysis.getId());
        }
      }
    }
  }
}
