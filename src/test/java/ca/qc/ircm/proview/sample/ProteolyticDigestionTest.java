package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.OTHER;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link ProteolyticDigestion}.
 */
@NonTransactionalTestAnnotations
public class ProteolyticDigestionTest {

  private static final String PROTEOLYTIC_DIGESTION_PREFIX =
      messagePrefix(ProteolyticDigestion.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("None",
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Aucun",
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Trypsine() {
    assertEquals("Trypsin", messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + TRYPSIN.name(),
        null, Locale.ENGLISH));
    assertEquals("Trypsine", messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + TRYPSIN.name(),
        null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Digested() {
    assertEquals("Already digested", messageSource
        .getMessage(PROTEOLYTIC_DIGESTION_PREFIX + DIGESTED.name(), null, Locale.ENGLISH));
    assertEquals("Déjà digérées", messageSource
        .getMessage(PROTEOLYTIC_DIGESTION_PREFIX + DIGESTED.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + OTHER.name(),
        null, Locale.ENGLISH));
    assertEquals("Autre",
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + OTHER.name(), null, Locale.FRENCH));
  }
}
