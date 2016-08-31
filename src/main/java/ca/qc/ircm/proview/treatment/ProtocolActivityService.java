package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.history.Activity;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Protocol} that can be recorded.
 */
public interface ProtocolActivityService {
  /**
   * Creates an activity about insertion of protocol.
   *
   * @param protocol
   *          inserted protocol
   * @return activity about insertion of protocol
   */
  @CheckReturnValue
  public Activity insert(Protocol protocol);
}
