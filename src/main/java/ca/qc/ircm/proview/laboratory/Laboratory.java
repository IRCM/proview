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

package ca.qc.ircm.proview.laboratory;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.user.User;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Laboratory where a user works.
 */
@Entity
@Table(name = "laboratory")
public class Laboratory implements Data, Serializable {

  private static final long serialVersionUID = 8294913257061846746L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Name.
   */
  @Column(name = "name", nullable = false)
  @Size(max = 255)
  private String name;
  /**
   * Organization / company.
   */
  @Column(name = "organization", nullable = false)
  @Size(max = 255)
  private String organization;
  /**
   * Managers of this laboratory.
   */
  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "laboratorymanager",
      joinColumns = @JoinColumn(name = "laboratoryId") ,
      inverseJoinColumns = @JoinColumn(name = "userId") )
  private List<User> managers;

  public Laboratory() {
  }

  public Laboratory(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Laboratory [id=" + id + ", organization=" + organization + ", name=" + name + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<User> getManagers() {
    return managers;
  }

  public void setManagers(List<User> managers) {
    this.managers = managers;
  }
}