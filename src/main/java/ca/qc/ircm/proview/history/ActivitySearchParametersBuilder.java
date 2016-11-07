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

import ca.qc.ircm.proview.history.Activity.ActionType;

/**
 * Search parameters for activity searches.
 */
public class ActivitySearchParametersBuilder {
  private static class ActivitySearchParametersDefault implements ActivitySearchParameters {
    private ActionType actionType;
    private String tableName;
    private Long recordId;

    @Override
    public ActionType getActionType() {
      return actionType;
    }

    @Override
    public String getTableName() {
      return tableName;
    }

    @Override
    public Long getRecordId() {
      return recordId;
    }
  }

  private final ActivitySearchParametersDefault parameters = new ActivitySearchParametersDefault();

  public ActivitySearchParametersBuilder actionType(ActionType actionType) {
    parameters.actionType = actionType;
    return this;
  }

  public ActivitySearchParametersBuilder tableName(String tableName) {
    parameters.tableName = tableName;
    return this;
  }

  public ActivitySearchParametersBuilder recordId(Long recordId) {
    parameters.recordId = recordId;
    return this;
  }

  public ActivitySearchParameters build() {
    return parameters;
  }
}
