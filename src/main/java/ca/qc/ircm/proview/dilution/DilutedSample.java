package ca.qc.ircm.proview.dilution;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A sample that was diluted.
 */
@Entity
@DiscriminatorValue("DILUTION")
public class DilutedSample extends TreatmentSample implements Data {
  /**
   * Volume of source transfered.
   */
  @Column(name = "sourceVolume", nullable = false)
  private Double sourceVolume;
  /**
   * Solvent used for dilution.
   */
  @Column(name = "solvent", nullable = false)
  private String solvent;
  /**
   * Volume of solvent used.
   */
  @Column(name = "solventVolume", nullable = false)
  private Double solventVolume;

  public Double getSourceVolume() {
    return sourceVolume;
  }

  public void setSourceVolume(Double sourceVolume) {
    this.sourceVolume = sourceVolume;
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
