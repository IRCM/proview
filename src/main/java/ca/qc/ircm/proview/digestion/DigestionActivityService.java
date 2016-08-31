package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Digestion} that can be recorded.
 */
public interface DigestionActivityService {
  /**
   * Creates an activity about insertion of digestion.
   *
   * @param digestion
   *          inserted digestion
   * @return activity about insertion of digestion
   */
  @CheckReturnValue
  public Activity insert(Digestion digestion);

  /**
   * Creates an activity about digestion being marked as erroneous.
   *
   * @param digestion
   *          erroneous digestion that was undone
   * @param justification
   *          explanation of what was incorrect with the digestion
   * @return activity about digestion being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Digestion digestion, String justification);

  /**
   * Creates an activity about digestion being marked as failed.
   *
   * @param digestion
   *          failed digestion that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about digestion being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Digestion digestion, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
