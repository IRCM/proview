package ca.qc.ircm.proview.dilution;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Dilution} that can be recorded.
 */
public interface DilutionActivityService {
  /**
   * Creates an activity about insertion of dilution.
   *
   * @param dilution
   *          inserted dilution
   * @return activity about insertion of dilution
   */
  @CheckReturnValue
  public Activity insert(Dilution dilution);

  /**
   * Creates an activity about dilution being marked as erroneous.
   *
   * @param dilution
   *          erroneous dilution that was undone
   * @param justification
   *          explanation of what was incorrect with the dilution
   * @return activity about dilution being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Dilution dilution, String justification);

  /**
   * Creates an activity about dilution being marked as failed.
   *
   * @param dilution
   *          failed dilution that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about dilution being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Dilution dilution, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
