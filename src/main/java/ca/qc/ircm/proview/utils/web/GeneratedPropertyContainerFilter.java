package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;

/**
 * Filter that overcomes the fact that {@link GeneratedPropertyContainer} does not override
 * {@link AbstractInMemoryContainer AbstractInMemoryContainer.getUnfilteredItem()}.
 */
public class GeneratedPropertyContainerFilter implements Filter {
  private static final long serialVersionUID = -125847147966129832L;
  private final Filter filter;
  private final GeneratedPropertyContainer container;

  public GeneratedPropertyContainerFilter(Filter filter, GeneratedPropertyContainer container) {
    this.filter = filter;
    this.container = container;
  }

  @Override
  public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
    // Get fresh copy of item from container.
    Item freshItem = container.getItem(itemId);
    return filter.passesFilter(itemId, freshItem);
  }

  @Override
  public boolean appliesToProperty(Object propertyId) {
    return filter.appliesToProperty(propertyId);
  }
}