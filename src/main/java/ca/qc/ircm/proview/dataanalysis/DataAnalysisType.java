package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Type of data analysis.
 */
public enum DataAnalysisType {
  /**
   * Analysis made only to confirm very probable protein presence.
   */
  PROTEIN,
  /**
   * Analysis made to confirm correctiveness of specific peptides.
   */
  PEPTIDE,
  /**
   * Analyse both protein and and peptides.
   */
  PROTEIN_PEPTIDE;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(DataAnalysisType.class, locale);
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
