package ca.qc.ircm.proview.plate.web.platelayout;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 * Well click event.
 */
public class WellClickEvent extends Event {
  private static final long serialVersionUID = -7127891611808715599L;
  private final int column;
  private final int row;
  private final MouseEventDetails details;

  /**
   * Creates well click event.
   *
   * @param source
   *          source component
   * @param column
   *          column
   * @param row
   *          row
   */
  public WellClickEvent(Component source, int column, int row) {
    super(source);
    this.column = column;
    this.row = row;
    this.details = null;
  }

  /**
   * Creates well click event.
   *
   * @param source
   *          source component
   * @param column
   *          column
   * @param row
   *          row
   * @param details
   *          mouse details
   */
  public WellClickEvent(Component source, int column, int row, MouseEventDetails details) {
    super(source);
    this.column = column;
    this.row = row;
    this.details = details;
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

  public MouseEventDetails getDetails() {
    return details;
  }
}
