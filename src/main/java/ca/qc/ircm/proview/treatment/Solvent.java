package ca.qc.ircm.proview.treatment;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Solvents used in our lab.
 */
public enum Solvent {
  ACETONITRILE, METHANOL, CHCL3, OTHER;

  private static final String MESSAGE_BASE = "solvent.";

  public String getLabel(Locale locale) {
    ResourceBundle bundle = ResourceBundle.getBundle(Solvent.class.getName(), locale);
    return bundle.getString(MESSAGE_BASE + name());
  }

  public static String getNullLabel(Locale locale) {
    ResourceBundle bundle = ResourceBundle.getBundle(Solvent.class.getName(), locale);
    return bundle.getString(MESSAGE_BASE + "NULL");
  }
}
