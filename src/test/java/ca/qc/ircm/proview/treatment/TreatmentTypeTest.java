package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.treatment.TreatmentType.DIGESTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.DILUTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.ENRICHMENT;
import static ca.qc.ircm.proview.treatment.TreatmentType.FRACTIONATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.SOLUBILISATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.STANDARD_ADDITION;
import static ca.qc.ircm.proview.treatment.TreatmentType.TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link TreatmentType}.
 */
@NonTransactionalTestAnnotations
public class TreatmentTypeTest {
  private static final String TREATMENT_TYPE_PREFIX = messagePrefix(TreatmentType.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getLabel_Digestion() {
    assertEquals("Digestion",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + DIGESTION.name(), null, Locale.ENGLISH));
    assertEquals("Digestion",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + DIGESTION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Dilution() {
    assertEquals("Dilution",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + DILUTION.name(), null, Locale.ENGLISH));
    assertEquals("Dilution",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + DILUTION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Enrichment() {
    assertEquals("Enrichment",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + ENRICHMENT.name(), null, Locale.ENGLISH));
    assertEquals("Enrichissement",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + ENRICHMENT.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Fractionation() {
    assertEquals("Fractionation", messageSource
        .getMessage(TREATMENT_TYPE_PREFIX + FRACTIONATION.name(), null, Locale.ENGLISH));
    assertEquals("Fractionnement", messageSource
        .getMessage(TREATMENT_TYPE_PREFIX + FRACTIONATION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Solubilisation() {
    assertEquals("Solubilisation", messageSource
        .getMessage(TREATMENT_TYPE_PREFIX + SOLUBILISATION.name(), null, Locale.ENGLISH));
    assertEquals("Solubilisation", messageSource
        .getMessage(TREATMENT_TYPE_PREFIX + SOLUBILISATION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_StandardAddition() {
    assertEquals("Standard addition", messageSource
        .getMessage(TREATMENT_TYPE_PREFIX + STANDARD_ADDITION.name(), null, Locale.ENGLISH));
    assertEquals("Addition de standard", messageSource
        .getMessage(TREATMENT_TYPE_PREFIX + STANDARD_ADDITION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Transfer() {
    assertEquals("Transfer",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + TRANSFER.name(), null, Locale.ENGLISH));
    assertEquals("Transfert",
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + TRANSFER.name(), null, Locale.FRENCH));
  }
}
