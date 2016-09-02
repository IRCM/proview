package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
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
public class MascotFileActivityServiceImpl implements MascotFileActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected MascotFileActivityServiceImpl() {
  }

  protected MascotFileActivityServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Optional<Activity> update(final AcquisitionMascotFile newAcquisitionMascotFile) {
    User user = authorizationService.getCurrentUser();

    AcquisitionMascotFile oldAcquisitionMascotFile =
        entityManager.find(AcquisitionMascotFile.class, newAcquisitionMascotFile.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    class AcquisitionToMascotFileUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("acquisition_to_mascotfile");
        actionType(ActionType.UPDATE);
        recordId(newAcquisitionMascotFile.getId());
      }
    }

    updateBuilders.add(new AcquisitionToMascotFileUpdateActivityBuilder().column("visible")
        .oldValue(oldAcquisitionMascotFile.isVisible())
        .newValue(newAcquisitionMascotFile.isVisible()));
    updateBuilders.add(new AcquisitionToMascotFileUpdateActivityBuilder().column("comments")
        .oldValue(oldAcquisitionMascotFile.getComments())
        .newValue(newAcquisitionMascotFile.getComments()));

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
      activity.setRecordId(newAcquisitionMascotFile.getId());
      activity.setUser(user);
      activity.setTableName("acquisition_to_mascotfile");
      activity.setJustification(null);
      activity.setUpdates(new LinkedList<UpdateActivity>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
