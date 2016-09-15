package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.user.User;

import java.util.List;
import java.util.Map;

/**
 * Service for submission.
 */
public interface SubmissionService {
  /**
   * Report containing submitted samples.
   */
  public static interface Report {
    List<Submission> getSubmissions();

    Map<Submission, Boolean> getLinkedToResults();
  }

  /**
   * Selects submission from database.
   *
   * @param id
   *          database identifier of submission
   * @return submission
   */
  public Submission get(Long id);

  /**
   * Selects submission from database.
   *
   * @param filter
   *          filters submissions
   * @return submission
   */
  public Report report(SubmissionFilter filter);

  /**
   * Selects submission from database for admin users.
   *
   * @param filter
   *          filters submissions
   * @return submission
   */
  public Report adminReport(SubmissionFilter filter);

  /**
   * Returns all gel images that are linked to submission.
   *
   * @param submission
   *          submission of samples
   * @return all gel images that are linked to submission
   */
  @Deprecated
  public List<GelImage> gelImages(Submission submission);

  /**
   * Add a submission to database.<br>
   * Submission's date should not be older than yesterday.
   *
   * @param submission
   *          submission
   */
  public void insert(Submission submission);

  /**
   * Updates submission.
   *
   * @param submission
   *          submission with new information
   * @param owner
   *          new submission's owner
   * @param justification
   *          justification for changes made to submission
   */
  public void update(Submission submission, User owner, String justification);
}
