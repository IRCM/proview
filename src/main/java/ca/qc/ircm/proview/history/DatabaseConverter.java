package ca.qc.ircm.proview.history;

/**
 * Converts an object to a string value to save in database.
 */
public interface DatabaseConverter {
  /**
   * Converts an object to a string value to save in database.
   *
   * @param value
   *          object to convert to string
   * @return string value to save in database
   */
  public String convert(Object value);
}
