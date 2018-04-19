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
 * Services for dilutions.
 */
@Service
@Transactional
public class DilutionService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private DilutionActivityService dilutionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DilutionService() {
  }

  protected DilutionService(EntityManager entityManager, JPAQueryFactory queryFactory,
      DilutionActivityService dilutionActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.dilutionActivityService = dilutionActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects dilution from database.
   *
   * @param id
   *          database identifier of dilution
   * @return dilution
   */
  public Dilution get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Dilution.class, id);
  }

  /**
   * Insert a dilution in database.
   *
   * @param dilution
   *          dilution to insert
   */
  public void insert(Dilution dilution) {
    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(dilution);
    User user = authorizationService.getCurrentUser();

    dilution.setInsertTime(Instant.now());
    dilution.setUser(user);

    entityManager.persist(dilution);

    // Log insertion of dilution.
    entityManager.flush();
    Activity activity = dilutionActivityService.insert(dilution);
    activityService.insert(activity);
  }

  /**
   * Updates dilution's information in database.
   *
   * @param dilution
   *          dilution containing new information
   * @param explanation
   *          explanation
   */
  public void update(Dilution dilution, String explanation) {
    authorizationService.checkAdminRole();

    Dilution old = entityManager.find(Dilution.class, dilution.getId());
    Set<Long> treatedSampleIds =
        dilution.getTreatedSamples().stream().map(ts -> ts.getId()).collect(Collectors.toSet());
    if (old.getTreatedSamples().stream().filter(ts -> !treatedSampleIds.contains(ts.getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + TreatedSample.class.getSimpleName()
          + " from " + Dilution.class.getSimpleName() + " on update");
    }

    Optional<Activity> activity = dilutionActivityService.update(dilution, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(dilution);
  }

  /**
   * Undo dilution.
   *
   * @param dilution
   *          dilution to undo
   * @param explanation
   *          explanation
   * @param banContainers
   *          true if containers used in dilution should be banned, this will also ban any container
   *          were samples were transfered after dilution
   */
  public void undo(Dilution dilution, String explanation, boolean banContainers) {
    authorizationService.checkAdminRole();

    dilution.setDeleted(true);
    dilution.setDeletionExplanation(explanation);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during dilution.
      for (TreatedSample treatedSample : dilution.getTreatedSamples()) {
        SampleContainer container = treatedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after dilution.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity = dilutionActivityService.undoFailed(dilution, explanation, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(dilution);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
