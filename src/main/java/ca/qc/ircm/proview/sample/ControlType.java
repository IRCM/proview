package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Sample type.
 */
public enum ControlType {
  /**
   * Negative control.
   */
  NEGATIVE_CONTROL,
  /**
   * Positive control.
   */
  POSITIVE_CONTROL;

  private static AppResources getResources(Locale locale) {
    return new AppResources(ControlType.class, locale);
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
