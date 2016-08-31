package ca.qc.ircm.proview.standard;

import static javax.persistence.EnumType.STRING;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

/**
 * Addition of a single standard to a sample.
 */
@Entity
@DiscriminatorValue("STANDARD_ADDITION")
public class AddedStandard extends TreatmentSample implements Data {
  /**
   * Quantity's unit.
   */
  public static enum QuantityUnit {
    MICRO_GRAMS, PICO_MOL
  }

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
  @Size(max = 50)
  private String quantity;
  /**
   * Quantity's unit.
   */
  @Column(name = "quantityUnit", nullable = false)
  @Enumerated(STRING)
  private QuantityUnit quantityUnit;

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

  public QuantityUnit getQuantityUnit() {
    return quantityUnit;
  }

  public void setQuantityUnit(QuantityUnit quantityUnit) {
    this.quantityUnit = quantityUnit;
  }
}
