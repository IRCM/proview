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

import ca.qc.ircm.proview.sample.SubmissionSampleService.Sort;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for {@link SubmissionSample submitted samples}.
 */
public class SubmissionSampleComparator implements Comparator<SubmissionSample>, Serializable {
  private static final long serialVersionUID = 4809070366723354595L;

  private final Sort sort;
  private final Locale locale;

  public SubmissionSampleComparator(Sort sort, Locale locale) {
    this.sort = sort;
    this.locale = locale;
  }

  @Override
  public int compare(SubmissionSample o1, SubmissionSample o2) {
    Collator collator = Collator.getInstance(locale);
    if (sort != null) {
      switch (sort) {
        case LABORATORY:
          return collator.compare(o1.getLaboratory().getOrganization(),
              o2.getLaboratory().getOrganization());
        case USER:
          return collator.compare(o1.getUser().getEmail(), o2.getUser().getEmail());
        case SUBMISSION:
          return o1.getSubmission().getSubmissionDate()
              .compareTo(o2.getSubmission().getSubmissionDate());
        case LIMS:
          SampleLimsComparator comparator = new SampleLimsComparator(locale);
          return comparator.compare(o1, o2);
        case NAME:
          return collator.compare(o1.getName(), o2.getName());
        case STATUS:
          return o1.getStatus().compareTo(o2.getStatus());
        case SUPPORT:
          return o1.getSupport().compareTo(o2.getSupport());
        default:
          throw new AssertionError("sort " + sort + " not covered in switch");
      }
    } else {
      // Cannot compare.
      return 0;
    }
  }
}