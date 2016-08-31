package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Transfer samples from one plate to another.
 */
@Entity
@DiscriminatorValue("TRANSFER")
public class Transfer extends Treatment<SampleTransfer> {
  public Transfer() {
    super();
  }

  public Transfer(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Transfer) {
      Transfer other = (Transfer) obj;
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
    StringBuilder buff = new StringBuilder("Transfer(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.TRANSFER;
  }
}
