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
import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.QAcquisition;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
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
 * Default implementation of authorization services.
 */
@Service
@Transactional
public class AuthorizationServiceImpl implements AuthorizationService {
  private static final String ADMIN = UserRole.ADMIN.name();
  private static final String MANAGER = UserRole.MANAGER.name();
  private static final String USER = UserRole.USER.name();
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthenticationService authenticationService;

  protected AuthorizationServiceImpl() {
  }

  protected AuthorizationServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthenticationService authenticationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authenticationService = authenticationService;
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

  private String getRealmName() {
    return ShiroRealm.REALM_NAME;
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

  @Override
  public User getCurrentUser() {
    return getUser((Long) getSubject().getPrincipal());
  }

  @Override
  public boolean isUser() {
    return getSubject().isAuthenticated() || getSubject().isRemembered();
  }

  @Override
  public boolean isRunAs() {
    return getSubject().isRunAs();
  }

  @Override
  public boolean hasAdminRole() {
    return getSubject().hasRole(ADMIN);
  }

  @Override
  public boolean hasManagerRole() {
    return getSubject().hasRole(MANAGER);
  }

  @Override
  public boolean hasManagerRole(User user) {
    if (user == null) {
      return false;
    }

    AuthorizationInfo authorizationInfo = authenticationService
        .getAuthorizationInfo(new SimplePrincipalCollection(user.getId(), getRealmName()));
    return authorizationInfo.getRoles() != null && authorizationInfo.getRoles().contains(MANAGER);
  }

  @Override
  public boolean hasUserRole() {
    return getSubject().hasRole(USER);
  }

  @Override
  public void checkAdminRole() {
    getSubject().checkRole(ADMIN);
  }

  @Override
  public void checkUserRole() {
    getSubject().checkRole(USER);
  }

  @Override
  public void checkRobotRole() {
    getSubject().checkPermission(new RobotPermission());
  }

  @Override
  public void checkLaboratoryReadPermission(Laboratory laboratory) {
    if (laboratory != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("laboratory:read:" + laboratory.getId());
      }
    }
  }

  @Override
  public boolean hasLaboratoryManagerPermission(Laboratory laboratory) {
    if (laboratory != null && getSubject().hasRole(USER)) {
      return getSubject().hasRole(ADMIN)
          || getSubject().isPermitted("laboratory:manager:" + laboratory.getId());
    }
    return false;
  }

  @Override
  public void checkLaboratoryManagerPermission(Laboratory laboratory) {
    if (laboratory != null) {
      getSubject().checkRole(USER);
      if (!getSubject().hasRole(ADMIN)) {
        getSubject().checkPermission("laboratory:manager:" + laboratory.getId());
      }
    }
  }

  @Override
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

  @Override
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

  @Override
  public void checkUserWritePermission(User user) {
    if (user != null) {
      user = getUser(user.getId());
      getSubject().checkRole(USER);
      if (!hasUserWritePermission(user)) {
        getSubject().checkPermission("user:write:" + user.getId());
      }
    }
  }

  @Override
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

  @Override
  public void checkSampleReadPermission(Sample sample) {
    if (sample != null) {
      sample = getSample(sample.getId());
      getSubject().checkRole("USER");
      if (!getSubject().hasRole("PROTEOMIC")) {
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

  @Override
  public void checkSubmissionReadPermission(Submission submission) {
    if (submission != null) {
      submission = getSubmission(submission.getId());
      getSubject().checkRole("USER");
      if (!getSubject().hasRole("PROTEOMIC")) {
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

  private boolean sampleOwnerByMsAnalysis(MsAnalysis msAnalysisParam) {
    User user = getCurrentUser();

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

  @Override
  public void checkMsAnalysisReadPermission(MsAnalysis msAnalysis) {
    if (msAnalysis != null) {
      getSubject().checkRole("USER");
      if (!getSubject().hasRole("PROTEOMIC")) {
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

    JPAQuery<Long> query = queryFactory.select(dataAnalysis.id);
    query.from(dataAnalysis);
    query.join(dataAnalysis.sample, submissionSample);
    query.join(submissionSample.submission, submission);
    query.join(submission.laboratory, laboratory);
    query.where(dataAnalysis.eq(dataAnalysisParam));
    query.where(laboratory.managers.contains(user));
    return query.fetchCount() > 0;
  }

  @Override
  public void checkDataAnalysisReadPermission(DataAnalysis dataAnalysis) {
    if (dataAnalysis != null) {
      getSubject().checkRole("USER");
      if (!getSubject().hasRole("PROTEOMIC")) {
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
