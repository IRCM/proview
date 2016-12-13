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

import java.util.function.BiFunction;

/**
 * Filter that uses a function to get item's value.
 */
public class FunctionFilter implements Filter {
  private static final long serialVersionUID = -9055301945319342341L;
  public static final BiFunction<Object, Object, Boolean> EQUALS_FUNCTION =
      (itemValue, value) -> value == null ? itemValue == null : value.equals(itemValue);
  private final Object propertyId;
  private final Object value;
  private final transient BiFunction<Object, Item, Object> itemValueFunction;
  private final transient BiFunction<Object, Object, Boolean> comparisonFunction;

  /**
   * Creates function filter.
   *
   * @param propertyId
   *          property on which filter is applied
   * @param value
   *          value to test for equality
   * @param itemValueFunction
   *          function that returns item's value
   */
  public FunctionFilter(Object propertyId, Object value,
      BiFunction<Object, Item, Object> itemValueFunction) {
    this(propertyId, value, itemValueFunction, EQUALS_FUNCTION);
  }

  public static BiFunction<Object, Item, Object> itemValueFunction(Object propertyId) {
    return (itemId, item) -> item.getItemProperty(propertyId).getValue();
  }

  /**
   * Creates function filter.
   *
   * @param propertyId
   *          property on which filter is applied
   * @param value
   *          value to test
   * @param itemValueFunction
   *          function that returns item's value
   * @param comparisonFunction
   *          operation used to compare values
   */
  public FunctionFilter(Object propertyId, Object value,
      BiFunction<Object, Item, Object> itemValueFunction,
      BiFunction<Object, Object, Boolean> comparisonFunction) {
    this.propertyId = propertyId;
    this.value = value;
    this.itemValueFunction = itemValueFunction;
    this.comparisonFunction = comparisonFunction;
  }

  @Override
  public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
    Object itemValue = itemValueFunction.apply(itemId, item);
    return comparisonFunction.apply(itemValue, value);
  }

  @Override
  public boolean appliesToProperty(Object propertyId) {
    return this.propertyId.equals(propertyId);
  }

  public Object getPropertyId() {
    return propertyId;
  }

  public Object getValue() {
    return value;
  }

  public BiFunction<Object, Item, Object> getItemValueFunction() {
    return itemValueFunction;
  }

  public BiFunction<Object, Object, Boolean> getComparisonFunction() {
    return comparisonFunction;
  }
}
