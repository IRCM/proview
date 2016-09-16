package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes filters associated with property before adding a new filter.
 */
public abstract class RemovePropertyFiltersValueChangeListener
    implements ValueChangeListener, TextChangeListener {
  private static final long serialVersionUID = -5664330958927608720L;
  protected final Container.Filterable container;
  protected final Object propertyId;

  public RemovePropertyFiltersValueChangeListener(Container.Filterable container,
      Object propertyId) {
    this.container = container;
    this.propertyId = propertyId;
  }

  private void removeContainerFilters() {
    if (container instanceof SimpleFilterable) {
      ((SimpleFilterable) container).removeContainerFilters(propertyId);
    } else {
      List<Filter> remove = new ArrayList<>();
      for (Filter filter : container.getContainerFilters()) {
        if (filter.appliesToProperty(propertyId)) {
          remove.add(filter);
        }
      }
      for (Filter filter : remove) {
        container.removeContainerFilter(filter);
      }
    }
  }

  @Override
  public final void valueChange(ValueChangeEvent event) {
    removeContainerFilters();
    addFilter(event.getProperty().getValue());
  }

  @Override
  public final void textChange(TextChangeEvent event) {
    removeContainerFilters();
    addFilter(event.getText());
  }

  protected abstract void addFilter(Object propertyValue);
}
