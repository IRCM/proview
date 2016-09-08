package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.ProteinIdentification.MSDB_ID;
import static ca.qc.ircm.proview.sample.ProteinIdentification.NCBINR;
import static ca.qc.ircm.proview.sample.ProteinIdentification.OTHER;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteinIdentification.UNIPROT;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class ProteinIdentificationTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", ProteinIdentification.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", ProteinIdentification.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Refseq() {
    assertEquals("NCBI (RefSeq)", REFSEQ.getLabel(Locale.ENGLISH));
    assertEquals("NCBI (RefSeq)", REFSEQ.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Uniprot() {
    assertEquals("UniProt", UNIPROT.getLabel(Locale.ENGLISH));
    assertEquals("UniProt", UNIPROT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Ncbinr() {
    assertEquals("NCBInr", NCBINR.getLabel(Locale.ENGLISH));
    assertEquals("NCBInr", NCBINR.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Msdb() {
    assertEquals("MSDB_ID", MSDB_ID.getLabel(Locale.ENGLISH));
    assertEquals("MSDB_ID", MSDB_ID.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre", OTHER.getLabel(Locale.FRENCH));
  }
}
