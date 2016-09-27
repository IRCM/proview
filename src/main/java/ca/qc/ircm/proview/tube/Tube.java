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

package ca.qc.ircm.proview.tube;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Tube where sample is put for some treatment.
 */
@Entity
@DiscriminatorValue("TUBE")
public class Tube extends SampleContainer implements Data, Named, Serializable {
  private static final long serialVersionUID = 2723772707033001099L;

  /**
   * Name used to identify tube.
   */
  @Column(name = "name", unique = true, nullable = false)
  private String name;

  public Tube() {
  }

  public Tube(Long id) {
    super(id);
  }

  public Tube(Long id, String name) {
    super(id);
    this.name = name;
  }

  @Override
  public String toString() {
    return "Tube [name=" + name + ", getId()=" + getId() + "]";
  }

  @Override
  public SampleContainer.Type getType() {
    return SampleContainer.Type.TUBE;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
