package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.Quantification.LABEL_FREE;
import static ca.qc.ircm.proview.submission.Quantification.NULL;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Quantification.TMT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link Quantification}.
 */
@NonTransactionalTestAnnotations
public class QuantificationTest {

  private static final String QUANTIFICATION_PREFIX = messagePrefix(Quantification.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("None",
        messageSource.getMessage(QUANTIFICATION_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Aucune",
        messageSource.getMessage(QUANTIFICATION_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Null() {
    assertEquals("None",
        messageSource.getMessage(QUANTIFICATION_PREFIX + NULL.name(), null, Locale.ENGLISH));
    assertEquals("Aucune",
        messageSource.getMessage(QUANTIFICATION_PREFIX + NULL.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_LabelFree() {
    assertEquals("Label-free",
        messageSource.getMessage(QUANTIFICATION_PREFIX + LABEL_FREE.name(), null, Locale.ENGLISH));
    assertEquals("Label-free",
        messageSource.getMessage(QUANTIFICATION_PREFIX + LABEL_FREE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Silac() {
    assertEquals("Silac",
        messageSource.getMessage(QUANTIFICATION_PREFIX + SILAC.name(), null, Locale.ENGLISH));
    assertEquals("Silac",
        messageSource.getMessage(QUANTIFICATION_PREFIX + SILAC.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Tmt() {
    assertEquals("Tandem mass tags",
        messageSource.getMessage(QUANTIFICATION_PREFIX + TMT.name(), null, Locale.ENGLISH));
    assertEquals("Tandem mass tags",
        messageSource.getMessage(QUANTIFICATION_PREFIX + TMT.name(), null, Locale.FRENCH));
  }
}
