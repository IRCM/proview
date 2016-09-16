package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;

/**
 * Allows to set a custom value as being a accept all filter.
 */
public abstract class CutomNullPropertyFilterValueChangeListener
    extends RemovePropertyFiltersValueChangeListener {
  private static final long serialVersionUID = -2571240525301281819L;
  protected final Object acceptAllId;

  public CutomNullPropertyFilterValueChangeListener(Container.Filterable container,
      Object propertyId) {
    this(container, propertyId, null);
  }

  /**
   * Creates a listener that will not add filter to container if filter matches acceptAllId.
   *
   * @param container
   *          container
   * @param propertyId
   *          property id
   * @param acceptAllId
   *          value that should be considered an accept all filter
   */
  public CutomNullPropertyFilterValueChangeListener(Container.Filterable container,
      Object propertyId, Object acceptAllId) {
    super(container, propertyId);
    this.acceptAllId = acceptAllId;
  }

  @Override
  protected final void addFilter(Object propertyValue) {
    if (propertyValue != null && !propertyValue.equals(acceptAllId)) {
      addNonNullFilter(propertyValue);
    }
  }

  protected abstract void addNonNullFilter(Object propertyValue);
}
