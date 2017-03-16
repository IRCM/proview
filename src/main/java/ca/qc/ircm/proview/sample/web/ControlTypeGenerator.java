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

package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.ControlType;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Generator for control's type.
 */
public class ControlTypeGenerator extends PropertyValueGenerator<String> {
  private static final long serialVersionUID = -3329280893598906015L;
  private transient Supplier<Locale> localeSupplier;

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
