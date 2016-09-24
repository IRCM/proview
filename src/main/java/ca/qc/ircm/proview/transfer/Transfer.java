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

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Transfer samples from one plate to another.
 */
@Entity
@DiscriminatorValue("TRANSFER")
public class Transfer extends Treatment<SampleTransfer> {
  public Transfer() {
    super();
  }

  public Transfer(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Transfer) {
      Transfer other = (Transfer) obj;
      return this.getId() != null && this.getId().equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getId() != null ? getId().intValue() : 0;
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Transfer(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.TRANSFER;
  }
}
