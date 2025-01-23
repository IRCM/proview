package ca.qc.ircm.proview.sample;

import java.io.IOException;
import java.io.Serial;

/**
 * Could not save structure.
 */
public class SaveStructureException extends IOException {

  @Serial
  private static final long serialVersionUID = -5275985535305716239L;

  /**
   * Sample linked to structure that could not be saved.
   */
  public final SubmissionSample sample;

  public SaveStructureException(Throwable cause, SubmissionSample sample) {
    super(cause);
    this.sample = sample;
  }
}
