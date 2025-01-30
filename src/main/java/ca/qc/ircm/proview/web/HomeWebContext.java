package ca.qc.ircm.proview.web;

import java.util.Locale;

/**
 * Web context for home URLs.
 */
public interface HomeWebContext {

  /**
   * Returns URL that leads to home page. This URL must begin with with a <code>/</code> and must
   * begin with the context path, if applicable.
   *
   * @param locale adapt URL to specified locale
   * @return URL that leads to home page
   */
  String getHomeUrl(Locale locale);
}