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

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for plate's wells.
 */
@Service
@Transactional
public class WellService {
  @Autowired
  private WellRepository wellRepository;

  protected WellService() {
  }

  /**
   * Selects well from database.
   *
   * @param id
   *          database identifier of well
   * @return well
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Well> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return wellRepository.findById(id);
  }
}
