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

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;

/**
 * Filters container based on user's text input.
 */
public class FilterTextChangeListener extends CutomNullPropertyFilterValueChangeListener
    implements TextChangeListener {
  private static final long serialVersionUID = 4325600451461475685L;
  private final Object propertyId;
  private final boolean ignoreCase;
  private final boolean onlyMatchPrefix;

  /**
   * Creates contains text filter.
   *
   * @param container
   *          container
   * @param propertyId
   *          property
   * @param ignoreCase
   *          true to ignore case in comparison
   * @param onlyMatchPrefix
   *          true to only match prefix rather than doing a contains check
   */
  public FilterTextChangeListener(Container.Filterable container, Object propertyId,
      boolean ignoreCase, boolean onlyMatchPrefix) {
    super(container, propertyId, "");
    this.propertyId = propertyId;
    this.ignoreCase = ignoreCase;
    this.onlyMatchPrefix = onlyMatchPrefix;
  }

  @Override
  protected void addNonNullFilter(Object propertyValue) {
    String value = (String) propertyValue;
    container
        .addContainerFilter(new SimpleStringFilter(propertyId, value, ignoreCase, onlyMatchPrefix));
  }
}
