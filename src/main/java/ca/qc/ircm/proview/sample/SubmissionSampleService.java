package ca.qc.ircm.proview.sample;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
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
           * Mass spec service asked for sample.
           */
    SERVICE, /**
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
             * Sample project.
             */
    PROJECT, /**
              * Sample experience.
              */
    EXPERIENCE, /**
                 * Sample's support.
                 */
    SUPPORT;
  }

  /**
   * Limit search to samples with that support.
   */
  public static enum Support {
    /**
     * @see ca.qc.ircm.proview.sample.Sample.Support#SOLUTION
     */
    SOLUTION, /**
               * @see ca.qc.ircm.proview.sample.Sample.Support#GEL
               */
    GEL, /**
          * Small molecule to analyse with high resolution.
          *
          * @see ca.qc.ircm.proview.sample.MoleculeSample#isHighResolution()
          */
    MOLECULE_HIGH, /**
                    * Small molecule to analyse with low resolution.
                    *
                    * @see ca.qc.ircm.proview.sample.MoleculeSample#isLowResolution()
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
   * Compute price for sample.
   *
   * @param sample
   *          sample
   * @param date
   *          date of prices to use
   * @return sample's price
   */
  public BigDecimal computePrice(SubmissionSample sample, Date date);

  /**
   * Update many sample's status.
   *
   * @param samples
   *          samples containing new status
   */
  public void updateStatus(Collection<? extends SubmissionSample> samples);
}
