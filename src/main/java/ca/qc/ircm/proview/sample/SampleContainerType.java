package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Type of {@link SampleContainer}.
 */
public enum SampleContainerType {
  TUBE, WELL;

  private static AppResources getResources(Locale locale) {
    return new AppResources(SampleContainerType.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }
}
