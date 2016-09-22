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