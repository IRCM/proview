package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Enrichment of some Samples.
 */
@Entity
@DiscriminatorValue("ENRICHMENT")
public class Enrichment extends Treatment<EnrichedSample> {
  /**
   * Protocol used for enrichment of sample.
   */
  @ManyToOne
  @JoinColumn(name = "protocolId", nullable = false)
  private EnrichmentProtocol protocol;

  public Enrichment() {
    super();
  }

  public Enrichment(Long id) {
    super(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Enrichment) {
      Enrichment other = (Enrichment) obj;
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
    StringBuilder buff = new StringBuilder("Enrichment(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Type getType() {
    return Type.ENRICHMENT;
  }

  public EnrichmentProtocol getProtocol() {
    return protocol;
  }

  public void setProtocol(EnrichmentProtocol protocol) {
    this.protocol = protocol;
  }
}
