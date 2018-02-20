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

package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.treatment.TreatmentSample;
import ca.qc.ircm.proview.treatment.TreatmentType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

/**
 * A sample that was fractionated.
 */
@Entity
@DiscriminatorValue("FRACTIONATION")
public class Fraction extends TreatmentSample implements Named {
  /**
   * Fractionation.
   */
  @ManyToOne
  @JoinColumn(name = "treatmentId", nullable = false)
  private Fractionation fractionation;
  /**
   * Sample's destination container.
   */
  @ManyToOne
  @JoinColumn(name = "destinationContainerId", nullable = false)
  private SampleContainer destinationContainer;
  /**
   * Fraction index number that is appended when showing LIMS number of fraction.
   */
  @Column(name = "position", nullable = false)
  private Integer position;
  /**
   * Fraction number. Used with {@link TreatmentType#MUDPIT}.
   */
  @Column(name = "number")
  private Integer number;
  /**
   * PI interval. Used with {@link TreatmentType#PI}
   */
  @Column(name = "piInterval")
  @Size(max = 255)
  private String piInterval;

  @Override
  public String getName() {
    if (getSample() != null && getSample().getName() != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(getSample().getName());
      builder.append(".F");
      builder.append(position);
      return builder.toString();
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("fraction(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public String getPiInterval() {
    return piInterval;
  }

  public void setPiInterval(String piInterval) {
    this.piInterval = piInterval;
  }

  public SampleContainer getDestinationContainer() {
    return destinationContainer;
  }

  public void setDestinationContainer(SampleContainer destinationContainer) {
    this.destinationContainer = destinationContainer;
  }

  public Fractionation getFractionation() {
    return fractionation;
  }

  public void setFractionation(Fractionation fractionation) {
    this.fractionation = fractionation;
  }
}
