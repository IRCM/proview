package ca.qc.ircm.proview.msanalysis;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link MascotFile} and {@link AcquisitionMascotFile} that can be
 * recorded.
 */
public interface MascotFileActivityService {
  /**
   * Creates an activity about update of a Mascot file.
   *
   * @param newAcquisitionMascotFile
   *          Mascot file containing new properties/values
   * @return activity about update of a Mascot file
   */
  @CheckReturnValue
  public Optional<Activity> update(AcquisitionMascotFile newAcquisitionMascotFile);
}
