package ca.qc.ircm.proview.web.data;

import com.vaadin.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Optional;

/**
 * {@link ListDataProvider} that supports null.
 */
public class NullableListDataProvider<T> extends ListDataProvider<T> {
  private static final long serialVersionUID = -5388667963908490315L;

  public NullableListDataProvider(Collection<T> items) {
    super(items);
  }

  @Override
  public Object getId(T item) {
    if (item == null) {
      return Optional.empty();
    } else {
      return super.getId(item);
    }
  }
}
