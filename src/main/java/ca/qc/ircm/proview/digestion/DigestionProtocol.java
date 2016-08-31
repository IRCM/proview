package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.treatment.Protocol;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Digestion Protocol.
 */
@Entity
@DiscriminatorValue("DIGESTION")
public class DigestionProtocol extends Protocol {
  private static final long serialVersionUID = -9069433618607320198L;

  public DigestionProtocol() {
    super();
  }

  public DigestionProtocol(Long id) {
    super(id);
  }

  public DigestionProtocol(Long id, String name) {
    super(id, name);
  }

  @Override
  public Type getType() {
    return Protocol.Type.DIGESTION;
  }
}
