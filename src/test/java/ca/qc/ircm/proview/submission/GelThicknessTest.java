package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.GelThickness.ONE;
import static ca.qc.ircm.proview.submission.GelThickness.ONE_HALF;
import static ca.qc.ircm.proview.submission.GelThickness.TWO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link GelThickness}.
 */
@NonTransactionalTestAnnotations
public class GelThicknessTest {

  private static final String GEL_THICKNESS_PREFIX = messagePrefix(GelThickness.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminée",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_One() {
    assertEquals("1",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + ONE.name(), null, Locale.ENGLISH));
    assertEquals("1",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + ONE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_OneHalf() {
    assertEquals("1.5",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + ONE_HALF.name(), null, Locale.ENGLISH));
    assertEquals("1.5",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + ONE_HALF.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Two() {
    assertEquals("2",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + TWO.name(), null, Locale.ENGLISH));
    assertEquals("2",
        messageSource.getMessage(GEL_THICKNESS_PREFIX + TWO.name(), null, Locale.FRENCH));
  }
}
