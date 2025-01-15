package ca.qc.ircm.proview.history;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.lang.Nullable;

/**
 * Utilities for database log.
 */
public class DatabaseLogUtil {
  /**
   * A boolean values as it is represented in the database.
   */
  public enum DatabaseBoolean {
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
  public static boolean equals(@Nullable Object first, @Nullable Object second) {
    boolean same = false;
    same |= first == null && second == null;
    if (first != null && second != null) {
      if (first.getClass().isArray() && second.getClass().isArray()) {
        same |= Arrays.equals((Object[]) first, (Object[]) second);
      } else if (first instanceof Collection && second instanceof Collection) {
        same |= Arrays.equals(((Collection<?>) first).toArray(new Object[0]),
            ((Collection<?>) second).toArray(new Object[0]));
      } else {
        same |= first.equals(second);
      }
    }
    return same;
  }
}
