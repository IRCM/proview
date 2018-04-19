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

package ca.qc.ircm.proview.standard;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.BanSampleContainerUpdateActivityBuilder;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link StandardAddition} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StandardAdditionActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected StandardAdditionActivityService() {
  }

  protected StandardAdditionActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of addition of standards.
   *
   * @param standardAddition
   *          inserted addition of standards
   * @return activity about insertion of addition of standards
   */
  @CheckReturnValue
  public Activity insert(final StandardAddition standardAddition) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(standardAddition.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(null);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about update of standardAddition.
   *
   * @param standardAddition
   *          standardAddition containing new properties/values
   * @param explanation
   *          explanation
   * @return activity about update of standardAddition
   */
  @CheckReturnValue
  public Optional<Activity> update(StandardAddition standardAddition, String explanation) {
    User user = authorizationService.getCurrentUser();
    final StandardAddition oldStandardAddition =
        entityManager.find(StandardAddition.class, standardAddition.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    Map<Long, TreatedSample> oldTreatedSampleIds = oldStandardAddition.getTreatedSamples()
        .stream().collect(Collectors.toMap(ts -> ts.getId(), ts -> ts));
    standardAddition.getTreatedSamples().stream()
        .filter(ts -> !oldTreatedSampleIds.containsKey(ts.getId()))
        .forEach(ts -> updateBuilders.add(treatedSampleAction(ts, ActionType.INSERT)));
    standardAddition.getTreatedSamples().stream()
        .filter(ts -> oldTreatedSampleIds.containsKey(ts.getId())).forEach(ts -> {
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("sampleId")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getSample().getId())
              .newValue(ts.getSample().getId()));
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("containerId")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getContainer().getId())
              .newValue(ts.getContainer().getId()));
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("name")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getName()).newValue(ts.getName()));
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("quantity")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getQuantity())
              .newValue(ts.getQuantity()));
          updateBuilders.add(treatedSampleAction(ts, ActionType.UPDATE).column("comment")
              .oldValue(oldTreatedSampleIds.get(ts.getId()).getComment())
              .newValue(ts.getComment()));
        });

    List<UpdateActivity> updates = updateBuilders.stream().filter(builder -> builder.isChanged())
        .map(builder -> builder.build()).collect(Collectors.toList());

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(standardAddition.getId());
      activity.setUser(user);
      activity.setTableName("treatment");
      activity.setExplanation(explanation);
      activity.setUpdates(updates);
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }

  private UpdateActivityBuilder treatedSampleAction(TreatedSample ts, ActionType actionType) {
    return new UpdateActivityBuilder().tableName("treatedsample").recordId(ts.getId())
        .actionType(actionType);
  }

  /**
   * Creates an activity about insertion of addition of standards.
   *
   * @param standardAddition
   *          inserted addition of standards
   * @param explanation
   *          explanation of what was incorrect with the addition of standards
   * @return activity about insertion of addition of standards
   */
  @CheckReturnValue
  public Activity undoErroneous(final StandardAddition standardAddition, final String explanation) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(standardAddition.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about addition of standards being marked as failed.
   *
   * @param standardAddition
   *          failed addition of standards that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about addition of standards being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(final StandardAddition standardAddition, String failedDescription,
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

    // Keep updates that changed.
    final List<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    final String explanation = failedDescription;
    Activity activity = new Activity();
    activity.setActionType(ActionType.DELETE);
    activity.setRecordId(standardAddition.getId());
    activity.setUser(user);
    activity.setTableName("treatment");
    activity.setExplanation(explanation);
    activity.setUpdates(updates);
    return activity;
  }
}
