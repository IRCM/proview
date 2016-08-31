package ca.qc.ircm.proview.msanalysis;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for acquisitions.
 */
public class AcquisitionComparator implements Comparator<Acquisition>, Serializable {
  private static final long serialVersionUID = -9122886885859749441L;

  public AcquisitionComparator() {
    super();
  }

  @Override
  public int compare(Acquisition o1, Acquisition o2) {
    return o1.getListIndex().compareTo(o2.getListIndex());
  }
}
