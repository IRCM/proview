package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Source for mass spectrometer.
 */
public enum MassDetectionInstrumentSource {
  NSI, ESI, LDTD;
  private static MessageResource getResources(Locale locale) {
    return new MessageResource(MassDetectionInstrumentSource.class, locale);
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
