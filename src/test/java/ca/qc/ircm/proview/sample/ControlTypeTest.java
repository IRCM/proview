package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.ControlType.NEGATIVE_CONTROL;
import static ca.qc.ircm.proview.sample.ControlType.POSITIVE_CONTROL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ControlType}.
 */
public class ControlTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Other", ControlType.getNullLabel(Locale.ENGLISH));
    assertEquals("Autre", ControlType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Negative() {
    assertEquals("Negative control", NEGATIVE_CONTROL.getLabel(Locale.ENGLISH));
    assertEquals("Contrôle négatif", NEGATIVE_CONTROL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Positive() {
    assertEquals("Positive control", POSITIVE_CONTROL.getLabel(Locale.ENGLISH));
    assertEquals("Contrôle positif", POSITIVE_CONTROL.getLabel(Locale.FRENCH));
  }
}
