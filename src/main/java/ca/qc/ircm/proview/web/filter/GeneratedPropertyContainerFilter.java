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

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;

/**
 * Filter that overcomes the fact that {@link GeneratedPropertyContainer} does not override
 * {@link AbstractInMemoryContainer AbstractInMemoryContainer.getUnfilteredItem()}.
 */
public class GeneratedPropertyContainerFilter implements Filter {
  private static final long serialVersionUID = -125847147966129832L;
  private final Filter filter;
  private final GeneratedPropertyContainer container;

  public GeneratedPropertyContainerFilter(Filter filter, GeneratedPropertyContainer container) {
    this.filter = filter;
    this.container = container;
  }

  @Override
  public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
    // Get fresh copy of item from container.
    Item freshItem = container.getItem(itemId);
    return filter.passesFilter(itemId, freshItem);
  }

  @Override
  public boolean appliesToProperty(Object propertyId) {
    return filter.appliesToProperty(propertyId);
  }
}