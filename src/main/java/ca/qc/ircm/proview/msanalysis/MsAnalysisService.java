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

package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;
import ca.qc.ircm.proview.sample.Sample;

import java.util.List;
import java.util.Map;

/**
 * Services for MS analysis.
 */
public interface MsAnalysisService {
  /**
   * Aggregate representing a complete {@link MsAnalysis}.
   */
  public static interface MsAnalysisAggregate {
    /**
     * Returns MS analysis that was done.
     *
     * @return MS analysis that was done
     */
    public MsAnalysis getMsAnalysis();

    /**
     * Returns acquisitions done during MS analysis.
     *
     * @return acquisitions done during MS analysis.
     */
    public List<Acquisition> getAcquisitions();

    /**
     * Returns checks done before MS analysis.
     *
     * @return checks done before MS analysis.
     */
    public Map<VerificationType, Map<String, Boolean>> getVerifications();
  }

  /**
   * Selects MS analysis from database.
   *
   * @param id
   *          database identifier of MS analysis
   * @return MS analysis
   */
  public MsAnalysis get(Long id);

  /**
   * Selects MS analysis of acquisition.
   *
   * @param acquisition
   *          acquisition
   * @return MS analysis of acquisition
   */
  public MsAnalysis get(Acquisition acquisition);

  /**
   * Selects all MS analysis made on sample.
   *
   * @param sample
   *          sample
   * @return all MS analysis made on sample
   */
  public List<MsAnalysis> all(Sample sample);

  /**
   * Selects all checks performed for MS analysis.
   *
   * @param msAnalysis
   *          MS analysis
   * @return all checks performed for MS analysis
   */
  public Map<VerificationType, Map<String, Boolean>> verifications(MsAnalysis msAnalysis);

  /**
   * Analyse samples by MS.
   *
   * @param msAnalysisAggregate
   *          MS analysis information
   * @return MS analysis with complete information for acquisitions
   * @throws SamplesFromMultipleUserException
   *           MS analysis contains samples from more than one user
   */
  public MsAnalysis insert(MsAnalysisAggregate msAnalysisAggregate)
      throws SamplesFromMultipleUserException;

  /**
   * Undo erroneous MS analysis that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for MS
   * analysis are not the right ones. So, in practice, the MS analysis never actually occurred.
   *
   * @param msAnalysis
   *          erroneous MS analysis to undo
   * @param justification
   *          explanation of what was incorrect with the MS analysis
   */
  public void undoErroneous(MsAnalysis msAnalysis, String justification);

  /**
   * Report that a problem occurred during MS analysis causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the MS analysis was done but the incorrect MS
   * analysis could only be detected later in the sample processing. Thus the MS analysis is not
   * undone but flagged as having failed.
   *
   * @param msAnalysis
   *          MS analysis to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in MS analysis should be banned, this will also ban any
   *          container were samples were transfered after MS analysis
   */
  public void undoFailed(MsAnalysis msAnalysis, String failedDescription, boolean banContainers);
}
