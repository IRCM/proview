package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

/**
 * Destination container(s) is used in another treatment and sample cannot be remove.
 */
public class DestinationUsedInTreatmentException extends Exception {
  private static final long serialVersionUID = -6800335650110838829L;
  public final Collection<SampleContainer> containers;

  public DestinationUsedInTreatmentException(String message,
      Collection<SampleContainer> containers) {
    super(message);
    this.containers = containers;
  }
}