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

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.DatabaseLogUtil;
import ca.qc.ircm.proview.history.SampleStatusUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService.MsAnalysisAggregate;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link MsAnalysis} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class MsAnalysisActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected MsAnalysisActivityService() {
  }

  protected MsAnalysisActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of MS analysis.
   *
   * @param msAnalysisAggregate
   *          inserted MS analysis
   * @return activity about insertion of MS analysis
   */
  @CheckReturnValue
  public Activity insert(final MsAnalysisAggregate msAnalysisAggregate) {
    final User user = authorizationService.getCurrentUser();

    final MsAnalysis msAnalysis = msAnalysisAggregate.getMsAnalysis();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    for (Acquisition acquisition : msAnalysisAggregate.getAcquisitions()) {
      Sample newSample = acquisition.getSample();
      Sample oldSample = entityManager.find(Sample.class, acquisition.getSample().getId());
      if (newSample instanceof SubmissionSample) {
        SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
        SubmissionSample oldSubmissionSample = (SubmissionSample) oldSample;
        updateBuilders.add(new SampleStatusUpdateActivityBuilder().oldSample(oldSubmissionSample)
            .newSample(newSubmissionSample));
      }
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(msAnalysis.getId());
    activity.setUser(user);
    activity.setTableName("msanalysis");
    activity.setJustification(null);
    activity.setUpdates(new LinkedList<>(updates));
    return activity;
  }

  /**
   * Creates an activity about MS analysis being marked as erroneous.
   *
   * @param msAnalysis
   *          erroneous MS analysis that was undone
   * @param justification
   *          explanation of what was incorrect with the MS analysis
   * @return activity about MS analysis being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(final MsAnalysis msAnalysis, final String justification) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(msAnalysis.getId());
    activity.setUser(user);
    activity.setTableName("msanalysis");
    activity.setJustification(justification);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about MS analysis being marked as failed.
   *
   * @param msAnalysis
   *          failed MS analysis that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about MS analysis being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(final MsAnalysis msAnalysis, String failedDescription,
      final Collection<SampleContainer> bannedContainers) {
    final User user = authorizationService.getCurrentUser();

    // Log update for banned containers.
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    if (bannedContainers != null) {
      for (SampleContainer container : bannedContainers) {
        SampleContainer oldContainer = entityManager.find(SampleContainer.class, container.getId());
        updateBuilders
            .add(new BanSampleContainerUpdateActivityBuilder().oldContainer(oldContainer));
      }
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    final String justification = DatabaseLogUtil.reduceLength(failedDescription, 255);
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(msAnalysis.getId());
    activity.setUser(user);
    activity.setTableName("msanalysis");
    activity.setJustification(justification);
    activity.setUpdates(new LinkedList<>(updates));
    return activity;
  }
}
