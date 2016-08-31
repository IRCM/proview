package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Enrichment} that can be recorded.
 */
public interface EnrichmentActivityService {
  /**
   * Creates an activity about insertion of enrichment.
   *
   * @param enrichment
   *          inserted enrichment
   * @return activity about insertion of enrichment
   */
  @CheckReturnValue
  public Activity insert(Enrichment enrichment);

  /**
   * Creates an activity about enrichment being marked as erroneous.
   *
   * @param enrichment
   *          erroneous enrichment that was undone
   * @param justification
   *          explanation of what was incorrect with the enrichment
   * @return activity about enrichment being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Enrichment enrichment, String justification);

  /**
   * Creates an activity about enrichment being marked as failed.
   *
   * @param enrichment
   *          failed enrichment that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about enrichment being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Enrichment enrichment, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
