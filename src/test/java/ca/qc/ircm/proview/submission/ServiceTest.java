package ca.qc.ircm.proview.submission;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class ServiceTest {
  @Test
  public void getLabel() {
    Locale locale = Locale.CANADA;

    assertEquals("LC/MS/MS", Service.LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", Service.TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", Service.MALDI_MS.getLabel(locale));
    assertEquals("Small molecule", Service.SMALL_MOLECULE.getLabel(locale));
    assertEquals("Intact protein", Service.INTACT_PROTEIN.getLabel(locale));
  }

  @Test
  public void getLabel_French() {
    Locale locale = Locale.CANADA_FRENCH;

    assertEquals("LC/MS/MS", Service.LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", Service.TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", Service.MALDI_MS.getLabel(locale));
    assertEquals("Petite molecule", Service.SMALL_MOLECULE.getLabel(locale));
    assertEquals("Protéine intacte", Service.INTACT_PROTEIN.getLabel(locale));
  }
}
