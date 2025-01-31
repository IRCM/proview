package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.LTQ_ORBI_TRAP;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.NULL;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.ORBITRAP_FUSION;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_EXACTIVE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_TOF;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TOF;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TSQ_VANTAGE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.VELOS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link MassDetectionInstrument}.
 */
@NonTransactionalTestAnnotations
public class MassDetectionInstrumentTest {

  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  @Autowired
  private MessageSource messageSource;

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
    assertEquals("Specialist's choice",
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Choix du spécialiste",
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Null() {
    assertEquals("Specialist's choice", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + NULL.name(), null, Locale.ENGLISH));
    assertEquals("Choix du spécialiste", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + NULL.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Velos() {
    assertEquals("LTQ-ORBITRAP-VELOS", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + VELOS.name(), null, Locale.ENGLISH));
    assertEquals("LTQ-ORBITRAP-VELOS", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + VELOS.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Qexactive() {
    assertEquals("Q-Exactive", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + Q_EXACTIVE.name(), null, Locale.ENGLISH));
    assertEquals("Q-Exactive", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + Q_EXACTIVE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Vantage() {
    assertEquals("TSQ-Vantage", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + TSQ_VANTAGE.name(), null, Locale.ENGLISH));
    assertEquals("TSQ-Vantage", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + TSQ_VANTAGE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Fusion() {
    assertEquals("Orbitrap-FUSION", messageSource.getMessage(
        MASS_DETECTION_INSTRUMENT_PREFIX + ORBITRAP_FUSION.name(), null, Locale.ENGLISH));
    assertEquals("Orbitrap-FUSION", messageSource.getMessage(
        MASS_DETECTION_INSTRUMENT_PREFIX + ORBITRAP_FUSION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Orbitrap() {
    assertEquals("LTQ-ORBI Trap", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + LTQ_ORBI_TRAP.name(), null, Locale.ENGLISH));
    assertEquals("LTQ-ORBI Trap", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + LTQ_ORBI_TRAP.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Qtof() {
    assertEquals("Q-TOF II", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + Q_TOF.name(), null, Locale.ENGLISH));
    assertEquals("Q-TOF II", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + Q_TOF.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Tof() {
    assertEquals("MALDI TOF", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + TOF.name(), null, Locale.ENGLISH));
    assertEquals("MALDI TOF", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + TOF.name(), null, Locale.FRENCH));
  }
}
