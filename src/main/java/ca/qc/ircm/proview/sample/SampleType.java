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

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Sample support.
 */
public enum SampleType {
  /**
   * Sample is dry.
   */
  DRY,
  /**
   * Sample is in solution.
   */
  SOLUTION,
  /**
   * Sample is in a Gel.
   */
  GEL, BIOID_BEADS, MAGNETIC_BEADS, AGAROSE_BEADS;
  private static AppResources getResources(Locale locale) {
    return new AppResources(SampleType.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }

  public boolean isDry() {
    return this == DRY;
  }

  public boolean isSolution() {
    return this == SOLUTION || this == BIOID_BEADS || this == MAGNETIC_BEADS
        || this == AGAROSE_BEADS;
  }

  public boolean isGel() {
    return this == GEL;
  }

  public boolean isBeads() {
    return this == BIOID_BEADS || this == MAGNETIC_BEADS || this == AGAROSE_BEADS;
  }
}
