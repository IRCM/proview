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

package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSampleContainer.sampleContainer;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleStatus.DATA_ANALYSIS;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_ANALYSE;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_APPROVE;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_DIGEST;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_ENRICH;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_RECEIVE;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service class for Sample.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionSampleService {
  /**
   * Samples can be sorted by these properties.
   */
  public static enum Sort {
    /**
     * Laboratory that submitted sample.
     */
    LABORATORY,
    /**
     * Director of laboratory that submitted sample.
     */
    USER,
    /**
     * Submission date.
     */
    SUBMISSION,
    /**
     * Sample lims.
     */
    LIMS,
    /**
     * Sample name.
     */
    NAME,
    /**
     * Sample status.
     */
    STATUS,
    /**
     * Sample support.
     */
    SUPPORT;
  }

  /**
   * Limit search to samples with that support.
   */
  public static enum Support {
    /**
     * @see ca.qc.ircm.proview.sample.SampleSupport#SOLUTION
     */
    SOLUTION,
    /**
     * @see ca.qc.ircm.proview.sample.SampleSupport#GEL
     */
    GEL,
    /**
     * Small molecule to analyse with high resolution.
     *
     * @see ca.qc.ircm.proview.submission.Submission#isHighResolution()
     */
    MOLECULE_HIGH,
    /**
     * Small molecule to analyse with low resolution.
     *
     * @see ca.qc.ircm.proview.submission.Submission#isLowResolution()
     */
    MOLECULE_LOW,
    /**
     * @see ca.qc.ircm.proview.submission.Service#INTACT_PROTEIN
     */
    INTACT_PROTEIN;
  }

  /**
   * Report containing submitted samples.
   */
  public static interface Report {
    List<SubmissionSample> getSamples();

    Map<SubmissionSample, Boolean> getLinkedToResults();
  }

  private static class ReportDefault implements Report {
    private List<SubmissionSample> samples;
    private Map<SubmissionSample, Boolean> linkedToResults;

    private ReportDefault(List<SubmissionSample> samples,
        Map<SubmissionSample, Boolean> linkedToResults) {
      this.samples = samples;
      this.linkedToResults = linkedToResults;
    }

    @Override
    public List<SubmissionSample> getSamples() {
      return samples;
    }

    @Override
    public Map<SubmissionSample, Boolean> getLinkedToResults() {
      return linkedToResults;
    }
  }

  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionSampleService() {
  }

  protected SubmissionSampleService(EntityManager entityManager, JPAQueryFactory queryFactory,
      SampleActivityService sampleActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects submitted sample from database.
   *
   * @param id
   *          database identifier of submitted sample
   * @return submitted sample
   */
  public SubmissionSample get(Long id) {
    if (id == null) {
      return null;
    }

    SubmissionSample sample = entityManager.find(SubmissionSample.class, id);
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  /**
   * Returns submitted sample having this name.
   *
   * @param name
   *          sample's name
   * @return submitted sample having this name
   */
  public SubmissionSample getSubmission(String name) {
    if (name == null) {
      return null;
    }

    JPAQuery<SubmissionSample> query = queryFactory.select(submissionSample);
    query.from(submissionSample);
    query.where(submissionSample.name.eq(name));
    SubmissionSample sample = query.fetchOne();
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  /**
   * Returns true if a sample with this name is already in database, false otherwise.
   *
   * @param name
   *          name of sample
   * @return true if a sample with this name is already in database, false otherwise
   */
  public boolean exists(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkUserRole();

    JPAQuery<Long> query = queryFactory.select(submissionSample.id);
    query.from(submissionSample);
    query.where(submissionSample.name.eq(name));
    return query.fetchCount() > 0;
  }

  /**
   * Selects samples to show in a report.
   *
   * @param filter
   *          filters samples
   * @return samples found
   */
  public Report report(SampleFilter filter) {
    authorizationService.checkUserRole();

    return report(filter, false);
  }

  private Report report(SampleFilter filter, boolean admin) {
    if (filter == null) {
      filter = new SampleFilterBuilder().build();
    }

    final List<SubmissionSample> samples = fetchReportSamples(filter, admin);

    List<Tuple> tuples;
    if (!samples.isEmpty()) {
      JPAQuery<Tuple> query = queryFactory.select(sample.id, acquisitionMascotFile.count());
      query.from(sample);
      query.join(acquisition);
      query.join(acquisitionMascotFile);
      query.where(sample.in(samples));
      query.where(acquisition.sample.eq(sample));
      query.where(acquisitionMascotFile.acquisition.eq(acquisition));
      if (!admin) {
        query.where(acquisitionMascotFile.visible.eq(true));
      }
      query.groupBy(sample.id);
      tuples = query.fetch();
    } else {
      tuples = Collections.emptyList();
    }
    final Map<SubmissionSample, Boolean> linkedToResults = new HashMap<>();
    final Map<Long, SubmissionSample> samplesById = new HashMap<>();
    for (SubmissionSample sample : samples) {
      samplesById.put(sample.getId(), sample);
      linkedToResults.put(sample, false);
    }
    for (Tuple tuple : tuples) {
      SubmissionSample actualSample = samplesById.get(tuple.get(sample.id));
      linkedToResults.put(actualSample, tuple.get(acquisitionMascotFile.count()) > 0);
    }
    return new ReportDefault(samples, linkedToResults);
  }

  private List<SubmissionSample> fetchReportSamples(SampleFilter filter, boolean admin) {
    final User _user;
    final Laboratory _laboratory;
    boolean manager;
    if (admin) {
      _user = null;
      _laboratory = null;
      manager = false;
    } else {
      _user = authorizationService.getCurrentUser();
      _laboratory = _user.getLaboratory();
      manager = authorizationService.hasLaboratoryManagerPermission(_laboratory);
    }

    JPAQuery<SubmissionSample> query = queryFactory.select(submissionSample);
    query.from(submissionSample);
    query.join(submissionSample.submission, submission).fetch();
    query.join(submission.laboratory, laboratory);
    query.join(submission.user, user);
    if (filter.getExperienceContains() != null) {
      query.where(submission.experience.contains(filter.getExperienceContains()));
    }
    if (filter.getLaboratoryContains() != null) {
      query.where(laboratory.organization.eq(filter.getLaboratoryContains()));
    }
    if (filter.getLaboratory() != null) {
      query.where(laboratory.eq(filter.getLaboratory()));
    }
    if (filter.getLimsContains() != null) {
      query.where(submissionSample.lims.contains(filter.getLimsContains()));
    }
    if (filter.getMinimalSubmissionDate() != null) {
      query.where(submission.submissionDate.goe(filter.getMinimalSubmissionDate()));
    }
    if (filter.getMaximalSubmissionDate() != null) {
      query.where(submission.submissionDate.loe(filter.getMaximalSubmissionDate()));
    }
    if (filter.getNameContains() != null) {
      query.where(submissionSample.name.contains(filter.getNameContains()));
    }
    if (filter.getProjectContains() != null) {
      query.where(submission.project.contains(filter.getProjectContains()));
    }
    if (filter.getStatuses() != null) {
      query.where(submissionSample.status.in(filter.getStatuses()));
    }
    if (filter.getSupport() != null) {
      if (filter.getSupport() == SubmissionSampleService.Support.SOLUTION) {
        query.where(submissionSample.support.in(SampleSupport.SOLUTION, SampleSupport.DRY));
        query.where(submission.service.notIn(Service.INTACT_PROTEIN, Service.SMALL_MOLECULE));
      } else if (filter.getSupport() == SubmissionSampleService.Support.GEL) {
        query.where(submissionSample.support.eq(SampleSupport.GEL));
        query.where(submission.service.ne(Service.INTACT_PROTEIN));
      } else if (filter.getSupport() == SubmissionSampleService.Support.MOLECULE_HIGH) {
        query.where(submission.service.eq(Service.SMALL_MOLECULE));
        query.where(submission.highResolution.eq(true));
      } else if (filter.getSupport() == SubmissionSampleService.Support.MOLECULE_LOW) {
        query.where(submission.service.eq(Service.SMALL_MOLECULE));
        query.where(submission.highResolution.eq(false));
      } else if (filter.getSupport() == SubmissionSampleService.Support.INTACT_PROTEIN) {
        query.where(submission.service.eq(Service.INTACT_PROTEIN));
      }
    }
    if (filter.getUserContains() != null) {
      query.where(user.name.contains(filter.getUserContains()));
    }
    if (filter.getUser() != null) {
      query.where(user.eq(filter.getUser()));
    }
    if (!admin) {
      if (manager) {
        query.where(laboratory.eq(_laboratory));
      } else {
        query.where(user.eq(_user));
      }
    }
    return query.fetch();
  }

  /**
   * Selects samples to show in an admin report.
   *
   * @param filter
   *          filters samples
   * @return samples found
   */
  public Report adminReport(SampleFilter filter) {
    authorizationService.checkAdminRole();

    return report(filter, true);
  }

  /**
   * Selects samples to show in sample monitoring page.
   *
   * @return samples to show in sample monitoring page
   */
  public List<SubmissionSample> sampleMonitoring() {
    authorizationService.checkAdminRole();

    JPAQuery<SubmissionSample> query = queryFactory.select(submissionSample);
    query.from(submissionSample);
    query.join(submissionSample.submission, submission).fetch();
    query.join(submission.laboratory, laboratory).fetch();
    query.join(submission.user, user).fetch();
    query.join(submissionSample.originalContainer, sampleContainer).fetch();
    query.where(submissionSample.status.in(TO_APPROVE, TO_RECEIVE, RECEIVED, TO_DIGEST, TO_ENRICH,
        TO_ANALYSE, DATA_ANALYSIS));
    return query.fetch();
  }

  /**
   * Selects all projects of signed user.
   *
   * @return all projects of signed user
   */
  public List<String> projects() {
    authorizationService.checkUserRole();
    User user = authorizationService.getCurrentUser();

    JPAQuery<String> query = queryFactory.select(submission.project);
    query.from(submission);
    query.where(submission.project.isNotNull());
    query.where(submission.user.eq(user));
    return query.distinct().fetch();
  }

  /**
   * Updates sample's information in database.
   *
   * @param sample
   *          sample containing new information
   * @param justification
   *          justification for changes made to sample
   */
  public void update(SubmissionSample sample, String justification) {
    authorizationService.checkAdminRole();

    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(sample, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(sample);
  }

  /**
   * Update many sample's status.
   *
   * @param samples
   *          samples containing new status
   */
  public void updateStatus(Collection<? extends SubmissionSample> samples) {
    authorizationService.checkAdminRole();

    for (SubmissionSample sample : samples) {
      // Log changes.
      Optional<Activity> activity = sampleActivityService.update(sample, null);
      if (activity.isPresent()) {
        activityService.insert(activity.get());
      }

      entityManager.merge(sample);
    }
  }
}
