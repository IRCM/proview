package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.treatment.TreatmentType.DIGESTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.DILUTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.ENRICHMENT;
import static ca.qc.ircm.proview.treatment.TreatmentType.FRACTIONATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.SOLUBILISATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.STANDARD_ADDITION;
import static ca.qc.ircm.proview.treatment.TreatmentType.TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TreatmentType}.
 */
public class TreatmentTypeTest {
  @Test
  public void getLabel_Digestion() {
    assertEquals("Digestion", DIGESTION.getLabel(Locale.ENGLISH));
    assertEquals("Digestion", DIGESTION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Dilution() {
    assertEquals("Dilution", DILUTION.getLabel(Locale.ENGLISH));
    assertEquals("Dilution", DILUTION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Enrichment() {
    assertEquals("Enrichment", ENRICHMENT.getLabel(Locale.ENGLISH));
    assertEquals("Enrichissement", ENRICHMENT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Fractionation() {
    assertEquals("Fractionation", FRACTIONATION.getLabel(Locale.ENGLISH));
    assertEquals("Fractionnement", FRACTIONATION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Solubilisation() {
    assertEquals("Solubilisation", SOLUBILISATION.getLabel(Locale.ENGLISH));
    assertEquals("Solubilisation", SOLUBILISATION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_StandardAddition() {
    assertEquals("Standard addition", STANDARD_ADDITION.getLabel(Locale.ENGLISH));
    assertEquals("Addition de standard", STANDARD_ADDITION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Transfer() {
    assertEquals("Transfer", TRANSFER.getLabel(Locale.ENGLISH));
    assertEquals("Transfert", TRANSFER.getLabel(Locale.FRENCH));
  }
}
