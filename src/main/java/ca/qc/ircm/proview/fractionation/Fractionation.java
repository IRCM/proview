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

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Fractionation of sample.
 */
@Entity
@DiscriminatorValue("FRACTIONATION")
@GeneratePropertyNames
public class Fractionation extends Treatment {
  private static final long serialVersionUID = 1416679934489708298L;

  public Fractionation() {
    super();
  }

  public Fractionation(Long id) {
    super(id);
  }

  @Override
  public TreatmentType getType() {
    return TreatmentType.FRACTIONATION;
  }
}
