package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.Quantification.LABEL_FREE;
import static ca.qc.ircm.proview.submission.Quantification.NULL;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Quantification.TMT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Quantification}.
 */
public class QuantificationTest {
  @Test
  public void getNullLabel() {
    assertEquals("None", Quantification.getNullLabel(Locale.ENGLISH));
    assertEquals("Aucune", Quantification.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Null() {
    assertEquals("None", NULL.getLabel(Locale.ENGLISH));
    assertEquals("Aucune", NULL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_LabelFree() {
    assertEquals("Label-free", LABEL_FREE.getLabel(Locale.ENGLISH));
    assertEquals("Label-free", LABEL_FREE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Silac() {
    assertEquals("Silac", SILAC.getLabel(Locale.ENGLISH));
    assertEquals("Silac", SILAC.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Tmt() {
    assertEquals("Tandem mass tags", TMT.getLabel(Locale.ENGLISH));
    assertEquals("Tandem mass tags", TMT.getLabel(Locale.FRENCH));
  }
}
