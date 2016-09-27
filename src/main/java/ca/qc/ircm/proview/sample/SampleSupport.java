package ca.qc.ircm.proview.sample;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Sample support.
 */
public enum SampleSupport {
  /**
   * Sample is dry.
   */
  DRY,
  /**
   * Sample is in solution.
   */
  SOLUTION,
  /**
   * Sample is in a Gel.
   */
  GEL;
  private static MessageResource getResources(Locale locale) {
    return new MessageResource(SampleSupport.class, locale);
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
