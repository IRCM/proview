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

package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for protocols.
 */
@Service
@Transactional
public class ProtocolService {
  @Autowired
  private ProtocolRepository repository;

  protected ProtocolService() {
  }

  /**
   * Selects protocol from database.
   *
   * @param id
   *          database identifier of protocol
   * @return protocol
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Protocol> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Returns all protocols of specified type.
   *
   * @param type
   *          protocol type
   * @return all protocols of specified type
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Protocol> all(Protocol.Type type) {
    return repository.findByType(type);
  }
}
