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

import ca.qc.ircm.proview.plate.PlateSpot;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for sample containers.
 */
public class SampleContainerComparator implements Comparator<SampleContainer>, Serializable {
  private static final long serialVersionUID = -1704857519143636051L;
  private final Locale locale;

  public SampleContainerComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(SampleContainer o1, SampleContainer o2) {
    Collator collator = Collator.getInstance(locale);
    switch (o1.getType()) {
      case TUBE:
        switch (o2.getType()) {
          case TUBE:
            return collator.compare(o1.getName(), o2.getName());
          case SPOT:
            return -1;
          default:
        }
        break;
      case SPOT:
        switch (o2.getType()) {
          case TUBE:
            return 1;
          case SPOT:
            PlateSpot spot1 = (PlateSpot) o1;
            PlateSpot spot2 = (PlateSpot) o2;
            int compare = Integer.valueOf(spot1.getColumn()).compareTo(spot2.getColumn());
            compare =
                compare == 0 ? Integer.valueOf(spot1.getRow()).compareTo(spot2.getRow()) : compare;
            return compare;
          default:
        }
        break;
      default:
    }
    throw new UnsupportedOperationException("type of one container has an unknown value");
  }
}
