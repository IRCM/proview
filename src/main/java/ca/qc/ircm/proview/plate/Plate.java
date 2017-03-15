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
   * Initializes spots, if type property is not null and spots property is null.
   */
  public void initSpots() {
    if (type != null && spots == null) {
      List<PlateSpot> spots = new ArrayList<>();
      for (int row = 0; row < getRowCount(); row++) {
        for (int column = 0; column < getColumnCount(); column++) {
          PlateSpot plateSpot = new PlateSpot(row, column);
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
    if (this.spots != null) {
      for (PlateSpot spot : this.spots) {
        if (spot.getRow() == row && spot.getColumn() == column) {
          return spot;
        }
      }
    }
    return null;
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
    Predicate<PlateSpot> afterOrEqualsFrom =
        s -> (s.getColumn() == from.getColumn() && s.getRow() >= from.getRow())
            || s.getColumn() > from.getColumn();
    Predicate<PlateSpot> beforeOrEqualsTo =
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
  public List<PlateSpot> column(int index) {
    List<PlateSpot> spots = new ArrayList<>();
    if (this.spots != null) {
      for (PlateSpot spot : this.spots) {
        if (spot.getColumn() == index) {
          spots.add(spot);
        }
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
  public int getEmptySpotCount() {
    int count = 0;
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
  public int getSampleCount() {
    int count = 0;
    for (PlateSpot spot : this.spots) {
      if (spot.getSample() != null && !spot.isBanned()) {
        count++;
      }
    }
    return count;
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

  public List<PlateSpot> getSpots() {
    return spots;
  }

  public void setSpots(List<PlateSpot> spots) {
    this.spots = spots;
  }
}
