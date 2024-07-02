package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.treatment.FractionationType.MUDPIT;
import static ca.qc.ircm.proview.treatment.FractionationType.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FractionationType}.
 */
public class FractionationTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Not applicable", FractionationType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", FractionationType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Mudpit() {
    assertEquals("Ions exchange column", MUDPIT.getLabel(Locale.ENGLISH));
    assertEquals("Colonne d'échange d'ions", MUDPIT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Pi() {
    assertEquals("pI", PI.getLabel(Locale.ENGLISH));
    assertEquals("pI", PI.getLabel(Locale.FRENCH));
  }
}
