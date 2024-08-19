package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.ProteinIdentification.MSDB_ID;
import static ca.qc.ircm.proview.sample.ProteinIdentification.NCBINR;
import static ca.qc.ircm.proview.sample.ProteinIdentification.OTHER;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteinIdentification.UNIPROT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link ProteinIdentification}.
 */
@NonTransactionalTestAnnotations
public class ProteinIdentificationTest {
  private static final String PROTEIN_IDENTIFICATION_PREFIX =
      messagePrefix(ProteinIdentification.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void availables() {
    assertTrue(REFSEQ.available);
    assertTrue(UNIPROT.available);
    assertFalse(NCBINR.available);
    assertFalse(MSDB_ID.available);
    assertTrue(OTHER.available);

    List<ProteinIdentification> identifications = ProteinIdentification.availables();
    assertEquals(3, identifications.size());
    assertEquals(REFSEQ, identifications.get(0));
    assertEquals(UNIPROT, identifications.get(1));
    assertEquals(OTHER, identifications.get(2));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminée",
        messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Refseq() {
    assertEquals("NCBI (RefSeq)", messageSource
        .getMessage(PROTEIN_IDENTIFICATION_PREFIX + REFSEQ.name(), null, Locale.ENGLISH));
    assertEquals("NCBI (RefSeq)", messageSource
        .getMessage(PROTEIN_IDENTIFICATION_PREFIX + REFSEQ.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Uniprot() {
    assertEquals("UniProt", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + UNIPROT.name(),
        null, Locale.ENGLISH));
    assertEquals("UniProt", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + UNIPROT.name(),
        null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Ncbinr() {
    assertEquals("NCBInr", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + NCBINR.name(),
        null, Locale.ENGLISH));
    assertEquals("NCBInr", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + NCBINR.name(),
        null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Msdb() {
    assertEquals("MSDB_ID", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + MSDB_ID.name(),
        null, Locale.ENGLISH));
    assertEquals("MSDB_ID", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + MSDB_ID.name(),
        null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + OTHER.name(),
        null, Locale.ENGLISH));
    assertEquals("Autre", messageSource.getMessage(PROTEIN_IDENTIFICATION_PREFIX + OTHER.name(),
        null, Locale.FRENCH));
  }
}
