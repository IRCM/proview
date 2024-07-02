package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.GelSeparation.ONE_DIMENSION;
import static ca.qc.ircm.proview.submission.GelSeparation.TWO_DIMENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GelSeparation}.
 */
public class GelSeparationTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", GelSeparation.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", GelSeparation.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_OneDimension() {
    assertEquals("1D", ONE_DIMENSION.getLabel(Locale.ENGLISH));
    assertEquals("1D", ONE_DIMENSION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_TwoDimension() {
    assertEquals("2D", TWO_DIMENSION.getLabel(Locale.ENGLISH));
    assertEquals("2D", TWO_DIMENSION.getLabel(Locale.FRENCH));
  }
}
