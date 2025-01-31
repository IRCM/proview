package ca.qc.ircm.proview.plate;

/**
 * Well location on a plate.
 */
public class WellLocation {

  /**
   * Well's row.
   */
  private int row;
  /**
   * Well's column.
   */
  private int column;

  public WellLocation() {
  }

  public WellLocation(int row, int column) {
    this.row = row;
    this.column = column;
  }

  public WellLocation(Well well) {
    this.row = well.getRow();
    this.column = well.getColumn();
  }

  @Override
  public String toString() {
    return ((char) ('a' + row)) + "-" + (column + 1);
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }
}
