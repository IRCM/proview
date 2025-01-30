package ca.qc.ircm.proview.history;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for history objects.
 */
public class ActivityComparator implements Comparator<Activity>, Serializable {

  @Serial
  private static final long serialVersionUID = 447023857161219684L;

  /**
   * Creates an activity comparator.
   */
  public ActivityComparator() {
  }

  @Override
  public int compare(Activity o1, Activity o2) {
    int compare = o1.getTimestamp().compareTo(o2.getTimestamp());
    compare = compare == 0 ? Long.compare(o1.getId(), o2.getId()) : compare;
    return compare;
  }
}
