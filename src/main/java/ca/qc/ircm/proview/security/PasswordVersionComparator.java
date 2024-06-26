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

package ca.qc.ircm.proview.security;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for password versions.
 */
public class PasswordVersionComparator implements Comparator<PasswordVersion>, Serializable {
  private static final long serialVersionUID = -7244984829370331243L;

  public PasswordVersionComparator() {
  }

  @Override
  public int compare(PasswordVersion o1, PasswordVersion o2) {
    return o1.version() - o2.version();
  }
}
