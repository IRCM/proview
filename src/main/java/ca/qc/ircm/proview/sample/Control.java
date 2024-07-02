package ca.qc.ircm.proview.sample;

import static jakarta.persistence.EnumType.STRING;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

/**
 * Control samples.
 */
@Entity
@DiscriminatorValue("CONTROL")
@GeneratePropertyNames
public class Control extends Sample implements Named {
  private static final long serialVersionUID = 5008215649619278441L;

  /**
   * Control type.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private ControlType controlType;

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
  public Category getCategory() {
    return Sample.Category.CONTROL;
  }

  public void setControlType(ControlType controlType) {
    this.controlType = controlType;
  }

  public ControlType getControlType() {
    return controlType;
  }
}
