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

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for sample tubes.
 */
@Service
@Transactional
public class TubeService {
  @Inject
  private TubeRepository repository;
  @Inject
  private AuthorizationService authorizationService;

  protected TubeService() {
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

    Tube tube = repository.findOne(id);
    if (tube != null) {
      authorizationService.checkSampleReadPermission(tube.getSample());
    }
    return tube;
  }

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          tube's name
   * @return true if name is available in database, false otherwise
   */
  public boolean nameAvailable(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkAdminRole();

    return repository.countByName(name) == 0;
  }

  /**
   * Returns tubes used for sample.
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

    return repository.findBySample(sample);
  }
}
