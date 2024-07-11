package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.treatment.FractionationType.MUDPIT;
import static ca.qc.ircm.proview.treatment.FractionationType.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link FractionationType}.
 */
@NonTransactionalTestAnnotations
public class FractionationTypeTest {
  private static final String FRACTIONATION_TYPE_PREFIX = messagePrefix(FractionationType.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Not applicable",
        messageSource.getMessage(FRACTIONATION_TYPE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminé",
        messageSource.getMessage(FRACTIONATION_TYPE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Mudpit() {
    assertEquals("Ions exchange column",
        messageSource.getMessage(FRACTIONATION_TYPE_PREFIX + MUDPIT.name(), null, Locale.ENGLISH));
    assertEquals("Colonne d'échange d'ions",
        messageSource.getMessage(FRACTIONATION_TYPE_PREFIX + MUDPIT.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Pi() {
    assertEquals("pI",
        messageSource.getMessage(FRACTIONATION_TYPE_PREFIX + PI.name(), null, Locale.ENGLISH));
    assertEquals("pI",
        messageSource.getMessage(FRACTIONATION_TYPE_PREFIX + PI.name(), null, Locale.FRENCH));
  }
}
