package ca.qc.ircm.proview.test.utils;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.RadioButtonGroup;

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

  public static String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }
}
