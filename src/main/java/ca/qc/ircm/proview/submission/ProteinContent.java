package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Protein content of samples.
 */
public enum ProteinContent {
  SMALL(1, 4), MEDIUM(5, 10), LARGE(10, 20), XLARGE(20, Integer.MAX_VALUE);

  ProteinContent(int start, int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Start of interval.
   */
  private int start;
  /**
   * End of interval.
   */
  private int end;

  private static AppResources getResources(Locale locale) {
    return new AppResources(ProteinContent.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }
}