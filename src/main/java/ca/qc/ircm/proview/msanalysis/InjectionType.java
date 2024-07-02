package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Instruments available for protein mass detection.
 */
public enum InjectionType {
  LC_MS, DIRECT_INFUSION;

  private static AppResources getResources(Locale locale) {
    return new AppResources(InjectionType.class, locale);
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
