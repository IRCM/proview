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
