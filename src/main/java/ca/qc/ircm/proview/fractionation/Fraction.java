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
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("fraction(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  public Fractionation getFractionation() {
    return fractionation;
  }

  public void setFractionation(Fractionation fractionation) {
    this.fractionation = fractionation;
  }
}
