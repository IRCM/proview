package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Gel thickness.
 */
public enum GelThickness {
  ONE, ONE_HALF, TWO;

  private static AppResources getResources(Locale locale) {
    return new AppResources(GelThickness.class, locale);
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
