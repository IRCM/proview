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

package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Digestion of some samples.
 */
@Entity
@DiscriminatorValue("DIGESTION")
public class Digestion extends Treatment<DigestedSample> {
  /**
   * Protocol used for digestion of sample.
   */
  @ManyToOne
  @JoinColumn(name = "protocolId", nullable = false)
  private DigestionProtocol protocol;

  public Digestion() {
  }

  public Digestion(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Digestion) {
      Digestion other = (Digestion) obj;
      return this.getId() != null && this.getId().equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.getId() == null ? 0 : this.getId().intValue();
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Digestion(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.DIGESTION;
  }

  public DigestionProtocol getProtocol() {
    return protocol;
  }

  public void setProtocol(DigestionProtocol protocol) {
    this.protocol = protocol;
  }
}
