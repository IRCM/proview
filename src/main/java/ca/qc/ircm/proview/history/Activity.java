package ca.qc.ircm.proview.history;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.user.User;

import java.util.Date;
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
import javax.persistence.Temporal;

/**
 * History of an action in database.
 */
@Entity
@Table(name = "activity")
public class Activity implements Data {
  /**
   * Type of action done on data.
   */
  public static enum ActionType {
    /**
     * Data was just inserted.
     */
    INSERT, /**
             * Data was just updated.
             */
    UPDATE, /**
             * Data was just deleted.
             */
    DELETE
  }

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
  @Temporal(TIMESTAMP)
  private Date timestamp;
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

  public Date getTimestamp() {
    return timestamp != null ? (Date) timestamp.clone() : null;
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

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp != null ? (Date) timestamp.clone() : null;
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
}
