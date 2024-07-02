package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.StorageTemperature.LOW;
import static ca.qc.ircm.proview.submission.StorageTemperature.MEDIUM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link StorageTemperature}.
 */
public class StorageTemperatureTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", StorageTemperature.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", StorageTemperature.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Medium() {
    assertEquals("4 °C", MEDIUM.getLabel(Locale.ENGLISH));
    assertEquals("4 °C", MEDIUM.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Low() {
    assertEquals("-20 °C", LOW.getLabel(Locale.ENGLISH));
    assertEquals("-20 °C", LOW.getLabel(Locale.FRENCH));
  }
}
