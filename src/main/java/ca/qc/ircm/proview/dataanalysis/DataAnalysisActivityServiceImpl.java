package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.SampleStatusUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DataAnalysisActivityServiceImpl implements DataAnalysisActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected DataAnalysisActivityServiceImpl() {
  }

  protected DataAnalysisActivityServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Activity insert(final DataAnalysis dataAnalysis) {
    final User user = authorizationService.getCurrentUser();

    // Get old sample outside of transaction.
    SubmissionSample oldSample =
        entityManager.find(SubmissionSample.class, dataAnalysis.getSample().getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    updateBuilders.add(new SampleStatusUpdateActivityBuilder().oldSample(oldSample)
        .newValue(SubmissionSample.Status.DATA_ANALYSIS));

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
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
    activity.setJustification(null);
    activity.setUpdates(new LinkedList<UpdateActivity>(updates));
    return activity;
  }

  @Override
  public Optional<Activity> update(final DataAnalysis newDataAnalysis, final String justification) {
    User user = authorizationService.getCurrentUser();

    // Get old data analysis outside of transaction.
    DataAnalysis oldDataAnalysis = entityManager.find(DataAnalysis.class, newDataAnalysis.getId());

    class DataAnalysisUpdateBuilder extends UpdateActivityBuilder {
      {
        this.tableName("dataanalysis");
        this.actionType(ActionType.UPDATE);
        this.recordId(newDataAnalysis.getId());
      }
    }

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("protein")
        .oldValue(oldDataAnalysis.getProtein()).newValue(newDataAnalysis.getProtein()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("peptide")
        .oldValue(oldDataAnalysis.getPeptide()).newValue(newDataAnalysis.getPeptide()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("analysisType")
        .oldValue(oldDataAnalysis.getType()).newValue(newDataAnalysis.getType()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("maxWorkTime")
        .oldValue(oldDataAnalysis.getMaxWorkTime()).newValue(newDataAnalysis.getMaxWorkTime()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("score")
        .oldValue(oldDataAnalysis.getScore()).newValue(newDataAnalysis.getScore()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("workTime")
        .oldValue(oldDataAnalysis.getWorkTime()).newValue(newDataAnalysis.getWorkTime()));
    updateBuilders.add(new DataAnalysisUpdateBuilder().column("status")
        .oldValue(oldDataAnalysis.getStatus()).newValue(newDataAnalysis.getStatus()));
    updateBuilders.add(new SampleStatusUpdateActivityBuilder()
        .oldSample(oldDataAnalysis.getSample()).newSample(newDataAnalysis.getSample()));

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      // Log changes.
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(newDataAnalysis.getId());
      activity.setUser(user);
      activity.setTableName("dataanalysis");
      activity.setJustification(justification);
      activity.setUpdates(new LinkedList<UpdateActivity>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
