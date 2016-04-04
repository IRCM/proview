package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.PhoneNumberType.FAX;
import static ca.qc.ircm.proview.user.PhoneNumberType.MOBILE;
import static ca.qc.ircm.proview.user.PhoneNumberType.WORK;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.utils.MessageResource;
import org.junit.Test;

public class PhoneNumberTypeTest {
  MessageResource englishResource = new MessageResource(PhoneNumberType.class, ENGLISH);
  MessageResource frenchResource = new MessageResource(PhoneNumberType.class, FRENCH);

  @Test
  public void getLabel_WorkEnglish() {
    assertEquals(englishResource.message(WORK.name()), WORK.getLabel(ENGLISH));
  }

  @Test
  public void getLabel_WorkFrench() {
    assertEquals(frenchResource.message(WORK.name()), WORK.getLabel(FRENCH));
  }

  @Test
  public void getLabel_MobileEnglish() {
    assertEquals(englishResource.message(MOBILE.name()), MOBILE.getLabel(ENGLISH));
  }

  @Test
  public void getLabel_MobileFrench() {
    assertEquals(frenchResource.message(MOBILE.name()), MOBILE.getLabel(FRENCH));
  }

  @Test
  public void getLabel_FaxEnglish() {
    assertEquals(englishResource.message(FAX.name()), FAX.getLabel(ENGLISH));
  }

  @Test
  public void getLabel_FaxFrench() {
    assertEquals(frenchResource.message(FAX.name()), FAX.getLabel(FRENCH));
  }
}
