package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.UsedBy.HIBERNATE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.UsedBy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

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
  private long id;
  /**
   * Table name of affected data.
   */
  @Column(nullable = false)
  private String tableName;
  /**
   * Database identifier of data.
   */
  @Column(nullable = false)
  private long recordId;
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
  public long getId() {
    return id;
  }

  @UsedBy(HIBERNATE)
  public void setId(long id) {
    this.id = id;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public long getRecordId() {
    return recordId;
  }

  public void setRecordId(long recordId) {
    this.recordId = recordId;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  @Nullable
  public String getColumn() {
    return column;
  }

  public void setColumn(@Nullable String column) {
    this.column = column;
  }

  @Nullable
  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(@Nullable String oldValue) {
    this.oldValue = oldValue;
  }

  @Nullable
  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(@Nullable String newValue) {
    this.newValue = newValue;
  }
}
