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

public class UpdateActivityBuilder {
  private String tableName;
  private Long recordId;
  private ActionType actionType;
  private String column;
  private Object oldValue;
  private Object newValue;
  private DatabaseConverter converter = new DatabaseConverter();

  public UpdateActivityBuilder() {
  }

  public UpdateActivityBuilder(Object oldValue, Object newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public boolean isChanged() {
    return actionType != ActionType.UPDATE || !DatabaseLogUtil.equals(oldValue, newValue);
  }

  /**
   * Returns update activity for this builder.
   * 
   * @return update activity
   */
  public UpdateActivity build() {
    UpdateActivity activity = new UpdateActivity();
    activity.setTableName(tableName);
    activity.setRecordId(recordId);
    activity.setActionType(actionType);
    activity.setColumn(column);
    activity.setOldValue(converter.convert(oldValue));
    activity.setNewValue(converter.convert(newValue));
    return activity;
  }

  public UpdateActivityBuilder tableName(String tableName) {
    this.tableName = tableName;
    return this;
  }

  public UpdateActivityBuilder recordId(Long recordId) {
    this.recordId = recordId;
    return this;
  }

  public UpdateActivityBuilder actionType(ActionType actionType) {
    this.actionType = actionType;
    return this;
  }

  public UpdateActivityBuilder column(String column) {
    this.column = column;
    return this;
  }

  public UpdateActivityBuilder oldValue(Object oldValue) {
    this.oldValue = oldValue;
    return this;
  }

  public UpdateActivityBuilder newValue(Object newValue) {
    this.newValue = newValue;
    return this;
  }

  public UpdateActivityBuilder converter(DatabaseConverter converter) {
    this.converter = converter;
    return this;
  }
}
