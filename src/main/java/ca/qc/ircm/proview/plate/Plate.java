package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.submission.Submission;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

/**
 * Treatment Plate.
 */
@Entity
@Table(name = Plate.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class Plate implements Data, Serializable, Named {
  public static final String TABLE_NAME = "plate";
  public static final int DEFAULT_COLUMN_COUNT = 12;
  public static final int DEFAULT_ROW_COUNT = 8;
  public static final int DEFAULT_PLATE_SIZE = DEFAULT_COLUMN_COUNT * DEFAULT_ROW_COUNT;
  @Serial
  private static final long serialVersionUID = 342820436770987756L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Name of this Plate.
   */
  @Column(unique = true, nullable = false)
  private String name;
  /**
   * Number of columns.
   */
  @Column(nullable = false)
  @Min(1)
  private int columnCount = DEFAULT_COLUMN_COUNT;
  /**
   * Number of rows.
   */
  @Column(nullable = false)
  @Min(1)
  private int rowCount = DEFAULT_ROW_COUNT;
  /**
   * True if plate was submitted by a user.
   */
  @ManyToOne
  @JoinColumn
  private Submission submission;
  /**
   * Time when analysis was inserted.
   */
  @Column(nullable = false)
  private LocalDateTime insertTime;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "plate", orphanRemoval = true)
  private List<Well> wells;

  public Plate() {
  }

  public Plate(long id) {
    this.id = id;
  }

  /**
   * Initializes plate.
   *
   * @param id
   *          id
   * @param name
   *          name
   */
  public Plate(long id, String name) {
    this.id = id;
    this.name = name;
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
    return wells.stream().filter(well -> well.getSample() != null)
        .filter(well -> well.getSample().getId() == sample.getId()).collect(Collectors.toList());
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
    wells.sort(new WellComparator(WellComparator.Compare.LOCATION));
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
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public LocalDateTime getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(LocalDateTime insertTime) {
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

  @Nullable
  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(@Nullable Submission submission) {
    this.submission = submission;
  }
}
