package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare;

/**
 * Filters container based on user's value input using equality.
 */
public class FilterEqualsChangeListener extends AbstractFilterListener
    implements ValueChangeListener {
  private static final long serialVersionUID = 3858086889104287641L;
  private final Object propertyId;
  private final Object nullId;

  public FilterEqualsChangeListener(Container.Filterable container, Object propertyId) {
    this(container, propertyId, null);
  }

  /**
   * Creates equals filter.
   *
   * @param container
   *          container
   * @param propertyId
   *          property
   * @param nullId
   *          value that should be considered equal to null in addition to null itself
   */
  public FilterEqualsChangeListener(Container.Filterable container, Object propertyId,
      Object nullId) {
    super(container);
    this.propertyId = propertyId;
    this.nullId = nullId;
  }

  @Override
  public void valueChange(ValueChangeEvent event) {
    removeContainerFilters(propertyId);
    if (event.getProperty().getValue() != null && !event.getProperty().getValue().equals(nullId)) {
      container.addContainerFilter(new Compare.Equal(propertyId, event.getProperty().getValue()));
    }
  }
}
