package ca.qc.ircm.proview.web;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Window;

/**
 * Closes a window when view is changed.
 */
public class CloseWindowOnViewChange {
  private final Window window;
  private Registration viewChangeListenerRegistration;

  private CloseWindowOnViewChange(Window window) {
    this.window = window;
    viewChangeListenerRegistration =
        window.getUI().getNavigator().addViewChangeListener(listener());
  }

  private ViewChangeListener listener() {
    return e -> {
      viewChangeListenerRegistration.remove();
      window.close();
      return true;
    };
  }

  public static void closeWindowOnViewChange(Window window) {
    new CloseWindowOnViewChange(window);
  }
}
