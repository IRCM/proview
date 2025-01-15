package ca.qc.ircm.proview.treatment;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for treated samples by their LIMS.
 */
public class TreatmentComparator implements Comparator<Treatment>, Serializable {
  /**
   * Types of comparison that can be made.
   */
  public enum TreatmentCompareType {
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
    this.compare = compare;
  }

  @Override
  public int compare(Treatment o1, Treatment o2) {
    switch (compare) {
      case TIMESTAMP:
        return o1.getInsertTime().compareTo(o2.getInsertTime());
      default:
        throw new AssertionError("TreatmentCompareType " + compare + " not covered in switch");
    }
  }
}
