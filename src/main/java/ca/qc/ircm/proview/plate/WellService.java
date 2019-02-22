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

import ca.qc.ircm.proview.security.AuthorizationService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for plate's wells.
 */
@Service
@Transactional
public class WellService {
  @Inject
  private WellRepository wellRepository;
  @Inject
  private AuthorizationService authorizationService;

  protected WellService() {
  }

  /**
   * Selects well from database.
   *
   * @param id
   *          database identifier of well
   * @return well
   */
  public Well get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return wellRepository.findOne(id);
  }
}
