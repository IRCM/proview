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

package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.submission.QSubmission.submission;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for MS analysis.
 */
@Service
@Transactional
public class MsAnalysisService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private MsAnalysisActivityService msAnalysisActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected MsAnalysisService() {
  }

  protected MsAnalysisService(EntityManager entityManager, JPAQueryFactory queryFactory,
      MsAnalysisActivityService msAnalysisActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.msAnalysisActivityService = msAnalysisActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects MS analysis from database.
   *
   * @param id
   *          database identifier of MS analysis
   * @return MS analysis
   */
  public MsAnalysis get(Long id) {
    if (id == null) {
      return null;
    }

    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, id);
    authorizationService.checkMsAnalysisReadPermission(msAnalysis);
    return msAnalysis;
  }

  /**
   * Selects MS analysis of acquisition.
   *
   * @param acquisition
   *          acquisition
   * @return MS analysis of acquisition
   */
  public MsAnalysis get(Acquisition acquisition) {
    if (acquisition == null) {
      return null;
    }

    JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis);
    query.from(msAnalysis);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    MsAnalysis ret = query.fetchOne();
    authorizationService.checkMsAnalysisReadPermission(ret);
    return ret;
  }

  /**
   * Selects all MS analysis made on sample.
   *
   * @param sample
   *          sample
   * @return all MS analysis made on sample
   */
  public List<MsAnalysis> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis);
    query.from(msAnalysis, acquisition);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.where(acquisition.sample.eq(sample));
    return query.fetch();
  }

  /**
   * Selects all MS analysis made on submission.
   *
   * @param submissionParam
   *          submission
   * @return all MS analysis made on submission
   */
  public List<MsAnalysis> all(Submission submissionParam) {
    if (submissionParam == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSubmissionReadPermission(submissionParam);

    JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis).distinct();
    query.from(msAnalysis, acquisition, submission);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.where(acquisition.sample.in(submission.samples));
    query.where(submission.eq(submissionParam));
    return query.fetch();
  }

  /**
   * Analyse samples by MS.
   *
   * @param msAnalysis
   *          MS analysis
   * @return MS analysis with complete information for acquisitions
   * @throws IllegalArgumentException
   *           MS analysis contains samples from more than one user
   */
  public MsAnalysis insert(MsAnalysis msAnalysis) throws IllegalArgumentException {
    authorizationService.checkAdminRole();

    // Check that all samples where submitted by the same user.
    chechSameUserForAllSamples(msAnalysis);

    // Add MS analysis to database.
    msAnalysis.setInsertTime(Instant.now());
    setPositions(msAnalysis.getAcquisitions());
    entityManager.persist(msAnalysis);

    // Set status of submission samples to analysed.
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getSample() instanceof SubmissionSample) {
        SubmissionSample submissionSample = (SubmissionSample) acquisition.getSample();
        submissionSample.setStatus(SampleStatus.ANALYSED);
      }
    }

    entityManager.flush();
    // Log insertion of MS analysis.
    Activity activity = msAnalysisActivityService.insert(msAnalysis);
    activityService.insert(activity);

    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getSample() instanceof SubmissionSample) {
        entityManager.merge(acquisition.getSample());
      }
    }
    return msAnalysis;
  }

  private void setPositions(List<Acquisition> acquisitions) {
    Map<Sample, Integer> positions = new HashMap<>();
    acquisitions.stream().filter(ac -> ac.getPosition() == null).forEach(acquisition -> {
      Sample sample = acquisition.getSample();
      Integer lastPosition = lastPosition(sample);
      if (lastPosition == null) {
        lastPosition = 0;
      }
      positions.put(sample, lastPosition + 1);
    });
    acquisitions.stream().filter(ac -> ac.getPosition() == null).forEach(acquisition -> {
      Sample sample = acquisition.getSample();
      Integer position = positions.get(sample);
      acquisition.setPosition(position);
      positions.put(sample, position + 1);
    });
  }

  private Integer lastPosition(Sample sample) {
    JPAQuery<Integer> query = queryFactory.select(acquisition.position.max());
    query.from(acquisition);
    query.where(acquisition.sample.eq(sample));
    return query.fetchOne();
  }

  private void chechSameUserForAllSamples(MsAnalysis msAnalysis) throws IllegalArgumentException {
    User expectedUser = null;
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getSample() instanceof SubmissionSample) {
        SubmissionSample sample = (SubmissionSample) acquisition.getSample();
        if (expectedUser == null) {
          expectedUser = sample.getUser();
        } else if (!expectedUser.equals(sample.getUser())) {
          throw new IllegalArgumentException("Cannot analyse samples from multiple users");
        }
      }
    }
  }

  /**
   * Updates MS analysis's information in database.
   *
   * @param msAnalysis
   *          MS analysis containing new information
   * @param explanation
   *          explanation
   */
  public void update(MsAnalysis msAnalysis, String explanation) {
    authorizationService.checkAdminRole();

    MsAnalysis old = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    Set<Long> acquisitionsIds =
        msAnalysis.getAcquisitions().stream().map(ts -> ts.getId()).collect(Collectors.toSet());
    if (old.getAcquisitions().stream().filter(ts -> !acquisitionsIds.contains(ts.getId())).findAny()
        .isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + Acquisition.class.getSimpleName()
          + " from " + MsAnalysis.class.getSimpleName() + " on update");
    }

    setPositions(msAnalysis.getAcquisitions());

    Optional<Activity> activity = msAnalysisActivityService.update(msAnalysis, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(msAnalysis);
  }

  /**
   * Undo erroneous MS analysis that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for MS
   * analysis are not the right ones. So, in practice, the MS analysis never actually occurred.
   *
   * @param msAnalysis
   *          erroneous MS analysis to undo
   * @param explanation
   *          explanation of what was incorrect with the MS analysis
   */
  public void undoErroneous(MsAnalysis msAnalysis, String explanation) {
    authorizationService.checkAdminRole();

    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionType(MsAnalysis.DeletionType.ERRONEOUS);
    msAnalysis.setDeletionExplanation(explanation);

    // Log changes.
    Activity activity = msAnalysisActivityService.undoErroneous(msAnalysis, explanation);
    activityService.insert(activity);

    entityManager.merge(msAnalysis);
  }

  /**
   * Report that a problem occurred during MS analysis causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the MS analysis was done but the incorrect MS
   * analysis could only be detected later in the sample processing. Thus the MS analysis is not
   * undone but flagged as having failed.
   *
   * @param msAnalysis
   *          MS analysis to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in MS analysis should be banned, this will also ban any
   *          container were samples were transfered after MS analysis
   */
  public void undoFailed(MsAnalysis msAnalysis, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionType(MsAnalysis.DeletionType.FAILED);
    msAnalysis.setDeletionExplanation(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during MS analysis.
      List<Acquisition> acquisitions = msAnalysis.getAcquisitions();
      for (Acquisition acquisition : acquisitions) {
        SampleContainer container = acquisition.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after digestion.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        msAnalysisActivityService.undoFailed(msAnalysis, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(msAnalysis);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
