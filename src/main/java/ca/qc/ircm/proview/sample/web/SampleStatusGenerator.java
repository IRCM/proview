package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.SampleStatus;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Generator for sample's status.
 */
public class SampleStatusGenerator extends PropertyValueGenerator<String> {
  private static final long serialVersionUID = -3329280893598906015L;
  private Supplier<Locale> localeSupplier;

  public SampleStatusGenerator(Supplier<Locale> localeSupplier) {
    this.localeSupplier = localeSupplier;
  }

  @Override
  public String getValue(Item item, Object itemId, Object propertyId) {
    SampleStatus status = (SampleStatus) item.getItemProperty(propertyId).getValue();
    return status != null ? status.getLabel(localeSupplier.get())
        : SampleStatus.getNullLabel(localeSupplier.get());
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
