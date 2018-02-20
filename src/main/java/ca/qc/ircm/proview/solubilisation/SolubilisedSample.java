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

package ca.qc.ircm.proview.solubilisation;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

/**
 * A dried sample that was solubilised.
 */
@Entity
@DiscriminatorValue("SOLUBILISATION")
public class SolubilisedSample extends TreatmentSample implements Data {
  /**
   * Solubilisation.
   */
  @ManyToOne
  @JoinColumn(name = "treatmentId", nullable = false)
  private Solubilisation solubilisation;
  /**
   * Solvent used for solubilization.
   */
  @Column(name = "solvent", nullable = false)
  private String solvent;
  /**
   * Volume of solvent used.
   */
  @Column(name = "solventVolume", nullable = false)
  @Min(0)
  private Double solventVolume;

  @Override
  public String toString() {
    return "SolubilisedSample [getId()=" + getId() + "]";
  }

  public String getSolvent() {
    return solvent;
  }

  public void setSolvent(String solvent) {
    this.solvent = solvent;
  }

  public Double getSolventVolume() {
    return solventVolume;
  }

  public void setSolventVolume(Double solventVolume) {
    this.solventVolume = solventVolume;
  }

  public Solubilisation getSolubilisation() {
    return solubilisation;
  }

  public void setSolubilisation(Solubilisation solubilisation) {
    this.solubilisation = solubilisation;
  }
}
