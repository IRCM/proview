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

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of MS analysis services.
 */
@Service
@Transactional
public class MsAnalysisServiceImpl extends BaseTreatmentService implements MsAnalysisService {
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

  protected MsAnalysisServiceImpl() {
  }

  protected MsAnalysisServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      MsAnalysisActivityService msAnalysisActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.msAnalysisActivityService = msAnalysisActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public MsAnalysis get(Long id) {
    if (id == null) {
      return null;
    }

    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, id);
    authorizationService.checkMsAnalysisReadPermission(msAnalysis);
    return msAnalysis;
  }

  @Override
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

  @Override
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

  @Override
  public Map<VerificationType, Map<String, Boolean>> verifications(MsAnalysis msAnalysis) {
    if (msAnalysis == null) {
      return new HashMap<>();
    }
    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    List<MsAnalysisVerification> rawVerifications = msAnalysis.getVerifications();
    Map<VerificationType, Map<String, Boolean>> verifications = new HashMap<>();
    for (MsAnalysisVerification verification : rawVerifications) {
      VerificationType type = verification.getType();
      if (!verifications.containsKey(type)) {
        verifications.put(type, new HashMap<String, Boolean>());
      }
      verifications.get(type).put(verification.getName(), verification.isValue());
    }
    return verifications;
  }

  @Override
  public MsAnalysis insert(MsAnalysisAggregate msAnalysisAggregate)
      throws SamplesFromMultipleUserException {
    authorizationService.checkAdminRole();

    // Check that all samples where submitted by the same user.
    chechSameUserForAllSamples(msAnalysisAggregate);

    // Add MS analysis to database.
    MsAnalysis msAnalysis = msAnalysisAggregate.getMsAnalysis();
    msAnalysis.setInsertTime(Instant.now());
    msAnalysis.setAcquisitions(msAnalysisAggregate.getAcquisitions());
    Map<Sample, Integer> positions = new HashMap<>();
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      Sample sample = acquisition.getSample();
      Integer lastPosition = lastPosition(sample);
      if (lastPosition == null) {
        lastPosition = 0;
      }
      positions.put(sample, lastPosition + 1);
    }
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      Sample sample = acquisition.getSample();
      Integer position = positions.get(sample);
      acquisition.setPosition(position);
      positions.put(sample, position + 1);
    }
    List<MsAnalysisVerification> verifications = new ArrayList<>();
    for (VerificationType type : VerificationType.values()) {
      for (Map.Entry<String, Boolean> verificationEntry : msAnalysisAggregate.getVerifications()
          .get(type).entrySet()) {
        verifications.add(new MsAnalysisVerification(type, verificationEntry.getKey(),
            verificationEntry.getValue()));
      }
    }
    msAnalysis.setVerifications(verifications);
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
    Activity activity = msAnalysisActivityService.insert(msAnalysisAggregate);
    activityService.insert(activity);

    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getSample() instanceof SubmissionSample) {
        entityManager.merge(acquisition.getSample());
      }
    }
    return msAnalysis;
  }

  private Integer lastPosition(Sample sample) {
    JPAQuery<Integer> query = queryFactory.select(acquisition.position.max());
    query.from(acquisition);
    query.where(acquisition.sample.eq(sample));
    return query.fetchOne();
  }

  private void chechSameUserForAllSamples(MsAnalysisAggregate msAnalysisAggregate)
      throws SamplesFromMultipleUserException {
    User expectedUser = null;
    for (Acquisition acquisition : msAnalysisAggregate.getAcquisitions()) {
      if (acquisition.getSample() instanceof SubmissionSample) {
        SubmissionSample sample = (SubmissionSample) acquisition.getSample();
        if (expectedUser == null) {
          expectedUser = sample.getUser();
        } else if (!expectedUser.equals(sample.getUser())) {
          throw new SamplesFromMultipleUserException();
        }
      }
    }
  }

  @Override
  public void undoErroneous(MsAnalysis msAnalysis, String justification) {
    authorizationService.checkAdminRole();

    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionType(MsAnalysis.DeletionType.ERRONEOUS);
    msAnalysis.setDeletionJustification(justification);

    // Log changes.
    Activity activity = msAnalysisActivityService.undoErroneous(msAnalysis, justification);
    activityService.insert(activity);

    entityManager.merge(msAnalysis);
  }

  @Override
  public void undoFailed(MsAnalysis msAnalysis, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionType(MsAnalysis.DeletionType.FAILED);
    msAnalysis.setDeletionJustification(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
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
