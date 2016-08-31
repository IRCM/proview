package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.proview.sample.SampleLimsComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * Compares 2 fractionation details.
 */
public class FractionationDetailComparator
    implements Comparator<FractionationDetail>, Serializable {
  private static final long serialVersionUID = -8569081716901502581L;

  private final SampleLimsComparator sampleComparator;

  public FractionationDetailComparator(Locale locale) {
    sampleComparator = new SampleLimsComparator(locale);
  }

  @Override
  public int compare(FractionationDetail o1, FractionationDetail o2) {
    // Compare sample name first.
    int compare = sampleComparator.compare(o1.getSample(), o2.getSample());
    return compare;
  }

}
