package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

/**
 * Saved event.
 */
public class SavedEvent<V extends Component> extends ComponentEvent<V> {
  private static final long serialVersionUID = 1558070508317511253L;

  public SavedEvent(V source, boolean fromClient) {
    super(source, fromClient);
  }
}
