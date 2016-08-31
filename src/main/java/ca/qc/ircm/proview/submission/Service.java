package ca.qc.ircm.proview.submission;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Protemoics analysis Services.
 */
public enum Service {
  /**
   * LC/MS/MS analysis.
   */
  LC_MS_MS, /**
             * 2D-LC/MS/MS analysis.
             */
  TWO_DIMENSION_LC_MS_MS, /**
                           * Maldi/MS analysis.
                           */
  MALDI_MS, /**
             * Small molecule analysis.
             */
  SMALL_MOLECULE, /**
                   * Intact protein analysis.
                   */
  INTACT_PROTEIN;

  public String getLabel(Locale locale) {
    MessageResource resources = new MessageResource(Service.class, locale);
    return resources.message(name());
  }
}
