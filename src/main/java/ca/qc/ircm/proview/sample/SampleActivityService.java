package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.history.Activity;

import java.util.Optional;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Sample} that can be recorded.
 */
public interface SampleActivityService {
  /**
   * Creates an activity about insertion of control.
   *
   * @param control
   *          inserted control
   * @return activity about insertion of control
   */
  @CheckReturnValue
  public Activity insertControl(Control control);

  /**
   * Creates an activity about update of sample.
   *
   * @param newSample
   *          sample containing new properties/values
   * @param justification
   *          justification for changes made to sample
   * @return activity about update of sample
   */
  @CheckReturnValue
  public Optional<Activity> update(Sample newSample, String justification);
}
