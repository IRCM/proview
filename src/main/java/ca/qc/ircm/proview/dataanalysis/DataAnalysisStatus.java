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

package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Completed status.
 */
public enum DataAnalysisStatus {
  /**
   * Data has to be analysed.
   */
  TO_DO,
  /**
   * Data is analysed.
   */
  ANALYSED,
  /**
   * Data analysis was cancelled.
   */
  CANCELLED;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(DataAnalysisStatus.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
