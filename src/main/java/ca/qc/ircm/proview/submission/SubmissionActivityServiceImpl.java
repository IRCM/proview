package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@org.springframework.stereotype.Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SubmissionActivityServiceImpl implements SubmissionActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionActivityServiceImpl() {
  }

  protected SubmissionActivityServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Activity insert(final Submission submission) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(submission.getId());
    activity.setUser(user);
    activity.setTableName("submission");
    activity.setJustification(null);
    activity.setUpdates(null);
    return activity;
  }

  @Override
  public Optional<Activity> update(final Submission newSubmission, final String justification) {
    User user = authorizationService.getCurrentUser();

    Submission oldSubmission = entityManager.find(Submission.class, newSubmission.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    class SubmissionUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("submission");
        actionType(ActionType.UPDATE);
        recordId(newSubmission.getId());
      }
    }

    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("userId")
        .oldValue(oldSubmission.getUser().getId()).newValue(newSubmission.getUser().getId()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("laboratoryId")
        .oldValue(oldSubmission.getLaboratory().getId())
        .newValue(newSubmission.getLaboratory().getId()));
    updateBuilders.add(new SubmissionUpdateActivityBuilder().column("submissionDate")
        .oldValue(oldSubmission.getSubmissionDate()).newValue(newSubmission.getSubmissionDate()));

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(newSubmission.getId());
      activity.setUser(user);
      activity.setTableName("submission");
      activity.setJustification(justification);
      activity.setUpdates(new LinkedList<UpdateActivity>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
