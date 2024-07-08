package ca.qc.ircm.proview.sample;

import java.util.List;
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
}
