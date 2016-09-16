package ca.qc.ircm.proview.utils.web;

import com.google.common.collect.Range;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * Filters container based on user's range input.
 */
public class FilterRangeChangeListener extends CutomNullPropertyFilterValueChangeListener
    implements ValueChangeListener {
  private static final long serialVersionUID = 3858086889104287641L;

  public FilterRangeChangeListener(Container.Filterable container, Object propertyId) {
    super(container, propertyId);
  }

  public FilterRangeChangeListener(Container.Filterable container, Object propertyId,
      Object acceptAllId) {
    super(container, propertyId, acceptAllId);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addNonNullFilter(Object propertyValue) {
    Range<?> range = (Range<?>) propertyValue;
    container.addContainerFilter(new RangeFilter(propertyId, range));
  }
}
