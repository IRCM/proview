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

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Services for plate's spots.
 */
public interface PlateSpotService {
  /**
   * Spot location on a plate.
   */
  public static interface SpotLocation {
    /**
     * Returns spot's row.
     *
     * @return spot's row
     */
    public int getRow();

    /**
     * Returns spot's column.
     *
     * @return spot's column
     */
    public int getColumn();
  }

  /**
   * Simple implementation of {@link SpotLocation}.
   */
  public static class SimpleSpotLocation implements SpotLocation {
    public final int row;
    public final int column;

    public SimpleSpotLocation(PlateSpot spot) {
      this.row = spot.getRow();
      this.column = spot.getColumn();
    }

    public SimpleSpotLocation(int row, int column) {
      this.row = row;
      this.column = column;
    }

    @Override
    public int getRow() {
      return row;
    }

    @Override
    public int getColumn() {
      return column;
    }

    @Override
    public String toString() {
      return ((char) ('a' + row)) + "-" + (column + 1);
    }
  }

  /**
   * Selects spot from database.
   *
   * @param id
   *          database identifier of spot
   * @return spot
   */
  public PlateSpot get(Long id);

  /**
   * Returns PlateSpot on plate at specified location.
   *
   * @param plate
   *          spot's plate
   * @param location
   *          spot's location on plate
   * @return plateSpot on plate at specified location
   */
  public PlateSpot get(Plate plate, SpotLocation location);

  /**
   * Selects most recent spot where sample was put.
   *
   * @param sample
   *          sample
   * @return most recent spot where sample was put
   */
  public PlateSpot last(Sample sample);

  /**
   * Selects all plate's spots.
   *
   * @param plate
   *          plate
   * @return all plate's spots
   */
  public List<PlateSpot> all(Plate plate);

  /**
   * Returns spots where sample is located on plate.
   *
   * @param sample
   *          sample
   * @param plate
   *          plate
   * @return Spots where sample is located.
   */
  public List<PlateSpot> location(Sample sample, Plate plate);

}
