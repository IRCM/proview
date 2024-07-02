package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.treatment.Solvent.OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Solvent}.
 */
public class SolventTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", Solvent.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", Solvent.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Acetonitrile() {
    assertEquals("Acetonitrile", ACETONITRILE.getLabel(Locale.ENGLISH));
    assertEquals("Acétonitrile", ACETONITRILE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Methanol() {
    assertEquals("Methanol", METHANOL.getLabel(Locale.ENGLISH));
    assertEquals("Méthanol", METHANOL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Chcl3() {
    assertEquals("CHCl\u2083", CHCL3.getLabel(Locale.ENGLISH));
    assertEquals("CHCl\u2083", CHCL3.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre", OTHER.getLabel(Locale.FRENCH));
  }
}
