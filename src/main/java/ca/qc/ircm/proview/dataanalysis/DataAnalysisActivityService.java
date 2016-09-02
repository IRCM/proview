package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.proview.history.Activity;

import java.util.Optional;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link DataAnalysis} that can be recorded.
 */
public interface DataAnalysisActivityService {
  /**
   * Creates an activity about insertion of data analysis.
   *
   * @param dataAnalysis
   *          inserted data analysis
   * @return activity about insertion of data analysis
   */
  @CheckReturnValue
  public Activity insert(DataAnalysis dataAnalysis);

  /**
   * Creates an activity about update of a data analysis.
   *
   * @param dataAnalysis
   *          data analysis after update
   * @param justification
   *          justification for changes made to data analysis
   * @return activity about update of a data analysis
   */
  @CheckReturnValue
  public Optional<Activity> update(DataAnalysis dataAnalysis, String justification);
}
