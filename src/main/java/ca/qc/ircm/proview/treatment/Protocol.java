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

package ca.qc.ircm.proview.treatment;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A Protocol.
 */
@Entity
@Table(name = "protocol")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Protocol implements Data, Serializable, Named {
  /**
   * Protocol types.
   */
  public static enum Type {
    DIGESTION, ENRICHMENT;
  }

  private static final long serialVersionUID = -7624493017948317986L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Name of the Protocol.
   */
  @Column(name = "name", unique = true, nullable = false)
  @Size(max = 100)
  private String name;

  public Protocol() {
  }

  public Protocol(Long id) {
    this.id = id;
  }

  public Protocol(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Type of Protocol.
   *
   * @return type of protocol
   */
  public abstract Type getType();

  @Override
  public String toString() {
    return "Protocol [id=" + id + ", name=" + name + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
