package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.SimpleFilterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides common functionalities for filter listeners.
 */
public class AbstractFilterListener {
  protected final Container.Filterable container;

  public AbstractFilterListener(Container.Filterable container) {
    this.container = container;
  }

  /**
   * Removes container filters for property.
   *
   * @param propertyId
   *          property
   */
  public void removeContainerFilters(Object propertyId) {
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
}
