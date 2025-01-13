package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.NamedComparator;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    NAME,
    /**
     * Plate with most empty well will be first.
     */
    EMPTY_WELL,
    /**
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
      case EMPTY_WELL: {
        int compare = Integer.compare(o2.getEmptyWellCount(), o1.getEmptyWellCount());
        compare = compare == 0 ? namedComparator.compare(o1, o2) : compare;
        return compare;
      }
      case TIME_STAMP: {
        LocalDateTime min1 = o1.getWells().get(0).getTimestamp();
        for (Well well : o1.getWells()) {
          min1 = min1.isBefore(well.getTimestamp()) ? well.getTimestamp() : min1;
        }
        LocalDateTime min2 = o2.getWells().get(0).getTimestamp();
        for (Well well : o2.getWells()) {
          min2 = min2.isBefore(well.getTimestamp()) ? well.getTimestamp() : min2;
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
