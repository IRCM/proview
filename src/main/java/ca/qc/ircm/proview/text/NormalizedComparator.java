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

package ca.qc.ircm.proview.text;

import static ca.qc.ircm.proview.text.Strings.normalize;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * Comparator that normalizes string before comparison.
 */
public class NormalizedComparator implements Comparator<String>, Serializable {
  private static final long serialVersionUID = -3243433160557573573L;

  @Override
  public int compare(String o1, String o2) {
    o1 = Objects.toString(o1, "");
    o2 = Objects.toString(o2, "");
    return normalize(o1).compareToIgnoreCase(normalize(o2));
  }
}
