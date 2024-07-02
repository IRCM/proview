package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.LTQ_ORBI_TRAP;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.ORBITRAP_FUSION;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_EXACTIVE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_TOF;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TOF;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TSQ_VANTAGE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.VELOS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link InjectionType}.
 */
public class InjectionTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", InjectionType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", InjectionType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Velos() {
    assertEquals("LTQ-ORBITRAP-VELOS", VELOS.getLabel(Locale.ENGLISH));
    assertEquals("LTQ-ORBITRAP-VELOS", VELOS.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Qexactive() {
    assertEquals("Q-Exactive", Q_EXACTIVE.getLabel(Locale.ENGLISH));
    assertEquals("Q-Exactive", Q_EXACTIVE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Vantage() {
    assertEquals("TSQ-Vantage", TSQ_VANTAGE.getLabel(Locale.ENGLISH));
    assertEquals("TSQ-Vantage", TSQ_VANTAGE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Fusion() {
    assertEquals("Orbitrap-FUSION", ORBITRAP_FUSION.getLabel(Locale.ENGLISH));
    assertEquals("Orbitrap-FUSION", ORBITRAP_FUSION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Orbitrap() {
    assertEquals("LTQ-ORBI Trap", LTQ_ORBI_TRAP.getLabel(Locale.ENGLISH));
    assertEquals("LTQ-ORBI Trap", LTQ_ORBI_TRAP.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Qtof() {
    assertEquals("Q-TOF II", Q_TOF.getLabel(Locale.ENGLISH));
    assertEquals("Q-TOF II", Q_TOF.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Tof() {
    assertEquals("MALDI TOF", TOF.getLabel(Locale.ENGLISH));
    assertEquals("MALDI TOF", TOF.getLabel(Locale.FRENCH));
  }
}
