package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * All statuses of a sample.
 */
public enum SampleStatus {
  /**
   * Waiting to receive sample.
   */
  WAITING,
  /**
   * Sample was received and awaits treatments and analysis.
   */
  RECEIVED,
  /**
   * Sample was digested.
   */
  DIGESTED,
  /**
   * Sample was enriched.
   */
  ENRICHED,
  /**
   * Sample analysis was cancelled.
   */
  CANCELLED,
  /**
   * Sample was analysed and have results.
   */
  ANALYSED;

  public static SampleStatus[] analysedStatuses() {
    return new SampleStatus[] { CANCELLED, ANALYSED };
  }

  private static AppResources getResources(Locale locale) {
    return new AppResources(SampleStatus.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }
}