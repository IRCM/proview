package ca.qc.ircm.proview.client.platelayout;

import com.google.gwt.user.client.ui.Grid;

import java.util.List;
import java.util.logging.Logger;

/**
 * Plate layout widget.
 */
public class PlateLayoutWidget extends Grid {
  public static final String CLASSNAME = "v-plate-layout";
  public static final String HEADER_CLASSNAME = "v-plate-layout-header";
  public static final String COLUMN_HEADER_CLASSNAME = "v-plate-layout-header-column";
  public static final String ROW_HEADER_CLASSNAME = "v-plate-layout-header-row";
  public static final String WELL_CLASSNAME = "v-plate-layout-well";

  /**
   * Initializes plate layout widget.
   */
  public PlateLayoutWidget() {
    super(1, 1);
    setCellSpacing(0);
    setCellPadding(0);
    addStyleName(CLASSNAME);
    getCellFormatter().addStyleName(0, 0, HEADER_CLASSNAME);
  }

  @SuppressWarnings("unused")
  private Logger getLogger() {
    return Logger.getLogger(PlateLayoutWidget.class.getName());
  }

  /**
   * Resizes number of columns.
   *
   * @param columns
   *          number of columns
   */
  @Override
  public void resizeColumns(int columns) {
    int column = Math.max(numColumns, 1);
    super.resizeColumns(columns);
    if (numRows < 1) {
      return;
    }
    while (column < columns) {
      createColumnHeader(column);
      for (int row = 1; row < numRows; row++) {
        createWell(column, row);
      }
      column++;
    }
  }

  private void createColumnHeader(int column) {
    applyDefaultCellStyles(0, column);
    setText(0, column, String.valueOf((char) ('A' + column - 1)));
  }

  /**
   * Resizes number of rows.
   *
   * @param rows
   *          number of rows
   */
  @Override
  public void resizeRows(int rows) {
    int row = Math.max(numRows, 1);
    super.resizeRows(rows);
    if (numColumns < 1) {
      return;
    }
    while (row < rows) {
      createRowHeader(row);
      for (int column = 1; column < numColumns; column++) {
        createWell(column, row);
      }
      row++;
    }
  }

  private void createRowHeader(int row) {
    applyDefaultCellStyles(row, 0);
    setText(row, 0, String.valueOf(row));
  }

  private void createWell(int column, int row) {
    applyDefaultCellStyles(row, column);
  }

  private void applyDefaultCellStyles(int row, int column) {
    if (row == 0 || column == 0) {
      getCellFormatter().addStyleName(row, column, HEADER_CLASSNAME);
      if (row == 0 && column > 0) {
        getCellFormatter().addStyleName(row, column, COLUMN_HEADER_CLASSNAME);
      }
      if (row > 0 && column == 0) {
        getCellFormatter().addStyleName(row, column, ROW_HEADER_CLASSNAME);
      }
    } else {
      getCellFormatter().addStyleName(row, column, WELL_CLASSNAME);
    }
  }

  /**
   * Sets styles for cell.
   *
   * @param row
   *          row
   * @param column
   *          column
   * @param styles
   *          styles to set
   */
  public void setCellStyles(int row, int column, List<String> styles) {
    String styleName = getCellFormatter().getStyleName(row, column);
    if (!styleName.isEmpty()) {
      getCellFormatter().removeStyleName(row, column, styleName);
    }
    applyDefaultCellStyles(row, column);
    if (styles != null) {
      for (String style : styles) {
        getCellFormatter().addStyleName(row, column, style);
      }
    }
  }

  /**
   * Clear all well widgets.
   */
  public void clearWells() {
    for (int column = 1; column < numColumns; column++) {
      for (int row = 1; row < numRows; row++) {
        setWidget(row, column, null);
      }
    }
  }
}
