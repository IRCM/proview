package ca.qc.ircm.proview.files;

import java.util.List;
import java.util.Locale;

/**
 * Guidelines' configuration.
 */
public interface GuidelinesConfiguration {
  /**
   * Returns guideline categories for locale.
   *
   * @param locale
   *          locale
   * @return guideline categories for locale
   */
  public List<Category> categories(Locale locale);
}
