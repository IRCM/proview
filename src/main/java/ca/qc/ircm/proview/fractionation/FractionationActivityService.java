package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Fractionation} that can be recorded.
 */
public interface FractionationActivityService {
  /**
   * Creates an activity about insertion of fractionation.
   *
   * @param fractionation
   *          inserted fractionation
   * @return activity about insertion of fractionation
   */
  @CheckReturnValue
  public Activity insert(Fractionation fractionation);

  /**
   * Creates an activity about fractionation being marked as erroneous.
   *
   * @param fractionation
   *          erroneous fractionation that was undone
   * @param justification
   *          explanation of what was incorrect with the fractionation
   * @param samplesRemoved
   *          containers were sample was removed
   * @return activity about fractionation being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Fractionation fractionation, String justification,
      Collection<SampleContainer> samplesRemoved);

  /**
   * Creates an activity about fractionation being marked as failed.
   *
   * @param fractionation
   *          failed fractionation that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about fractionation being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Fractionation fractionation, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
