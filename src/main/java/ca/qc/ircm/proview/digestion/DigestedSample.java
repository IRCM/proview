package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A sample that was digested.
 */
@Entity
@DiscriminatorValue("DIGESTION")
public class DigestedSample extends TreatmentSample {
  @Override
  public String toString() {
    return "DigestedSample(" + getId() + ")";
  }
}
