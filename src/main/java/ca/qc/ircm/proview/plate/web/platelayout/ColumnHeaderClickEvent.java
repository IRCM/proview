package ca.qc.ircm.proview.plate.web.platelayout;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 * Column header click event.
 */
public class ColumnHeaderClickEvent extends Event {
  private static final long serialVersionUID = -7127891611808715599L;
  private final int column;
  private final MouseEventDetails details;

  /**
   * Creates column header click event.
   *
   * @param source
   *          source component
   * @param column
   *          column
   */
  public ColumnHeaderClickEvent(Component source, int column) {
    super(source);
    this.column = column;
    this.details = null;
  }

  /**
   * Creates column header click event.
   *
   * @param source
   *          source component
   * @param column
   *          column
   * @param details
   *          mouse details
   */
  public ColumnHeaderClickEvent(Component source, int column, MouseEventDetails details) {
    super(source);
    this.column = column;
    this.details = details;
  }

  public int getColumn() {
    return column;
  }

  public MouseEventDetails getDetails() {
    return details;
  }
}
