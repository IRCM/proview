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

import ca.qc.ircm.proview.sample.SampleLimsComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for treated samples by their LIMS.
 */
public class TreatmentSampleLimsComparator implements Comparator<TreatmentSample>, Serializable {
  private static final long serialVersionUID = -5024911239066204507L;
  private final SampleLimsComparator sampleLimsComparator;

  public TreatmentSampleLimsComparator(Locale locale) {
    sampleLimsComparator = new SampleLimsComparator(locale);
  }

  @Override
  public int compare(TreatmentSample o1, TreatmentSample o2) {
    return sampleLimsComparator.compare(o1.getSample(), o2.getSample());
  }
}
