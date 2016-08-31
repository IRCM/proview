package ca.qc.ircm.proview.standard;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Addition of a some standards to samples.
 */
@Entity
@DiscriminatorValue("STANDARD_ADDITION")
public class StandardAddition extends Treatment<AddedStandard> implements Data {
  public StandardAddition() {
    super();
  }

  public StandardAddition(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof StandardAddition) {
      StandardAddition other = (StandardAddition) obj;
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
    StringBuilder buff = new StringBuilder("StandardAddition(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.STANDARD_ADDITION;
  }
}
