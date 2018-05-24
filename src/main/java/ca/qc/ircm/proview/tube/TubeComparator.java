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

import ca.qc.ircm.proview.NamedComparator;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * <p>
 * Comparator for digestion tubes.
 * </p>
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals when using other
 * comparison type then NAME.
 * </p>
 */
public class TubeComparator implements Comparator<Tube>, Serializable {

  private static final long serialVersionUID = -3778027579497723915L;

  /**
   * Comparison type.
   */
  public enum Compare {
    /**
     * Compare tubes only by tube name.
     */
    NAME,
    /**
     * <p>
     * Compare tubes by time stamp of their digestions.
     * </p>
     * <p>
     * Comparison will return a negative integer when digestion time stamp is later.
     * </p>
     */
    TIME_STAMP;
  }

  /**
   * Comparison type.
   */
  private final Compare compare;
  private final NamedComparator namedComparator;

  /**
   * Compare digestion tubes.
   *
   * @param locale
   *          user's locale
   * @param compare
   *          comparison type
   */
  public TubeComparator(Locale locale, Compare compare) {
    this.compare = compare;
    namedComparator = new NamedComparator(locale);
  }

  @Override
  public int compare(Tube o1, Tube o2) {
    switch (compare) {
      case NAME: {
        return namedComparator.compare(o1, o2);
      }
      case TIME_STAMP: {
        return o2.getTimestamp().compareTo(o1.getTimestamp());
      }
      default:
        return 0;
    }
  }
}
