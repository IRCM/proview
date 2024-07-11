package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.PhoneNumberType.FAX;
import static ca.qc.ircm.proview.user.PhoneNumberType.MOBILE;
import static ca.qc.ircm.proview.user.PhoneNumberType.WORK;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link PhoneNumberType}.
 */
@NonTransactionalTestAnnotations
public class PhoneNumberTypeTest {
  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getLabel_WorkEnglish() {
    assertEquals("Work",
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + WORK.name(), null, ENGLISH));
  }

  @Test
  public void getLabel_WorkFrench() {
    assertEquals("Travail",
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + WORK.name(), null, FRENCH));
  }

  @Test
  public void getLabel_MobileEnglish() {
    assertEquals("Mobile",
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + MOBILE.name(), null, ENGLISH));
  }

  @Test
  public void getLabel_MobileFrench() {
    assertEquals("Mobile",
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + MOBILE.name(), null, FRENCH));
  }

  @Test
  public void getLabel_FaxEnglish() {
    assertEquals("Fax",
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + FAX.name(), null, ENGLISH));
  }

  @Test
  public void getLabel_FaxFrench() {
    assertEquals("Fax",
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + FAX.name(), null, FRENCH));
  }
}
