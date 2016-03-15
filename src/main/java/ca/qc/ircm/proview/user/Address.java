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

package ca.qc.ircm.proview.user;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * User's address.
 */
@Entity
@Table(name = "address")
public class Address implements Serializable {
  private static final long serialVersionUID = 6036866850504348215L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Address.
   */
  @Column(name = "address", nullable = false)
  @Size(max = 150)
  private String address;
  /**
   * Second line of address.
   */
  @Column(name = "address2")
  @Size(max = 150)
  private String address2;
  /**
   * Town.
   */
  @Column(name = "town", nullable = false)
  @Size(max = 50)
  private String town;
  /**
   * State / province.
   */
  @Column(name = "state", nullable = false)
  @Size(max = 50)
  private String state;
  /**
   * Country.
   */
  @Column(name = "country", nullable = false)
  @Size(max = 50)
  private String country;
  /**
   * Postal code.
   */
  @Column(name = "postalCode", nullable = false)
  @Size(max = 50)
  private String postalCode;
  /**
   * True if address is billing address.
   */
  @Column(name = "billing", nullable = false)
  private boolean billing;

  @Override
  public String toString() {
    return "Address [id=" + id + ", address=" + address + "]";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public boolean isBilling() {
    return billing;
  }

  public void setBilling(boolean billing) {
    this.billing = billing;
  }
}
