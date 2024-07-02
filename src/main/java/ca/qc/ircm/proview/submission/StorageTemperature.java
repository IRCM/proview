package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Available storage temperatures.
 */
public enum StorageTemperature {
  MEDIUM(4), LOW(-20);

  StorageTemperature(int temperature) {
    this.temperature = temperature;
  }

  /**
   * Real temperature of this enum.
   */
  private int temperature;

  public int getTemperature() {
    return temperature;
  }

  private static AppResources getResources(Locale locale) {
    return new AppResources(StorageTemperature.class, locale);
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