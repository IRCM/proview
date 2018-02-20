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

import static ca.qc.ircm.proview.sample.QSampleContainer.sampleContainer;

import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for sample containers.
 */
@Service
@Transactional
public class SampleContainerService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected SampleContainerService() {
  }

  protected SampleContainerService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects sample container from database.
   *
   * @param id
   *          database identifier of sample container
   * @return sample container
   */
  public SampleContainer get(Long id) {
    if (id == null) {
      return null;
    }

    SampleContainer container = entityManager.find(SampleContainer.class, id);
    if (container != null) {
      authorizationService.checkSampleReadPermission(container.getSample());
    }
    return container;
  }

  /**
   * Selects last sample container in which sample was.
   *
   * @param sample
   *          sample
   * @return last sample container in which sample was
   */
  public SampleContainer last(Sample sample) {
    if (sample == null) {
      return null;
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<SampleContainer> query = queryFactory.select(sampleContainer);
    query.from(sampleContainer);
    query.where(sampleContainer.sample.eq(sample));
    query.orderBy(sampleContainer.timestamp.desc());
    query.limit(1);
    return query.fetchOne();
  }
}
