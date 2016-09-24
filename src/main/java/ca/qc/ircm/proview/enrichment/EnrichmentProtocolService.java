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

package ca.qc.ircm.proview.enrichment;

import java.util.List;

/**
 * Services for enrichment protocols.
 */
public interface EnrichmentProtocolService {
  /**
   * Selects enrichment protocol from database.
   *
   * @param id
   *          enrichment protocol's object identifier
   * @return enrichment protocol
   */
  public EnrichmentProtocol get(Long id);

  /**
   * Returns all enrichment protocols.
   *
   * @return All enrichment protocols.
   */
  public List<EnrichmentProtocol> all();

  /**
   * Returns true if enrichment protocol's name is available for insertion.
   *
   * @param name
   *          enrichment protocol's name
   * @return true if enrichment protocol's name is available for insertion
   */
  public boolean availableName(String name);

  /**
   * Inserts enrichment protocol into database.
   *
   * @param protocol
   *          protocol
   */
  public void insert(EnrichmentProtocol protocol);
}
