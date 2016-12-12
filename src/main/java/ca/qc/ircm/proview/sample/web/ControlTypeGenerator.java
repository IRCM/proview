package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.ControlType;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Generator for control's type.
 */
public class ControlTypeGenerator extends PropertyValueGenerator<String> {
  private static final long serialVersionUID = -3329280893598906015L;
  private Supplier<Locale> localeSupplier;

  public ControlTypeGenerator(Supplier<Locale> localeSupplier) {
    this.localeSupplier = localeSupplier;
  }

  @Override
  public String getValue(Item item, Object itemId, Object propertyId) {
    ControlType type = (ControlType) item.getItemProperty(propertyId).getValue();
    return type != null ? type.getLabel(localeSupplier.get())
        : ControlType.getNullLabel(localeSupplier.get());
  }

  @Override
  public Class<String> getType() {
    return String.class;
  }

  @Override
  public SortOrder[] getSortProperties(SortOrder order) {
    return new SortOrder[] { order };
  }

  @Override
  public Filter modifyFilter(Filter filter) throws UnsupportedFilterException {
    return filter;
  }
}
