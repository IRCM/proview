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

/**
 * Service class for Sample.
 */
public interface SampleService {
  /**
   * Selects sample from database.
   *
   * @param id
   *          database identifier of sample
   * @return sample
   */
  public Sample get(Long id);

  /**
   * Returns true if sample is linked to some results, false otherwise.
   *
   * @param sample
   *          sample
   * @return true if sample is linked to some results, false otherwise
   */
  public boolean linkedToResults(Sample sample);
}
