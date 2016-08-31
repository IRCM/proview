package ca.qc.ircm.proview.test.utils;

import ca.qc.ircm.proview.history.UpdateActivity;

/**
 * Envelops {@link UpdateActivity} into a comparable object to allow different {@link UpdateActivity
 * UpdateActivities} to be compared.
 */
public class ComparableUpdateActivity {
  private final UpdateActivity updateActivity;

  public ComparableUpdateActivity(UpdateActivity updateActivity) {
    this.updateActivity = updateActivity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((updateActivity.getActionType() == null) ? 0
        : updateActivity.getActionType().hashCode());
    result = prime * result
        + ((updateActivity.getColumn() == null) ? 0 : updateActivity.getColumn().hashCode());
    result = prime * result
        + ((updateActivity.getNewValue() == null) ? 0 : updateActivity.getNewValue().hashCode());
    result = prime * result
        + ((updateActivity.getOldValue() == null) ? 0 : updateActivity.getOldValue().hashCode());
    result = prime * result
        + ((updateActivity.getRecordId() == null) ? 0 : updateActivity.getRecordId().hashCode());
    result = prime * result
        + ((updateActivity.getTableName() == null) ? 0 : updateActivity.getTableName().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ComparableUpdateActivity)) {
      return false;
    }
    ComparableUpdateActivity other = (ComparableUpdateActivity) obj;
    if (updateActivity.getActionType() != other.updateActivity.getActionType()) {
      return false;
    }
    if (updateActivity.getColumn() == null) {
      if (other.updateActivity.getColumn() != null) {
        return false;
      }
    } else if (!updateActivity.getColumn().equals(other.updateActivity.getColumn())) {
      return false;
    }
    if (updateActivity.getNewValue() == null) {
      if (other.updateActivity.getNewValue() != null) {
        return false;
      }
    } else if (!updateActivity.getNewValue().equals(other.updateActivity.getNewValue())) {
      return false;
    }
    if (updateActivity.getOldValue() == null) {
      if (other.updateActivity.getOldValue() != null) {
        return false;
      }
    } else if (!updateActivity.getOldValue().equals(other.updateActivity.getOldValue())) {
      return false;
    }
    if (updateActivity.getRecordId() == null) {
      if (other.updateActivity.getRecordId() != null) {
        return false;
      }
    } else if (!updateActivity.getRecordId().equals(other.updateActivity.getRecordId())) {
      return false;
    }
    if (updateActivity.getTableName() == null) {
      if (other.updateActivity.getTableName() != null) {
        return false;
      }
    } else if (!updateActivity.getTableName().equals(other.updateActivity.getTableName())) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "UpdateActivity [tableName=" + updateActivity.getTableName() + ", recordId="
        + updateActivity.getRecordId() + ", actionType=" + updateActivity.getActionType()
        + ", column=" + updateActivity.getColumn() + ", oldValue=" + updateActivity.getOldValue()
        + ", newValue=" + updateActivity.getNewValue() + "]";
  }
}
