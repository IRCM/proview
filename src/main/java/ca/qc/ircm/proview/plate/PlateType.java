package ca.qc.ircm.proview.plate;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Plate types.
 */
public enum PlateType {
  PM(8, 12), G(8, 12), A(8, 12), SUBMISSION(8, 12);
  PlateType(int rowCount, int columnCount) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
  }

  /**
   * Number of rows in plate.
   */
  int rowCount;
  /**
   * Number of columns in plate.
   */
  int columnCount;

  public int getRowCount() {
    return rowCount;
  }

  public int getColumnCount() {
    return columnCount;
  }

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(PlateType.class, locale);
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
