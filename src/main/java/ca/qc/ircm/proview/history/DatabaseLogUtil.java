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

import java.util.Arrays;

/**
 * Utilities for database log.
 */
public class DatabaseLogUtil {
  /**
   * A boolean values as it is represented in the database.
   */
  public static enum DatabaseBoolean {
    FALSE("0"), TRUE("1");
    public final String databaseValue;

    DatabaseBoolean(String databaseValue) {
      this.databaseValue = databaseValue;
    }

    /**
     * Returns database value for boolean.
     *
     * @param value
     *          boolean
     * @return database value for boolean
     */
    public static DatabaseBoolean get(boolean value) {
      if (value) {
        return DatabaseBoolean.TRUE;
      } else {
        return DatabaseBoolean.FALSE;
      }
    }
  }

  /**
   * Returns true if 2 objects are equal, false otherwise. 2 objects are equal if they are both null
   * or if both are not null and <code>oldValue.equals(newValue)</code> returns true. If oldValue
   * and newValue are arrays, arrays content are compared with
   * <code>Arrays.equals((Object[])oldValue, (Object[])newValue)</code> rather than
   * <code>oldValue.equals(newValue)</code>.
   *
   * @param first
   *          first object
   * @param second
   *          second object
   * @return true if 2 objects are equal, false otherwise
   */
  public static boolean equals(Object first, Object second) {
    boolean same = false;
    same |= first == null && second == null;
    if (first != null && second != null) {
      if (first.getClass().isArray() && second.getClass().isArray()) {
        same |= Arrays.equals((Object[]) first, (Object[]) second);
      } else {
        same |= first.equals(second);
      }
    }
    return same;
  }
}
