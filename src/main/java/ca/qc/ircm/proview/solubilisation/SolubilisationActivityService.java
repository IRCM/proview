package ca.qc.ircm.proview.solubilisation;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Solubilisation} that can be recorded.
 */
public interface SolubilisationActivityService {
  /**
   * Creates an activity about insertion of solubilisation.
   *
   * @param solubilisation
   *          insertion solubilisation
   * @return activity about insertion of solubilisation
   */
  @CheckReturnValue
  public Activity insert(Solubilisation solubilisation);

  /**
   * Creates an activity about solubilisation being marked as erroneous.
   *
   * @param solubilisation
   *          erroneous solubilisation that was undone
   * @param justification
   *          explanation of what was incorrect with the solubilisation
   * @return activity about solubilisation being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Solubilisation solubilisation, String justification);

  /**
   * Creates an activity about solubilisation being marked as failed.
   *
   * @param solubilisation
   *          failed solubilisation that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about solubilisation being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Solubilisation solubilisation, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
