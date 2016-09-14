package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;

/**
 * Filters container based on user's text input.
 */
public class FilterTextChangeListener extends AbstractFilterListener implements TextChangeListener {
  private static final long serialVersionUID = 4325600451461475685L;
  private final Object propertyId;
  private final boolean ignoreCase;
  private final boolean onlyMatchPrefix;

  /**
   * Creates contains text filter.
   *
   * @param container
   *          container
   * @param propertyId
   *          property
   * @param ignoreCase
   *          true to ignore case in comparison
   * @param onlyMatchPrefix
   *          true to only match prefix rather than doing a contains check
   */
  public FilterTextChangeListener(Container.Filterable container, Object propertyId,
      boolean ignoreCase, boolean onlyMatchPrefix) {
    super(container);
    this.propertyId = propertyId;
    this.ignoreCase = ignoreCase;
    this.onlyMatchPrefix = onlyMatchPrefix;
  }

  @Override
  public void textChange(TextChangeEvent event) {
    removeContainerFilters(propertyId);
    if (!event.getText().isEmpty()) {
      container.addContainerFilter(
          new SimpleStringFilter(propertyId, event.getText(), ignoreCase, onlyMatchPrefix));
    }
  }
}
