package ca.qc.ircm.proview.plate.web.platelayout;

import com.vaadin.util.ReflectTools;

import java.lang.reflect.Method;

/**
 * Listener for column header click events.
 */
public interface ColumnHeaderClickListener {
  public static final Method COLUMN_HEADER_CLICK_METHOD = ReflectTools.findMethod(
      ColumnHeaderClickListener.class, "columnHeaderClick", ColumnHeaderClickEvent.class);

  public void columnHeaderClick(ColumnHeaderClickEvent event);
}
