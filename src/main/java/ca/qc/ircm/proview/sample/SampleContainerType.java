package ca.qc.ircm.proview.sample;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Type of {@link SampleContainer}.
 */
public enum SampleContainerType {
  TUBE, SPOT;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(SampleContainerType.class, locale);
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
