package ca.qc.ircm.proview.treatment;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for treated samples by their LIMS.
 */
public class TreatmentComparator implements Comparator<Treatment<?>>, Serializable {
  public static enum TreatmentCompareType {
    TIMESTAMP;
  }

  private static final long serialVersionUID = -7443140194033594333L;
  private TreatmentCompareType compare;

  /**
   * Creates comparator for treatments.
   * 
   * @param compare
   *          how to compare treatments
   */
  public TreatmentComparator(TreatmentCompareType compare) {
    if (compare == null) {
      throw new NullPointerException("compare cannot be null");
    }
    this.compare = compare;
  }

  @Override
  public int compare(Treatment<?> o1, Treatment<?> o2) {
    switch (compare) {
      case TIMESTAMP:
        return o1.getInsertTime().compareTo(o2.getInsertTime());
      default:
        throw new AssertionError("TreatmentCompareType " + compare + " not covered in switch");
    }
  }
}
