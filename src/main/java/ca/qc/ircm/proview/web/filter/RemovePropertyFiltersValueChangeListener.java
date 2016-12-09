/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web.filter;

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
