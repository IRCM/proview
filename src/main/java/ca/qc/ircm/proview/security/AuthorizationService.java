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
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
  @Inject
  private UserRepository repository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private JPAQueryFactory queryFactory;

  protected AuthorizationService() {
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
}
