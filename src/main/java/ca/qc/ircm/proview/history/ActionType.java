package ca.qc.ircm.proview.history;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Type of action done on data.
 */
public enum ActionType {
  /**
   * Data was just inserted.
   */
  INSERT,
  /**
   * Data was just updated.
   */
  UPDATE,
  /**
   * Data was just deleted.
   */
  DELETE;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(ActionType.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
