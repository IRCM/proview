package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.NamedComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Comparator for plates.
 */
public class PlateComparator implements Comparator<Plate>, Serializable {

  static final long serialVersionUID = 2810870614255728640L;

  /**
   * Comparison used.
   */
  public enum Compare {
    /**
     * Compare by name.
     */
    NAME, /**
           * Plate with most empty spot will be first.
           */
    EMPTY_SPOT, /**
                 * Plate with low timestamp will be first.
                 */
    TIME_STAMP;
  }

  /**
   * Compare plate using selected comparison.
   *
   * @param compare
   *          Comparison type see {@link Compare Compare}.
   * @param locale
   *          user's locale
   */
  public PlateComparator(Compare compare, Locale locale) {
    if (compare == null) {
      throw new NullPointerException("compare cannot be null");
    }
    this.compare = compare;
    namedComparator = new NamedComparator(locale);
  }

  /**
   * Type of comparison used.
   */
  private Compare compare;
  private final NamedComparator namedComparator;

  @Override
  public int compare(Plate o1, Plate o2) {
    switch (compare) {
      case NAME:
        return namedComparator.compare(o1, o2);
      case EMPTY_SPOT: {
        int compare = o2.getEmptySpotCount().compareTo(o1.getEmptySpotCount());
        compare = compare == 0 ? namedComparator.compare(o1, o2) : compare;
        return compare;
      }
      case TIME_STAMP: {
        Date min1 = o1.getSpots().get(0).getTimestamp();
        for (PlateSpot spot : o1.getSpots()) {
          min1 = min1.before(spot.getTimestamp()) ? spot.getTimestamp() : min1;
        }
        Date min2 = o2.getSpots().get(0).getTimestamp();
        for (PlateSpot spot : o2.getSpots()) {
          min2 = min2.before(spot.getTimestamp()) ? spot.getTimestamp() : min2;
        }
        int compare = min2.compareTo(min1);
        compare = compare == 0 ? namedComparator.compare(o1, o2) : compare;
        return compare;
      }
      default:
        throw new AssertionError("Case not covered in switch");
    }
  }
}
