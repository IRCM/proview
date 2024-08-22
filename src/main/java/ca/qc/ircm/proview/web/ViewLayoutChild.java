package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.Component;
import java.util.Optional;

/**
 * Allows to interact with {@link ViewLayout} (if ViewLayout is a parent component).
 */
public interface ViewLayoutChild {
  /**
   * Returns {@link ViewLayout} if it is a parent of current component.
   * 
   * @return {@link ViewLayout} if it is a parent of current component
   */
  default Optional<ViewLayout> viewLayout() {
    Component parent = getParent().orElse(null);
    while (parent != null && !(parent instanceof ViewLayout)) {
      parent = parent.getParent().orElse(null);
    }
    return Optional.ofNullable((ViewLayout) parent);
  }

  Optional<Component> getParent();
}
