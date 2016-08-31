package ca.qc.ircm.proview.plate;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for spots.
 */
public class PlateSpotComparator implements Comparator<PlateSpot>, Serializable {

  static final long serialVersionUID = 4182686766891972608L;

  /**
   * Comparison types.
   */
  public static enum Compare {
    /**
     * Compare by location on plate.
     */
    LOCATION, /**
               * Compare by modification time.
               */
    TIME_STAMP, /**
                 * Sort spots to allow auto assignment of samples to spot. Spot are sorted by column
                 * first then by row.
                 */
    SAMPLE_ASSIGN;
  }

  /**
   * Use {@link ca.qc.ircm.proview.plate.PlateSpotComparator.Compare#LOCATION location} comparison
   * by default.
   */
  public PlateSpotComparator() {
    this.compare = Compare.LOCATION;
  }

  /**
   * Use specified comparison type.
   *
   * @param compare
   *          comparison type
   * @see ca.qc.ircm.proview.plate.PlateSpotComparator.Compare
   * @throws NullPointerException
   *           if compare is null
   */
  public PlateSpotComparator(Compare compare) {
    if (compare == null) {
      throw new NullPointerException("compare cannot be null");
    }
    this.compare = compare;
  }

  /**
   * Comparison type.
   *
   * @see ca.qc.ircm.proview.plate.PlateSpotComparator.Compare
   */
  private final Compare compare;

  @Override
  public int compare(PlateSpot o1, PlateSpot o2) {
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
