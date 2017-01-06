package ca.qc.ircm.proview.plate.web.platelayout;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 * Row header click event.
 */
public class RowHeaderClickEvent extends Event {
  private static final long serialVersionUID = -7127891611808715599L;
  private final int row;
  private final MouseEventDetails details;

  /**
   * Creates row header click event.
   *
   * @param source
   *          source component
   * @param row
   *          row
   */
  public RowHeaderClickEvent(Component source, int row) {
    super(source);
    this.row = row;
    this.details = null;
  }

  /**
   * Creates row header click event.
   *
   * @param source
   *          source component
   * @param row
   *          row
   * @param details
   *          mouse details
   */
  public RowHeaderClickEvent(Component source, int row, MouseEventDetails details) {
    super(source);
    this.row = row;
    this.details = details;
  }

  public int getRow() {
    return row;
  }

  public MouseEventDetails getDetails() {
    return details;
  }
}
