package ca.qc.ircm.proview.utils.web;

import com.google.common.collect.Range;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * Filters container based on user's range input.
 */
public class FilterRangeChangeListener extends AbstractFilterListener
    implements ValueChangeListener {
  private static final long serialVersionUID = 3858086889104287641L;
  private final Object propertyId;
  private final Object nullId;

  public FilterRangeChangeListener(Container.Filterable container, Object propertyId) {
    this(container, propertyId, null);
  }

  /**
   * Creates interval filter.
   *
   * @param container
   *          container
   * @param propertyId
   *          property
   * @param nullId
   *          value that should be considered equal to null in addition to null itself
   */
  public FilterRangeChangeListener(Container.Filterable container, Object propertyId,
      Object nullId) {
    super(container);
    this.propertyId = propertyId;
    this.nullId = nullId;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void valueChange(ValueChangeEvent event) {
    removeContainerFilters(propertyId);
    Object rawValue = event.getProperty().getValue();
    if (rawValue != null && !rawValue.equals(nullId)) {
      Range<?> range = (Range<?>) rawValue;
      container.addContainerFilter(new RangeFilter(propertyId, range));
    }
  }
}
