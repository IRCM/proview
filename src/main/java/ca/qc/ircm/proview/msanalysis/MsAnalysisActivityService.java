package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService.MsAnalysisAggregate;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link MsAnalysis} that can be recorded.
 */
public interface MsAnalysisActivityService {
  /**
   * Creates an activity about insertion of MS analysis.
   *
   * @param msAnalysisAggregate
   *          inserted MS analysis
   * @return activity about insertion of MS analysis
   */
  @CheckReturnValue
  public Activity insert(MsAnalysisAggregate msAnalysisAggregate);

  /**
   * Creates an activity about MS analysis being marked as erroneous.
   *
   * @param msAnalysis
   *          erroneous MS analysis that was undone
   * @param justification
   *          explanation of what was incorrect with the MS analysis
   * @return activity about MS analysis being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(MsAnalysis msAnalysis, String justification);

  /**
   * Creates an activity about MS analysis being marked as failed.
   *
   * @param msAnalysis
   *          failed MS analysis that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about MS analysis being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(MsAnalysis msAnalysis, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
