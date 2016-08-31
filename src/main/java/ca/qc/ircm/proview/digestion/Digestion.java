package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Digestion of some samples.
 */
@Entity
@DiscriminatorValue("DIGESTION")
public class Digestion extends Treatment<DigestedSample> {
  /**
   * Protocol used for digestion of sample.
   */
  @ManyToOne
  @JoinColumn(name = "protocolId", nullable = false)
  private DigestionProtocol protocol;

  public Digestion() {
  }

  public Digestion(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Digestion) {
      Digestion other = (Digestion) obj;
      return this.getId() != null && this.getId().equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.getId() == null ? 0 : this.getId().intValue();
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Digestion(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.DIGESTION;
  }

  public DigestionProtocol getProtocol() {
    return protocol;
  }

  public void setProtocol(DigestionProtocol protocol) {
    this.protocol = protocol;
  }
}
