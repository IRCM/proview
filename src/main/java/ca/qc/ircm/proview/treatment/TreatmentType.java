package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Type of treatment.
 */
public enum TreatmentType {
  DIGESTION, DILUTION, ENRICHMENT, FRACTIONATION, SOLUBILISATION, STANDARD_ADDITION, TRANSFER;

  public String getLabel(Locale locale) {
    AppResources resources = new AppResources(TreatmentType.class, locale);
    return resources.message(name());
  }
}
