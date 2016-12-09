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

/**
 * Allows to set a custom value as being a accept all filter.
 */
public abstract class CutomNullPropertyFilterValueChangeListener
    extends RemovePropertyFiltersValueChangeListener {
  private static final long serialVersionUID = -2571240525301281819L;
  protected final Object acceptAllId;

  public CutomNullPropertyFilterValueChangeListener(Container.Filterable container,
      Object propertyId) {
    this(container, propertyId, null);
  }

  /**
   * Creates a listener that will not add filter to container if filter matches acceptAllId.
   *
   * @param container
   *          container
   * @param propertyId
   *          property id
   * @param acceptAllId
   *          value that should be considered an accept all filter
   */
  public CutomNullPropertyFilterValueChangeListener(Container.Filterable container,
      Object propertyId, Object acceptAllId) {
    super(container, propertyId);
    this.acceptAllId = acceptAllId;
  }

  @Override
  protected final void addFilter(Object propertyValue) {
    if (propertyValue != null && !propertyValue.equals(acceptAllId)) {
      addNonNullFilter(propertyValue);
    }
  }

  protected abstract void addNonNullFilter(Object propertyValue);
}
