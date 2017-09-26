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

package ca.qc.ircm.proview.dilution;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.DatabaseLogUtil;
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

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link Dilution} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DilutionActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected DilutionActivityService() {
  }

  protected DilutionActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of dilution.
   *
   * @param dilution
   *          inserted dilution
   * @return activity about insertion of dilution
   */
  @CheckReturnValue
  public Activity insert(final Dilution dilution) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(dilution.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(null);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about dilution being marked as erroneous.
   *
   * @param dilution
   *          erroneous dilution that was undone
   * @param explanation
   *          explanation of what was incorrect with the dilution
   * @return activity about dilution being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(final Dilution dilution, final String explanation) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(dilution.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about dilution being marked as failed.
   *
   * @param dilution
   *          failed dilution that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about dilution being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(final Dilution dilution, String failedDescription,
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

    final String explanation = DatabaseLogUtil.reduceLength(failedDescription, 255);
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(dilution.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(new LinkedList<>(updates));
    return activity;
  }
}
