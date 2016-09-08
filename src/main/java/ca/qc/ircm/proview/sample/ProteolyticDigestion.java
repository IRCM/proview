package ca.qc.ircm.proview.sample;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Available type of digestions.
 */
public enum ProteolyticDigestion {
  TRYPSIN, DIGESTED, OTHER;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(ProteolyticDigestion.class, locale);
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
