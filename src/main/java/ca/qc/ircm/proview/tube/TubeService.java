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

import java.util.Collection;
import java.util.List;

/**
 * Service for digestion tubes.
 */
public interface TubeService {
  /**
   * Selects tube from database.
   *
   * @param id
   *          database identifier of tube
   * @return tube
   */
  public Tube get(Long id);

  /**
   * Returns digestion tube.
   *
   * @param name
   *          tube name.
   * @return digestion tube.
   */
  public Tube get(String name);

  /**
   * Selects sample's original (first) tube. For submitted samples, this returns the tube in which
   * sample was submitted.
   *
   * @param sample
   *          sample
   * @return sample's original tube
   */
  public Tube original(Sample sample);

  /**
   * Selects last tube in which sample was put.
   *
   * @param sample
   *          sample
   * @return last tube in which sample was put
   */
  public Tube last(Sample sample);

  /**
   * <p>
   * Returns digestion tubes used for sample.
   * </p>
   * <p>
   * Tubes are ordered from most recent to older tubes.
   * </p>
   *
   * @param sample
   *          sample.
   * @return digestion tubes used for sample.
   */
  public List<Tube> all(Sample sample);

  /**
   * Selects all tube names beginning with specified string.
   *
   * @param beginning
   *          beginning of tube's name
   * @return all tube names beginning with specified string
   */
  public List<String> selectNameSuggestion(String beginning);

  /**
   * Generates an available tube name for sample. <br>
   * For speed purposes, excludes' contains operation should be fast. Using a Set is recommended.
   *
   * @param sample
   *          sample
   * @param excludes
   *          names to excludes
   * @return available tube name for sample
   */
  public String generateTubeName(Sample sample, Collection<String> excludes);
}
