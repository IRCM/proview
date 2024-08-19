package ca.qc.ircm.proview.history;

/**
 * Type of action done on data.
 */
public enum ActionType {
  /**
   * Data was just inserted.
   */
  INSERT,
  /**
   * Data was just updated.
   */
  UPDATE,
  /**
   * Data was just deleted.
   */
  DELETE;
}
