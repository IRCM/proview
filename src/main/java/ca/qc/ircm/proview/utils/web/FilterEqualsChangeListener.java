package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare;

/**
 * Filters container based on user's value input using equality.
 */
public class FilterEqualsChangeListener extends CutomNullPropertyFilterValueChangeListener
    implements ValueChangeListener {
  private static final long serialVersionUID = 3858086889104287641L;

  public FilterEqualsChangeListener(Container.Filterable container, Object propertyId) {
    super(container, propertyId, null);
  }

  public FilterEqualsChangeListener(Container.Filterable container, Object propertyId,
      Object acceptAllId) {
    super(container, propertyId, acceptAllId);
  }

  @Override
  protected void addNonNullFilter(Object propertyValue) {
    container.addContainerFilter(new Compare.Equal(propertyId, propertyValue));
  }
}
