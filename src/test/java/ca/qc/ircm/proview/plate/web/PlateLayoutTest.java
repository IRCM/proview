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

package ca.qc.ircm.proview.plate.web;

import static ca.qc.ircm.proview.plate.web.PlateLayout.HEADER_COLUMN_STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.HEADER_ROW_STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.HEADER_STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.WELL_STYLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateLayoutTest {
  private static final String GRID_LAYOUTS_FIELD = "gridLayout";
  private static final String COLUMN_HEADERS_FIELD = "columnHeaders";
  private static final String COLUMN_HEADER_LAYOUTS_FIELD = "columnHeaderLayouts";
  private static final String ROW_HEADERS_FIELD = "rowHeaders";
  private static final String ROW_HEADER_LAYOUTS_FIELD = "rowHeaderLayouts";
  private static final String WELL_LAYOUTS_FIELD = "wellLayouts";
  private PlateLayout plateLayout;
  @Mock
  private LayoutClickListener layoutClickListener;
  private int columns = 12;
  private int rows = 8;

  @Before
  public void beforeTest() {
    plateLayout = new PlateLayout(12, 8);
  }

  private IntStream columns() {
    return IntStream.range(0, columns);
  }

  private IntStream rows() {
    return IntStream.range(0, rows);
  }

  private Object getField(String fieldName) throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Field field = PlateLayout.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(plateLayout);
  }

  private GridLayout gridLayout() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    return (GridLayout) getField(GRID_LAYOUTS_FIELD);
  }

  @SuppressWarnings("unchecked")
  private List<Label> columnHeaders() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    return (List<Label>) getField(COLUMN_HEADERS_FIELD);
  }

  @SuppressWarnings("unchecked")
  private List<VerticalLayout> columnHeaderLayouts() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    return (List<VerticalLayout>) getField(COLUMN_HEADER_LAYOUTS_FIELD);
  }

  @SuppressWarnings("unchecked")
  private List<Label> rowHeaders() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    return (List<Label>) getField(ROW_HEADERS_FIELD);
  }

  @SuppressWarnings("unchecked")
  private List<VerticalLayout> rowHeaderLayouts() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    return (List<VerticalLayout>) getField(ROW_HEADER_LAYOUTS_FIELD);
  }

  @SuppressWarnings("unchecked")
  private List<List<VerticalLayout>> wellLayouts() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    return (List<List<VerticalLayout>>) getField(WELL_LAYOUTS_FIELD);
  }

  @Test
  public void styles() throws Throwable {
    assertTrue(plateLayout.getStyleName().contains(STYLE));
    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    for (VerticalLayout header : columnHeaderLayouts) {
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_COLUMN_STYLE));
    }
    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    for (VerticalLayout header : rowHeaderLayouts) {
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_ROW_STYLE));
    }
    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    for (List<VerticalLayout> array : wellLayouts) {
      for (VerticalLayout well : array) {
        assertTrue(well.getStyleName().contains(WELL_STYLE));
      }
    }
  }

  @Test
  public void captions() throws Throwable {
    columns()
        .forEach(i -> assertEquals(String.valueOf(1 + i), plateLayout.getColumnHeaderCaption(i)));
    List<Label> columnHeaders = columnHeaders();
    columns().forEach(i -> assertEquals(String.valueOf(1 + i), columnHeaders.get(i).getValue()));
    rows().forEach(
        i -> assertEquals(String.valueOf((char) ('A' + i)), plateLayout.getRowHeaderCaption(i)));
    List<Label> rowHeaders = rowHeaders();
    rows()
        .forEach(i -> assertEquals(String.valueOf((char) ('A' + i)), rowHeaders.get(i).getValue()));
  }

  @Test
  public void setColumnCaption() throws Throwable {
    columns().forEach(i -> plateLayout.setColumnHeaderCaption(String.valueOf((char) ('E' + i)), i));
    rows().forEach(i -> plateLayout.setRowHeaderCaption(String.valueOf(22 + i), i));

    columns().forEach(
        i -> assertEquals(String.valueOf((char) ('E' + i)), plateLayout.getColumnHeaderCaption(i)));
    List<Label> columnHeaders = columnHeaders();
    columns().forEach(
        i -> assertEquals(String.valueOf((char) ('E' + i)), columnHeaders.get(i).getValue()));
    rows().forEach(i -> assertEquals(String.valueOf(22 + i), plateLayout.getRowHeaderCaption(i)));
    List<Label> rowHeaders = rowHeaders();
    rows().forEach(i -> assertEquals(String.valueOf(22 + i), rowHeaders.get(i).getValue()));
  }

  @Test
  public void addWellComponent() throws Throwable {
    Label label00 = new Label("test");
    plateLayout.addWellComponent(label00, 0, 0);
    Label label110 = new Label("test");
    plateLayout.addWellComponent(label110, 11, 0);
    Label label14 = new Label("test");
    plateLayout.addWellComponent(label14, 1, 4);
    Label label117 = new Label("test");
    plateLayout.addWellComponent(label117, 11, 7);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(0, wellLayouts.get(0).get(0).getComponentIndex(label00));
    assertEquals(0, wellLayouts.get(11).get(0).getComponentIndex(label110));
    assertEquals(0, wellLayouts.get(1).get(4).getComponentIndex(label14));
    assertEquals(0, wellLayouts.get(11).get(7).getComponentIndex(label117));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addWellComponent_InvalidColumn() {
    plateLayout.addWellComponent(new Label(), columns, 0);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addWellComponent_InvalidRow() {
    plateLayout.addWellComponent(new Label(), 0, rows);
  }

  @Test
  public void addWellComponent_Multiple() throws Throwable {
    Label label1 = new Label("test1");
    Label label2 = new Label("test2");

    plateLayout.addWellComponent(label1, 0, 0);
    plateLayout.addWellComponent(label2, 0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(0, wellLayouts.get(0).get(0).getComponentIndex(label1));
    assertEquals(1, wellLayouts.get(0).get(0).getComponentIndex(label2));
  }

  @Test
  public void removeAllWellComponents() throws Throwable {
    plateLayout.addWellComponent(new Label("test1"), 0, 0);
    plateLayout.addWellComponent(new Label("test2"), 0, 0);

    plateLayout.removeAllWellComponents(0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertFalse(wellLayouts.get(0).get(0).iterator().hasNext());
  }

  @Test
  public void removeWellComponent() throws Throwable {
    Label label00 = new Label("test");
    plateLayout.addWellComponent(label00, 0, 0);

    plateLayout.removeWellComponent(label00, 0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(-1, wellLayouts.get(0).get(0).getComponentIndex(label00));
  }

  @Test
  public void addWellClickListener() throws Throwable {
    plateLayout.addWellClickListener(layoutClickListener, 0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertTrue(wellLayouts.get(0).get(0).getListeners(LayoutClickEvent.class)
        .contains(layoutClickListener));
  }

  @Test
  public void getWellListeners() throws Throwable {
    plateLayout.addWellClickListener(layoutClickListener, 0, 0);

    Collection<?> listeners = plateLayout.getWellListener(LayoutClickEvent.class, 0, 0);
    assertTrue(listeners.contains(layoutClickListener));
  }

  @Test
  public void removeWellClickListener() throws Throwable {
    plateLayout.addWellClickListener(layoutClickListener, 0, 0);

    plateLayout.removeWellClickListener(layoutClickListener, 0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertFalse(wellLayouts.get(0).get(0).getListeners(LayoutClickEvent.class)
        .contains(layoutClickListener));
  }

  @Test
  public void addWellStyleName() throws Throwable {
    plateLayout.addWellStyleName("test", 0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertTrue(wellLayouts.get(0).get(0).getStyleName().contains("test"));
  }

  @Test
  public void getWellStyleName() throws Throwable {
    plateLayout.addWellStyleName("test", 0, 0);

    assertTrue(plateLayout.getWellStyleName(0, 0).contains(WELL_STYLE));
    assertTrue(plateLayout.getWellStyleName(0, 0).contains("test"));
  }

  @Test
  public void removeWellStyleName() throws Throwable {
    plateLayout.addWellStyleName("test", 0, 0);

    plateLayout.removeWellStyleName("test", 0, 0);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertFalse(wellLayouts.get(0).get(0).getStyleName().contains("test"));
  }

  @Test
  public void addColumnHeaderClickListener() throws Throwable {
    plateLayout.addColumnHeaderClickListener(layoutClickListener, 0);

    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    assertTrue(columnHeaderLayouts.get(0).getListeners(LayoutClickEvent.class)
        .contains(layoutClickListener));
  }

  @Test
  public void getColumnHeaderListeners() throws Throwable {
    plateLayout.addColumnHeaderClickListener(layoutClickListener, 0);

    Collection<?> listeners = plateLayout.getColumnHeaderListener(LayoutClickEvent.class, 0);
    assertTrue(listeners.contains(layoutClickListener));
  }

  @Test
  public void removeColumnHeaderClickListener() throws Throwable {
    plateLayout.addColumnHeaderClickListener(layoutClickListener, 0);

    plateLayout.removeColumnHeaderClickListener(layoutClickListener, 0);

    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    assertFalse(columnHeaderLayouts.get(0).getListeners(LayoutClickEvent.class)
        .contains(layoutClickListener));
  }

  @Test
  public void addRowHeaderClickListener() throws Throwable {
    plateLayout.addRowHeaderClickListener(layoutClickListener, 0);

    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    assertTrue(
        rowHeaderLayouts.get(0).getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void getRowHeaderListeners() throws Throwable {
    plateLayout.addRowHeaderClickListener(layoutClickListener, 0);

    Collection<?> listeners = plateLayout.getRowHeaderListener(LayoutClickEvent.class, 0);
    assertTrue(listeners.contains(layoutClickListener));
  }

  @Test
  public void removeRowHeaderClickListener() throws Throwable {
    plateLayout.addRowHeaderClickListener(layoutClickListener, 0);

    plateLayout.removeRowHeaderClickListener(layoutClickListener, 0);

    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    assertFalse(
        rowHeaderLayouts.get(0).getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void setColumns_Increase() throws Throwable {
    int columns = this.columns + 2;

    plateLayout.setColumns(columns);

    GridLayout gridLayout = gridLayout();
    assertEquals(columns + 1, gridLayout.getColumns());
    assertEquals(rows + 1, gridLayout.getRows());
    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    assertEquals(columns, columnHeaderLayouts.size());
    for (int column = 0; column < columns; column++) {
      VerticalLayout header = columnHeaderLayouts.get(column);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_COLUMN_STYLE));
      assertEquals(header, gridLayout.getComponent(column + 1, 0));
    }
    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    assertEquals(rows, rowHeaderLayouts.size());
    for (int row = 0; row < rows; row++) {
      VerticalLayout header = rowHeaderLayouts.get(row);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_ROW_STYLE));
      assertEquals(header, gridLayout.getComponent(0, row + 1));
    }
    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(columns, wellLayouts.size());
    for (int column = 0; column < columns; column++) {
      List<VerticalLayout> array = wellLayouts.get(column);
      assertEquals(rows, array.size());
      for (int row = 0; row < rows; row++) {
        VerticalLayout well = array.get(row);
        assertTrue(well.getStyleName().contains(WELL_STYLE));
        assertEquals(well, gridLayout.getComponent(column + 1, row + 1));
      }
    }
  }

  @Test
  public void setColumns_Decrease() throws Throwable {
    int columns = this.columns - 2;

    plateLayout.setColumns(columns);

    GridLayout gridLayout = gridLayout();
    assertEquals(columns + 1, gridLayout.getColumns());
    assertEquals(rows + 1, gridLayout.getRows());
    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    assertEquals(columns, columnHeaderLayouts.size());
    for (int column = 0; column < columns; column++) {
      VerticalLayout header = columnHeaderLayouts.get(column);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_COLUMN_STYLE));
      assertEquals(header, gridLayout.getComponent(column + 1, 0));
    }
    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    assertEquals(rows, rowHeaderLayouts.size());
    for (int row = 0; row < rows; row++) {
      VerticalLayout header = rowHeaderLayouts.get(row);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_ROW_STYLE));
      assertEquals(header, gridLayout.getComponent(0, row + 1));
    }
    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(columns, wellLayouts.size());
    for (int column = 0; column < columns; column++) {
      List<VerticalLayout> array = wellLayouts.get(column);
      assertEquals(rows, array.size());
      for (int row = 0; row < rows; row++) {
        VerticalLayout well = array.get(row);
        assertTrue(well.getStyleName().contains(WELL_STYLE));
        assertEquals(well, gridLayout.getComponent(column + 1, row + 1));
      }
    }
  }

  @Test
  public void setRows_Increase() throws Throwable {
    int rows = this.rows + 2;

    plateLayout.setRows(rows);

    GridLayout gridLayout = gridLayout();
    assertEquals(columns + 1, gridLayout.getColumns());
    assertEquals(rows + 1, gridLayout.getRows());
    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    assertEquals(columns, columnHeaderLayouts.size());
    for (int column = 0; column < columns; column++) {
      VerticalLayout header = columnHeaderLayouts.get(column);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_COLUMN_STYLE));
      assertEquals(header, gridLayout.getComponent(column + 1, 0));
    }
    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    assertEquals(rows, rowHeaderLayouts.size());
    for (int row = 0; row < rows; row++) {
      VerticalLayout header = rowHeaderLayouts.get(row);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_ROW_STYLE));
      assertEquals(header, gridLayout.getComponent(0, row + 1));
    }
    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(columns, wellLayouts.size());
    for (int column = 0; column < columns; column++) {
      List<VerticalLayout> array = wellLayouts.get(column);
      assertEquals(rows, array.size());
      for (int row = 0; row < rows; row++) {
        VerticalLayout well = array.get(row);
        assertTrue(well.getStyleName().contains(WELL_STYLE));
        assertEquals(well, gridLayout.getComponent(column + 1, row + 1));
      }
    }
  }

  @Test
  public void setRows_Decrease() throws Throwable {
    int rows = this.rows - 2;

    plateLayout.setRows(rows);

    GridLayout gridLayout = gridLayout();
    assertEquals(columns + 1, gridLayout.getColumns());
    assertEquals(rows + 1, gridLayout.getRows());
    List<VerticalLayout> columnHeaderLayouts = columnHeaderLayouts();
    assertEquals(columns, columnHeaderLayouts.size());
    for (int column = 0; column < columns; column++) {
      VerticalLayout header = columnHeaderLayouts.get(column);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_COLUMN_STYLE));
      assertEquals(header, gridLayout.getComponent(column + 1, 0));
    }
    List<VerticalLayout> rowHeaderLayouts = rowHeaderLayouts();
    assertEquals(rows, rowHeaderLayouts.size());
    for (int row = 0; row < rows; row++) {
      VerticalLayout header = rowHeaderLayouts.get(row);
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_ROW_STYLE));
      assertEquals(header, gridLayout.getComponent(0, row + 1));
    }
    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(columns, wellLayouts.size());
    for (int column = 0; column < columns; column++) {
      List<VerticalLayout> array = wellLayouts.get(column);
      assertEquals(rows, array.size());
      for (int row = 0; row < rows; row++) {
        VerticalLayout well = array.get(row);
        assertTrue(well.getStyleName().contains(WELL_STYLE));
        assertEquals(well, gridLayout.getComponent(column + 1, row + 1));
      }
    }
  }

  @Test
  public void addLayoutClickListener() throws Throwable {
    plateLayout.addLayoutClickListener(layoutClickListener);

    GridLayout gridLayout = gridLayout();
    assertTrue(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  @SuppressWarnings("deprecation")
  public void addListener() throws Throwable {
    plateLayout.addListener(layoutClickListener);

    GridLayout gridLayout = gridLayout();
    assertTrue(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void getListeners() throws Throwable {
    plateLayout.addLayoutClickListener(layoutClickListener);

    Collection<?> listeners = plateLayout.getListeners(LayoutClickEvent.class);
    assertTrue(listeners.contains(layoutClickListener));
  }

  @Test
  public void removeLayoutClickListener() throws Throwable {
    plateLayout.addLayoutClickListener(layoutClickListener);

    plateLayout.removeLayoutClickListener(layoutClickListener);

    GridLayout gridLayout = gridLayout();
    assertFalse(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  @SuppressWarnings("deprecation")
  public void removeListener() throws Throwable {
    plateLayout.addListener(layoutClickListener);

    plateLayout.removeListener(layoutClickListener);

    GridLayout gridLayout = gridLayout();
    assertFalse(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void setMargin_Boolean() throws Throwable {
    GridLayout gridLayout = gridLayout();
    assertFalse(gridLayout.getMargin().hasAll());

    plateLayout.setMargin(true);

    assertTrue(gridLayout.getMargin().hasAll());
  }

  @Test
  public void setMargin_MarginInfo() throws Throwable {
    MarginInfo marginInfo = new MarginInfo(true, false, false, true);

    plateLayout.setMargin(marginInfo);

    GridLayout gridLayout = gridLayout();
    assertEquals(marginInfo, gridLayout.getMargin());
  }

  @Test
  public void getMargin() throws Throwable {
    MarginInfo marginInfo = new MarginInfo(true, false, false, true);
    plateLayout.setMargin(marginInfo);

    assertEquals(marginInfo, plateLayout.getMargin());
  }

  @Test
  public void setComponentAlignment() throws Throwable {
    Alignment alignment = Alignment.MIDDLE_CENTER;
    Label label = new Label();
    plateLayout.addWellComponent(label, 0, 0);

    plateLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    assertEquals(alignment, wellLayouts.get(0).get(0).getComponentAlignment(label));
  }

  @Test
  public void getComponentAlignment() throws Throwable {
    Alignment alignment = Alignment.MIDDLE_CENTER;
    Label label = new Label();
    plateLayout.addWellComponent(label, 0, 0);
    plateLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

    assertEquals(alignment, plateLayout.getComponentAlignment(label));
  }

  @Test
  public void setDefaultComponentAlignment() throws Throwable {
    Alignment alignment = Alignment.MIDDLE_CENTER;

    plateLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

    List<List<VerticalLayout>> wellLayouts = wellLayouts();
    wellLayouts.stream().flatMap(list -> list.stream())
        .forEach(well -> assertEquals(alignment, well.getDefaultComponentAlignment()));
  }

  @Test
  public void getDefaultComponentAlignment() throws Throwable {
    assertEquals(Alignment.TOP_LEFT, plateLayout.getDefaultComponentAlignment());

    Alignment alignment = Alignment.MIDDLE_CENTER;
    plateLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
    assertEquals(alignment, plateLayout.getDefaultComponentAlignment());
  }
}
