package ca.qc.ircm.proview.web;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Interface for listening for a {@link SaveEvent} fired by a {@link Component}.
 */
public interface SaveListener extends Serializable {
  public static final Method SAVED_METHOD =
      ReflectTools.findMethod(SaveListener.class, "saved", SaveEvent.class);

  /**
   * Called when an object has been saved. A reference to the saved object is given by
   * {@link SaveEvent#getObject()}.
   *
   * @param event
   *          an event containing information about the save
   */
  public void saved(SaveEvent event);
}
