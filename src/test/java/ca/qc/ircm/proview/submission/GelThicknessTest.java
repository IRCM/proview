package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.GelThickness.ONE;
import static ca.qc.ircm.proview.submission.GelThickness.ONE_HALF;
import static ca.qc.ircm.proview.submission.GelThickness.TWO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GelThickness}.
 */
public class GelThicknessTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", GelThickness.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", GelThickness.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_One() {
    assertEquals("1", ONE.getLabel(Locale.ENGLISH));
    assertEquals("1", ONE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_OneHalf() {
    assertEquals("1.5", ONE_HALF.getLabel(Locale.ENGLISH));
    assertEquals("1.5", ONE_HALF.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Two() {
    assertEquals("2", TWO.getLabel(Locale.ENGLISH));
    assertEquals("2", TWO.getLabel(Locale.FRENCH));
  }
}
