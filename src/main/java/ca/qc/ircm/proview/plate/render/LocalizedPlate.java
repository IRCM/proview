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

package ca.qc.ircm.proview.plate.render;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;

/**
 * Get localized plate information for plate PDF.
 */
public class LocalizedPlate {
  /**
   * Get header for table containing plate information.
   *
   * @param plate
   *          Plate.
   * @return Header.
   */
  public String getHeader(Plate plate) {
    return plate.getName();
  }

  /**
   * Get well information.
   *
   * @param well
   *          well
   * @return well information
   */
  public String getWell(Well well) {
    return well.getName();
  }

  /**
   * Get well's sample name.
   *
   * @param well
   *          well
   * @return well's sample name
   */
  public String getSampleName(Well well) {
    if (well.getSample() != null) {
      return well.getSample().getName();
    } else {
      return null;
    }
  }
}
