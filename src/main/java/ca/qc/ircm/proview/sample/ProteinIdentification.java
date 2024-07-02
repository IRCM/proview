package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.AppResources;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Available protein identifications.
 */
public enum ProteinIdentification {
  REFSEQ(true), UNIPROT(true), NCBINR(false), MSDB_ID(false), OTHER(true);

  public final boolean available;

  ProteinIdentification(boolean available) {
    this.available = available;
  }

  public static List<ProteinIdentification> availables() {
    return Stream.of(ProteinIdentification.values())
        .filter(identification -> identification.available).collect(Collectors.toList());
  }

  private static AppResources getResources(Locale locale) {
    return new AppResources(ProteinIdentification.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }
}
