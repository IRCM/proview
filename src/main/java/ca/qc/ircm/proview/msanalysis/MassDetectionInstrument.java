package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Instruments available for protein mass detection.
 */
public enum MassDetectionInstrument {
  VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION, LTQ_ORBI_TRAP, Q_TOF, TOF;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(MassDetectionInstrument.class, locale);
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
