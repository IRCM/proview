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

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service class for Sample.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionSampleService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionSampleService() {
  }

  protected SubmissionSampleService(EntityManager entityManager, JPAQueryFactory queryFactory,
      SampleActivityService sampleActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects submitted sample from database.
   *
   * @param id
   *          database identifier of submitted sample
   * @return submitted sample
   */
  public SubmissionSample get(Long id) {
    if (id == null) {
      return null;
    }

    SubmissionSample sample = entityManager.find(SubmissionSample.class, id);
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  /**
   * Returns true if a sample with this name is already in database, false otherwise.
   *
   * @param name
   *          name of sample
   * @return true if a sample with this name is already in database, false otherwise
   */
  public boolean exists(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkUserRole();

    JPAQuery<Long> query = queryFactory.select(submissionSample.id);
    query.from(submissionSample);
    query.where(submissionSample.name.eq(name));
    return query.fetchCount() > 0;
  }

  /**
   * Update many sample's status.
   *
   * @param samples
   *          samples containing new status
   */
  public void updateStatus(Collection<? extends SubmissionSample> samples) {
    authorizationService.checkAdminRole();

    for (SubmissionSample sample : samples) {
      // Log changes.
      Optional<Activity> activity = sampleActivityService.update(sample, null);
      if (activity.isPresent()) {
        activityService.insert(activity.get());
      }

      entityManager.merge(sample);
    }
  }
}
