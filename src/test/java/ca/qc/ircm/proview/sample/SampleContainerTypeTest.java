package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SampleContainerType}.
 */
public class SampleContainerTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", SampleContainerType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", SampleContainerType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Tube() {
    assertEquals("Tube", TUBE.getLabel(Locale.ENGLISH));
    assertEquals("Tube", TUBE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Well() {
    assertEquals("Plate", WELL.getLabel(Locale.ENGLISH));
    assertEquals("Plaque", WELL.getLabel(Locale.FRENCH));
  }
}
