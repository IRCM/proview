package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.tube.Tube;

/**
 * Activity of removing a sample from container.
 */
public class RemoveSampleFromSampleContainerUpdateActivityBuilder extends UpdateActivityBuilder {
  {
    tableName("samplecontainer");
    actionType(ActionType.UPDATE);
    column("sampleId");
    newValue(null);
  }

  /**
   * Sets old container for activity.
   *
   * @param oldContainer
   *          old container
   * @return builder
   */
  public RemoveSampleFromSampleContainerUpdateActivityBuilder
      oldContainer(SampleContainer oldContainer) {
    recordId(oldContainer.getId());
    oldValue(oldContainer.getSample().getId());
    if (oldContainer instanceof Tube) {
      actionType(ActionType.DELETE);
      column(null);
      newValue(null);
      oldValue(null);
    }
    return this;
  }
}
