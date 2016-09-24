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

package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.sample.SampleLimsComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * Compares 2 sample transfers.
 */
public class SampleTransferComparator implements Comparator<SampleTransfer>, Serializable {
  private static final long serialVersionUID = 8135995214223936190L;

  private final SampleLimsComparator sampleComparator;

  public SampleTransferComparator(Locale locale) {
    sampleComparator = new SampleLimsComparator(locale);
  }

  @Override
  public int compare(SampleTransfer o1, SampleTransfer o2) {
    // Compare sample name first.
    int compare = sampleComparator.compare(o1.getSample(), o2.getSample());
    return compare;
  }

}
