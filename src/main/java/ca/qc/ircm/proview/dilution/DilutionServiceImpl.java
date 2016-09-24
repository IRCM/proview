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

import static ca.qc.ircm.proview.dilution.QDilutedSample.dilutedSample;
import static ca.qc.ircm.proview.dilution.QDilution.dilution;

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
 * Default implementation of dilution services.
 */
@Service
@Transactional
public class DilutionServiceImpl extends BaseTreatmentService implements DilutionService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private DilutionActivityService dilutionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DilutionServiceImpl() {
  }

  protected DilutionServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      DilutionActivityService dilutionActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.dilutionActivityService = dilutionActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Dilution get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Dilution.class, id);
  }

  @Override
  public List<Dilution> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Dilution> query = queryFactory.select(dilution);
    query.from(dilution, dilutedSample);
    query.where(dilutedSample._super.in(dilution.treatmentSamples));
    query.where(dilutedSample.sample.eq(sample));
    query.where(dilution.deleted.eq(false));
    return query.distinct().fetch();
  }

  @Override
  public void insert(Dilution dilution) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    dilution.setInsertTime(Instant.now());
    dilution.setUser(user);

    entityManager.persist(dilution);

    // Log insertion of dilution.
    entityManager.flush();
    Activity activity = dilutionActivityService.insert(dilution);
    activityService.insert(activity);
  }

  @Override
  public void undoErroneous(Dilution dilution, String justification) {
    authorizationService.checkAdminRole();

    dilution.setDeleted(true);
    dilution.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    dilution.setDeletionJustification(justification);

    // Log changes.
    Activity activity = dilutionActivityService.undoErroneous(dilution, justification);
    activityService.insert(activity);

    entityManager.merge(dilution);
  }

  @Override
  public void undoFailed(Dilution dilution, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    dilution.setDeleted(true);
    dilution.setDeletionType(Treatment.DeletionType.FAILED);
    dilution.setDeletionJustification(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<SampleContainer>();
    if (banContainers) {
      // Ban containers used during dilution.
      for (DilutedSample dilutedSample : dilution.getTreatmentSamples()) {
        SampleContainer container = dilutedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after dilution.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        dilutionActivityService.undoFailed(dilution, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(dilution);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
