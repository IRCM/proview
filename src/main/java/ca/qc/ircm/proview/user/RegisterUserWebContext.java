package ca.qc.ircm.proview.user;

import java.util.Locale;

/**
 * Web context for register user.
 */
public interface RegisterUserWebContext {
  /**
   * Returns URL that leads to validate user function. This URL must begin with with a
   * <code>/</code> and must begin with the context path, if applicable.
   *
   * @param locale
   *          adapt URL to specified locale
   * @return URL that leads to validate user function
   */
  public String getValidateUserUrl(Locale locale);
}