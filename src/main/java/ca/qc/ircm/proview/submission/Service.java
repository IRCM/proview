package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.AppResources;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Protemoics analysis Services.
 */
public enum Service {
  /**
   * LC/MS/MS analysis.
   */
  LC_MS_MS(true),
  /**
   * 2D-LC/MS/MS analysis.
   */
  TWO_DIMENSION_LC_MS_MS(false),
  /**
   * Maldi/MS analysis.
   */
  MALDI_MS(false),
  /**
   * Small molecule analysis.
   */
  SMALL_MOLECULE(true),
  /**
   * Intact protein analysis.
   */
  INTACT_PROTEIN(true);

  public final boolean available;

  Service(boolean available) {
    this.available = available;
  }

  public static List<Service> availables() {
    return Stream.of(Service.values()).filter(service -> service.available)
        .collect(Collectors.toList());
  }

  private static AppResources getResources(Locale locale) {
    return new AppResources(Service.class, locale);
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
