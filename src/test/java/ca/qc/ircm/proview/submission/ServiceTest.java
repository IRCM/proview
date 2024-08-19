package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.MALDI_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.Service.TWO_DIMENSION_LC_MS_MS;
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
 * Tests for {@link Service}.
 */
@NonTransactionalTestAnnotations
public class ServiceTest {
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void availables() {
    assertTrue(LC_MS_MS.available);
    assertFalse(TWO_DIMENSION_LC_MS_MS.available);
    assertFalse(MALDI_MS.available);
    assertTrue(SMALL_MOLECULE.available);
    assertTrue(INTACT_PROTEIN.available);

    List<Service> availables = Service.availables();
    assertEquals(3, availables.size());
    assertEquals(LC_MS_MS, availables.get(0));
    assertEquals(SMALL_MOLECULE, availables.get(1));
    assertEquals(INTACT_PROTEIN, availables.get(2));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(SERVICE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminé",
        messageSource.getMessage(SERVICE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel() {
    Locale locale = Locale.CANADA;

    assertEquals("LC/MS/MS",
        messageSource.getMessage(SERVICE_PREFIX + LC_MS_MS.name(), null, locale));
    assertEquals("2D-LC/MS/MS (MudPit)",
        messageSource.getMessage(SERVICE_PREFIX + TWO_DIMENSION_LC_MS_MS.name(), null, locale));
    assertEquals("MALDI/MS",
        messageSource.getMessage(SERVICE_PREFIX + MALDI_MS.name(), null, locale));
    assertEquals("Small molecule",
        messageSource.getMessage(SERVICE_PREFIX + SMALL_MOLECULE.name(), null, locale));
    assertEquals("Intact protein",
        messageSource.getMessage(SERVICE_PREFIX + INTACT_PROTEIN.name(), null, locale));
  }

  @Test
  public void getLabel_French() {
    Locale locale = Locale.CANADA_FRENCH;

    assertEquals("LC/MS/MS",
        messageSource.getMessage(SERVICE_PREFIX + LC_MS_MS.name(), null, locale));
    assertEquals("2D-LC/MS/MS (MudPit)",
        messageSource.getMessage(SERVICE_PREFIX + TWO_DIMENSION_LC_MS_MS.name(), null, locale));
    assertEquals("MALDI/MS",
        messageSource.getMessage(SERVICE_PREFIX + MALDI_MS.name(), null, locale));
    assertEquals("Petite molécule",
        messageSource.getMessage(SERVICE_PREFIX + SMALL_MOLECULE.name(), null, locale));
    assertEquals("Protéine intacte",
        messageSource.getMessage(SERVICE_PREFIX + INTACT_PROTEIN.name(), null, locale));
  }
}
