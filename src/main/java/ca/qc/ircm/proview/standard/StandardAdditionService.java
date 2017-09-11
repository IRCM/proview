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

import static ca.qc.ircm.proview.standard.QAddedStandard.addedStandard;
import static ca.qc.ircm.proview.standard.QStandardAddition.standardAddition;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for addition of standards.
 */
@Service
@Transactional
public class StandardAdditionService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private StandardAdditionActivityService standardAdditionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected StandardAdditionService() {
  }

  protected StandardAdditionService(EntityManager entityManager, JPAQueryFactory queryFactory,
      StandardAdditionActivityService standardAdditionActivityService,
      ActivityService activityService, AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.standardAdditionActivityService = standardAdditionActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
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

    return entityManager.find(StandardAddition.class, id);
  }

  /**
   * Returns all standard additions done on any of submission's samples.
   *
   * @param submission
   *          submission
   * @return all standard additions done on any of submission's samples
   */
  public List<StandardAddition> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<StandardAddition> query = queryFactory.select(standardAddition);
    query.from(standardAddition, addedStandard);
    query.where(addedStandard._super.in(standardAddition.treatmentSamples));
    query.where(addedStandard.sample.in(submission.getSamples()));
    query.where(standardAddition.deleted.eq(false));
    return query.distinct().fetch();
  }

  /**
   * Inserts standard addition into database.
   *
   * @param standardAddition
   *          standard addition
   */
  public void insert(StandardAddition standardAddition) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    standardAddition.setInsertTime(Instant.now());
    standardAddition.setUser(user);

    // Insert standard addition.
    entityManager.persist(standardAddition);

    // Log insertion of addition of standards.
    entityManager.flush();
    Activity activity = standardAdditionActivityService.insert(standardAddition);
    activityService.insert(activity);
  }

  /**
   * Undo erroneous standard addition that never actually occurred. This method is usually called
   * shortly after action was inserted into the database. The user realises that the samples checked
   * for standard addition are not the right ones. So, in practice, the standard addition never
   * actually occurred.
   *
   * @param standardAddition
   *          erroneous standard addition to undo
   * @param justification
   *          explanation of what was incorrect with the standard addition
   */
  public void undoErroneous(StandardAddition standardAddition, String justification) {
    authorizationService.checkAdminRole();

    standardAddition.setDeleted(true);
    standardAddition.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    standardAddition.setDeletionJustification(justification);

    // Log changes.
    Activity activity =
        standardAdditionActivityService.undoErroneous(standardAddition, justification);
    activityService.insert(activity);

    entityManager.merge(standardAddition);
  }

  /**
   * Report that a problem occurred during standard addition causing it to fail. Problems usually
   * occur because of an experimental error. In this case, the standard addition was done but the
   * incorrect standard addition could only be detected later in the sample processing. Thus the
   * standard addition is not undone but flagged as having failed.
   *
   * @param standardAddition
   *          standard addition to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in standard addition should be banned, this will also ban any
   *          container were samples were transfered after standard addition
   */
  public void undoFailed(StandardAddition standardAddition, String failedDescription,
      boolean banContainers) {
    authorizationService.checkAdminRole();

    standardAddition.setDeleted(true);
    standardAddition.setDeletionType(Treatment.DeletionType.FAILED);
    standardAddition.setDeletionJustification(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during standardAddition.
      for (AddedStandard addedStandard : standardAddition.getTreatmentSamples()) {
        SampleContainer container = addedStandard.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after
        // standardAddition.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity = standardAdditionActivityService.undoFailed(standardAddition,
        failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(standardAddition);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
