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

package ca.qc.ircm.proview.sample;

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
import java.util.List;
import java.util.Optional;

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Creates activities about {@link Sample} that can be recorded.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SampleActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected SampleActivityService() {
  }

  protected SampleActivityService(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates an activity about insertion of control.
   *
   * @param control
   *          inserted control
   * @return activity about insertion of control
   */
  @CheckReturnValue
  public Activity insertControl(final Control control) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(control.getId());
    activity.setUser(user);
    activity.setTableName("sample");
    activity.setJustification(null);
    activity.setUpdates(null);
    return activity;
  }

  /**
   * Creates an activity about update of sample.
   *
   * @param newSample
   *          sample containing new properties/values
   * @param justification
   *          justification for changes made to sample
   * @return activity about update of sample
   */
  @CheckReturnValue
  public Optional<Activity> update(final Sample newSample, final String justification) {
    User user = authorizationService.getCurrentUser();

    final Sample oldSample = entityManager.find(Sample.class, newSample.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<>();
    class SampleUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("sample");
        actionType(ActionType.UPDATE);
        recordId(newSample.getId());
      }
    }

    class ContaminantUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("contaminant");
        actionType(ActionType.UPDATE);
      }

      ContaminantUpdateActivityBuilder oldContaminant(Contaminant oldContaminant) {
        recordId(oldContaminant.getId());
        return this;
      }

      ContaminantUpdateActivityBuilder newContaminant(Contaminant newContaminant) {
        recordId(newContaminant.getId());
        return this;
      }
    }

    class StandardUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("standard");
        actionType(ActionType.UPDATE);
      }

      StandardUpdateActivityBuilder oldStandard(Standard oldStandard) {
        recordId(oldStandard.getId());
        return this;
      }

      StandardUpdateActivityBuilder newStandard(Standard newStandard) {
        recordId(newStandard.getId());
        return this;
      }
    }

    updateBuilders.add(new SampleUpdateActivityBuilder().column("lims")
        .oldValue(oldSample.getLims()).newValue(newSample.getLims()));
    updateBuilders.add(new SampleUpdateActivityBuilder().column("name")
        .oldValue(oldSample.getName()).newValue(newSample.getName()));
    updateBuilders.add(new SampleUpdateActivityBuilder().column("support")
        .oldValue(oldSample.getSupport()).newValue(newSample.getSupport()));
    updateBuilders.add(new SampleUpdateActivityBuilder().column("volume")
        .oldValue(oldSample.getVolume()).newValue(newSample.getVolume()));
    updateBuilders.add(new SampleUpdateActivityBuilder().column("quantity")
        .oldValue(oldSample.getQuantity()).newValue(newSample.getQuantity()));
    // Standards.
    List<Standard> oldStandards =
        oldSample.getStandards() != null ? oldSample.getStandards() : new ArrayList<>();
    List<Standard> newStandards =
        newSample.getStandards() != null ? newSample.getStandards() : new ArrayList<>();
    for (Standard oldStandard : oldStandards) {
      boolean deleted = true;
      for (Standard newStandard : newStandards) {
        if (newStandard.getId().equals(oldStandard.getId())) {
          deleted = false;
        }
      }
      if (deleted) {
        updateBuilders.add(new StandardUpdateActivityBuilder().oldStandard(oldStandard)
            .actionType(ActionType.DELETE));
      }
    }
    for (Standard oldStandard : oldStandards) {
      for (Standard newStandard : newStandards) {
        if (newStandard.getId().equals(oldStandard.getId())) {
          updateBuilders.add(new StandardUpdateActivityBuilder().newStandard(newStandard)
              .column("name").oldValue(oldStandard.getName()).newValue(newStandard.getName()));
          updateBuilders
              .add(new StandardUpdateActivityBuilder().newStandard(newStandard).column("quantity")
                  .oldValue(oldStandard.getQuantity()).newValue(newStandard.getQuantity()));
          updateBuilders
              .add(new StandardUpdateActivityBuilder().newStandard(newStandard).column("comments")
                  .oldValue(oldStandard.getComments()).newValue(newStandard.getComments()));
        }
      }
    }
    for (Standard newStandard : newStandards) {
      boolean inserted = true;
      for (Standard oldStandard : oldStandards) {
        if (newStandard.getId().equals(oldStandard.getId())) {
          inserted = false;
        }
      }
      if (inserted) {
        updateBuilders.add(new StandardUpdateActivityBuilder().newStandard(newStandard)
            .actionType(ActionType.INSERT));
      }
    }
    if (newSample instanceof SubmissionSample) {
      SubmissionSample oldSubmission = (SubmissionSample) oldSample;
      SubmissionSample newSubmission = (SubmissionSample) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("status")
          .oldValue(oldSubmission.getStatus()).newValue(newSubmission.getStatus()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("numberProtein")
          .oldValue(oldSubmission.getNumberProtein())
          .newValue(newSubmission.getNumberProtein()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("molecularWeight")
          .oldValue(oldSubmission.getMolecularWeight())
          .newValue(newSubmission.getMolecularWeight()));
      // Contaminants.
      List<Contaminant> oldContaminants = oldSubmission.getContaminants() != null
          ? oldSubmission.getContaminants() : new ArrayList<>();
      List<Contaminant> newContaminants = newSubmission.getContaminants() != null
          ? newSubmission.getContaminants() : new ArrayList<>();
      for (Contaminant oldContaminant : oldContaminants) {
        boolean deleted = true;
        for (Contaminant newContaminant : newContaminants) {
          if (newContaminant.getId().equals(oldContaminant.getId())) {
            deleted = false;
          }
        }
        if (deleted) {
          updateBuilders.add(new ContaminantUpdateActivityBuilder().oldContaminant(oldContaminant)
              .actionType(ActionType.DELETE));
        }
      }
      for (Contaminant oldContaminant : oldContaminants) {
        for (Contaminant newContaminant : newContaminants) {
          if (newContaminant.getId().equals(oldContaminant.getId())) {
            updateBuilders.add(
                new ContaminantUpdateActivityBuilder().newContaminant(newContaminant).column("name")
                    .oldValue(oldContaminant.getName()).newValue(newContaminant.getName()));
            updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
                .column("quantity").oldValue(oldContaminant.getQuantity())
                .newValue(newContaminant.getQuantity()));
            updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
                .column("comments").oldValue(oldContaminant.getComments())
                .newValue(newContaminant.getComments()));
          }
        }
      }
      for (Contaminant newContaminant : newContaminants) {
        boolean inserted = true;
        for (Contaminant oldContaminant : oldContaminants) {
          if (newContaminant.getId().equals(oldContaminant.getId())) {
            inserted = false;
          }
        }
        if (inserted) {
          updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
              .actionType(ActionType.INSERT));
        }
      }
    }
    if (newSample instanceof Control) {
      Control oldControl = (Control) oldSample;
      Control newControl = (Control) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("controlType")
          .oldValue(oldControl.getControlType()).newValue(newControl.getControlType()));
    }

    // Keep updateBuilders that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(newSample.getId());
      activity.setUser(user);
      activity.setTableName("sample");
      activity.setJustification(justification);
      activity.setUpdates(new LinkedList<>(updates));
      return Optional.of(activity);
    } else {
      return Optional.empty();
    }
  }
}
