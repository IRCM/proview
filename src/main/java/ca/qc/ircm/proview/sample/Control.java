package ca.qc.ircm.proview.sample;

import static jakarta.persistence.EnumType.STRING;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.io.Serial;

/**
 * Control samples.
 */
@Entity
@DiscriminatorValue("CONTROL")
@GeneratePropertyNames
public class Control extends Sample implements Named {

  @Serial
  private static final long serialVersionUID = 5008215649619278441L;

  /**
   * Control type.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private ControlType controlType;

  public Control() {
  }

  public Control(long id) {
    setId(id);
  }

  public Control(long id, String name) {
    setId(id);
    setName(name);
  }

  @Override
  public Category getCategory() {
    return Sample.Category.CONTROL;
  }

  public ControlType getControlType() {
    return controlType;
  }

  public void setControlType(ControlType controlType) {
    this.controlType = controlType;
  }
}
