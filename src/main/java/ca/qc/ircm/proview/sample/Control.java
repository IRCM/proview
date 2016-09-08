package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;

import ca.qc.ircm.proview.Named;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Control samples.
 */
@Entity
@DiscriminatorValue("CONTROL")
public class Control extends Sample implements Named {
  /**
   * Sample type.
   */
  public static enum ControlType {
    /**
     * Negative control.
     */
    NEGATIVE_CONTROL, /**
                       * Positive control.
                       */
    POSITIVE_CONTROL
  }

  private static final long serialVersionUID = 5008215649619278441L;

  /**
   * Control type.
   */
  @Column(name = "controlType", nullable = false)
  @Enumerated(STRING)
  private ControlType controlType;
  /**
   * Support.
   */
  @Column(name = "support")
  @Enumerated(STRING)
  private Support support;
  /**
   * Volume.
   */
  @Column(name = "volume")
  @Min(0)
  private Double volume;
  /**
   * Quantity.
   */
  @Column(name = "quantity")
  @Size(max = 100)
  private String quantity;

  public Control() {
  }

  public Control(Long id) {
    setId(id);
  }

  public Control(Long id, String name) {
    setId(id);
    setName(name);
  }

  @Override
  public Type getType() {
    return Sample.Type.CONTROL;
  }

  @Override
  public Support getSupport() {
    return support;
  }

  public void setSupport(Support support) {
    this.support = support;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public void setControlType(ControlType controlType) {
    this.controlType = controlType;
  }

  public ControlType getControlType() {
    return controlType;
  }
}
