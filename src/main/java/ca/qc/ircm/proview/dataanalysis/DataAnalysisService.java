/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
