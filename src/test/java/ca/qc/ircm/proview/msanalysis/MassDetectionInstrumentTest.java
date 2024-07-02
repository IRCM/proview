package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.LTQ_ORBI_TRAP;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.NULL;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.ORBITRAP_FUSION;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_EXACTIVE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_TOF;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TOF;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TSQ_VANTAGE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.VELOS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MassDetectionInstrument}.
 */
public class MassDetectionInstrumentTest {
  @Test
  public void userChoices() {
    List<MassDetectionInstrument> availables = MassDetectionInstrument.userChoices();
    assertEquals(4, availables.size());
    assertEquals(NULL, availables.get(0));
    assertEquals(VELOS, availables.get(1));
    assertEquals(Q_EXACTIVE, availables.get(2));
    assertEquals(ORBITRAP_FUSION, availables.get(3));
  }

  @Test
  public void platformChoices() {
    List<MassDetectionInstrument> availables = MassDetectionInstrument.platformChoices();
    assertEquals(3, availables.size());
    assertEquals(VELOS, availables.get(0));
    assertEquals(Q_EXACTIVE, availables.get(1));
    assertEquals(ORBITRAP_FUSION, availables.get(2));
  }

  @Test
  public void filterChoices() {
    List<MassDetectionInstrument> availables = MassDetectionInstrument.filterChoices();
    assertEquals(4, availables.size());
    assertEquals(NULL, availables.get(0));
    assertEquals(VELOS, availables.get(1));
    assertEquals(Q_EXACTIVE, availables.get(2));
    assertEquals(ORBITRAP_FUSION, availables.get(3));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Specialist's choice", MassDetectionInstrument.getNullLabel(Locale.ENGLISH));
    assertEquals("Choix du spécialiste", MassDetectionInstrument.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Null() {
    assertEquals("Specialist's choice", NULL.getLabel(Locale.ENGLISH));
    assertEquals("Choix du spécialiste", NULL.getLabel(Locale.FRENCH));
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
