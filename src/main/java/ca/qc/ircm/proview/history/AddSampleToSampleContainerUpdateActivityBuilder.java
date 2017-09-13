/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.tube.Tube;

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
    newValue(newContainer.getSample().getId());
    if (newContainer instanceof Tube) {
      actionType(ActionType.INSERT);
      column(null);
      newValue(null);
      oldValue(null);
    }
    return this;
  }
}
