package ca.qc.ircm.proview.standard;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * Addition of a single standard to a sample.
 */
@Entity
@DiscriminatorValue("STANDARD_ADDITION")
public class AddedStandard extends TreatmentSample implements Data {
  /**
   * Name of standard added.
   */
  @Column(name = "name", nullable = false)
  @Size(max = 100)
  private String name;
  /**
   * Quantity of standard added.
   */
  @Column(name = "quantity", nullable = false)
  @Size(max = 100)
  private String quantity;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }
}
