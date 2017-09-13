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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.user.User;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * History of an action in database.
 */
@Entity
@Table(name = "activity")
public class Activity implements Data {
  /**
   * Database identifier of activity.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * User that made the action.
   */
  @ManyToOne
  @JoinColumn(name = "userId", updatable = false)
  private User user;
  /**
   * Table name of affected data.
   */
  @Column(name = "tableName", nullable = false)
  private String tableName;
  /**
   * Database identifier of data.
   */
  @Column(name = "recordId", nullable = false)
  private Long recordId;
  /**
   * Type of action.
   */
  @Column(name = "actionType", nullable = false)
  @Enumerated(STRING)
  private ActionType actionType;
  /**
   * Moment where action was performed.
   */
  @Column(name = "time", nullable = false)
  private Instant timestamp;
  /**
   * Justification of changes.
   */
  @Column(name = "justification", nullable = false)
  private String justification;
  /**
   * Updates done in this action, if any. This will most likely be null or empty if
   * {@link #getActionType()} does not return {@link ActionType#UPDATE}.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "activityId")
  private List<UpdateActivity> updates;

  @Override
  public String toString() {
    return "Activity [id=" + id + ", tableName=" + tableName + ", recordId=" + recordId
        + ", actionType=" + actionType + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
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

  public void setUser(User user) {
    this.user = user;
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

  public List<UpdateActivity> getUpdates() {
    return updates;
  }

  public void setUpdates(List<UpdateActivity> updates) {
    this.updates = updates;
  }

  public String getJustification() {
    return justification;
  }

  public void setJustification(String justification) {
    this.justification = justification;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }
}
