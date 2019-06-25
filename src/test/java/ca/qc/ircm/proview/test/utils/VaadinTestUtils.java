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

package ca.qc.ircm.proview.test.utils;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.EditorImpl;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VaadinTestUtils {
  @SuppressWarnings("unchecked")
  public static <V> ListDataProvider<V> dataProvider(Grid<V> grid) {
    return (ListDataProvider<V>) grid.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  public static <V> ListDataProvider<V> dataProvider(ComboBox<V> comboBox) {
    return (ListDataProvider<V>) comboBox.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  public static <V> ListDataProvider<V> dataProvider(RadioButtonGroup<V> radios) {
    return (ListDataProvider<V>) radios.getDataProvider();
  }

  /**
   * Returns items in grid, unsorted and non-filtered.
   *
   * @param grid
   *          grid
   * @return items in grid, unsorted and non-filtered
   */
  @SuppressWarnings("unchecked")
  public static <V> List<V> items(Grid<V> grid) {
    if (grid.getDataProvider() instanceof ListDataProvider) {
      return new ArrayList<>(((ListDataProvider<V>) grid.getDataProvider()).getItems());
    } else {
      return grid.getDataProvider().fetch(new Query<>(0, Integer.MAX_VALUE, null, null, null))
          .collect(Collectors.toList());
    }
  }

  public static <V> List<V> items(ComboBox<V> comboBox) {
    return new ArrayList<>(dataProvider(comboBox).getItems());
  }

  public static <V> List<V> items(RadioButtonGroup<V> radios) {
    return new ArrayList<>(dataProvider(radios).getItems());
  }

  /**
   * Simulates a open edit event in grid.
   *
   * @param grid
   *          grid
   * @param value
   *          value to edit
   */
  public static <V> void gridStartEdit(Grid<V> grid, V value) {
    try {
      Method method = EditorImpl.class.getDeclaredMethod("doEdit", Object.class);
      method.setAccessible(true);
      method.invoke(grid.getEditor(), value);
    } catch (SecurityException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException("Could not call doEdit", e);
    }
  }
}
