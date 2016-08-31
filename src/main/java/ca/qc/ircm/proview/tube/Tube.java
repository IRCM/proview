package ca.qc.ircm.proview.tube;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Tube where sample is put for some treatment.
 */
@Entity
@DiscriminatorValue("TUBE")
public class Tube extends SampleContainer implements Data, Named, Serializable, Comparable<Tube> {
  private static final long serialVersionUID = 2723772707033001099L;

  /**
   * Name used to identify tube.
   */
  @Column(name = "name", unique = true, nullable = false)
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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Tube) {
      Tube other = (Tube) obj;
      return this.name != null && this.name.equalsIgnoreCase(other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return name != null ? name.toUpperCase().hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Tube [name=" + name + ", getId()=" + getId() + "]";
  }

  @Override
  public int compareTo(Tube other) {
    return this.name.compareToIgnoreCase(other.getName());
  }

  @Override
  public SampleContainer.Type getType() {
    return SampleContainer.Type.TUBE;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
