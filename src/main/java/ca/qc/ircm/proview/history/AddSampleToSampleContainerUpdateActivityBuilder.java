package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.tube.Tube;
import java.util.Objects;

/**
 * {@link UpdateActivityBuilder} for addition of sample to a sample container.
 */
public class AddSampleToSampleContainerUpdateActivityBuilder extends UpdateActivityBuilder {
  {
    tableName("samplecontainer");
    actionType(ActionType.UPDATE);
    column("sampleId");
    oldValue(null);
  }

  /**
   * Sets new container for activity.
   *
   * @param newContainer
   *          new container
   * @return builder
   */
  public AddSampleToSampleContainerUpdateActivityBuilder
      newContainer(SampleContainer newContainer) {
    recordId(newContainer.getId());
    newValue(Objects.requireNonNull(newContainer.getSample()).getId());
    if (newContainer instanceof Tube) {
      actionType(ActionType.INSERT);
      column(null);
      newValue(null);
      oldValue(null);
    }
    return this;
  }
}
