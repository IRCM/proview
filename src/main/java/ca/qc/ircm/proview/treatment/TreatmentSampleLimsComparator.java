package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.sample.SampleLimsComparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for treated samples by their LIMS.
 */
public class TreatmentSampleLimsComparator implements Comparator<TreatmentSample>, Serializable {
  private static final long serialVersionUID = -5024911239066204507L;
  private final SampleLimsComparator sampleLimsComparator;

  public TreatmentSampleLimsComparator(Locale locale) {
    sampleLimsComparator = new SampleLimsComparator(locale);
  }

  @Override
  public int compare(TreatmentSample o1, TreatmentSample o2) {
    return sampleLimsComparator.compare(o1.getSample(), o2.getSample());
  }
}
