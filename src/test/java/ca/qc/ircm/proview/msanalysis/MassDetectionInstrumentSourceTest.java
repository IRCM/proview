package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.ESI;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.LDTD;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.NSI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MassDetectionInstrumentSource}.
 */
public class MassDetectionInstrumentSourceTest {
  @Test
  public void availables() {
    assertTrue(ESI.available);
    assertTrue(NSI.available);
    assertFalse(LDTD.available);

    List<MassDetectionInstrumentSource> availables = MassDetectionInstrumentSource.availables();
    assertEquals(2, availables.size());
    assertEquals(ESI, availables.get(0));
    assertEquals(NSI, availables.get(1));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", MassDetectionInstrumentSource.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", MassDetectionInstrumentSource.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Esi() {
    assertEquals("ESI", ESI.getLabel(Locale.ENGLISH));
    assertEquals("ESI", ESI.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Nsi() {
    assertEquals("NSI", NSI.getLabel(Locale.ENGLISH));
    assertEquals("NSI", NSI.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Ldtd() {
    assertEquals("LDTD", LDTD.getLabel(Locale.ENGLISH));
    assertEquals("LDTD", LDTD.getLabel(Locale.FRENCH));
  }
}
