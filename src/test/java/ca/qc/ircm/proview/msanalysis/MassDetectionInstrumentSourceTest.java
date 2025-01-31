package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.ESI;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.LDTD;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.NSI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link MassDetectionInstrumentSource}.
 */
@NonTransactionalTestAnnotations
public class MassDetectionInstrumentSourceTest {

  private static final String MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX =
      messagePrefix(MassDetectionInstrumentSource.class);
  @Autowired
  private MessageSource messageSource;

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
    assertEquals("Undetermined", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminée", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Esi() {
    assertEquals("ESI", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + ESI.name(), null, Locale.ENGLISH));
    assertEquals("ESI", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + ESI.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Nsi() {
    assertEquals("NSI", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + NSI.name(), null, Locale.ENGLISH));
    assertEquals("NSI", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + NSI.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Ldtd() {
    assertEquals("LDTD", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + LDTD.name(), null, Locale.ENGLISH));
    assertEquals("LDTD", messageSource
        .getMessage(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + LDTD.name(), null, Locale.FRENCH));
  }
}
