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

import static ca.qc.ircm.proview.digestion.QDigestionProtocol.digestionProtocol;
import static ca.qc.ircm.proview.treatment.QProtocol.protocol;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.ProtocolActivityService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for digestion protocol.
 */
@Service
@Transactional
public class DigestionProtocolService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private ProtocolActivityService protocolActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DigestionProtocolService() {
  }

  protected DigestionProtocolService(EntityManager entityManager, JPAQueryFactory queryFactory,
      ProtocolActivityService protocolActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.protocolActivityService = protocolActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects digestion protocol from database.
   *
   * @param id
   *          digestion protocol's object identifier
   * @return digestion protocol
   */
  public DigestionProtocol get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(DigestionProtocol.class, id);
  }

  /**
   * Returns all digestion protocols.
   *
   * @return All digestion protocols.
   */
  public List<DigestionProtocol> all() {
    authorizationService.checkAdminRole();

    JPAQuery<DigestionProtocol> query = queryFactory.select(digestionProtocol);
    query.from(digestionProtocol);
    return query.fetch();
  }

  /**
   * Returns true if digestion protocol's name is available for insertion.
   *
   * @param name
   *          digestion protocol's name
   * @return true if digestion protocol's name is available for insertion
   */
  public boolean availableName(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkAdminRole();

    JPAQuery<Long> query = queryFactory.select(protocol.id);
    query.from(protocol);
    query.where(protocol.name.eq(name));
    return query.fetchCount() == 0;
  }

  /**
   * Inserts digestion protocol into database.
   *
   * @param protocol
   *          digestion protocol
   */
  public void insert(DigestionProtocol protocol) {
    authorizationService.checkAdminRole();

    entityManager.persist(protocol);
    entityManager.flush();

    // Log insertion of protocol.
    Activity activity = protocolActivityService.insert(protocol);
    activityService.insert(activity);
  }
}
