package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Method used to split sample into fractions.
 */
public enum FractionationType {
  /**
   * MudPit treatedSample.
   */
  MUDPIT,
  /**
   * Split by pI.
   */
  PI;

  private static AppResources getResources(Locale locale) {
    return new AppResources(FractionationType.class, locale);
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