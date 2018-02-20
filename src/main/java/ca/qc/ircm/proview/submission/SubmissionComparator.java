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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for submissions.
 */
public class SubmissionComparator implements Comparator<Submission>, Serializable {
  private static final long serialVersionUID = -151938109581440731L;

  @Override
  public int compare(Submission o1, Submission o2) {
    return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
  }
}
