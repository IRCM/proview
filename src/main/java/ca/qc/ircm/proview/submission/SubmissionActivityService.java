package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.history.Activity;

import java.util.Optional;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Submission} that can be recorded.
 */
public interface SubmissionActivityService {
  /**
   * Creates an activity about insertion of samples submission.
   *
   * @param submission
   *          samples submission
   * @return activity about insertion of samples submission
   */
  @CheckReturnValue
  public Activity insert(Submission submission);

  /**
   * Creates an activity about update of samples submission.
   *
   * @param newSubmission
   *          submission containing new properties/values
   * @param justification
   *          justification for the changes
   * @return activity about update of samples submission
   */
  @CheckReturnValue
  public Optional<Activity> update(Submission newSubmission, String justification);
}
