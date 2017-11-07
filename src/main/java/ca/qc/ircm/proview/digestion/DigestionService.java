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

package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
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
 * Service for Digestion class.
 */
@Service
@Transactional
public class DigestionService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private DigestionProtocolService digestionProtocolService;
  @Inject
  private DigestionActivityService digestionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DigestionService() {
  }

  protected DigestionService(EntityManager entityManager, JPAQueryFactory queryFactory,
      DigestionProtocolService digestionProtocolService,
      DigestionActivityService digestionActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.digestionProtocolService = digestionProtocolService;
    this.digestionActivityService = digestionActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects digestion from database.
   *
   * @param id
   *          database identifier of digestion
   * @return digestion
   */
  public Digestion get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Digestion.class, id);
  }

  /**
   * Inserts a digestion into the database.
   *
   * @param digestion
   *          digestion to insert
   */
  public void insert(Digestion digestion) {
    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(digestion);
    User user = authorizationService.getCurrentUser();

    digestion.setUser(user);
    digestion.setInsertTime(Instant.now());

    if (digestion.getProtocol().getId() == null) {
      digestionProtocolService.insert(digestion.getProtocol());
    }
    entityManager.persist(digestion);
    digestion.getTreatmentSamples().stream().map(ts -> ts.getSample())
        .filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> (SubmissionSample) sample).forEach(sample -> {
          sample.setStatus(SampleStatus.DIGESTED);
          entityManager.merge(sample);
        });

    // Log insertion of digestion.
    entityManager.flush();
    Activity activity = digestionActivityService.insert(digestion);
    activityService.insert(activity);
  }

  /**
   * Updates digestion's information in database.
   *
   * @param digestion
   *          digestion containing new information
   * @param explanation
   *          explanation
   */
  public void update(Digestion digestion, String explanation) {
    authorizationService.checkAdminRole();

    Digestion old = entityManager.find(Digestion.class, digestion.getId());
    Set<Long> digestedSampleIds =
        digestion.getTreatmentSamples().stream().map(ts -> ts.getId()).collect(Collectors.toSet());
    if (old.getTreatmentSamples().stream().filter(ts -> !digestedSampleIds.contains(ts.getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + DigestedSample.class.getSimpleName()
          + " from " + Digestion.class.getSimpleName() + " on update");
    }

    if (digestion.getProtocol().getId() == null) {
      digestionProtocolService.insert(digestion.getProtocol());
    }

    Optional<Activity> activity = digestionActivityService.update(digestion, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(digestion);
  }

  /**
   * Undo erroneous digestion that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * digestion are not the right ones. So, in practice, the digestion never actually occurred.
   *
   * @param digestion
   *          erroneous digestion to undo
   * @param explanation
   *          explanation of what was incorrect with the digestion
   */
  public void undoErroneous(Digestion digestion, String explanation) {
    authorizationService.checkAdminRole();

    digestion.setDeleted(true);
    digestion.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    digestion.setDeletionExplanation(explanation);

    // Log changes.
    Activity activity = digestionActivityService.undoErroneous(digestion, explanation);
    activityService.insert(activity);

    entityManager.merge(digestion);
  }

  /**
   * Report that a problem occurred during digestion causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the digestion was done but the incorrect
   * digestion could only be detected later in the sample processing. Thus the digestion is not
   * undone but flagged as having failed.
   *
   * @param digestion
   *          digestion to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in digestion should be banned, this will also ban any
   *          container were samples were transfered after digestion
   */
  public void undoFailed(Digestion digestion, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    digestion.setDeleted(true);
    digestion.setDeletionType(Treatment.DeletionType.FAILED);
    digestion.setDeletionExplanation(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during digestion.
      for (DigestedSample digestedSample : digestion.getTreatmentSamples()) {
        SampleContainer container = digestedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers where sample were transfered after digestion.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        digestionActivityService.undoFailed(digestion, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(digestion);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
