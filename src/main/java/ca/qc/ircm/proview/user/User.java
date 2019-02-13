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

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * User of this program.
 */
@Entity
@Table(name = User.TABLE_NAME)
@GeneratePropertyNames
public class User implements Data, Named, Serializable {
  public static final String TABLE_NAME = "user";
  public static final String LOCALE_PREFERENCE = "locale";
  private static final long serialVersionUID = 4251923438573972499L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Email of User. This is also a unique id.
   */
  @Column(unique = true, nullable = false)
  @Size(max = 255)
  private String email;
  /**
   * First name.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String name;
  /**
   * True if user is registered and is currently valid.
   */
  @Column(nullable = false)
  private boolean valid = false;
  /**
   * True if User is active. An active user can log into program.
   */
  @Column(nullable = false)
  private boolean active = false;
  /**
   * True if User is an admin user.
   */
  @Column(nullable = false)
  private boolean admin = false;
  /**
   * Hashed password.
   */
  @Column
  @Size(max = 255)
  private String hashedPassword;
  /**
   * Password's salt.
   */
  @Column
  @Size(max = 255)
  private String salt;
  /**
   * Password's version.
   */
  @Column
  private Integer passwordVersion;
  /**
   * Number of sign attempts since last successful sign.
   */
  @Column
  private int signAttempts;
  /**
   * Last sign attempts (success or fail).
   */
  @Column
  private Instant lastSignAttempt;
  /**
   * Register time.
   */
  @Column
  private Instant registerTime;
  /**
   * User's prefered locale.
   */
  @Column
  private Locale locale;
  /**
   * User's laboratory.
   */
  @ManyToOne
  @JoinTable(name = "laboratoryuser")
  private Laboratory laboratory;
  /**
   * Address.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn
  private Address address;
  /**
   * Phone numbers.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn
  private List<PhoneNumber> phoneNumbers;

  public User() {
  }

  public User(Long id) {
    this.id = id;
  }

  public User(Long id, String email) {
    this.id = id;
    this.email = email;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", email=" + email + "]";
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public Laboratory getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(Laboratory laboratory) {
    this.laboratory = laboratory;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public Integer getPasswordVersion() {
    return passwordVersion;
  }

  public void setPasswordVersion(Integer passwordVersion) {
    this.passwordVersion = passwordVersion;
  }

  public List<PhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public int getSignAttempts() {
    return signAttempts;
  }

  public void setSignAttempts(int signAttempts) {
    this.signAttempts = signAttempts;
  }

  public Instant getLastSignAttempt() {
    return lastSignAttempt;
  }

  public void setLastSignAttempt(Instant lastSignAttempt) {
    this.lastSignAttempt = lastSignAttempt;
  }

  public Instant getRegisterTime() {
    return registerTime;
  }

  public void setRegisterTime(Instant registerTime) {
    this.registerTime = registerTime;
  }
}
