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

package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.SampleStatusUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link DataAnalysis} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DataAnalysisActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected DataAnalysisActivityService() {
  }

  protected DataAnalysisActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of data analysis.
   *
   * @param dataAnalysis
   *          inserted data analysis
   * @return activity about insertion of data analysis
   */
  @CheckReturnValue
  public Activity insert(final DataAnalysis dataAnalysis) {
    final User user = authorizationService.getCurrentUser();

    // Get old sample outside of transaction.
    SubmissionSample oldSample =
        entityManager.find(SubmissionSample.class, dataAnalysis.getSample().getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    updateBuilders.add(new SampleStatusUpdateActivityBuilder().oldSample(oldSample)
        .newValue(SampleStatus.DATA_ANALYSIS));

    // Keep updates that changed.
    final List<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(dataAnalysis.getId());
    activity.setUser(user);
    activity.setTableName("dataanalysis");
    activity.setExplanation(null);
    activity.setUpdates(updates);
    return activity;
  }

  /**
   * Creates an activity about update of a data analysis.
   *
   * @param dataAnalysis
   *          data analysis after update
   * @param explanation
   *          explanation for changes made to data analysis
   * @return activity about update of a data analysis
   */
  @CheckReturnValue
  public Optional<Activity> update(final DataAnalysis dataAnalysis, final String explanation) {
    User user = authorizationService.getCurrentUser();

    // Get old data analysis outside of transaction.
    DataAnalysis oldDataAnalysis = entityManager.find(DataAnalysis.class, dataAnalysis.getId());

    class DataAnalysisUpdateBuilder extends UpdateActivityBuilder {
      {
        this.tableName("dataanalysis");
        this.actionType(ActionType.UPDATE);
        this.recordId(dataAnalysis.getId());
      }
    }

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("protein")
        .oldValue(oldDataAnalysis.getProtein()).newValue(dataAnalysis.getProtein()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("peptide")
        .oldValue(oldDataAnalysis.getPeptide()).newValue(dataAnalysis.getPeptide()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("analysisType")
        .oldValue(oldDataAnalysis.getType()).newValue(dataAnalysis.getType()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("maxWorkTime")
        .oldValue(oldDataAnalysis.getMaxWorkTime()).newValue(dataAnalysis.getMaxWorkTime()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("score")
        .oldValue(oldDataAnalysis.getScore()).newValue(dataAnalysis.getScore()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("workTime")
        .oldValue(oldDataAnalysis.getWorkTime()).newValue(dataAnalysis.getWorkTime()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("status")
        .oldValue(oldDataAnalysis.getStatus()).newValue(dataAnalysis.getStatus()));
    updateBuilders.add(new SampleStatusUpdateActivityBuilder()
        .oldSample(oldDataAnalysis.getSample()).newSample(dataAnalysis.getSample()));

    // Keep updates that changed.
    final List<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      // Log changes.
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(dataAnalysis.getId());
      activity.setUser(user);
      activity.setTableName("dataanalysis");
      activity.setExplanation(explanation);
      activity.setUpdates(updates);
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
