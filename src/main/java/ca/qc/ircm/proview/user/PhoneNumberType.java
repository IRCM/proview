package ca.qc.ircm.proview.user;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Phone number type.
 */
public enum PhoneNumberType {
  WORK, MOBILE, FAX;

  public String getLabel(Locale locale) {
    AppResources resource = new AppResources(PhoneNumberType.class, locale);
    return resource.message(name());
  }
}
