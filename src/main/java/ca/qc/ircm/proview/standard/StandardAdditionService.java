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

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.user.User;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for addition of standards.
 */
@Service
@Transactional
public class StandardAdditionService extends BaseTreatmentService {
  @Inject
  private StandardAdditionRepository repository;
  @Inject
  private StandardAdditionActivityService standardAdditionActivityService;
  @Inject
  private SampleContainerRepository sampleContainerRepository;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected StandardAdditionService() {
  }

  /**
   * Selects standard addition from database.
   *
   * @param id
   *          standard addition's database identifier
   * @return standard addition
   */
  public StandardAddition get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return repository.findOne(id);
  }

  /**
   * Inserts standard addition into database.
   *
   * @param standardAddition
   *          standard addition
   */
  public void insert(StandardAddition standardAddition) {
    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(standardAddition);
    User user = authorizationService.getCurrentUser();

    standardAddition.getTreatedSamples().forEach(ts -> ts.setTreatment(standardAddition));
    standardAddition.setInsertTime(Instant.now());
    standardAddition.setUser(user);

    // Insert standard addition.
    repository.saveAndFlush(standardAddition);

    // Log insertion of addition of standards.
    Activity activity = standardAdditionActivityService.insert(standardAddition);
    activityService.insert(activity);
  }

  /**
   * Updates standard addition's information in database.
   *
   * @param standardAddition
   *          standard addition containing new information
   * @param explanation
   *          explanation
   */
  public void update(StandardAddition standardAddition, String explanation) {
    authorizationService.checkAdminRole();

    StandardAddition old = repository.findOne(standardAddition.getId());
    Set<Long> treatedSampleIds = standardAddition.getTreatedSamples().stream().map(ts -> ts.getId())
        .collect(Collectors.toSet());
    if (old.getTreatedSamples().stream().filter(ts -> !treatedSampleIds.contains(ts.getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + TreatedSample.class.getSimpleName()
          + " from " + StandardAddition.class.getSimpleName() + " on update");
    }

    Optional<Activity> activity =
        standardAdditionActivityService.update(standardAddition, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    repository.save(standardAddition);
  }

  /**
   * Undo standard addition.
   *
   * @param standardAddition
   *          standard addition to undo
   * @param explanation
   *          explanation
   * @param banContainers
   *          true if containers used in standard addition should be banned, this will also ban any
   *          container were samples were transfered after standard addition
   */
  public void undo(StandardAddition standardAddition, String explanation, boolean banContainers) {
    authorizationService.checkAdminRole();

    standardAddition.setDeleted(true);
    standardAddition.setDeletionExplanation(explanation);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during standardAddition.
      for (TreatedSample treatedSample : standardAddition.getTreatedSamples()) {
        SampleContainer container = treatedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after
        // standardAddition.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        standardAdditionActivityService.undoFailed(standardAddition, explanation, bannedContainers);
    activityService.insert(activity);

    repository.save(standardAddition);
    for (SampleContainer container : bannedContainers) {
      sampleContainerRepository.save(container);
    }
  }
}
