package ca.qc.ircm.proview.plate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for wells.
 */
public class WellComparator implements Comparator<Well>, Serializable {

  @Serial
  static final long serialVersionUID = 4182686766891972608L;

  /**
   * Comparison types.
   */
  public enum Compare {
    /**
     * Compare by location on plate.
     */
    LOCATION,
    /**
     * Compare by modification time.
     */
    TIME_STAMP,
    /**
     * Sort wells to allow auto assignment of samples to well. Well are sorted by column first then
     * by row.
     */
    SAMPLE_ASSIGN
  }

  /**
   * Use {@link ca.qc.ircm.proview.plate.WellComparator.Compare#LOCATION location} comparison by
   * default.
   */
  public WellComparator() {
    this.compare = Compare.LOCATION;
  }

  /**
   * Use specified comparison type.
   *
   * @param compare
   *          comparison type
   * @see ca.qc.ircm.proview.plate.WellComparator.Compare
   * @throws NullPointerException
   *           if compare is null
   */
  public WellComparator(Compare compare) {
    this.compare = compare;
  }

  /**
   * Comparison type.
   *
   * @see ca.qc.ircm.proview.plate.WellComparator.Compare
   */
  private final Compare compare;

  @Override
  public int compare(Well o1, Well o2) {
    switch (compare) {
      case LOCATION: {
        int compare = Integer.compare(o1.getRow(), o2.getRow());
        if (compare == 0) {
          compare = Integer.compare(o1.getColumn(), o2.getColumn());
        }
        return compare;
      }
      case TIME_STAMP:
        return o1.getTimestamp().compareTo(o2.getTimestamp());
      case SAMPLE_ASSIGN: {
        int compare = Integer.compare(o1.getColumn(), o2.getColumn());
        if (compare == 0) {
          compare = Integer.compare(o1.getRow(), o2.getRow());
        }
        return compare;
      }
      default:
        throw new UnsupportedOperationException("Compare has an unknown value.");
    }
  }
}
