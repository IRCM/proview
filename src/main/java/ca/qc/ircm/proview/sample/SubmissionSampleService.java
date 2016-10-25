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

package ca.qc.ircm.proview.sample;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service class for Sample.
 */
public interface SubmissionSampleService {
  /**
   * Samples can be sorted by these properties.
   */
  public static enum Sort {
    /**
     * Laboratory that submitted sample.
     */
    LABORATORY, /**
                 * Director of laboratory that submitted sample.
                 */
    USER, /**
           * Submission date.
           */
    SUBMISSION, /**
                 * Sample lims.
                 */
    LIMS, /**
           * Sample name.
           */
    NAME, /**
           * Sample status.
           */
    STATUS, /**
             * Sample support.
             */
    SUPPORT;
  }

  /**
   * Limit search to samples with that support.
   */
  public static enum Support {
    /**
     * @see ca.qc.ircm.proview.sample.SampleSupport#SOLUTION
     */
    SOLUTION, /**
               * @see ca.qc.ircm.proview.sample.SampleSupport#GEL
               */
    GEL, /**
          * Small molecule to analyse with high resolution.
          *
          * @see ca.qc.ircm.proview.submission.Submission#isHighResolution()
          */
    MOLECULE_HIGH, /**
                    * Small molecule to analyse with low resolution.
                    *
                    * @see ca.qc.ircm.proview.submission.Submission#isLowResolution()
                    */
    MOLECULE_LOW, /**
                   * @see ca.qc.ircm.proview.submission.Service#INTACT_PROTEIN
                   */
    INTACT_PROTEIN;
  }

  /**
   * Report containing submitted samples.
   */
  public static interface Report {
    List<SubmissionSample> getSamples();

    Map<SubmissionSample, Boolean> getLinkedToResults();
  }

  /**
   * Selects submitted sample from database.
   *
   * @param id
   *          database identifier of submitted sample
   * @return submitted sample
   */
  public SubmissionSample get(Long id);

  /**
   * Returns submitted sample having this name.
   *
   * @param name
   *          sample's name
   * @return submitted sample having this name
   */
  public SubmissionSample getSubmission(String name);

  /**
   * Returns true if a sample with this name is already in database, false otherwise.
   *
   * @param name
   *          name of sample
   * @return true if a sample with this name is already in database, false otherwise
   */
  public boolean exists(String name);

  /**
   * Selects samples to show in a report.
   *
   * @param filter
   *          filters samples
   * @return samples found
   */
  public Report report(SampleFilter filter);

  /**
   * Selects samples to show in an admin report.
   *
   * @param filter
   *          filters samples
   * @return samples found
   */
  public Report adminReport(SampleFilter filter);

  /**
   * Selects samples to show in sample monitoring page.
   *
   * @return samples to show in sample monitoring page
   */
  public List<SubmissionSample> sampleMonitoring();

  /**
   * Selects all projects of signed user.
   *
   * @return all projects of signed user
   */
  public List<String> projects();

  /**
   * Updates sample's information in database.
   *
   * @param sample
   *          sample containing new information
   * @param justification
   *          justification for changes made to sample
   */
  public void update(SubmissionSample sample, String justification);

  /**
   * Update many sample's status.
   *
   * @param samples
   *          samples containing new status
   */
  public void updateStatus(Collection<? extends SubmissionSample> samples);
}
