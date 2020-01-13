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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.grid.editor.EditorImpl;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.BasicRenderer;
import com.vaadin.flow.function.ValueProvider;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   */
  public static <E> void doubleClickItem(Grid<E> grid, E item) {
    doubleClickItem(grid, item, (gv, key) -> new ItemDoubleClickEvent<>(gv, false, key, -1, -1, -1,
        -1, 2, 0, false, false, false, false));
  }

  /**
   * Simulates an item click on grid.
   *
   * @param grid
   *          grid
   * @param item
   *          item
   * @param mockEventConsumer
   *          allows to alter event
   */
  public static <E> void doubleClickItem(Grid<E> grid, E item,
      BiFunction<Grid<E>, String, ItemDoubleClickEvent<E>> eventGenerator) {
    try {
      String key = grid.getDataCommunicator().getKeyMapper().key(item);
      Method method = Component.class.getDeclaredMethod("getEventBus");
      method.setAccessible(true);
      ComponentEventBus eventBus = (ComponentEventBus) method.invoke(grid);
      ItemDoubleClickEvent<E> event = eventGenerator.apply(grid, key);
      eventBus.fireEvent(event);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
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
        .filter(oc -> oc.isPresent()).findFirst().orElse(Optional.empty());
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
      logger.warn("Exception caught whe formatting value {} with renderer {}", item, renderer, e);
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
    assertEquals(expected.getWeek(), actual.getWeek());
    assertEquals(expected.getCalendar(), actual.getCalendar());
    assertEquals(expected.getClear(), actual.getClear());
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
    assertEquals(expected.getCancel(), actual.getCancel());
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
