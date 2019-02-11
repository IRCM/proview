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

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.Sample;
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * Treatment Plate.
 */
@Entity
@Table(name = Plate.TABLE_NAME)
@GeneratePropertyNames
public class Plate implements Data, Serializable, Named {
  public static final String TABLE_NAME = "plate";
  public static final int DEFAULT_COLUMN_COUNT = 12;
  public static final int DEFAULT_ROW_COUNT = 8;
  public static final int DEFAULT_PLATE_SIZE = DEFAULT_COLUMN_COUNT * DEFAULT_ROW_COUNT;
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
   * Number of columns.
   */
  @Column(name = "columnCount", nullable = false)
  @Min(1)
  private int columnCount = DEFAULT_COLUMN_COUNT;
  /**
   * Number of rows.
   */
  @Column(name = "rowCount", nullable = false)
  @Min(1)
  private int rowCount = DEFAULT_ROW_COUNT;
  /**
   * True if plate was submitted by a user.
   */
  @Column(name = "submission")
  private boolean submission;
  /**
   * Time when analysis was inserted.
   */
  @Column(name = "insertTime", nullable = false)
  private Instant insertTime;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "plate", orphanRemoval = true)
  private List<Well> wells;

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
   * Initializes wells, if wells property is null.
   */
  public void initWells() {
    if (wells == null) {
      List<Well> wells = new ArrayList<>();
      for (int row = 0; row < getRowCount(); row++) {
        for (int column = 0; column < getColumnCount(); column++) {
          Well well = new Well(row, column);
          well.setPlate(this);
          wells.add(well);
        }
      }
      setWells(wells);
    }
  }

  /**
   * Returns well at specified location.
   *
   * @param row
   *          row
   * @param column
   *          column
   * @return well at specified location, or null if plate has not well at this location
   */
  @Nonnull
  public Well well(int row, int column) {
    if (row < 0 || row >= getRowCount()) {
      throw new IllegalArgumentException("Row " + row
          + " is greater/equal then the number of rows in this plate (" + getRowCount() + ")");
    }
    if (column < 0 || column >= getColumnCount()) {
      throw new IllegalArgumentException(
          "Column " + column + " is greater/equal then the number of columns in this plate ("
              + getColumnCount() + ")");
    }
    if (this.wells != null) {
      for (Well well : this.wells) {
        if (well.getRow() == row && well.getColumn() == column) {
          return well;
        }
      }
    }
    throw new IllegalArgumentException(
        "No well at coordinates " + Plate.rowLabel(row) + "-" + Plate.columnLabel(column));
  }

  /**
   * Returns wells from the 'from' location up to the 'to' location.
   *
   * @param from
   *          starting location
   * @param to
   *          end location (inclusive)
   * @return wells from the 'from' location up to the 'to' location
   */
  public List<Well> wells(WellLocation from, WellLocation to) {
    Predicate<Well> afterOrEqualsFrom =
        s -> (s.getColumn() == from.getColumn() && s.getRow() >= from.getRow())
            || s.getColumn() > from.getColumn();
    Predicate<Well> beforeOrEqualsTo =
        s -> (s.getColumn() == to.getColumn() && s.getRow() <= to.getRow())
            || s.getColumn() < to.getColumn();
    return wells.stream().filter(afterOrEqualsFrom.and(beforeOrEqualsTo))
        .collect(Collectors.toList());
  }

  /**
   * Returns wells containing sample.
   *
   * @param sample
   *          sample
   * @return wells containing sample
   */
  public List<Well> wellsContainingSample(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    return wells.stream().filter(well -> well.getSample() != null)
        .filter(well -> well.getSample().getId().equals(sample.getId()))
        .collect(Collectors.toList());
  }

  /**
   * Returns wells in column.
   *
   * @param index
   *          column
   * @return wells in column
   */
  public List<Well> column(int index) {
    List<Well> wells = new ArrayList<>();
    if (this.wells != null) {
      for (Well well : this.wells) {
        if (well.getColumn() == index) {
          wells.add(well);
        }
      }
    }
    Collections.sort(wells, new WellComparator(WellComparator.Compare.LOCATION));
    return wells;
  }

  /**
   * Returns number of empty wells on plate.
   *
   * @return number of empty wells on plate
   */
  public int getEmptyWellCount() {
    int count = 0;
    for (Well well : this.wells) {
      if (well.getSample() == null && !well.isBanned()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns number of wells containing a sample on plate.
   *
   * @return number of wells containing a sample on plate
   */
  public int getSampleCount() {
    int count = 0;
    for (Well well : this.wells) {
      if (well.getSample() != null && !well.isBanned()) {
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
    return "Plate [id=" + id + ", name=" + name + "]";
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

  public Instant getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(Instant insertTime) {
    this.insertTime = insertTime;
  }

  public List<Well> getWells() {
    return wells;
  }

  public void setWells(List<Well> wells) {
    this.wells = wells;
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

  public boolean isSubmission() {
    return submission;
  }

  public void setSubmission(boolean submission) {
    this.submission = submission;
  }
}
