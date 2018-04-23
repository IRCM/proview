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

package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.treatment.QTreatedSample.treatedSample;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for treatments.
 */
@Service
@Transactional
public class TreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected TreatmentService() {
  }

  protected TreatmentService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects treatment from database.
   *
   * @param id
   *          database identifier of treatment
   * @return treatment
   */
  public Treatment get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Treatment.class, id);
  }

  /**
   * Returns all treatments where one of the submission's samples was treated.
   *
   * @param submission
   *          submission
   * @return all treatments where one of the submission's samples was treated
   */
  public List<Treatment> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Treatment> query = queryFactory.select(treatment);
    query.from(treatment, treatedSample);
    query.where(treatedSample.in(treatment.treatedSamples));
    query.where(treatedSample.sample.in(submission.getSamples()));
    query.where(treatment.deleted.eq(false));
    return query.distinct().fetch();
  }
}
