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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.plate.PlateSpotService.SimpleSpotLocation;
import ca.qc.ircm.proview.plate.PlateSpotService.SpotLocation;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Treatment Plate.
 */
@Entity
@Table(name = "plate")
public class Plate implements Data, Serializable, Named, Comparable<Plate> {

  /**
   * Plate types.
   */
  public static enum Type {
    PM(8, 12), G(8, 12), A(8, 12);
    Type(int rowCount, int columnCount) {
      this.rowCount = rowCount;
      this.columnCount = columnCount;
    }

    /**
     * Number of rows in plate.
     */
    private int rowCount;
    /**
     * Number of columns in plate.
     */
    private int columnCount;

    public int getRowCount() {
      return rowCount;
    }

    public int getColumnCount() {
      return columnCount;
    }
  }

  private static final long serialVersionUID = 342820436770987756L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Name of this Plate.
   */
  @Column(name = "name", unique = true, nullable = false)
  private String name;
  /**
   * Plate's type.
   */
  @Column(name = "type", nullable = false)
  @Enumerated(STRING)
  private Type type;
  /**
   * Time when analysis was inserted.
   */
  @Column(name = "insertTime", nullable = false)
  private Instant insertTime;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "plate")
  private List<PlateSpot> spots;

  public Plate() {
  }

  public Plate(Long id) {
    this.id = id;
  }

  public Plate(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Returns number of rows for Sample matrix.
   *
   * @return number of rows for Sample matrix.
   */
  public int getRowCount() {
    return type.rowCount;
  }

  /**
   * Returns number of columns for Sample matrix.
   *
   * @return number of columns for Sample matrix.
   */
  public int getColumnCount() {
    return type.columnCount;
  }

  /**
   * Returns spot at specified location.
   *
   * @param row
   *          row
   * @param column
   *          column
   * @return spot at specified location
   * @throws IllegalStateException
   *           invalid row or column for this plate
   */
  @Nonnull
  public PlateSpot spot(int row, int column) {
    if (row < 0 || row >= getRowCount()) {
      throw new IllegalArgumentException("Row " + row
          + " is greater/equal then the number of rows in this plate (" + getRowCount() + ")");
    }
    if (column < 0 || column >= getColumnCount()) {
      throw new IllegalArgumentException(
          "Column " + column + " is greater/equal then the number of columns in this plate ("
              + getColumnCount() + ")");
    }
    for (PlateSpot spot : this.spots) {
      if (spot.getRow() == row && spot.getColumn() == column) {
        return spot;
      }
    }
    throw new IllegalStateException("No spot could be found at location " + row + "-" + column);
  }

  /**
   * Returns spots from the 'from' location up to the 'to' location.
   *
   * @param from
   *          starting location
   * @param to
   *          end location (inclusive)
   * @return spots from the 'from' location up to the 'to' location
   */
  public List<PlateSpot> spots(SpotLocation from, SpotLocation to) {
    // Find all locations that are to be included.
    Collection<SpotLocation> locations = new HashSet<SpotLocation>();
    for (int column = from.getColumn(); column <= to.getColumn(); column++) {
      int endRow = column == to.getColumn() ? to.getRow() : this.getRowCount() - 1;
      for (int row = column == from.getColumn() ? from.getRow() : 0; row <= endRow; row++) {
        locations.add(new SimpleSpotLocation(row, column));
      }
    }
    // Add all spots that are on locations to include.
    List<PlateSpot> spots = new ArrayList<PlateSpot>();
    for (PlateSpot spot : getSpots()) {
      SpotLocation spotLocation = new SimpleSpotLocation(spot.getRow(), spot.getColumn());
      if (locations.contains(spotLocation)) {
        spots.add(spot);
      }
    }
    Collections.sort(spots, new PlateSpotComparator(PlateSpotComparator.Compare.LOCATION));
    return spots;
  }

  /**
   * Returns spots in column.
   *
   * @param index
   *          column
   * @return spots in column
   */
  public List<PlateSpot> column(int index) {
    List<PlateSpot> spots = new ArrayList<PlateSpot>();
    for (PlateSpot spot : this.spots) {
      if (spot.getColumn() == index) {
        spots.add(spot);
      }
    }
    Collections.sort(spots, new PlateSpotComparator(PlateSpotComparator.Compare.LOCATION));
    return spots;
  }

  /**
   * Returns number of empty spots on plate.
   *
   * @return number of empty spots on plate
   */
  public Integer getEmptySpotCount() {
    Integer count = 0;
    for (PlateSpot spot : this.spots) {
      if (spot.getSample() == null && !spot.isBanned()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns number of spots containing a sample on plate.
   *
   * @return number of spots containing a sample on plate
   */
  public Integer getSampleCount() {
    Integer count = 0;
    for (PlateSpot spot : this.spots) {
      if (spot.getSample() != null && !spot.isBanned()) {
        count++;
      }
    }
    return count;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Plate) {
      Plate other = (Plate) obj;
      return this.name != null && this.name.equalsIgnoreCase(other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.name == null ? 0 : this.name.toUpperCase().hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Plate(");
    buff.append(id);
    buff.append(",");
    buff.append(name);
    buff.append(",");
    buff.append(type);
    buff.append(")");
    return buff.toString();
  }

  @Override
  public int compareTo(Plate other) {
    return name.compareToIgnoreCase(other.getName());
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Instant getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(Instant insertTime) {
    this.insertTime = insertTime;
  }

  public List<PlateSpot> getSpots() {
    return spots;
  }

  public void setSpots(List<PlateSpot> spots) {
    this.spots = spots;
  }
}
