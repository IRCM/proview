package ca.qc.ircm.proview.sample;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Available protein identifications.
 */
public enum ProteinIdentification {
  REFSEQ, UNIPROT, NCBINR, MSDB_ID, OTHER;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(ProteinIdentification.class, locale);
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
