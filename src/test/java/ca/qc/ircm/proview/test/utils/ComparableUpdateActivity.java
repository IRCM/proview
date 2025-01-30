package ca.qc.ircm.proview.test.utils;

import ca.qc.ircm.proview.history.UpdateActivity;
import java.util.Objects;

/**
 * Envelops {@link UpdateActivity} into a comparable object to allow different
 * {@link UpdateActivity UpdateActivities} to be compared.
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
    result = prime * result + updateActivity.getActionType().hashCode();
    result = prime * result
        + ((updateActivity.getColumn() == null) ? 0 : updateActivity.getColumn().hashCode());
    result = prime * result
        + ((updateActivity.getNewValue() == null) ? 0 : updateActivity.getNewValue().hashCode());
    result = prime * result
        + ((updateActivity.getOldValue() == null) ? 0 : updateActivity.getOldValue().hashCode());
    result = prime * result + Long.hashCode(updateActivity.getRecordId());
    result = prime * result + updateActivity.getTableName().hashCode();
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
    if (!(obj instanceof ComparableUpdateActivity other)) {
      return false;
    }
    return Objects.equals(updateActivity.getActionType(), other.updateActivity.getActionType())
        && Objects.equals(updateActivity.getTableName(), other.updateActivity.getTableName())
        && Objects.equals(updateActivity.getRecordId(), other.updateActivity.getRecordId())
        && Objects.equals(updateActivity.getColumn(), other.updateActivity.getColumn())
        && Objects.equals(updateActivity.getNewValue(), other.updateActivity.getNewValue())
        && Objects.equals(updateActivity.getOldValue(), other.updateActivity.getOldValue());
  }

  @Override
  public String toString() {
    return "UpdateActivity [tableName=" + updateActivity.getTableName() + ", recordId="
        + updateActivity.getRecordId() + ", actionType=" + updateActivity.getActionType()
        + ", column=" + updateActivity.getColumn() + ", oldValue=" + updateActivity.getOldValue()
        + ", newValue=" + updateActivity.getNewValue() + "]";
  }
}
