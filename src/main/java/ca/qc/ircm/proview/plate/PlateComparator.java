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

package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.NamedComparator;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for plates.
 */
public class PlateComparator implements Comparator<Plate>, Serializable {

  static final long serialVersionUID = 2810870614255728640L;

  /**
   * Comparison used.
   */
  public enum Compare {
    /**
     * Compare by name.
     */
    NAME, /**
           * Plate with most empty spot will be first.
           */
    EMPTY_SPOT, /**
                 * Plate with low timestamp will be first.
                 */
    TIME_STAMP;
  }

  /**
   * Compare plate using selected comparison.
   *
   * @param compare
   *          Comparison type see {@link Compare Compare}.
   * @param locale
   *          user's locale
   */
  public PlateComparator(Compare compare, Locale locale) {
    if (compare == null) {
      throw new NullPointerException("compare cannot be null");
    }
    this.compare = compare;
    namedComparator = new NamedComparator(locale);
  }

  /**
   * Type of comparison used.
   */
  private Compare compare;
  private final NamedComparator namedComparator;

  @Override
  public int compare(Plate o1, Plate o2) {
    switch (compare) {
      case NAME:
        return namedComparator.compare(o1, o2);
      case EMPTY_SPOT: {
        int compare = Integer.compare(o2.getEmptySpotCount(), o1.getEmptySpotCount());
        compare = compare == 0 ? namedComparator.compare(o1, o2) : compare;
        return compare;
      }
      case TIME_STAMP: {
        Instant min1 = o1.getSpots().get(0).getTimestamp();
        for (Well spot : o1.getSpots()) {
          min1 = min1.isBefore(spot.getTimestamp()) ? spot.getTimestamp() : min1;
        }
        Instant min2 = o2.getSpots().get(0).getTimestamp();
        for (Well spot : o2.getSpots()) {
          min2 = min2.isBefore(spot.getTimestamp()) ? spot.getTimestamp() : min2;
        }
        int compare = min2.compareTo(min1);
        compare = compare == 0 ? namedComparator.compare(o1, o2) : compare;
        return compare;
      }
      default:
        throw new AssertionError("Case not covered in switch");
    }
  }
}
