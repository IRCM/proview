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
