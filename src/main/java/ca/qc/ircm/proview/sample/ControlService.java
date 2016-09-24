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
 * Service class for controls.
 */
public interface ControlService {
  /**
   * Selects control in database.
   *
   * @param id
   *          Database identifier of control
   * @return control in database
   */
  public Control get(Long id);

  /**
   * Inserts a control.
   *
   * @param control
   *          control
   */
  public void insert(Control control);

  /**
   * Updates control information.
   *
   * @param control
   *          control containing new information
   * @param justification
   *          justification for changes made to sample
   */
  public void update(Control control, String justification);
}
