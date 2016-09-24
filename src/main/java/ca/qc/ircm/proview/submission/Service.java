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

package ca.qc.ircm.proview.submission;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Protemoics analysis Services.
 */
public enum Service {
  /**
   * LC/MS/MS analysis.
   */
  LC_MS_MS, /**
             * 2D-LC/MS/MS analysis.
             */
  TWO_DIMENSION_LC_MS_MS, /**
                           * Maldi/MS analysis.
                           */
  MALDI_MS, /**
             * Small molecule analysis.
             */
  SMALL_MOLECULE, /**
                   * Intact protein analysis.
                   */
  INTACT_PROTEIN;

  public String getLabel(Locale locale) {
    MessageResource resources = new MessageResource(Service.class, locale);
    return resources.message(name());
  }
}
