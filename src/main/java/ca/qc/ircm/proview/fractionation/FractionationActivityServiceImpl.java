package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.AddSampleToSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.DatabaseLogUtil;
import ca.qc.ircm.proview.history.RemoveSampleFromSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class FractionationActivityServiceImpl implements FractionationActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected FractionationActivityServiceImpl() {
  }

  protected FractionationActivityServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Activity insert(final Fractionation fractionation) {
    final User user = authorizationService.getCurrentUser();

    // Update of spot's sample.
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    for (FractionationDetail detail : fractionation.getTreatmentSamples()) {
      updateBuilders.add(new AddSampleToSampleContainerUpdateActivityBuilder()
          .newContainer(detail.getDestinationContainer()));
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(fractionation.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setJustification(null);
    activity.setUpdates(new LinkedList<UpdateActivity>(updates));
    return activity;
  }

  @Override
  public Activity undoErroneous(final Fractionation fractionation, final String justification,
      final Collection<SampleContainer> samplesRemoved) {
    final User user = authorizationService.getCurrentUser();

    // Log update for removed samples.
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    for (SampleContainer container : samplesRemoved) {
      SampleContainer oldContainer = entityManager.find(SampleContainer.class, container.getId());
      updateBuilders.add(
          new RemoveSampleFromSampleContainerUpdateActivityBuilder().oldContainer(oldContainer));
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(fractionation.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setJustification(justification);
    activity.setUpdates(new LinkedList<UpdateActivity>(updates));
    return activity;
  }

  @Override
  public Activity undoFailed(final Fractionation fractionation, String failedDescription,
      final Collection<SampleContainer> bannedContainers) {
    final User user = authorizationService.getCurrentUser();

    // Log update for banned containers.
    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    if (bannedContainers != null) {
      for (SampleContainer container : bannedContainers) {
        SampleContainer oldContainer = entityManager.find(SampleContainer.class, container.getId());
        updateBuilders
            .add(new BanSampleContainerUpdateActivityBuilder().oldContainer(oldContainer));
      }
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    final String justification = DatabaseLogUtil.reduceLength(failedDescription, 255);
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(fractionation.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setJustification(justification);
    activity.setUpdates(new LinkedList<UpdateActivity>(updates));
    return activity;
  }
}