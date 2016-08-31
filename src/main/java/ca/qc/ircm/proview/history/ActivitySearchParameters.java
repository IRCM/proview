package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.Activity.ActionType;

/**
 * Search parameters for activity searches.
 */
public interface ActivitySearchParameters {
  public ActionType getActionType();

  public String getTableName();

  public Long getRecordId();
}
