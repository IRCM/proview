package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Completed status.
 */
public enum DataAnalysisStatus {
  /**
   * Data has to be analysed.
   */
  TO_DO,
  /**
   * Data is analysed.
   */
  ANALYSED,
  /**
   * Data analysis was cancelled.
   */
  CANCELLED;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(DataAnalysisStatus.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
