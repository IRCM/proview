package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.sample.SampleLimsComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * Compares 2 sample transfers.
 */
public class SampleTransferComparator implements Comparator<SampleTransfer>, Serializable {
  private static final long serialVersionUID = 8135995214223936190L;

  private final SampleLimsComparator sampleComparator;

  public SampleTransferComparator(Locale locale) {
    sampleComparator = new SampleLimsComparator(locale);
  }

  @Override
  public int compare(SampleTransfer o1, SampleTransfer o2) {
    // Compare sample name first.
    int compare = sampleComparator.compare(o1.getSample(), o2.getSample());
    return compare;
  }

}
