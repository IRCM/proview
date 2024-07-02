package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.AppResources;
import java.util.Locale;

/**
 * Sample support.
 */
public enum SampleType {
  /**
   * Sample is dry.
   */
  DRY,
  /**
   * Sample is in solution.
   */
  SOLUTION,
  /**
   * Sample is in a Gel.
   */
  GEL, BIOID_BEADS, MAGNETIC_BEADS, AGAROSE_BEADS;

  private static AppResources getResources(Locale locale) {
    return new AppResources(SampleType.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }

  public boolean isDry() {
    return this == DRY;
  }

  public boolean isSolution() {
    return this == SOLUTION || this == BIOID_BEADS || this == MAGNETIC_BEADS
        || this == AGAROSE_BEADS;
  }

  public boolean isGel() {
    return this == GEL;
  }

  public boolean isBeads() {
    return this == BIOID_BEADS || this == MAGNETIC_BEADS || this == AGAROSE_BEADS;
  }
}
