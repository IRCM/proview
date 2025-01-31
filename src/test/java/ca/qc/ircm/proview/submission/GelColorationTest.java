package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.GelColoration.COOMASSIE;
import static ca.qc.ircm.proview.submission.GelColoration.OTHER;
import static ca.qc.ircm.proview.submission.GelColoration.SILVER;
import static ca.qc.ircm.proview.submission.GelColoration.SILVER_INVITROGEN;
import static ca.qc.ircm.proview.submission.GelColoration.SYPRO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link GelColoration}.
 */
@NonTransactionalTestAnnotations
public class GelColorationTest {

  private static final String GEL_COLORATION_PREFIX = messagePrefix(GelColoration.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("None",
        messageSource.getMessage(GEL_COLORATION_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Aucune",
        messageSource.getMessage(GEL_COLORATION_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Coomassie() {
    assertEquals("Coomassie",
        messageSource.getMessage(GEL_COLORATION_PREFIX + COOMASSIE.name(), null, Locale.ENGLISH));
    assertEquals("Coomassie",
        messageSource.getMessage(GEL_COLORATION_PREFIX + COOMASSIE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Sypro() {
    assertEquals("Sypro",
        messageSource.getMessage(GEL_COLORATION_PREFIX + SYPRO.name(), null, Locale.ENGLISH));
    assertEquals("Sypro",
        messageSource.getMessage(GEL_COLORATION_PREFIX + SYPRO.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Silver() {
    assertEquals("Silver",
        messageSource.getMessage(GEL_COLORATION_PREFIX + SILVER.name(), null, Locale.ENGLISH));
    assertEquals("Silver",
        messageSource.getMessage(GEL_COLORATION_PREFIX + SILVER.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_SilverInvitrogen() {
    assertEquals("Silver (Invitrogen)", messageSource
        .getMessage(GEL_COLORATION_PREFIX + SILVER_INVITROGEN.name(), null, Locale.ENGLISH));
    assertEquals("Silver (Invitrogen)", messageSource
        .getMessage(GEL_COLORATION_PREFIX + SILVER_INVITROGEN.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other coloration",
        messageSource.getMessage(GEL_COLORATION_PREFIX + OTHER.name(), null, Locale.ENGLISH));
    assertEquals("Autre coloration",
        messageSource.getMessage(GEL_COLORATION_PREFIX + OTHER.name(), null, Locale.FRENCH));
  }
}
