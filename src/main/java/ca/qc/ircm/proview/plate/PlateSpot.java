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

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A Plate spot.
 */
@Entity
@DiscriminatorValue("SPOT")
public class PlateSpot extends SampleContainer implements Data, Named, Serializable {
  private static final long serialVersionUID = 212003765334493656L;

  /**
   * Plate where this PlateSpot is located.
   */
  @ManyToOne
  @JoinColumn(name = "plateId", nullable = false)
  private Plate plate;
  /**
   * Row where spot is located on plate.
   */
  @Column(name = "locationRow", updatable = false, nullable = false)
  private int row;
  /**
   * Column where spot is located on plate.
   */
  @Column(name = "locationColumn", updatable = false, nullable = false)
  private int column;

  /**
   * Constructor to be used only by persistence layer.
   */
  PlateSpot() {
    this.row = 0;
    this.column = 0;
  }

  public PlateSpot(Long id) {
    super(id);
  }

  /**
   * Creates a new PlateSpot.
   *
   * @param row
   *          row where spot is located on plate
   * @param column
   *          column where spot is located on plate
   */
  public PlateSpot(Integer row, Integer column) {
    this.row = row;
    this.column = column;
  }

  @Override
  public String toString() {
    return "PlateSpot [row=" + row + ", column=" + column + ", getId()=" + getId() + "]";
  }

  @Override
  public String getName() {
    return ((char) ('a' + this.getRow())) + "-" + (this.getColumn() + 1);
  }

  @Override
  public SampleContainer.Type getType() {
    return SampleContainer.Type.SPOT;
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
