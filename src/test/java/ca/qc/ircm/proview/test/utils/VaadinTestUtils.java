package ca.qc.ircm.proview.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.dataview.ComboBoxDataView;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.grid.editor.EditorImpl;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.dataview.RadioButtonGroupDataView;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.BasicRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;
import elemental.json.JsonArray;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for tests on Vaadin components.
 */
public class VaadinTestUtils {
  private static final String ICON_ATTRIBUTE = "icon";
  private static final Logger logger = LoggerFactory.getLogger(VaadinTestUtils.class);

  /**
   * Fires an event on component.
   *
   * @param component
   *          component
   * @param event
   *          event
   */
  public static <C extends Component> void fireEvent(C component, ComponentEvent<C> event) {
    try {
      Method method = Component.class.getDeclaredMethod("getEventBus");
      method.setAccessible(true);
      ComponentEventBus eventBus = (ComponentEventBus) method.invoke(component);
      eventBus.fireEvent(event);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Simulates a click on button.
   *
   * @param button
   *          button
   */
  public static void clickButton(Button button) {
    fireEvent(button, new ClickEvent<>(button));
  }

  /**
   * Simulates an item click on grid.
   *
   * @param grid
   *          grid
   * @param item
   *          item
   * @param column
   *          grid column
   */
  public static <E> void clickItem(Grid<E> grid, E item, Grid.Column<E> column) {
    clickItem(grid, item, column, false, false, false, false);
  }

  /**
   * Simulates an item click on grid.
   *
   * @param grid
   *          grid
   * @param item
   *          item
   * @param column
   *          grid column
   * @param ctrlKey
   *          <code>true</code> if the control key was down when the event was fired,
   *          <code>false</code> otherwise
   * @param shiftKey
   *          <code>true</code> if the shift key was down when the event was fired,
   *          <code>false</code> otherwise
   * @param altKey
   *          <code>true</code> if the alt key was down when the event was fired, <code>false</code>
   *          otherwise
   * @param metaKey
   *          <code>true</code> if the meta key was down when the event was fired,
   *          <code>false</code> otherwise
   */
  public static <E> void clickItem(Grid<E> grid, E item, Grid.Column<E> column, boolean ctrlKey,
      boolean shiftKey, boolean altKey, boolean metaKey) {
    try {
      String key = grid.getDataCommunicator().getKeyMapper().key(item);
      Method method = Component.class.getDeclaredMethod("getEventBus");
      method.setAccessible(true);
      ComponentEventBus eventBus = (ComponentEventBus) method.invoke(grid);
      eventBus.fireEvent(new ItemClickEvent<>(grid, false, key,
          column != null ? column.getElement().getProperty("_flowId") : null, -1, -1, -1, -1, 2, 0,
          ctrlKey, shiftKey, altKey, metaKey));
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Simulates an item double click on grid.
   *
   * @param grid
   *          grid
   * @param item
   *          item
   * @param column
   *          grid column
   */
  public static <E> void doubleClickItem(Grid<E> grid, E item, Grid.Column<E> column) {
    doubleClickItem(grid, item, column, false, false, false, false);
  }

  /**
   * Simulates an item double click on grid.
   *
   * @param grid
   *          grid
   * @param item
   *          item
   * @param column
   *          grid column
   * @param ctrlKey
   *          <code>true</code> if the control key was down when the event was fired,
   *          <code>false</code> otherwise
   * @param shiftKey
   *          <code>true</code> if the shift key was down when the event was fired,
   *          <code>false</code> otherwise
   * @param altKey
   *          <code>true</code> if the alt key was down when the event was fired, <code>false</code>
   *          otherwise
   * @param metaKey
   *          <code>true</code> if the meta key was down when the event was fired,
   *          <code>false</code> otherwise
   */
  public static <E> void doubleClickItem(Grid<E> grid, E item, Grid.Column<E> column,
      boolean ctrlKey, boolean shiftKey, boolean altKey, boolean metaKey) {
    try {
      String key = grid.getDataCommunicator().getKeyMapper().key(item);
      Method method = Component.class.getDeclaredMethod("getEventBus");
      method.setAccessible(true);
      ComponentEventBus eventBus = (ComponentEventBus) method.invoke(grid);
      eventBus.fireEvent(new ItemDoubleClickEvent<>(grid, false, key,
          column != null ? column.getElement().getProperty("_flowId") : null, -1, -1, -1, -1, 2, 0,
          ctrlKey, shiftKey, altKey, metaKey));
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Returns renderer's template.
   *
   * @param renderer
   *          renderer
   * @return renderer's template
   */
  public static String rendererTemplate(Renderer<?> renderer) {
    try {
      Field field;
      if (renderer instanceof LitRenderer) {
        field = LitRenderer.class.getDeclaredField("templateExpression");
      } else {
        field = Renderer.class.getDeclaredField("template");
      }
      field.setAccessible(true);
      return (String) field.get(renderer);
    } catch (SecurityException | NoSuchFieldException | IllegalArgumentException
        | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Returns all registered functions of this renderer.
   *
   * @param <SOURCE>
   *          renderer source type
   * @param renderer
   *          renderer
   * @return all registered functions of this renderer
   */
  public static <SOURCE> Map<String, SerializableBiConsumer<SOURCE, JsonArray>>
      functions(LitRenderer<SOURCE> renderer) {
    try {
      Field field = LitRenderer.class.getDeclaredField("clientCallables");
      field.setAccessible(true);
      @SuppressWarnings("unchecked")
      Map<String, SerializableBiConsumer<SOURCE, JsonArray>> functions =
          (Map<String, SerializableBiConsumer<SOURCE, JsonArray>>) field.get(renderer);
      return functions;
    } catch (SecurityException | NoSuchFieldException | IllegalArgumentException
        | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Returns the first child of specified type.
   *
   * @param <C>
   *          Component
   * @param start
   *          component
   * @param componentType
   *          child's type
   * @return the first child of specified type
   */
  @SuppressWarnings("unchecked")
  public static <C extends Component> Optional<C> findChild(Component start,
      Class<C> componentType) {
    if (start == null) {
      return Optional.empty();
    }
    if (componentType.isAssignableFrom(start.getClass())) {
      return Optional.of((C) start);
    }
    return start.getChildren().map(child -> findChild(child, componentType))
        .filter(Optional::isPresent).findFirst().orElse(Optional.empty());
  }

  /**
   * Returns all the children of specified type.
   *
   * @param <C>
   *          Component
   * @param start
   *          component
   * @param componentType
   *          children's type
   * @return all the children of specified type
   */
  @SuppressWarnings("unchecked")
  public static <C extends Component> List<C> findChildren(Component start,
      Class<C> componentType) {
    if (start == null) {
      return Collections.emptyList();
    }
    if (componentType.isAssignableFrom(start.getClass())) {
      return Collections.nCopies(1, (C) start);
    }
    return start.getChildren().flatMap(child -> findChildren(child, componentType).stream())
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public static <V> ListDataProvider<V> dataProvider(Grid<V> grid) {
    return (ListDataProvider<V>) grid.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  public static <V> ComboBoxDataView<V> dataView(ComboBox<V> comboBox) {
    return comboBox.getGenericDataView();
  }

  @SuppressWarnings("unchecked")
  public static <V> RadioButtonGroupDataView<V> dataView(RadioButtonGroup<V> radios) {
    return radios.getGenericDataView();
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
    return dataView(comboBox).getItems().collect(Collectors.toList());
  }

  public static <V> List<V> items(RadioButtonGroup<V> radios) {
    return dataView(radios).getItems().collect(Collectors.toList());
  }

  public static Optional<BindingValidationStatus<?>>
      findValidationStatusByField(BinderValidationStatus<?> statuses, HasValue<?, ?> field) {
    return findValidationStatusByField(statuses.getFieldValidationErrors(), field);
  }

  public static Optional<BindingValidationStatus<?>>
      findValidationStatusByField(ValidationException e, HasValue<?, ?> field) {
    return findValidationStatusByField(e.getFieldValidationErrors(), field);
  }

  public static Optional<BindingValidationStatus<?>>
      findValidationStatusByField(List<BindingValidationStatus<?>> statuses, HasValue<?, ?> field) {
    return statuses.stream().filter(ve -> ve.getField().equals(field)).findFirst();
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

  /**
   * Validates that actual icon is the same as the expected icon.
   *
   * @param expected
   *          expected icon
   * @param actual
   *          actual icon
   */
  public static void validateIcon(Icon expected, Component actual) {
    assertEquals(expected.getElement().getAttribute(ICON_ATTRIBUTE),
        actual.getElement().getAttribute(ICON_ATTRIBUTE));
  }

  /**
   * Returns renderer's formatted value.
   *
   * @param renderer
   *          renderer
   * @param item
   *          item
   * @return renderer's formatted value
   */
  @SuppressWarnings("unchecked")
  public static <I, R> String getFormattedValue(BasicRenderer<I, R> renderer, I item) {
    try {
      Field field = BasicRenderer.class.getDeclaredField("valueProvider");
      field.setAccessible(true);
      ValueProvider<I, R> vp = (ValueProvider<I, R>) field.get(renderer);
      R value = vp.apply(item);
      Method method = renderer.getClass().getDeclaredMethod("getFormattedValue", Object.class);
      method.setAccessible(true);
      return (String) method.invoke(renderer, value);
    } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchFieldException | SecurityException e) {
      logger.warn("Cannot get formatted value for renderer {} and item {}", renderer, item, e);
      return null;
    }
  }

  /**
   * Validates that two {@link DatePickerI18n} are identical.
   *
   * @param expected
   *          expected
   * @param actual
   *          actual
   */
  public static void validateEquals(DatePickerI18n expected, DatePickerI18n actual) {
    assertEquals(expected.getToday(), actual.getToday());
    assertEquals(expected.getCancel(), actual.getCancel());
    assertEquals(expected.getFirstDayOfWeek(), actual.getFirstDayOfWeek());
    assertEquals(expected.getMonthNames(), actual.getMonthNames());
    assertEquals(expected.getWeekdays(), actual.getWeekdays());
    assertEquals(expected.getWeekdaysShort(), actual.getWeekdaysShort());
  }

  /**
   * Validates that two {@link UploadI18N} are identical.
   *
   * @param expected
   *          expected
   * @param actual
   *          actual
   */
  public static void validateEquals(UploadI18N expected, UploadI18N actual) {
    if (expected.getAddFiles() != null) {
      assertNotNull(actual.getAddFiles());
      assertEquals(expected.getAddFiles().getOne(), actual.getAddFiles().getOne());
      assertEquals(expected.getAddFiles().getMany(), actual.getAddFiles().getMany());
    } else {
      assertNull(actual.getAddFiles());
    }
    if (expected.getDropFiles() != null) {
      assertNotNull(actual.getDropFiles());
      assertEquals(expected.getDropFiles().getOne(), actual.getDropFiles().getOne());
      assertEquals(expected.getDropFiles().getMany(), actual.getDropFiles().getMany());
    } else {
      assertNull(actual.getDropFiles());
    }
    if (expected.getError() != null) {
      assertNotNull(actual.getError());
      assertEquals(expected.getError().getFileIsTooBig(), actual.getError().getFileIsTooBig());
      assertEquals(expected.getError().getIncorrectFileType(),
          actual.getError().getIncorrectFileType());
      assertEquals(expected.getError().getTooManyFiles(), actual.getError().getTooManyFiles());
    } else {
      assertNull(actual.getError());
    }
    if (expected.getUnits() != null) {
      assertNotNull(actual.getUnits());
      assertEquals(expected.getUnits().getSize(), actual.getUnits().getSize());
    } else {
      assertNull(actual.getUnits());
    }
    if (expected.getUploading() != null) {
      assertNotNull(actual.getUploading());
      if (expected.getUploading().getError() != null) {
        assertNotNull(actual.getUploading().getError());
        assertEquals(expected.getUploading().getError().getForbidden(),
            actual.getUploading().getError().getForbidden());
        assertEquals(expected.getUploading().getError().getServerUnavailable(),
            actual.getUploading().getError().getServerUnavailable());
        assertEquals(expected.getUploading().getError().getUnexpectedServerError(),
            actual.getUploading().getError().getUnexpectedServerError());
      } else {
        assertNull(actual.getUploading().getError());
      }
      if (expected.getUploading().getRemainingTime() != null) {
        assertNotNull(actual.getUploading().getRemainingTime());
        assertEquals(expected.getUploading().getRemainingTime().getPrefix(),
            actual.getUploading().getRemainingTime().getPrefix());
        assertEquals(expected.getUploading().getRemainingTime().getUnknown(),
            actual.getUploading().getRemainingTime().getUnknown());
      } else {
        assertNull(actual.getUploading().getRemainingTime());
      }
      if (expected.getUploading().getStatus() != null) {
        assertNotNull(actual.getUploading().getStatus());
        assertEquals(expected.getUploading().getStatus().getConnecting(),
            actual.getUploading().getStatus().getConnecting());
        assertEquals(expected.getUploading().getStatus().getHeld(),
            actual.getUploading().getStatus().getHeld());
        assertEquals(expected.getUploading().getStatus().getProcessing(),
            actual.getUploading().getStatus().getProcessing());
        assertEquals(expected.getUploading().getStatus().getStalled(),
            actual.getUploading().getStatus().getStalled());
      } else {
        assertNull(actual.getUploading().getStatus());
      }
    } else {
      assertNull(actual.getUploading());
    }
  }
}
