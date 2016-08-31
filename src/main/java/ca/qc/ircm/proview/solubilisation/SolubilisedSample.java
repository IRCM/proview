package ca.qc.ircm.proview.solubilisation;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A dried sample that was solubilised.
 */
@Entity
@DiscriminatorValue("SOLUBILISATION")
public class SolubilisedSample extends TreatmentSample implements Data {
  /**
   * Solvent used for solubilization.
   */
  @Column(name = "solvent", nullable = false)
  private String solvent;
  /**
   * Volume of solvent used.
   */
  @Column(name = "solventVolume", nullable = false)
  private Double solventVolume;

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof SolubilisedSample) {
      SolubilisedSample other = (SolubilisedSample) obj;
      return getId() != null && getId().equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return -1857400014 + (getId() != null ? getId().intValue() : 0);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("SolubilisedSample_");
    builder.append(getId());
    return super.toString();
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
}
