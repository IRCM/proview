package ca.qc.ircm.proview.dataanalysis;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;

import java.util.Collection;
import java.util.List;

/**
 * Services for data analysis.
 */
public interface DataAnalysisService {
  /**
   * Selects data analysis from database.
   *
   * @param id
   *          database identifier of data analysis
   * @return data analysis
   */
  public DataAnalysis get(Long id);

  /**
   * Selects all data analyses asked for sample.
   *
   * @param sample
   *          sample
   * @return all data analyses asked for sample
   */
  public List<DataAnalysis> all(SubmissionSample sample);

  /**
   * Insert data analysis requests into database.
   * <p>
   * Sample's status is changed to
   * {@link ca.qc.ircm.proview.sample.SampleStatus#DATA_ANALYSIS} .
   * </p>
   *
   * @param dataAnalyses
   *          data analysis requests
   */
  public void insert(Collection<DataAnalysis> dataAnalyses);

  /**
   * Data analysis was performed by proteomic.
   * <p>
   * Sample's status is changed to
   * {@link ca.qc.ircm.proview.sample.SampleStatus#ANALYSED} .
   * </p>
   *
   * @param dataAnalyses
   *          data analysis that were analysed
   */
  public void analyse(Collection<DataAnalysis> dataAnalyses);

  /**
   * Changes data analysis results.
   * <p>
   * If data analysis's status is changed to
   * {@link ca.qc.ircm.proview.dataanalysis.DataAnalysis.Status#TO_DO}, sample's status is changed
   * to {@link ca.qc.ircm.proview.sample.SampleStatus#DATA_ANALYSIS} .
   * </p>
   * <p>
   * If data analysis's status is changed to
   * {@link ca.qc.ircm.proview.dataanalysis.DataAnalysis.Status#ANALYSED} or
   * {@link ca.qc.ircm.proview.dataanalysis.DataAnalysis.Status#CANCELLED} and sample has no more
   * data analyses with {@link ca.qc.ircm.proview.dataanalysis.DataAnalysis.Status#TO_DO} status,
   * sample's status is changed to
   * {@link ca.qc.ircm.proview.sample.SampleStatus#ANALYSED} .
   * </p>
   *
   * @param dataAnalysis
   *          data analysis with new information
   * @param justification
   *          justification for changes made to data analysis
   */
  public void update(DataAnalysis dataAnalysis, String justification);
}
