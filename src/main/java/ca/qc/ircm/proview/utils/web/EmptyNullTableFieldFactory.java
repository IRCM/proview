package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

/**
 * TableFieldFactory that uses empty string to represent null values.
 */
public class EmptyNullTableFieldFactory implements TableFieldFactory {
  private static final long serialVersionUID = 3296013903469268936L;
  private DefaultFieldFactory defaultFactory = DefaultFieldFactory.get();

  @Override
  public Field<?> createField(Container container, Object itemId, Object propertyId,
      Component uiContext) {
    Field<?> field = defaultFactory.createField(container, itemId, propertyId, uiContext);
    if (field instanceof TextField) {
      ((TextField) field).setNullRepresentation("");
    }
    return field;
  }

}
