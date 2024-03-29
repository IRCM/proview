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

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * History of an action on an object in database. This may be the update of an object's simple
 * property or the insertion/modification/deletion of a complex property.
 */
@Entity
@Table(name = "activityupdate")
@GeneratePropertyNames
public class UpdateActivity implements Data {
  /**
   * Database identifier of update activity.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Table name of affected data.
   */
  @Column(nullable = false)
  private String tableName;
  /**
   * Database identifier of data.
   */
  @Column(nullable = false)
  private Long recordId;
  /**
   * Type of action.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private ActionType actionType;
  /**
   * Database column that changed. Only valid if {@link #actionType} is {@link ActionType#UPDATE}.
   */
  @Column(name = "actionColumn")
  @Size(max = 70)
  private String column;
  /**
   * Database column value before update. Only valid if {@link #actionType} is
   * {@link ActionType#UPDATE}.
   */
  @Column
  @Size(max = 255)
  private String oldValue;
  /**
   * Database column value after update. Only valid if {@link #actionType} is
   * {@link ActionType#UPDATE}.
   */
  @Column
  @Size(max = 255)
  private String newValue;

  @Override
  public String toString() {
    return "UpdateActivity [id=" + id + ", tableName=" + tableName + ", recordId=" + recordId
        + ", actionType=" + actionType + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public String getTableName() {
    return tableName;
  }

  public Long getRecordId() {
    return recordId;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void setRecordId(Long recordId) {
    this.recordId = recordId;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}
