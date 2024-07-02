package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Solvents used in our lab.
 */
public enum Solvent {
  ACETONITRILE, METHANOL, CHCL3, OTHER;

  private static AppResources getResources(Locale locale) {
    return new AppResources(Solvent.class, locale);
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }
}
