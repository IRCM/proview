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

package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.ActivateSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.Activity;
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

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link Plate} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PlateActivityService {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(PlateActivityService.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected PlateActivityService() {
  }

  protected PlateActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
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
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(plate.getId());
    activity.setUser(user);
    activity.setTableName("plate");
    activity.setExplanation(null);
    activity.setUpdates(null);
    return activity;
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
  public Activity ban(final Collection<Well> wells, final String explanation) {
    validateSamePlate(wells);
    final User user = authorizationService.getCurrentUser();
    final Plate plate = wells.iterator().next().getPlate();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    for (Well well : wells) {
      Well oldWell = entityManager.find(Well.class, well.getId());
      updateBuilders.add(new BanSampleContainerUpdateActivityBuilder().oldContainer(oldWell));
    }

    // Keep updates that did not change.
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
  public Activity activate(final Collection<Well> wells, final String explanation) {
    validateSamePlate(wells);
    final User user = authorizationService.getCurrentUser();
    Plate plate = wells.iterator().next().getPlate();

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    for (Well well : wells) {
      assert well.getPlate().equals(plate);
      Well oldWell = entityManager.find(Well.class, well.getId());
      updateBuilders.add(new ActivateSampleContainerUpdateActivityBuilder().oldContainer(oldWell));
    }

    // Keep updates that did not change.
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
