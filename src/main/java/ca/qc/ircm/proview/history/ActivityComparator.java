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

package ca.qc.ircm.proview.history;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for history objects.
 */
public class ActivityComparator implements Comparator<Activity>, Serializable {
  private static final long serialVersionUID = 447023857161219684L;

  /**
   * How to compare history objects.
   */
  public static enum Compare {
    /**
     * Compare history timestamp.
     */
    TIMESTAMP;
  }

  private final Compare compare;

  /**
   * Creates an activity comparator.
   *
   * @param compare
   *          how to compare activities
   */
  public ActivityComparator(Compare compare) {
    if (compare == null) {
      throw new NullPointerException("compare cannot be null");
    }
    this.compare = compare;
  }

  @Override
  public int compare(Activity o1, Activity o2) {
    int compare;
    switch (this.compare) {
      case TIMESTAMP:
        compare = o1.getTimestamp().compareTo(o2.getTimestamp());
        compare = compare == 0 ? o1.getId().compareTo(o2.getId()) : compare;
        return compare;
      default:
        throw new AssertionError("compare " + this.compare + " not covered in switch case");
    }
  }
}
