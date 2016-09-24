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

import java.util.List;

/**
 * Services for digestion protocol.
 */
public interface DigestionProtocolService {
  /**
   * Selects digestion protocol from database.
   *
   * @param id
   *          digestion protocol's object identifier
   * @return digestion protocol
   */
  public DigestionProtocol get(Long id);

  /**
   * Returns all digestion protocols.
   *
   * @return All digestion protocols.
   */
  public List<DigestionProtocol> all();

  /**
   * Returns true if digestion protocol's name is available for insertion.
   *
   * @param name
   *          digestion protocol's name
   * @return true if digestion protocol's name is available for insertion
   */
  public boolean availableName(String name);

  /**
   * Inserts digestion protocol into database.
   *
   * @param protocol
   *          digestion protocol
   */
  public void insert(DigestionProtocol protocol);
}
