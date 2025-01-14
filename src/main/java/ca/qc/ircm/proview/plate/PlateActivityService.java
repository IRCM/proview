package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.ActivateSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.user.User;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates activities about {@link Plate} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PlateActivityService {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(PlateActivityService.class);
  private final PlateRepository repository;
  private final WellRepository wellRepository;
  private final AuthenticatedUser authenticatedUser;

  @Autowired
  protected PlateActivityService(PlateRepository repository, WellRepository wellRepository,
      AuthenticatedUser authenticatedUser) {
    this.repository = repository;
    this.wellRepository = wellRepository;
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Creates an activity about insertion of plate.
   *
   * @param plate
   *          inserted plate
   * @return activity about insertion of plate
   */
  @CheckReturnValue
  public Activity insert(final Plate plate) {
    User user = authenticatedUser.getUser().orElseThrow();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(plate.getId());
    activity.setUser(user);
    activity.setTableName("plate");
    activity.setExplanation(null);
    activity.setUpdates(new ArrayList<>());
    return activity;
  }

  /**
   * Creates an activity about update of plate.
   *
   * @param plate
   *          updated plate
   * @return activity about update of plate
   */
  @CheckReturnValue
  public Optional<Activity> update(final Plate plate) {
    User user = authenticatedUser.getUser().orElseThrow();

    Plate oldPlate = repository.findById(plate.getId()).orElseThrow();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    updateBuilders.add(updateActivity(plate).column("name").oldValue(oldPlate.getName())
        .newValue(plate.getName()));
    updateBuilders.add(updateActivity(plate).column("columnCount")
        .oldValue(oldPlate.getColumnCount()).newValue(plate.getColumnCount()));
    updateBuilders.add(updateActivity(plate).column("rowCount").oldValue(oldPlate.getRowCount())
        .newValue(plate.getRowCount()));
    updateBuilders.add(updateActivity(plate).column("insertTime").oldValue(oldPlate.getInsertTime())
        .newValue(plate.getInsertTime()));
    updateBuilders.add(updateActivity(plate).column("submission")
        .oldValue(oldPlate.getSubmission() != null ? oldPlate.getSubmission().getId() : null)
        .newValue(plate.getSubmission() != null ? plate.getSubmission().getId() : null));

    // Keep updates that changed.
    final Collection<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(plate.getId());
      activity.setUser(user);
      activity.setTableName(Plate.TABLE_NAME);
      activity.setUpdates(new LinkedList<>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  private UpdateActivityBuilder updateActivity(Plate plate) {
    return new UpdateActivityBuilder().tableName(Plate.TABLE_NAME).actionType(ActionType.UPDATE)
        .recordId(plate.getId());
  }

  private void validateSamePlate(Collection<Well> wells) {
    if (!wells.isEmpty()) {
      long plateId = wells.iterator().next().getPlate().getId();
      for (Well well : wells) {
        if (well.getPlate().getId() != plateId) {
          throw new IllegalArgumentException("Wells are not from the same plates");
        }
      }
    }
  }

  /**
   * Creates an activity about wells being marked as banned.
   *
   * @param wells
   *          wells that were banned
   * @param explanation
   *          explanation for banning wells
   * @return activity about wells being marked as banned
   */
  @CheckReturnValue
  public Activity ban(final Collection<Well> wells, @Nullable final String explanation) {
    validateSamePlate(wells);
    final User user = authenticatedUser.getUser().orElseThrow();
    final Plate plate = wells.iterator().next().getPlate();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    for (Well well : wells) {
      Well oldWell = wellRepository.findById(well.getId()).orElseThrow();
      updateBuilders.add(new BanSampleContainerUpdateActivityBuilder().oldContainer(oldWell));
    }

    // Keep updates that changed.
    final Collection<UpdateActivity> updates = new ArrayList<>();
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
    activity.setExplanation(explanation);
    activity.setUpdates(new LinkedList<>(updates));
    return activity;
  }

  /**
   * Creates an activity about wells being marked as reactivated.
   *
   * @param wells
   *          wells that were reactivated
   * @param explanation
   *          explanation for reactivating wells
   * @return activity about wells being marked as reactivated
   */
  @CheckReturnValue
  public Activity activate(final Collection<Well> wells, @Nullable final String explanation) {
    validateSamePlate(wells);
    final User user = authenticatedUser.getUser().orElseThrow();
    Plate plate = wells.iterator().next().getPlate();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    for (Well well : wells) {
      assert well.getPlate().equals(plate);
      Well oldWell = wellRepository.findById(well.getId()).orElseThrow();
      updateBuilders.add(new ActivateSampleContainerUpdateActivityBuilder().oldContainer(oldWell));
    }

    // Keep updates that changed.
    final Collection<UpdateActivity> updates = new ArrayList<>();
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
    activity.setExplanation(explanation);
    activity.setUpdates(new LinkedList<>(updates));
    return activity;
  }
}
