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

package ca.qc.ircm.proview.vaadin;

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Grid;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for Vaadin.
 */
public class VaadinUtils {
  public static String property(Object... names) {
    return Arrays.asList(names).stream().filter(name -> name != null)
        .map(name -> String.valueOf(name)).collect(Collectors.joining("."));
  }

  public static String styleName(Object... names) {
    return Arrays.asList(names).stream().filter(name -> name != null)
        .map(name -> String.valueOf(name)).map(name -> name.replaceAll("\\.", "-"))
        .collect(Collectors.joining("-"));
  }

  /**
   * Returns comparator currently used by grid to sort items.
   *
   * @param <T>
   *          grid item type
   * @param grid
   *          grid
   * @return comparator currently used by grid to sort items
   */
  public static <T> Comparator<T> gridComparator(Grid<T> grid) {
    Comparator<T> comparator = (o1, o2) -> 0;
    for (GridSortOrder<T> sortOrder : grid.getSortOrder()) {
      comparator =
          comparator.thenComparing(sortOrder.getSorted().getComparator(sortOrder.getDirection()));
    }
    return comparator;
  }

  /**
   * Returns grid items filtered and sorted base on grid's current state.
   *
   * @param <T>
   *          grid item type
   * @param grid
   *          grid using an instance of ListDataProvider as data provider
   * @return grid items filtered and sorted base on grid's current state
   * @throws IllegalArgumentException
   *           grid's data provider is not an instance of ListDataProvider
   */
  @SuppressWarnings("unchecked")
  public static <T> Stream<T> gridItems(Grid<T> grid) {
    if (!(grid.getDataProvider() instanceof ListDataProvider)) {
      throw new IllegalArgumentException(
          "Grid data provider not of type " + ListDataProvider.class.getSimpleName());
    }

    Comparator<T> comparator = gridComparator(grid);
    ListDataProvider<T> dataProvider = (ListDataProvider<T>) grid.getDataProvider();
    return dataProvider
        .fetch(new Query<>(0, Integer.MAX_VALUE, null, comparator, dataProvider.getFilter()));
  }
}
