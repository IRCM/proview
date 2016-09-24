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

import static ca.qc.ircm.proview.solubilisation.QSolubilisation.solubilisation;
import static ca.qc.ircm.proview.solubilisation.QSolubilisedSample.solubilisedSample;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
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
 * Default implementation of solubilisation services.
 */
@Service
@Transactional
public class SolubilisationServiceImpl extends BaseTreatmentService
    implements SolubilisationService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private SolubilisationActivityService solubilisationActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected SolubilisationServiceImpl() {
  }

  protected SolubilisationServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      SolubilisationActivityService solubilisationActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.solubilisationActivityService = solubilisationActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Solubilisation get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Solubilisation.class, id);
  }

  @Override
  public List<Solubilisation> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Solubilisation> query = queryFactory.select(solubilisation);
    query.from(solubilisation, solubilisedSample);
    query.where(solubilisedSample._super.in(solubilisation.treatmentSamples));
    query.where(solubilisedSample.sample.eq(sample));
    query.where(solubilisation.deleted.eq(false));
    return query.distinct().fetch();
  }

  @Override
  public void insert(Solubilisation solubilisation) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    solubilisation.setInsertTime(Instant.now());
    solubilisation.setUser(user);

    entityManager.persist(solubilisation);

    // Log insertion of solubilisation.
    entityManager.flush();
    Activity activity = solubilisationActivityService.insert(solubilisation);
    activityService.insert(activity);
  }

  @Override
  public void undoErroneous(Solubilisation solubilisation, String justification) {
    authorizationService.checkAdminRole();

    solubilisation.setDeleted(true);
    solubilisation.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    solubilisation.setDeletionJustification(justification);

    // Log changes.
    Activity activity = solubilisationActivityService.undoErroneous(solubilisation, justification);
    activityService.insert(activity);

    entityManager.merge(solubilisation);
  }

  @Override
  public void undoFailed(Solubilisation solubilisation, String failedDescription,
      boolean banContainers) {
    authorizationService.checkAdminRole();

    solubilisation.setDeleted(true);
    solubilisation.setDeletionType(Treatment.DeletionType.FAILED);
    solubilisation.setDeletionJustification(failedDescription);

    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during solubilisation.
      for (SolubilisedSample solubilisedSample : solubilisation.getTreatmentSamples()) {
        SampleContainer container = solubilisedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after solubilisation.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity = solubilisationActivityService.undoFailed(solubilisation, failedDescription,
        bannedContainers);
    activityService.insert(activity);

    entityManager.merge(solubilisation);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
