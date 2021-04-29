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

package ca.qc.ircm.proview.treatment;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for treated samples by their LIMS.
 */
public class TreatmentComparator implements Comparator<Treatment>, Serializable {
  public static enum TreatmentCompareType {
    TIMESTAMP;
  }

  private static final long serialVersionUID = -7443140194033594333L;
  private TreatmentCompareType compare;

  /**
   * Creates comparator for treatments.
   *
   * @param compare
   *          how to compare treatments
   */
  public TreatmentComparator(TreatmentCompareType compare) {
    if (compare == null) {
      throw new NullPointerException("compare cannot be null");
    }
    this.compare = compare;
  }

  @Override
  public int compare(Treatment o1, Treatment o2) {
    switch (compare) {
      case TIMESTAMP:
        return o1.getInsertTime().compareTo(o2.getInsertTime());
      default:
        throw new AssertionError("TreatmentCompareType " + compare + " not covered in switch");
    }
  }
}
