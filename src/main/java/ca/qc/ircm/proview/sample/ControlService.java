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

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for controls.
 */
@Service
@Transactional
public class ControlService {
  @Inject
  private ControlRepository repository;
  @Inject
  private TubeRepository tubeRepository;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;

  protected ControlService() {
  }

  /**
   * Selects control in database.
   *
   * @param id
   *          Database identifier of control
   * @return control in database
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Control get(Long id) {
    if (id == null) {
      return null;
    }

    return repository.findById(id).orElse(null);
  }

  /**
   * Returns all controls.
   *
   * @return all controls
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Control> all() {
    return repository.findAll();
  }

  /**
   * Returns true if a control with this name is already in database, false otherwise.
   *
   * @param name
   *          name of control
   * @return true if a control with this name is already in database, false otherwise
   */
  @PreAuthorize("hasAuthority('" + USER + "')")
  public boolean exists(String name) {
    if (name == null) {
      return false;
    }

    return repository.countByName(name) > 0;
  }

  /**
   * Inserts a control.
   *
   * @param control
   *          control
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void insert(Control control) {
    // Insert tube.
    Tube tube = new Tube();
    tube.setSample(control);
    tube.setName(control.getName());
    tube.setTimestamp(LocalDateTime.now());
    control.setOriginalContainer(tube);

    repository.save(control);
    tubeRepository.saveAndFlush(tube);
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
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void update(Control control, String explanation) {
    // Log changes.
    Optional<Activity> activity = sampleActivityService.update(control, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    repository.save(control);
  }
}
