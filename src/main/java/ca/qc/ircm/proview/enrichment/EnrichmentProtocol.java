package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.treatment.Protocol;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Enrichment Protocol.
 */
@Entity
@DiscriminatorValue("ENRICHMENT")
public class EnrichmentProtocol extends Protocol {
  private static final long serialVersionUID = -6447630204474357444L;

  public EnrichmentProtocol() {
    super();
  }

  public EnrichmentProtocol(Long id) {
    super(id);
  }

  public EnrichmentProtocol(Long id, String name) {
    super(id, name);
  }

  @Override
  public Type getType() {
    return Protocol.Type.ENRICHMENT;
  }
}
