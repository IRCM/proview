package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.Activity.ActionType;

public class UpdateActivityBuilder {
  private String tableName;
  private Long recordId;
  private ActionType actionType;
  private String column;
  private Object oldValue;
  private Object newValue;
  private DatabaseConverter converter = new DatabaseConverterImpl();

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
