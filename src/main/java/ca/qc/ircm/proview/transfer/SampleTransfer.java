package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Transfer of a single sample from one spot to another.
 */
@Entity
@DiscriminatorValue("TRANSFER")
public class SampleTransfer extends TreatmentSample implements Data {
  /**
   * Sample's destination container.
   */
  @ManyToOne
  @JoinColumn(name = "destinationContainerId", nullable = false)
  private SampleContainer destinationContainer;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof SampleTransfer) {
      SampleTransfer other = (SampleTransfer) obj;
      return this.getId() != null && this.getId().equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getId() != null ? getId().intValue() : 0;
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("SampleTransfer(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  public SampleContainer getDestinationContainer() {
    return destinationContainer;
  }

  public void setDestinationContainer(SampleContainer destinationContainer) {
    this.destinationContainer = destinationContainer;
  }
}
