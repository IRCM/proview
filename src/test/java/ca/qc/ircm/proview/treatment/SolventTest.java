package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.treatment.Solvent.OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link Solvent}.
 */
@NonTransactionalTestAnnotations
public class SolventTest {
  private static final String SOLVENT_PREFIX = messagePrefix(Solvent.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(SOLVENT_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminé",
        messageSource.getMessage(SOLVENT_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Acetonitrile() {
    assertEquals("Acetonitrile",
        messageSource.getMessage(SOLVENT_PREFIX + ACETONITRILE.name(), null, Locale.ENGLISH));
    assertEquals("Acétonitrile",
        messageSource.getMessage(SOLVENT_PREFIX + ACETONITRILE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Methanol() {
    assertEquals("Methanol",
        messageSource.getMessage(SOLVENT_PREFIX + METHANOL.name(), null, Locale.ENGLISH));
    assertEquals("Méthanol",
        messageSource.getMessage(SOLVENT_PREFIX + METHANOL.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Chcl3() {
    assertEquals("CHCl\u2083",
        messageSource.getMessage(SOLVENT_PREFIX + CHCL3.name(), null, Locale.ENGLISH));
    assertEquals("CHCl\u2083",
        messageSource.getMessage(SOLVENT_PREFIX + CHCL3.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other",
        messageSource.getMessage(SOLVENT_PREFIX + OTHER.name(), null, Locale.ENGLISH));
    assertEquals("Autre",
        messageSource.getMessage(SOLVENT_PREFIX + OTHER.name(), null, Locale.FRENCH));
  }
}
