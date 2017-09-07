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
