package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.sample.SampleContainer;

public class BanSampleContainerUpdateActivityBuilder extends UpdateActivityBuilder {
  {
    tableName("samplecontainer");
    actionType(ActionType.UPDATE);
    column("banned");
    newValue(true);
  }

  /**
   * Sets old container for activity.
   *
   * @param oldContainer
   *          old container
   * @return builder
   */
  public BanSampleContainerUpdateActivityBuilder oldContainer(SampleContainer oldContainer) {
    recordId(oldContainer.getId());
    oldValue(oldContainer.isBanned());
    return this;
  }
}
