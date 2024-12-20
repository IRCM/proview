package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.DataNullableId;
import ca.qc.ircm.proview.user.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * History of an action in database.
 */
@Entity
@Table(name = "activity")
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class Activity implements DataNullableId {
  /**
   * Database identifier of activity.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * User that made the action.
   */
  @ManyToOne
  @JoinColumn(updatable = false)
  private User user;
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
   * Moment where action was performed.
   */
  @Column(nullable = false)
  private LocalDateTime timestamp;
  /**
   * Explanation of changes.
   */
  @Column(nullable = false)
  private String explanation;
  /**
   * Updates done in this action, if any. This will most likely be null or empty if
   * {@link #getActionType()} does not return {@link ActionType#UPDATE}.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn
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

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
