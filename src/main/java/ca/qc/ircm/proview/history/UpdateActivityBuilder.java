package ca.qc.ircm.proview.history;

import org.springframework.lang.Nullable;

/**
 * Builder for an update activity - change to a database column for some data.
 */
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

  /**
   * Creates a builder from activity.
   *
   * @param activity
   *          activity
   */
  public UpdateActivityBuilder(UpdateActivity activity) {
    tableName = activity.getTableName();
    recordId = activity.getRecordId();
    actionType = activity.getActionType();
    column = activity.getColumn();
    oldValue = activity.getOldValue();
    newValue = activity.getNewValue();
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
    activity.setOldValue(converter.convert(oldValue).orElse(null));
    activity.setNewValue(converter.convert(newValue).orElse(null));
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

  public UpdateActivityBuilder column(@Nullable String column) {
    this.column = column;
    return this;
  }

  public UpdateActivityBuilder oldValue(@Nullable Object oldValue) {
    this.oldValue = oldValue;
    return this;
  }

  public UpdateActivityBuilder newValue(@Nullable Object newValue) {
    this.newValue = newValue;
    return this;
  }

  public UpdateActivityBuilder converter(DatabaseConverter converter) {
    this.converter = converter;
    return this;
  }
}
