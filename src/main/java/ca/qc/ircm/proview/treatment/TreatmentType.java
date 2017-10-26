package ca.qc.ircm.proview.treatment;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Type of treatment.
 */
public enum TreatmentType {
  DIGESTION, DILUTION, ENRICHMENT, FRACTIONATION, SOLUBILISATION, STANDARD_ADDITION, TRANSFER;

  public String getLabel(Locale locale) {
    ResourceBundle bundle = ResourceBundle.getBundle(TreatmentType.class.getName(), locale);
    return bundle.getString(name());
  }
}
