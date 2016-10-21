package ca.qc.ircm.proview.plate.web;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Plate layout.
 */
public class PlateLayout extends CustomComponent
    implements Layout.AlignmentHandler, Layout.MarginHandler, LayoutClickNotifier {
  public static final String STYLE = "plate";
  public static final String HEADER_STYLE = "plate-header";
  public static final String HEADER_COLUMN_STYLE = "plate-header-column";
  public static final String HEADER_ROW_STYLE = "plate-header-row";
  public static final String WELL_STYLE = "plate-well";
  private static final long serialVersionUID = -1226204297646836402L;
  private final int columnsCount;
  private final int rowsCount;
  private GridLayout gridLayout;
  private VerticalLayout[] columnHeaderLayouts;
  private Label[] columnHeaders;
  private VerticalLayout[] rowHeaderLayouts;
  private Label[] rowHeaders;
  private VerticalLayout[][] wellLayouts;
  private DragAndDropWrapper[][] wellDragAndDropWrappers;

  /**
   * Constructor for a plate of given size (number of columns and rows).
   *
   * @param columns
   *          mumber of columns in the grid
   * @param rows
   *          number of rows in the grid
   */
  public PlateLayout(int columns, int rows) {
    this.columnsCount = columns;
    this.rowsCount = rows;
    gridLayout = new GridLayout(columnsCount + 1, rowsCount + 1);
    columnHeaderLayouts = new VerticalLayout[columnsCount];
    columnHeaders = new Label[columnsCount];
    rowHeaderLayouts = new VerticalLayout[rowsCount];
    rowHeaders = new Label[rowsCount];
    wellLayouts = new VerticalLayout[columnsCount][rowsCount];
    wellDragAndDropWrappers = new DragAndDropWrapper[columnsCount][rowsCount];
    setCompositionRoot(gridLayout);
    init();
  }

  private void init() {
    addStyleName(STYLE);
    IntStream.range(0, columnHeaders.length)
        .forEach(i -> columnHeaders[i] = new Label(String.valueOf(i + 1)));
    IntStream.range(0, columnHeaderLayouts.length).forEach(i -> {
      VerticalLayout layout = headerLayout(columnHeaders[i]);
      layout.addStyleName(HEADER_COLUMN_STYLE);
      columnHeaderLayouts[i] = layout;
    });
    IntStream.range(0, rowHeaders.length)
        .forEach(i -> rowHeaders[i] = new Label(String.valueOf((char) ('A' + i))));
    IntStream.range(0, rowHeaderLayouts.length).forEach(i -> {
      VerticalLayout layout = headerLayout(rowHeaders[i]);
      layout.addStyleName(HEADER_ROW_STYLE);
      rowHeaderLayouts[i] = layout;
    });
    IntStream.range(0, wellLayouts.length).forEach(i -> IntStream.range(0, wellLayouts[i].length)
        .forEach(j -> wellLayouts[i][j] = wellLayout()));
    IntStream.range(0, wellDragAndDropWrappers.length)
        .forEach(i -> IntStream.range(0, wellDragAndDropWrappers[i].length).forEach(
            j -> wellDragAndDropWrappers[i][j] = wellDragAndDropWrapper(wellLayouts[i][j])));

    gridLayout.addComponent(headerLayout(new Label()), 0, 0);
    IntStream.range(1, columnsCount + 1)
        .forEach(i -> gridLayout.addComponent(columnHeaderLayouts[i - 1], i, 0));
    IntStream.range(1, rowsCount + 1)
        .forEach(i -> gridLayout.addComponent(rowHeaderLayouts[i - 1], 0, i));
    IntStream.range(1, columnsCount + 1).forEach(i -> IntStream.range(1, rowsCount + 1)
        .forEach(j -> gridLayout.addComponent(wellDragAndDropWrappers[i - 1][j - 1], i, j)));
  }

  private VerticalLayout headerLayout(Component component) {
    VerticalLayout layout = new VerticalLayout();
    layout.addStyleName(HEADER_STYLE);
    layout.addComponent(component);
    layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
    return layout;
  }

  private VerticalLayout wellLayout() {
    VerticalLayout layout = new VerticalLayout();
    layout.addStyleName(WELL_STYLE);
    return layout;
  }

  private DragAndDropWrapper wellDragAndDropWrapper(Component well) {
    DragAndDropWrapper wellWrapper = new DragAndDropWrapper(well);
    wellWrapper.addStyleName(WELL_STYLE);
    return wellWrapper;
  }

  private Stream<VerticalLayout> wells() {
    return Stream.of(wellLayouts).flatMap(array -> Stream.of(array));
  }

  private VerticalLayout findWellByComponent(Component childComponent) {
    return wells().filter(w -> w.getComponentIndex(childComponent) > -1).findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Component must be added to layout before using setComponentAlignment()"));
  }

  private VerticalLayout anyWell() {
    return wells().findAny().orElseThrow(() -> new IllegalArgumentException(
        "Component must be added to layout before using setComponentAlignment()"));
  }

  public void addWellComponent(Component component, int column, int row) {
    wellLayouts[column][row].addComponent(component);
  }

  public void removeAllWellComponents(int column, int row) {
    wellLayouts[column][row].removeAllComponents();
  }

  public void removeWellComponent(Component component, int column, int row) {
    wellLayouts[column][row].removeComponent(component);
  }

  public void addWellClickListener(LayoutClickListener listener, int column, int row) {
    wellLayouts[column][row].addLayoutClickListener(listener);
  }

  public Collection<?> getWellListener(Class<?> eventType, int column, int row) {
    return wellLayouts[column][row].getListeners(eventType);
  }

  public void removeWellClickListener(LayoutClickListener listener, int column, int row) {
    wellLayouts[column][row].removeLayoutClickListener(listener);
  }

  public void addWellStyleName(String style, int column, int row) {
    wellLayouts[column][row].addStyleName(style);
  }

  public String getWellStyleName(int column, int row) {
    return wellLayouts[column][row].getStyleName();
  }

  public void removeWellStyleName(String style, int column, int row) {
    wellLayouts[column][row].removeStyleName(style);
  }

  public DragAndDropWrapper getWellDragAndDropWrapper(int column, int row) {
    return wellDragAndDropWrappers[column][row];
  }

  public String getColumnHeaderCaption(int column) {
    return columnHeaders[column].getValue();
  }

  public void setColumnHeaderCaption(String caption, int column) {
    columnHeaders[column].setValue(caption);
  }

  public void addColumnHeaderClickListener(LayoutClickListener listener, int column) {
    columnHeaderLayouts[column].addLayoutClickListener(listener);
  }

  public Collection<?> getColumnHeaderListener(Class<?> eventType, int column) {
    return columnHeaderLayouts[column].getListeners(eventType);
  }

  public void removeColumnHeaderClickListener(LayoutClickListener listener, int column) {
    columnHeaderLayouts[column].removeLayoutClickListener(listener);
  }

  public String getRowHeaderCaption(int row) {
    return rowHeaders[row].getValue();
  }

  public void setRowHeaderCaption(String caption, int row) {
    rowHeaders[row].setValue(caption);
  }

  public void addRowHeaderClickListener(LayoutClickListener listener, int row) {
    rowHeaderLayouts[row].addLayoutClickListener(listener);
  }

  public Collection<?> getRowHeaderListener(Class<?> eventType, int row) {
    return rowHeaderLayouts[row].getListeners(eventType);
  }

  public void removeRowHeaderClickListener(LayoutClickListener listener, int row) {
    rowHeaderLayouts[row].removeLayoutClickListener(listener);
  }

  @Override
  public void addLayoutClickListener(LayoutClickListener listener) {
    gridLayout.addLayoutClickListener(listener);
  }

  @Override
  @Deprecated
  public void addListener(LayoutClickListener listener) {
    gridLayout.addListener(listener);
  }

  @Override
  public Collection<?> getListeners(Class<?> eventType) {
    if (LayoutClickEvent.class.isAssignableFrom(eventType)) {
      return gridLayout.getListeners(eventType);
    } else {
      return super.getListeners(eventType);
    }
  }

  @Override
  public void removeLayoutClickListener(LayoutClickListener listener) {
    gridLayout.removeLayoutClickListener(listener);
  }

  @Override
  @Deprecated
  public void removeListener(LayoutClickListener listener) {
    gridLayout.removeListener(listener);
  }

  @Override
  public void setMargin(boolean enabled) {
    gridLayout.setMargin(enabled);
  }

  @Override
  public void setMargin(MarginInfo marginInfo) {
    gridLayout.setMargin(marginInfo);
  }

  @Override
  public MarginInfo getMargin() {
    return gridLayout.getMargin();
  }

  @Override
  public void setComponentAlignment(Component childComponent, Alignment alignment) {
    VerticalLayout well = findWellByComponent(childComponent);
    well.setComponentAlignment(childComponent, alignment);
  }

  @Override
  public Alignment getComponentAlignment(Component childComponent) {
    VerticalLayout well = findWellByComponent(childComponent);
    return well.getComponentAlignment(childComponent);
  }

  @Override
  public void setDefaultComponentAlignment(Alignment defaultComponentAlignment) {
    wells().forEach(w -> w.setDefaultComponentAlignment(defaultComponentAlignment));
  }

  @Override
  public Alignment getDefaultComponentAlignment() {
    return anyWell().getDefaultComponentAlignment();
  }
}
