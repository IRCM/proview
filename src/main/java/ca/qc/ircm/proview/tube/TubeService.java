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

package ca.qc.ircm.proview.tube;

import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.tube.QTube.tube;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for sample tubes.
 */
@Service
@Transactional
public class TubeService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected TubeService() {
  }

  protected TubeService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects tube from database.
   *
   * @param id
   *          database identifier of tube
   * @return tube
   */
  public Tube get(Long id) {
    if (id == null) {
      return null;
    }

    Tube tube = entityManager.find(Tube.class, id);
    if (tube != null) {
      authorizationService.checkSampleReadPermission(tube.getSample());
    }
    return tube;
  }

  /**
   * Returns digestion tube.
   *
   * @param name
   *          tube name.
   * @return digestion tube.
   */
  public Tube get(String name) {
    if (name == null) {
      return null;
    }

    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(tube);
    query.where(tube.name.eq(name));
    Tube ret = query.fetchOne();
    if (ret != null) {
      authorizationService.checkSampleReadPermission(ret.getSample());
    }
    return ret;
  }

  /**
   * Selects sample's original (first) tube. For submitted samples, this returns the tube in which
   * sample was submitted.
   *
   * @param sampleParam
   *          sample
   * @return sample's original tube
   */
  public Tube original(Sample sampleParam) {
    if (sampleParam == null) {
      return null;
    }

    authorizationService.checkSampleReadPermission(sampleParam);
    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(sample, tube);
    query.where(tube.eq(sample.originalContainer));
    query.where(sample.eq(sampleParam));
    return query.fetchOne();
  }

  /**
   * Selects last tube in which sample was put.
   *
   * @param sample
   *          sample
   * @return last tube in which sample was put
   */
  public Tube last(Sample sample) {
    if (sample == null) {
      return null;
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(tube);
    query.where(tube.sample.eq(sample));
    query.orderBy(tube.timestamp.desc());
    query.limit(1);
    return query.fetchOne();
  }

  /**
   * <p>
   * Returns digestion tubes used for sample.
   * </p>
   * <p>
   * Tubes are ordered from most recent to older tubes.
   * </p>
   *
   * @param sample
   *          sample.
   * @return digestion tubes used for sample.
   */
  public List<Tube> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(tube);
    query.where(tube.sample.eq(sample));
    return query.fetch();
  }

  /**
   * Selects all tube names beginning with specified string.
   *
   * @param beginning
   *          beginning of tube's name
   * @return all tube names beginning with specified string
   */
  public List<String> selectNameSuggestion(String beginning) {
    if (beginning == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<String> query = queryFactory.select(tube.name);
    query.from(tube);
    query.where(tube.name.startsWith(beginning));
    return query.fetch();
  }

  private boolean exists(String name) {
    JPAQuery<Long> query = queryFactory.select(tube.id);
    query.from(tube);
    query.where(tube.name.eq(name));
    return query.fetchCount() > 0;
  }

  /**
   * Generates an available tube name for sample. <br>
   * For speed purposes, excludes' contains operation should be fast. Using a Set is recommended.
   *
   * @param sample
   *          sample
   * @param excludes
   *          names to excludes
   * @return available tube name for sample
   */
  public String generateTubeName(Sample sample, Collection<String> excludes) {
    if (sample == null) {
      return null;
    }
    if (excludes == null) {
      excludes = Collections.emptySet();
    }
    authorizationService.checkUserRole();

    if (!exists(sample.getName()) && !excludes.contains(sample.getName())) {
      return sample.getName();
    }

    int index = 0;
    String name = sample.getName() + "_" + ++index;
    while (exists(name) || excludes.contains(name)) {
      name = sample.getName() + "_" + ++index;
    }
    return name;
  }
}
