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

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.RadioButtonGroup;

import java.util.ArrayList;
import java.util.List;

public class TestBenchUtils {
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

  public static <V> List<V> items(Grid<V> grid) {
    return new ArrayList<>(dataProvider(grid).getItems());
  }

  public static <V> List<V> items(ComboBox<V> comboBox) {
    return new ArrayList<>(dataProvider(comboBox).getItems());
  }

  public static <V> List<V> items(RadioButtonGroup<V> radios) {
    return new ArrayList<>(dataProvider(radios).getItems());
  }

  public static String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }
}
