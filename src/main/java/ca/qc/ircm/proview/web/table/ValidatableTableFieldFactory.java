/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web.table;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TableFieldFactory that uses empty string to represent null values.
 */
public class ValidatableTableFieldFactory implements TableFieldFactory {
  private static final long serialVersionUID = 3296013903469268936L;
  private final TableFieldFactory delegate;
  private final Map<Field<?>, Object> fields = new HashMap<>();

  public ValidatableTableFieldFactory(TableFieldFactory delegate) {
    this.delegate = delegate;
  }

  @Override
  public Field<?> createField(Container container, Object itemId, Object propertyId,
      Component uiContext) {
    Field<?> field = delegate.createField(container, itemId, propertyId, uiContext);
    fields.put(field, propertyId);
    field.addDetachListener(e -> fields.remove(field));
    return field;
  }

  public List<Field<?>> getFields() {
    return new ArrayList<>(fields.keySet());
  }

  public List<Field<?>> getFields(Object propertyId) {
    return fields.entrySet().stream().filter(e -> e.getValue().equals(propertyId))
        .map(e -> e.getKey()).collect(Collectors.toList());
  }

  /**
   * Returns true if all table fields are valid, false otherwise.
   *
   * @return true if all table fields are valid, false otherwise
   */
  public boolean isValid() {
    boolean valid = true;
    for (Field<?> field : fields.keySet()) {
      valid &= field.isValid();
    }
    return valid;
  }

  /**
   * Commits all table fields.
   */
  public void commit() {
    for (Field<?> field : fields.keySet()) {
      field.commit();
    }
  }
}
