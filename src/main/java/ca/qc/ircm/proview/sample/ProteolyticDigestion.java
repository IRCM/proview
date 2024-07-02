package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Available type of digestions.
 */
public enum ProteolyticDigestion {
  TRYPSIN, DIGESTED, OTHER;

  private static AppResources getResources(Locale locale) {
    return new AppResources(ProteolyticDigestion.class, locale);
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
