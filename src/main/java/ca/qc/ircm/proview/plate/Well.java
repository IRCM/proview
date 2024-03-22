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

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Locale;

/**
 * A plate well.
 */
@Entity
@DiscriminatorValue("WELL")
@GeneratePropertyNames
public class Well extends SampleContainer implements Data, Named, Serializable {
  private static final long serialVersionUID = 212003765334493656L;

  /**
   * Plate where this well is located.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private Plate plate;
  /**
   * Row where well is located on plate.
   */
  @Column(name = "wellrow", updatable = false, nullable = false)
  private int row;
  /**
   * Column where well is located on plate.
   */
  @Column(name = "wellcolumn", updatable = false, nullable = false)
  private int column;

  /**
   * Constructor to be used only by persistence layer.
   */
  Well() {
    this.row = 0;
    this.column = 0;
  }

  public Well(Long id) {
    super(id);
  }

  /**
   * Creates a new well.
   *
   * @param row
   *          row where well is located on plate
   * @param column
   *          column where well is located on plate
   */
  public Well(Integer row, Integer column) {
    this.row = row;
    this.column = column;
  }

  @Override
  public String toString() {
    return "Well [row=" + row + ", column=" + column + ", getId()=" + getId() + "]";
  }

  @Override
  public String getName() {
    return Plate.rowLabel(row) + "-" + Plate.columnLabel(column);
  }

  @Override
  public String getFullName() {
    AppResources resources = new AppResources(Well.class, Locale.getDefault());
    return resources.message("fullname", plate.getName(), Plate.rowLabel(row),
        Plate.columnLabel(column));
  }

  @Override
  public SampleContainerType getType() {
    return SampleContainerType.WELL;
  }

  public Plate getPlate() {
    return plate;
  }

  public void setPlate(Plate plate) {
    this.plate = plate;
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }
}
