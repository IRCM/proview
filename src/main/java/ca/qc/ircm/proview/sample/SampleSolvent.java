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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.Solvent;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Link between a sample and a solvent.
 */
@Entity
@Table(name = "solvent")
public class SampleSolvent implements Data, Serializable {
  private static final long serialVersionUID = 3304432592040869501L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Solvent.
   */
  @Column(name = "solvent", nullable = false)
  @Enumerated(STRING)
  private Solvent solvent;
  /**
   * Solvent was removed from sample's solvents.
   */
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  public SampleSolvent() {
  }

  public SampleSolvent(Long id) {
    this.id = id;
  }

  public SampleSolvent(Solvent solvent) {
    this.solvent = solvent;
  }

  public SampleSolvent(Long id, Solvent solvent) {
    this.id = id;
    this.solvent = solvent;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Solvent getSolvent() {
    return solvent;
  }

  public void setSolvent(Solvent solvent) {
    this.solvent = solvent;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
