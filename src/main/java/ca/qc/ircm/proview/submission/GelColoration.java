package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Gel thickness.
 */
public enum GelColoration {
  COOMASSIE, SYPRO, SILVER, SILVER_INVITROGEN, OTHER;

  private static AppResources getResources(Locale locale) {
    return new AppResources(GelColoration.class, locale);
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
