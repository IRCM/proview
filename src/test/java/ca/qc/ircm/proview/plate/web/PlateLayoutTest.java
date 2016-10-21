package ca.qc.ircm.proview.plate.web;

import static ca.qc.ircm.proview.plate.web.PlateLayout.HEADER_COLUMN_STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.HEADER_ROW_STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.HEADER_STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.STYLE;
import static ca.qc.ircm.proview.plate.web.PlateLayout.WELL_STYLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateLayoutTest {
  private static final String GRID_LAYOUTS_FIELD = "gridLayout";
  private static final String COLUMN_HEADER_LAYOUTS_FIELD = "columnHeaderLayouts";
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

  private <T> Stream<T> matrixStream(T[][] matrix) {
    return Stream.of(matrix).flatMap(array -> Stream.of(array));
  }

  private Object getField(String fieldName) throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Field field = PlateLayout.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(plateLayout);
  }

  @Test
  public void styles() throws Throwable {
    assertTrue(plateLayout.getStyleName().contains(STYLE));
    VerticalLayout[] columnHeaderLayouts = (VerticalLayout[]) getField(COLUMN_HEADER_LAYOUTS_FIELD);
    for (VerticalLayout header : columnHeaderLayouts) {
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_COLUMN_STYLE));
    }
    VerticalLayout[] rowHeaderLayouts = (VerticalLayout[]) getField(ROW_HEADER_LAYOUTS_FIELD);
    for (VerticalLayout header : rowHeaderLayouts) {
      assertTrue(header.getStyleName().contains(HEADER_STYLE));
      assertTrue(header.getStyleName().contains(HEADER_ROW_STYLE));
    }
    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    for (VerticalLayout[] array : wellLayouts) {
      for (VerticalLayout well : array) {
        assertTrue(well.getStyleName().contains(WELL_STYLE));
      }
    }
    columns().forEach(column -> rows().forEach(row -> assertTrue(
        plateLayout.getWellDragAndDropWrapper(column, row).getStyleName().contains(WELL_STYLE))));
  }

  @Test
  public void captions() throws Throwable {
    columns()
        .forEach(i -> assertEquals(String.valueOf(1 + i), plateLayout.getColumnHeaderCaption(i)));
    Label[] columnHeaders = (Label[]) getField("columnHeaders");
    columns().forEach(i -> assertEquals(String.valueOf(1 + i), columnHeaders[i].getValue()));
    rows().forEach(
        i -> assertEquals(String.valueOf((char) ('A' + i)), plateLayout.getRowHeaderCaption(i)));
    Label[] rowHeaders = (Label[]) getField("rowHeaders");
    rows().forEach(i -> assertEquals(String.valueOf((char) ('A' + i)), rowHeaders[i].getValue()));
  }

  @Test
  public void setColumnCaption() throws Throwable {
    columns().forEach(i -> plateLayout.setColumnHeaderCaption(String.valueOf((char) ('E' + i)), i));
    rows().forEach(i -> plateLayout.setRowHeaderCaption(String.valueOf(22 + i), i));

    columns().forEach(
        i -> assertEquals(String.valueOf((char) ('E' + i)), plateLayout.getColumnHeaderCaption(i)));
    Label[] columnHeaders = (Label[]) getField("columnHeaders");
    columns()
        .forEach(i -> assertEquals(String.valueOf((char) ('E' + i)), columnHeaders[i].getValue()));
    rows().forEach(i -> assertEquals(String.valueOf(22 + i), plateLayout.getRowHeaderCaption(i)));
    Label[] rowHeaders = (Label[]) getField("rowHeaders");
    rows().forEach(i -> assertEquals(String.valueOf(22 + i), rowHeaders[i].getValue()));
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

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertEquals(0, wellLayouts[0][0].getComponentIndex(label00));
    assertEquals(0, wellLayouts[11][0].getComponentIndex(label110));
    assertEquals(0, wellLayouts[1][4].getComponentIndex(label14));
    assertEquals(0, wellLayouts[11][7].getComponentIndex(label117));
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void addWellComponent_InvalidColumn() {
    plateLayout.addWellComponent(new Label(), columns, 0);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void addWellComponent_InvalidRow() {
    plateLayout.addWellComponent(new Label(), 0, rows);
  }

  @Test
  public void addWellComponent_Multiple() throws Throwable {
    Label label1 = new Label("test1");
    Label label2 = new Label("test2");

    plateLayout.addWellComponent(label1, 0, 0);
    plateLayout.addWellComponent(label2, 0, 0);

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertEquals(0, wellLayouts[0][0].getComponentIndex(label1));
    assertEquals(1, wellLayouts[0][0].getComponentIndex(label2));
  }

  @Test
  public void removeAllWellComponents() throws Throwable {
    plateLayout.addWellComponent(new Label("test1"), 0, 0);
    plateLayout.addWellComponent(new Label("test2"), 0, 0);

    plateLayout.removeAllWellComponents(0, 0);

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertFalse(wellLayouts[0][0].iterator().hasNext());
  }

  @Test
  public void removeWellComponent() throws Throwable {
    Label label00 = new Label("test");
    plateLayout.addWellComponent(label00, 0, 0);

    plateLayout.removeWellComponent(label00, 0, 0);

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertEquals(-1, wellLayouts[0][0].getComponentIndex(label00));
  }

  @Test
  public void addWellClickListener() throws Throwable {
    plateLayout.addWellClickListener(layoutClickListener, 0, 0);

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertTrue(
        wellLayouts[0][0].getListeners(LayoutClickEvent.class).contains(layoutClickListener));
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

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertFalse(
        wellLayouts[0][0].getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void addWellStyleName() throws Throwable {
    plateLayout.addWellStyleName("test", 0, 0);

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertTrue(wellLayouts[0][0].getStyleName().contains("test"));
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

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertFalse(wellLayouts[0][0].getStyleName().contains("test"));
  }

  @Test
  public void getWellDragAndDropWrapper() {
    DragAndDropWrapper wellWrapper = plateLayout.getWellDragAndDropWrapper(0, 0);

    assertNotNull(wellWrapper);
    assertEquals(DragStartMode.NONE, wellWrapper.getDragStartMode());
  }

  @Test
  public void addColumnHeaderClickListener() throws Throwable {
    plateLayout.addColumnHeaderClickListener(layoutClickListener, 0);

    VerticalLayout[] columnHeaderLayouts = (VerticalLayout[]) getField(COLUMN_HEADER_LAYOUTS_FIELD);
    assertTrue(
        columnHeaderLayouts[0].getListeners(LayoutClickEvent.class).contains(layoutClickListener));
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

    VerticalLayout[] columnHeaderLayouts = (VerticalLayout[]) getField(COLUMN_HEADER_LAYOUTS_FIELD);
    assertFalse(
        columnHeaderLayouts[0].getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void addRowHeaderClickListener() throws Throwable {
    plateLayout.addRowHeaderClickListener(layoutClickListener, 0);

    VerticalLayout[] rowHeaderLayouts = (VerticalLayout[]) getField(ROW_HEADER_LAYOUTS_FIELD);
    assertTrue(
        rowHeaderLayouts[0].getListeners(LayoutClickEvent.class).contains(layoutClickListener));
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

    VerticalLayout[] rowHeaderLayouts = (VerticalLayout[]) getField(ROW_HEADER_LAYOUTS_FIELD);
    assertFalse(
        rowHeaderLayouts[0].getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void addLayoutClickListener() throws Throwable {
    plateLayout.addLayoutClickListener(layoutClickListener);

    GridLayout gridLayout = (GridLayout) getField(GRID_LAYOUTS_FIELD);
    assertTrue(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  @SuppressWarnings("deprecation")
  public void addListener() throws Throwable {
    plateLayout.addListener(layoutClickListener);

    GridLayout gridLayout = (GridLayout) getField(GRID_LAYOUTS_FIELD);
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

    GridLayout gridLayout = (GridLayout) getField(GRID_LAYOUTS_FIELD);
    assertFalse(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  @SuppressWarnings("deprecation")
  public void removeListener() throws Throwable {
    plateLayout.addListener(layoutClickListener);

    plateLayout.removeListener(layoutClickListener);

    GridLayout gridLayout = (GridLayout) getField(GRID_LAYOUTS_FIELD);
    assertFalse(gridLayout.getListeners(LayoutClickEvent.class).contains(layoutClickListener));
  }

  @Test
  public void setMargin_Boolean() throws Throwable {
    GridLayout gridLayout = (GridLayout) getField(GRID_LAYOUTS_FIELD);
    assertFalse(gridLayout.getMargin().hasAll());

    plateLayout.setMargin(true);

    assertTrue(gridLayout.getMargin().hasAll());
  }

  @Test
  public void setMargin_MarginInfo() throws Throwable {
    MarginInfo marginInfo = new MarginInfo(true, false, false, true);

    plateLayout.setMargin(marginInfo);

    GridLayout gridLayout = (GridLayout) getField(GRID_LAYOUTS_FIELD);
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

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    assertEquals(alignment, wellLayouts[0][0].getComponentAlignment(label));
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

    VerticalLayout[][] wellLayouts = (VerticalLayout[][]) getField(WELL_LAYOUTS_FIELD);
    matrixStream(wellLayouts)
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
