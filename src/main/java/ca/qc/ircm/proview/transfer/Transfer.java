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

package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Transfer samples from one plate to another.
 */
@Entity
@DiscriminatorValue("TRANSFER")
@GeneratePropertyNames
public class Transfer extends Treatment {
  private static final long serialVersionUID = 2171898463655011446L;

  public Transfer() {
    super();
  }

  public Transfer(Long id) {
    super(id);
  }

  @Override
  public String toString() {
    return "Transfer [getId()=" + getId() + "]";
  }

  @Override
  public TreatmentType getType() {
    return TreatmentType.TRANSFER;
  }
}
