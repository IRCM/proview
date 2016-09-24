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

package ca.qc.ircm.proview.msanalysis;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MS analysis verification.
 */
@Entity
@Table(name = "msanalysisverification")
public class MsAnalysisVerification implements Data, Serializable {
  private static final long serialVersionUID = -2673221428998509453L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Type.
   */
  @Column(name = "verificationType", nullable = false)
  @Enumerated(STRING)
  private VerificationType type;
  /**
   * Name.
   */
  @Column(name = "verificationName", nullable = false)
  private String name;
  /**
   * Value.
   */
  @Column(name = "verificationValue", nullable = false)
  private boolean value;

  public MsAnalysisVerification() {
  }

  public MsAnalysisVerification(Long id) {
    this.id = id;
  }

  /**
   * Creates an MS analysis verification.
   * 
   * @param type
   *          type
   * @param name
   *          name
   * @param value
   *          value
   */
  public MsAnalysisVerification(VerificationType type, String name, boolean value) {
    this.type = type;
    this.name = name;
    this.value = value;
  }

  @Override
  public String toString() {
    return "MSAnalysisVerification [id=" + id + ", type=" + type + ", name=" + name + ", value="
        + value + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public VerificationType getType() {
    return type;
  }

  public void setType(VerificationType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }
}
