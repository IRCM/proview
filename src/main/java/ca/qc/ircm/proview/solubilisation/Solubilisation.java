package ca.qc.ircm.proview.solubilisation;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Solubilisation of dried samples.
 */
@Entity
@DiscriminatorValue("SOLUBILISATION")
public class Solubilisation extends Treatment<SolubilisedSample> implements Data {
  public Solubilisation() {
    super();
  }

  public Solubilisation(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Solubilisation) {
      Solubilisation other = (Solubilisation) obj;
      return getId() != null && getId().equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return -490850960 + (getId() != null ? getId().intValue() : 0);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Solubilisation_");
    builder.append(getId());
    return super.toString();
  }

  @Override
  public Type getType() {
    return Type.SOLUBILISATION;
  }
}
