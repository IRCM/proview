package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.MALDI_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.Service.TWO_DIMENSION_LC_MS_MS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Service}.
 */
public class ServiceTest {
  @Test
  public void availables() {
    assertTrue(LC_MS_MS.available);
    assertFalse(TWO_DIMENSION_LC_MS_MS.available);
    assertFalse(MALDI_MS.available);
    assertTrue(SMALL_MOLECULE.available);
    assertTrue(INTACT_PROTEIN.available);

    List<Service> availables = Service.availables();
    assertEquals(3, availables.size());
    assertEquals(LC_MS_MS, availables.get(0));
    assertEquals(SMALL_MOLECULE, availables.get(1));
    assertEquals(INTACT_PROTEIN, availables.get(2));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", Service.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", Service.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel() {
    Locale locale = Locale.CANADA;

    assertEquals("LC/MS/MS", LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", MALDI_MS.getLabel(locale));
    assertEquals("Small molecule", SMALL_MOLECULE.getLabel(locale));
    assertEquals("Intact protein", INTACT_PROTEIN.getLabel(locale));
  }

  @Test
  public void getLabel_French() {
    Locale locale = Locale.CANADA_FRENCH;

    assertEquals("LC/MS/MS", LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", MALDI_MS.getLabel(locale));
    assertEquals("Petite molécule", SMALL_MOLECULE.getLabel(locale));
    assertEquals("Protéine intacte", INTACT_PROTEIN.getLabel(locale));
  }
}
