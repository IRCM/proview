package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Transfer} that can be recorded.
 */
public interface TransferActivityService {
  /**
   * Creates an activity about insertion of transfer.
   *
   * @param transfer
   *          inserted transfer
   * @return an activity about insertion of transfer
   */
  @CheckReturnValue
  public Activity insert(Transfer transfer);

  /**
   * Creates an activity about transfer being marked as erroneous.
   *
   * @param transfer
   *          erroneous transfer that was undone
   * @param justification
   *          explanation of what was incorrect with the transfer
   * @param samplesRemoved
   *          containers were sample was removed
   * @return activity about transfer being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Transfer transfer, String justification,
      Collection<SampleContainer> samplesRemoved);

  /**
   * Creates an activity about transfer being marked as failed.
   *
   * @param transfer
   *          failed transfer that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about transfer being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Transfer transfer, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
