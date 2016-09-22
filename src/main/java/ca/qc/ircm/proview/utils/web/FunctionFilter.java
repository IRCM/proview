package ca.qc.ircm.proview.utils.web;

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
  private final BiFunction<Object, Item, Object> itemValueFunction;
  private final BiFunction<Object, Object, Boolean> comparisonFunction;

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
