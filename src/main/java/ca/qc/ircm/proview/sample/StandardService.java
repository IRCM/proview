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

import static ca.qc.ircm.proview.sample.QSample.sample;

import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for standards.
 */
@Service
@Transactional
public class StandardService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected StandardService() {
  }

  protected StandardService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  private Sample getSample(Standard standard) {
    JPAQuery<Sample> query = queryFactory.select(sample);
    query.from(sample);
    query.where(sample.standards.contains(standard));
    return query.fetchOne();
  }

  /**
   * Selects standard from database.
   *
   * @param id
   *          database identifier of standard
   * @return standard
   */
  public Standard get(Long id) {
    if (id == null) {
      return null;
    }

    Standard standard = entityManager.find(Standard.class, id);
    if (standard != null) {
      Sample sample = getSample(standard);
      authorizationService.checkSampleReadPermission(sample);
    }
    return standard;
  }
}
