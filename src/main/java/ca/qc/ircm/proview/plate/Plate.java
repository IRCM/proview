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

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * Treatment Plate.
 */
@Entity
@Table(name = "plate")
public class Plate implements Data, Serializable, Named {

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
  private PlateType type;
  /**
   * Number of columns.
   */
  @Column(name = "columns", nullable = false)
  @Min(1)
  private int columnCount = 12;
  /**
   * Number of rows.
   */
  @Column(name = "rows", nullable = false)
  @Min(1)
  private int rowCount = 8;
  /**
   * Time when analysis was inserted.
   */
  @Column(name = "insertTime", nullable = false)
  private Instant insertTime;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "plate")
  private List<Well> spots;

  public Plate() {
    this(null);
  }

  public Plate(Long id) {
    this(id, null);
  }

  /**
   * Initializes plate.
   *
   * @param id
   *          id
   * @param name
   *          name
   */
  public Plate(Long id, String name) {
    this.id = id;
    this.name = name;
    this.columnCount = 12;
    this.rowCount = 8;
  }

  /**
   * Initializes spots, if spots property is null.
   */
  public void initSpots() {
    if (spots == null) {
      List<Well> spots = new ArrayList<>();
      for (int row = 0; row < getRowCount(); row++) {
        for (int column = 0; column < getColumnCount(); column++) {
          Well plateSpot = new Well(row, column);
          plateSpot.setPlate(this);
          spots.add(plateSpot);
        }
      }
      setSpots(spots);
    }
  }

  /**
   * Returns spot at specified location.
   *
   * @param row
   *          row
   * @param column
   *          column
   * @return spot at specified location, or null if plate has not spot at this location
   */
  @Nonnull
  public Well spot(int row, int column) {
    if (row < 0 || row >= getRowCount()) {
      throw new IllegalArgumentException("Row " + row
          + " is greater/equal then the number of rows in this plate (" + getRowCount() + ")");
    }
    if (column < 0 || column >= getColumnCount()) {
      throw new IllegalArgumentException(
          "Column " + column + " is greater/equal then the number of columns in this plate ("
              + getColumnCount() + ")");
    }
    if (this.spots != null) {
      for (Well spot : this.spots) {
        if (spot.getRow() == row && spot.getColumn() == column) {
          return spot;
        }
      }
    }
    throw new IllegalArgumentException(
        "No spot at coordinates " + Plate.rowLabel(row) + "-" + Plate.rowLabel(column));
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
  public List<Well> spots(WellLocation from, WellLocation to) {
    Predicate<Well> afterOrEqualsFrom =
        s -> (s.getColumn() == from.getColumn() && s.getRow() >= from.getRow())
            || s.getColumn() > from.getColumn();
    Predicate<Well> beforeOrEqualsTo =
        s -> (s.getColumn() == to.getColumn() && s.getRow() <= to.getRow())
            || s.getColumn() < to.getColumn();
    return spots.stream().filter(afterOrEqualsFrom.and(beforeOrEqualsTo))
        .collect(Collectors.toList());
  }

  /**
   * Returns spots in column.
   *
   * @param index
   *          column
   * @return spots in column
   */
  public List<Well> column(int index) {
    List<Well> spots = new ArrayList<>();
    if (this.spots != null) {
      for (Well spot : this.spots) {
        if (spot.getColumn() == index) {
          spots.add(spot);
        }
      }
    }
    Collections.sort(spots, new WellComparator(WellComparator.Compare.LOCATION));
    return spots;
  }

  /**
   * Returns number of empty spots on plate.
   *
   * @return number of empty spots on plate
   */
  public int getEmptySpotCount() {
    int count = 0;
    for (Well spot : this.spots) {
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
  public int getSampleCount() {
    int count = 0;
    for (Well spot : this.spots) {
      if (spot.getSample() != null && !spot.isBanned()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns row label.
   *
   * @param row
   *          row
   * @return row label
   */
  public static String rowLabel(int row) {
    StringBuilder rowName = new StringBuilder();
    while (row >= 26) {
      rowName.append((char) ('A' + row % 26));
      row = row / 26;
      row--;
    }
    rowName.append((char) ('A' + row % 26));
    return rowName.reverse().toString();
  }

  /**
   * Returns column label.
   *
   * @param column
   *          column
   * @return column label
   */
  public static String columnLabel(int column) {
    return Integer.toString(column + 1);
  }

  @Override
  public String toString() {
    return "Plate [id=" + id + ", name=" + name + ", type=" + type + "]";
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

  public PlateType getType() {
    return type;
  }

  public void setType(PlateType type) {
    this.type = type;
  }

  public Instant getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(Instant insertTime) {
    this.insertTime = insertTime;
  }

  public List<Well> getSpots() {
    return spots;
  }

  public void setSpots(List<Well> spots) {
    this.spots = spots;
  }

  public int getColumnCount() {
    return columnCount;
  }

  public void setColumnCount(int columnCount) {
    this.columnCount = columnCount;
  }

  public int getRowCount() {
    return rowCount;
  }

  public void setRowCount(int rowCount) {
    this.rowCount = rowCount;
  }
}
