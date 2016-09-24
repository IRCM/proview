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

package ca.qc.ircm.proview.utils.web;

import com.google.common.collect.Range;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

public class RangeFilter<T extends Comparable<?>> implements Filter {
  private static final long serialVersionUID = 3935971819294022440L;
  private final Object propertyId;
  private final Range<T> value;

  public RangeFilter(Object propertyId, Range<T> value) {
    this.propertyId = propertyId;
    this.value = value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
    T itemValue = (T) item.getItemProperty(propertyId).getValue();
    return value.contains(itemValue);
  }

  @Override
  public boolean appliesToProperty(Object propertyId) {
    return this.propertyId.equals(propertyId);
  }

  public Object getPropertyId() {
    return propertyId;
  }

  public Range<T> getValue() {
    return value;
  }
}