package ca.qc.ircm.proview.dilution;

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Dilution of a small molecule sample.
 */
@Entity
@DiscriminatorValue("DILUTION")
public class Dilution extends Treatment<DilutedSample> {
  public Dilution() {
  }

  public Dilution(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Dilution) {
      Dilution other = (Dilution) obj;
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
    StringBuilder buff = new StringBuilder("Dilution(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.DILUTION;
  }
}
