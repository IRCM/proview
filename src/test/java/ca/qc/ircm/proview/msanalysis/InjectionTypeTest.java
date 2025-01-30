package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.msanalysis.InjectionType.DIRECT_INFUSION;
import static ca.qc.ircm.proview.msanalysis.InjectionType.LC_MS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link InjectionType}.
 */
@NonTransactionalTestAnnotations
public class InjectionTypeTest {

  private static final String INJECTION_TYPE_PREFIX = messagePrefix(InjectionType.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(INJECTION_TYPE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminée",
        messageSource.getMessage(INJECTION_TYPE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Lcms() {
    assertEquals("LC/MS",
        messageSource.getMessage(INJECTION_TYPE_PREFIX + LC_MS.name(), null, Locale.ENGLISH));
    assertEquals("LC/MS",
        messageSource.getMessage(INJECTION_TYPE_PREFIX + LC_MS.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_DirectInfusion() {
    assertEquals("Direct infusion", messageSource
        .getMessage(INJECTION_TYPE_PREFIX + DIRECT_INFUSION.name(), null, Locale.ENGLISH));
    assertEquals("Infusion directe", messageSource
        .getMessage(INJECTION_TYPE_PREFIX + DIRECT_INFUSION.name(), null, Locale.FRENCH));
  }
}
