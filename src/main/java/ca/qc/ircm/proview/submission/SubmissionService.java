package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.user.User;

import java.util.List;

/**
 * Service for submission.
 */
public interface SubmissionService {
  /**
   * Selects submission from database.
   *
   * @param id
   *          database identifier of submission
   * @return submission
   */
  public Submission get(Long id);

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
