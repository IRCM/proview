package ca.qc.ircm.proview.sample;

import java.io.IOException;

/**
 * Could not save structure.
 */
public class SaveStructureException extends IOException {

  private static final long serialVersionUID = -5275985535305716239L;

  /**
   * Sample linked to structure that could not be saved.
   */
  public final MoleculeSample sample;

  public SaveStructureException(Throwable cause, MoleculeSample sample) {
    super(cause);
    this.sample = sample;
  }
}
