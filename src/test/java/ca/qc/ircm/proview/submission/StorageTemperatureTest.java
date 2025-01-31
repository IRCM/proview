package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.StorageTemperature.LOW;
import static ca.qc.ircm.proview.submission.StorageTemperature.MEDIUM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link StorageTemperature}.
 */
@NonTransactionalTestAnnotations
public class StorageTemperatureTest {

  private static final String STORAGE_TEMPERATURE_PREFIX = messagePrefix(StorageTemperature.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminée",
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Medium() {
    assertEquals("4 °C",
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + MEDIUM.name(), null, Locale.ENGLISH));
    assertEquals("4 °C",
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + MEDIUM.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Low() {
    assertEquals("-20 °C",
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + LOW.name(), null, Locale.ENGLISH));
    assertEquals("-20 °C",
        messageSource.getMessage(STORAGE_TEMPERATURE_PREFIX + LOW.name(), null, Locale.FRENCH));
  }
}
