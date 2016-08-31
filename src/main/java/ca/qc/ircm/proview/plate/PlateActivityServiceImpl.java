package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.history.ActivateSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PlateActivityServiceImpl implements PlateActivityService {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(PlateActivityServiceImpl.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected PlateActivityServiceImpl() {
  }

  protected PlateActivityServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Activity insert(final Plate plate) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(plate.getId());
    activity.setUser(user);
    activity.setTableName("plate");
    activity.setJustification(null);
    activity.setUpdates(null);
    return activity;
  }

  private void validateSamePlate(Collection<PlateSpot> spots) {
    if (!spots.isEmpty()) {
      long plateId = spots.iterator().next().getPlate().getId();
      for (PlateSpot spot : spots) {
        if (spot.getPlate().getId() != plateId) {
          throw new IllegalArgumentException("Spots are not from the same plates");
        }
      }
    }
  }

  @Override
  public Activity ban(final Collection<PlateSpot> spots, final String justification) {
    validateSamePlate(spots);
    final User user = authorizationService.getCurrentUser();
    final Plate plate = spots.iterator().next().getPlate();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    for (PlateSpot spot : spots) {
      PlateSpot oldPlateSpot = entityManager.find(PlateSpot.class, spot.getId());
      updateBuilders.add(new BanSampleContainerUpdateActivityBuilder().oldContainer(oldPlateSpot));
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.UPDATE);
    activity.setRecordId(plate.getId());
    activity.setUser(user);
    activity.setTableName("plate");
    activity.setJustification(justification);
    activity.setUpdates(new LinkedList<UpdateActivity>(updates));
    return activity;
  }

  @Override
  public Activity activate(final Collection<PlateSpot> spots, final String justification) {
    validateSamePlate(spots);
    final User user = authorizationService.getCurrentUser();
    Plate plate = spots.iterator().next().getPlate();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    for (PlateSpot spot : spots) {
      assert spot.getPlate().equals(plate);
      PlateSpot oldPlateSpot = entityManager.find(PlateSpot.class, spot.getId());
      updateBuilders
          .add(new ActivateSampleContainerUpdateActivityBuilder().oldContainer(oldPlateSpot));
    }

    // Keep updates that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    Activity activity = new Activity();
    activity.setActionType(ActionType.UPDATE);
    activity.setRecordId(plate.getId());
    activity.setUser(user);
    activity.setTableName("plate");
    activity.setJustification(justification);
    activity.setUpdates(new LinkedList<UpdateActivity>(updates));
    return activity;
  }
}
