package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.Activity.ActionType;

/**
 * Search parameters for activity searches.
 */
public class ActivitySearchParametersBean implements ActivitySearchParameters {
  private ActionType actionType;
  private String tableName;
  private Long recordId;

  @Override
  public ActionType getActionType() {
    return actionType;
  }

  public ActivitySearchParametersBean actionType(ActionType actionType) {
    this.actionType = actionType;
    return this;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  public ActivitySearchParametersBean tableName(String tableName) {
    this.tableName = tableName;
    return this;
  }

  @Override
  public Long getRecordId() {
    return recordId;
  }

  public ActivitySearchParametersBean recordId(Long recordId) {
    this.recordId = recordId;
    return this;
  }
}
