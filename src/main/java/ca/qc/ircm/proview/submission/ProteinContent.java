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

import ca.qc.ircm.text.MessageResource;
import java.util.Locale;

/**
 * Protein content of samples.
 */
public enum ProteinContent {
  SMALL(1, 4), MEDIUM(5, 10), LARGE(10, 20), XLARGE(20, Integer.MAX_VALUE);
  ProteinContent(int start, int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Start of interval.
   */
  private int start;
  /**
   * End of interval.
   */
  private int end;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(ProteinContent.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }
}