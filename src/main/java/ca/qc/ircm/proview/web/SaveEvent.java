package ca.qc.ircm.proview.web;

import com.vaadin.ui.Component;

/**
 * Save event.
 */
public class SaveEvent extends Component.Event {
  private static final long serialVersionUID = 7709868652458561869L;
  private Object savedObject;

  public SaveEvent(Component source) {
    super(source);
  }

  public SaveEvent(Component source, Object savedObject) {
    super(source);
    this.savedObject = savedObject;
  }

  public Object getSavedObject() {
    return savedObject;
  }

  public void setSavedObject(Object savedObject) {
    this.savedObject = savedObject;
  }
}
