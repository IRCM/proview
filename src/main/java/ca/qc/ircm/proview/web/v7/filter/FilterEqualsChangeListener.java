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

package ca.qc.ircm.proview.web.v7.filter;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.filter.Compare;

/**
 * Filters container based on user's value input using equality.
 */
public class FilterEqualsChangeListener extends CutomNullPropertyFilterValueChangeListener
    implements ValueChangeListener {
  private static final long serialVersionUID = 3858086889104287641L;

  public FilterEqualsChangeListener(Container.Filterable container, Object propertyId) {
    super(container, propertyId, null);
  }

  public FilterEqualsChangeListener(Container.Filterable container, Object propertyId,
      Object acceptAllId) {
    super(container, propertyId, acceptAllId);
  }

  @Override
  protected void addNonNullFilter(Object propertyValue) {
    container.addContainerFilter(new Compare.Equal(propertyId, propertyValue));
  }
}
