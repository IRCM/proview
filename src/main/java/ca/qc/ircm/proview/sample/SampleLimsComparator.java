package ca.qc.ircm.proview.sample;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for samples.
 */
public class SampleLimsComparator implements Comparator<Sample>, Serializable {

  static final long serialVersionUID = 3617602908990386176L;

  private final Locale locale;

  public SampleLimsComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(Sample o1, Sample o2) {
    Collator collator = Collator.getInstance(locale);
    // Special case for samples.
    return collator.compare(this.padSampleLims(o1), this.padSampleLims(o2));
  }

  /**
   * Returns sample's LIMS padded in way that allows comparison with any other sample.
   * 
   * @param sample
   *          sample containing LIMS to pad
   * @return sample's LIMS padded in way that allows comparison with any other sample
   */
  protected String padSampleLims(Sample sample) {
    // Contains first part of sample (before any dot/underscore).
    String trimmedSampleLims =
        StringUtils.substringBefore(StringUtils.substringBefore(sample.getLims(), "_"), ".");
    // Part of sample LIMS after underscore and before any dots padded to 20
    // characters.
    String sampleUnderscore = "";
    if (sample.getLims().contains("_")) {
      sampleUnderscore =
          StringUtils.substringBefore(StringUtils.substringAfter(sample.getLims(), "_"), ".");
      sampleUnderscore = StringUtils.leftPad(sampleUnderscore, 20, "0");
      sampleUnderscore = "_" + sampleUnderscore;
    }
    // Part of sample LIMS after dot padded to 20 characters.
    String sampleDot = "";
    if (sample.getLims().contains(".")) {
      sampleDot = StringUtils.substringAfter(sample.getLims(), ".");
      sampleDot = StringUtils.leftPad(sampleDot, 20, "0");
      sampleDot = "." + sampleDot;
    }

    return trimmedSampleLims + sampleUnderscore + sampleDot;
  }
}
