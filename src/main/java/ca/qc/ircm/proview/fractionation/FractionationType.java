package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Method used to split sample into fractions.
 */
public enum FractionationType {
  /**
   * MudPit fraction.
   */
  MUDPIT,
  /**
   * Split by pI.
   */
  PI;
  private static MessageResource getResources(Locale locale) {
    return new MessageResource(FractionationType.class, locale);
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