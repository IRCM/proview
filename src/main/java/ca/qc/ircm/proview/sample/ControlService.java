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
import ca.qc.ircm.proview.tube.TubeService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
  private TubeService tubeService;
  @Inject
  private AuthorizationService authorizationService;

  protected ControlService() {
  }

  protected ControlService(EntityManager entityManager, JPAQueryFactory queryFactory,
      SampleActivityService sampleActivityService, ActivityService activityService,
      TubeService tubeService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.tubeService = tubeService;
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
    tube.setName(tubeService.generateTubeName(control, new HashSet<String>()));
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
   * @param justification
   *          justification for changes made to sample
   */
  public void update(Control control, String justification) {
    authorizationService.checkAdminRole();

    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(control, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(control);
  }
}
