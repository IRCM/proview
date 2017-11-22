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

import java.io.IOException;
import java.util.Locale;

/**
 * Renders plate information into an image.
 */
public interface PlateImageRenderer {
  /**
   * Renders an image containing plate information.
   *
   * @param plate
   *          plate
   * @param locale
   *          user's locale
   * @param type
   *          image type
   * @return image containing plate information
   * @throws IOException
   *           could not create image
   */
  public byte[] render(Plate plate, Locale locale, String type) throws IOException;
}
