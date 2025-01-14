package ca.qc.ircm.proview.history;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for history objects.
 */
public class ActivityComparator implements Comparator<Activity>, Serializable {
  private static final long serialVersionUID = 447023857161219684L;

  /**
   * How to compare history objects.
   */
  public static enum Compare {
    /**
     * Compare history timestamp.
     */
    TIMESTAMP;
  }

  private final Compare compare;

  /**
   * Creates an activity comparator.
   *
   * @param compare
   *          how to compare activities
   */
  public ActivityComparator(Compare compare) {
    this.compare = compare;
  }

  @Override
  public int compare(Activity o1, Activity o2) {
    int compare;
    switch (this.compare) {
      case TIMESTAMP:
        compare = o1.getTimestamp().compareTo(o2.getTimestamp());
        compare = compare == 0 ? Long.compare(o1.getId(), o2.getId()) : compare;
        return compare;
      default:
        throw new AssertionError("compare " + this.compare + " not covered in switch case");
    }
  }
}
