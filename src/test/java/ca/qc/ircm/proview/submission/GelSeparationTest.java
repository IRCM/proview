package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.GelSeparation.ONE_DIMENSION;
import static ca.qc.ircm.proview.submission.GelSeparation.TWO_DIMENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link GelSeparation}.
 */
@NonTransactionalTestAnnotations
public class GelSeparationTest {
  private static final String GEL_SEPARATION_PREFIX = messagePrefix(GelSeparation.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(GEL_SEPARATION_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminée",
        messageSource.getMessage(GEL_SEPARATION_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_OneDimension() {
    assertEquals("1D", messageSource.getMessage(GEL_SEPARATION_PREFIX + ONE_DIMENSION.name(), null,
        Locale.ENGLISH));
    assertEquals("1D", messageSource.getMessage(GEL_SEPARATION_PREFIX + ONE_DIMENSION.name(), null,
        Locale.FRENCH));
  }

  @Test
  public void getLabel_TwoDimension() {
    assertEquals("2D", messageSource.getMessage(GEL_SEPARATION_PREFIX + TWO_DIMENSION.name(), null,
        Locale.ENGLISH));
    assertEquals("2D", messageSource.getMessage(GEL_SEPARATION_PREFIX + TWO_DIMENSION.name(), null,
        Locale.FRENCH));
  }
}
