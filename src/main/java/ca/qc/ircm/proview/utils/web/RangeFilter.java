package ca.qc.ircm.proview.utils.web;

import com.google.common.collect.Range;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

public class RangeFilter<T extends Comparable<?>> implements Filter {
  private static final long serialVersionUID = 3935971819294022440L;
  private final Object propertyId;
  private final Range<T> range;

  public RangeFilter(Object propertyId, Range<T> range) {
    this.propertyId = propertyId;
    this.range = range;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
    T value = (T) item.getItemProperty(propertyId).getValue();
    return range.contains(value);
  }

  @Override
  public boolean appliesToProperty(Object propertyId) {
    return this.propertyId.equals(propertyId);
  }

  public Object getPropertyId() {
    return propertyId;
  }

  public Range<T> getRange() {
    return range;
  }
}