package ca.qc.ircm.proview.plate.web.platelayout;

import com.vaadin.util.ReflectTools;

import java.lang.reflect.Method;

/**
 * Listener for well click events.
 */
public interface WellClickListener {
  public static final Method WELL_CLICK_METHOD =
      ReflectTools.findMethod(WellClickListener.class, "wellClick", WellClickEvent.class);

  public void wellClick(WellClickEvent event);
}
