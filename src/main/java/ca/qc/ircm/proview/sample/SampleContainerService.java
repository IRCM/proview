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

import java.util.List;

/**
 * Service for sample containers.
 */
public interface SampleContainerService {
  /**
   * Selects sample container from database.
   *
   * @param id
   *          database identifier of sample container
   * @return sample container
   */
  public SampleContainer get(Long id);

  /**
   * Selects last sample container in which sample was.
   *
   * @param sample
   *          sample
   * @return last sample container in which sample was
   */
  public SampleContainer last(Sample sample);

  /**
   * Returns sample containers containing sample.
   *
   * @param sample
   *          sample
   * @return sample containers containing sample
   */
  public List<SampleContainer> all(Sample sample);
}
