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

import static ca.qc.ircm.proview.sample.QControl.control;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for controls.
 */
@Service
@Transactional
public class ControlService {
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

  protected ControlService() {
  }

  protected ControlService(EntityManager entityManager, JPAQueryFactory queryFactory,
      SampleActivityService sampleActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects control in database.
   *
   * @param id
   *          Database identifier of control
   * @return control in database
   */
  public Control get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Control.class, id);
  }

  /**
   * Returns all controls.
   *
   * @return all controls
   */
  public List<Control> all() {
    authorizationService.checkAdminRole();

    JPAQuery<Control> query = queryFactory.select(control);
    query.from(control);
    return query.fetch();
  }

  /**
   * Returns true if a control with this name is already in database, false otherwise.
   *
   * @param name
   *          name of control
   * @return true if a control with this name is already in database, false otherwise
   */
  public boolean exists(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkUserRole();

    JPAQuery<Long> query = queryFactory.select(control.id);
    query.from(control);
    query.where(control.name.eq(name));
    return query.fetchCount() > 0;
  }

  /**
   * Inserts a control.
   *
   * @param control
   *          control
   */
  public void insert(Control control) {
    authorizationService.checkAdminRole();

    entityManager.persist(control);

    // Insert tube.
    Tube tube = new Tube();
    tube.setSample(control);
    tube.setName(control.getName());
    tube.setTimestamp(Instant.now());
    entityManager.persist(tube);
    control.setOriginalContainer(tube);

    entityManager.flush();
    // Log insertion to database.
    Activity activity = sampleActivityService.insertControl(control);
    activityService.insert(activity);
  }

  /**
   * Updates control information.
   *
   * @param control
   *          control containing new information
   * @param explanation
   *          explanation for changes made to sample
   */
  public void update(Control control, String explanation) {
    authorizationService.checkAdminRole();

    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(control, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(control);
  }
}
