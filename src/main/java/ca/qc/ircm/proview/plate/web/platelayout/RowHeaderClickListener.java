package ca.qc.ircm.proview.plate.web.platelayout;

import com.vaadin.util.ReflectTools;

import java.lang.reflect.Method;

/**
 * Listener for row header click events.
 */
public interface RowHeaderClickListener {
  public static final Method ROW_HEADER_CLICK_METHOD = ReflectTools
      .findMethod(RowHeaderClickListener.class, "rowHeaderClick", RowHeaderClickEvent.class);

  public void rowHeaderClick(RowHeaderClickEvent event);
}
