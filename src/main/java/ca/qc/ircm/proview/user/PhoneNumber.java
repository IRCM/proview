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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.utils.MessageResource;
import java.io.Serializable;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Phone number.
 */
@Entity
@Table(name = PhoneNumber.TABLE_NAME)
@GeneratePropertyNames
public class PhoneNumber implements Serializable {
  public static final String TABLE_NAME = "phonenumber";
  private static final long serialVersionUID = 5548943595609304757L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Phone number type.
   */
  @Column(name = "type", nullable = false)
  @Enumerated(STRING)
  private PhoneNumberType type;
  /**
   * Phone number.
   */
  @Column(name = "number", nullable = false)
  @Size(max = 50)
  private String number;
  /**
   * Extension.
   */
  @Column(name = "extension")
  @Size(max = 20)
  private String extension;

  public PhoneNumber() {
  }

  public PhoneNumber(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "PhoneNumber [id=" + id + ", type=" + type + ", number=" + number + ", extension="
        + extension + "]";
  }

  /**
   * Returns value to show in user interface for this phone number.
   *
   * @param locale
   *          user's locale
   * @return value to show in user interface for this phone number
   */
  public String getValue(Locale locale) {
    MessageResource resources = new MessageResource(PhoneNumber.class, locale);
    return resources.message("value", number, extension != null && !extension.isEmpty() ? 1 : 0,
        extension);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PhoneNumberType getType() {
    return type;
  }

  public void setType(PhoneNumberType type) {
    this.type = type;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }
}
