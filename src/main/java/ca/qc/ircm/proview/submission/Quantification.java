package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Quantification.
 */
public enum Quantification {
  NULL, LABEL_FREE, SILAC, TMT;

  public static String getNullLabel(Locale locale) {
    return NULL.getLabel(locale);
  }

  private AppResources getResources(Locale locale) {
    return new AppResources(Quantification.class, locale);
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }
}
