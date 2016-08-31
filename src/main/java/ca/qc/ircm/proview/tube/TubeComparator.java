package ca.qc.ircm.proview.tube;

import ca.qc.ircm.proview.NamedComparator;
import ca.qc.ircm.proview.sample.SampleLimsComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * <p>
 * Comparator for digestion tubes.
 * </p>
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals when using other
 * comparison type then NAME.
 * </p>
 */
public class TubeComparator implements Comparator<Tube>, Serializable {

  private static final long serialVersionUID = -3778027579497723915L;

  /**
   * Comparison type.
   */
  public enum Compare {
    /**
     * Compare tubes only by tube name.
     */
    NAME, /**
           * Compare tubes by sample name, then tube name.
           */
    SAMPLE, /**
             * <p>
             * Compare tubes by time stamp of their digestions.
             * </p>
             * <p>
             * Comparison will return a negative integer when digestion time stamp is later.
             * </p>
             */
    TIME_STAMP;
  }

  /**
   * Comparison type.
   */
  private final Compare compare;
  private final NamedComparator namedComparator;
  private final SampleLimsComparator sampleComparator;

  /**
   * Compare digestion tubes.
   * 
   * @param locale
   *          user's locale
   * @param compare
   *          comparison type
   */
  public TubeComparator(Locale locale, Compare compare) {
    this.compare = compare;
    sampleComparator = new SampleLimsComparator(locale);
    namedComparator = new NamedComparator(locale);
  }

  @Override
  public int compare(Tube o1, Tube o2) {
    switch (compare) {
      case NAME: {
        return namedComparator.compare(o1, o2);
      }
      case SAMPLE: {
        int compare = sampleComparator.compare(o1.getSample(), o2.getSample());
        compare = compare == 0 ? o1.getName().compareTo(o2.getName()) : compare;
        return compare;
      }
      case TIME_STAMP: {
        return o2.getTimestamp().compareTo(o1.getTimestamp());
      }
      default:
        return 0;
    }
  }
}
