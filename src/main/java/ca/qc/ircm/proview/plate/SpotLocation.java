package ca.qc.ircm.proview.plate;

/**
 * Spot location on a plate.
 */
public class SpotLocation {
  /**
   * Spot's row.
   */
  private int row;
  /**
   * Spot's column.
   */
  private int column;

  public SpotLocation() {
  }

  public SpotLocation(int row, int column) {
    this.row = row;
    this.column = column;
  }

  public SpotLocation(PlateSpot spot) {
    this.row = spot.getRow();
    this.column = spot.getColumn();
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
