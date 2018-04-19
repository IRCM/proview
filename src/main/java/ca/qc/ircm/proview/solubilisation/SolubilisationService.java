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

package ca.qc.ircm.proview.solubilisation;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for solubilisation.
 */
@Service
@Transactional
public class SolubilisationService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private SolubilisationActivityService solubilisationActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected SolubilisationService() {
  }

  protected SolubilisationService(EntityManager entityManager, JPAQueryFactory queryFactory,
      SolubilisationActivityService solubilisationActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.solubilisationActivityService = solubilisationActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects solubilisation from database.
   *
   * @param id
   *          solubilisation's database identifier
   * @return solubilisation
   */
  public Solubilisation get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Solubilisation.class, id);
  }

  /**
   * Inserts solubilisation into database.
   *
   * @param solubilisation
   *          solubilisation
   */
  public void insert(Solubilisation solubilisation) {
    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(solubilisation);
    User user = authorizationService.getCurrentUser();

    solubilisation.setInsertTime(Instant.now());
    solubilisation.setUser(user);

    entityManager.persist(solubilisation);

    // Log insertion of solubilisation.
    entityManager.flush();
    Activity activity = solubilisationActivityService.insert(solubilisation);
    activityService.insert(activity);
  }

  /**
   * Updates solubilisation's information in database.
   *
   * @param solubilisation
   *          solubilisation containing new information
   * @param explanation
   *          explanation
   */
  public void update(Solubilisation solubilisation, String explanation) {
    authorizationService.checkAdminRole();

    Solubilisation old = entityManager.find(Solubilisation.class, solubilisation.getId());
    Set<Long> treatedSampleIds = solubilisation.getTreatedSamples().stream()
        .map(ts -> ts.getId()).collect(Collectors.toSet());
    if (old.getTreatedSamples().stream().filter(ts -> !treatedSampleIds.contains(ts.getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + TreatedSample.class.getSimpleName()
          + " from " + Solubilisation.class.getSimpleName() + " on update");
    }

    Optional<Activity> activity = solubilisationActivityService.update(solubilisation, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(solubilisation);
  }

  /**
   * Undo solubilisation.
   *
   * @param solubilisation
   *          solubilisation to undo
   * @param explanation
   *          explanation
   * @param banContainers
   *          true if containers used in solubilisation should be banned, this will also ban any
   *          container were samples were transfered after solubilisation
   */
  public void undo(Solubilisation solubilisation, String explanation, boolean banContainers) {
    authorizationService.checkAdminRole();

    solubilisation.setDeleted(true);
    solubilisation.setDeletionExplanation(explanation);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during solubilisation.
      for (TreatedSample treatedSample : solubilisation.getTreatedSamples()) {
        SampleContainer container = treatedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after solubilisation.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        solubilisationActivityService.undoFailed(solubilisation, explanation, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(solubilisation);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
