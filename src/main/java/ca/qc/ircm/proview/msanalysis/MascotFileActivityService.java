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

package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
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

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link MascotFile} and {@link AcquisitionMascotFile} that can be
 * recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class MascotFileActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected MascotFileActivityService() {
  }

  protected MascotFileActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about update of a Mascot file.
   *
   * @param newAcquisitionMascotFile
   *          Mascot file containing new properties/values
   * @return activity about update of a Mascot file
   */
  @CheckReturnValue
  public Optional<Activity> update(final AcquisitionMascotFile newAcquisitionMascotFile) {
    User user = authorizationService.getCurrentUser();

    AcquisitionMascotFile oldAcquisitionMascotFile =
        entityManager.find(AcquisitionMascotFile.class, newAcquisitionMascotFile.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
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
    final Collection<UpdateActivity> updates = new ArrayList<>();
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
      activity.setUpdates(new LinkedList<>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
