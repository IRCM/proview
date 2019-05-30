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

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.sample.Sample;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

  protected TubeService() {
  }

  /**
   * Selects tube from database.
   *
   * @param id
   *          database identifier of tube
   * @return tube
   */
  @PostAuthorize("returnObject == null || hasPermission(returnObject.sample, 'read')")
  public Tube get(Long id) {
    if (id == null) {
      return null;
    }

    return repository.findOne(id);
  }

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          tube's name
   * @return true if name is available in database, false otherwise
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public boolean nameAvailable(String name) {
    if (name == null) {
      return false;
    }

    return repository.countByName(name) == 0;
  }

  /**
   * Returns tubes used for sample.
   *
   * @param sample
   *          sample.
   * @return digestion tubes used for sample.
   */
  @PreAuthorize("hasPermission(#sample, 'read')")
  public List<Tube> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }

    return repository.findBySample(sample);
  }
}
