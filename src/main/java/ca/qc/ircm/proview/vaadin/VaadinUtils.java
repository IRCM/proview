package ca.qc.ircm.proview.vaadin;

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Grid;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Utilities for Vaadin.
 */
public class VaadinUtils {
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
