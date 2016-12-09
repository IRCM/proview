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
