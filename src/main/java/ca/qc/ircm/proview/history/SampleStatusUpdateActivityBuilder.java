package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.sample.SubmissionSample;

/**
 * {@link UpdateActivityBuilder} for update of a sample status.
 */
public class SampleStatusUpdateActivityBuilder extends UpdateActivityBuilder {
  {
    tableName("sample");
    actionType(ActionType.UPDATE);
    column("status");
  }

  /**
   * Sets old sample for activity.
   *
   * @param oldSample
   *          old sample
   * @return builder
   */
  public SampleStatusUpdateActivityBuilder oldSample(SubmissionSample oldSample) {
    recordId(oldSample.getId());
    oldValue(oldSample.getStatus());
    return this;
  }

  /**
   * Sets new sample for activity.
   *
   * @param newSample
   *          new sample
   * @return builder
   */
  public SampleStatusUpdateActivityBuilder newSample(SubmissionSample newSample) {
    recordId(newSample.getId());
    newValue(newSample.getStatus());
    return this;
  }
}
