package ca.qc.ircm.proview.tube;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.DataNullableId;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.io.Serializable;

/**
 * Tube where sample is put for some treatment.
 */
@Entity
@DiscriminatorValue("TUBE")
@GeneratePropertyNames
public class Tube extends SampleContainer implements DataNullableId, Named, Serializable {
  private static final long serialVersionUID = 2723772707033001099L;

  /**
   * Name used to identify tube.
   */
  @Column(unique = true, nullable = false)
  private String name;

  public Tube() {
  }

  public Tube(Long id) {
    super(id);
  }

  public Tube(Long id, String name) {
    super(id);
    this.name = name;
  }

  @Override
  public String toString() {
    return "Tube [name=" + name + ", getId()=" + getId() + "]";
  }

  @Override
  public SampleContainerType getType() {
    return SampleContainerType.TUBE;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
