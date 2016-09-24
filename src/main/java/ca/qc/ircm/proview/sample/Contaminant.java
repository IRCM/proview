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

package ca.qc.ircm.proview.sample;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A Contaminant in a Sample.
 */
@Entity
@Table(name = "contaminant")
public class Contaminant implements Data, Named, Cloneable, Serializable {
  private static final long serialVersionUID = 7596363652613794846L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Contaminant name.
   */
  @Column(name = "name", nullable = false)
  @Size(max = 100)
  private String name;
  /**
   * Quantity of Contaminant.
   */
  @Column(name = "quantity")
  @Size(max = 100)
  private String quantity;
  /**
   * Comments about contaminant.
   */
  @Column(name = "comments")
  private String comments;
  /**
   * True if contaminant was deleted.
   */
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Contaminant) {
      Contaminant other = (Contaminant) obj;
      return this.name != null && this.name.equalsIgnoreCase(other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.name != null ? this.name.toUpperCase().hashCode() : 0;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Contaminant(");
    builder.append(id);
    builder.append(",");
    builder.append(name);
    builder.append(")");
    return builder.toString();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
