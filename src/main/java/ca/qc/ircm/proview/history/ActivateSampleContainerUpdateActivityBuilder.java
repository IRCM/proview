package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.sample.SampleContainer;

/**
 * Activity of activating a container for sample.
 */
public class ActivateSampleContainerUpdateActivityBuilder extends UpdateActivityBuilder {
  {
    tableName("samplecontainer");
    actionType(ActionType.UPDATE);
    column("banned");
    newValue(false);
  }

  /**
   * Sets old container.
   *
   * @param oldContainer
   *          old container
   * @return builder
   */
  public ActivateSampleContainerUpdateActivityBuilder oldContainer(SampleContainer oldContainer) {
    recordId(oldContainer.getId());
    oldValue(oldContainer.isBanned());
    return this;
  }
}
