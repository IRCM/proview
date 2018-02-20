/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
