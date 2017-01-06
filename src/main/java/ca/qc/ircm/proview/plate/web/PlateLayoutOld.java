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

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Plate layout.
 */
@Deprecated
public class PlateLayoutOld extends CustomComponent
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
  private List<VerticalLayout> columnHeaderLayouts;
  private List<Label> columnHeaders;
  private List<VerticalLayout> rowHeaderLayouts;
  private List<Label> rowHeaders;
  private List<List<VerticalLayout>> wellLayouts;

  /**
   * Constructor for a plate.
   */
  public PlateLayoutOld() {
    this(12, 8);
  }

  /**
   * Constructor for a plate of given size (number of columns and rows).
   *
   * @param columns
   *          mumber of columns in the grid
   * @param rows
   *          number of rows in the grid
   */
  public PlateLayoutOld(int columns, int rows) {
    this.columnsCount = columns;
    this.rowsCount = rows;
    gridLayout = new GridLayout(columnsCount + 1, rowsCount + 1);
    columnHeaderLayouts = new ArrayList<>();
    columnHeaders = new ArrayList<>();
    rowHeaderLayouts = new ArrayList<>();
    rowHeaders = new ArrayList<>();
    wellLayouts = new ArrayList<>();
    setCompositionRoot(gridLayout);
    init();
  }

  private void init() {
    addStyleName(STYLE);
    gridLayout.addComponent(headerLayout(new Label()), 0, 0);
    changeGridSize(gridLayout.getColumns() - 1, gridLayout.getRows() - 1);
  }

  private void changeGridSize(int columns, int rows) {
    while (gridLayout.getColumns() - 1 > columns) {
      IntStream.range(0, gridLayout.getRows())
          .forEach(row -> gridLayout.removeComponent(gridLayout.getColumns() - 1, row));
      gridLayout.setColumns(gridLayout.getColumns() - 1);
    }
    while (gridLayout.getRows() - 1 > rows) {
      IntStream.range(0, gridLayout.getColumns())
          .forEach(column -> gridLayout.removeComponent(column, gridLayout.getRows() - 1));
      gridLayout.setRows(gridLayout.getRows() - 1);
    }
    gridLayout.setColumns(columns + 1);
    gridLayout.setRows(rows + 1);

    while (columnHeaders.size() < columns) {
      columnHeaders.add(new Label(String.valueOf(columnHeaders.size() + 1)));
    }
    while (columnHeaders.size() > columns) {
      columnHeaders.remove(columnHeaders.size() - 1);
    }
    while (columnHeaderLayouts.size() < columns) {
      int column = columnHeaderLayouts.size();
      VerticalLayout layout = headerLayout(columnHeaders.get(column));
      layout.addStyleName(HEADER_COLUMN_STYLE);
      columnHeaderLayouts.add(layout);
      gridLayout.addComponent(layout, column + 1, 0);
    }
    while (columnHeaderLayouts.size() > columns) {
      columnHeaderLayouts.remove(columnHeaderLayouts.size() - 1);
    }
    while (rowHeaders.size() < rows) {
      rowHeaders.add(new Label(String.valueOf((char) ('A' + rowHeaders.size()))));
    }
    while (rowHeaders.size() > rows) {
      rowHeaders.remove(rowHeaders.size() - 1);
    }
    while (rowHeaderLayouts.size() < rows) {
      int row = rowHeaderLayouts.size();
      VerticalLayout layout = headerLayout(rowHeaders.get(row));
      layout.addStyleName(HEADER_ROW_STYLE);
      rowHeaderLayouts.add(layout);
      gridLayout.addComponent(layout, 0, row + 1);
    }
    while (rowHeaderLayouts.size() > rows) {
      rowHeaderLayouts.remove(rowHeaderLayouts.size() - 1);
    }
    while (wellLayouts.size() < columns) {
      wellLayouts.add(new ArrayList<>());
    }
    while (wellLayouts.size() > columns) {
      wellLayouts.remove(wellLayouts.size() - 1);
    }
    IntStream.range(0, columns).forEach(column -> {
      List<VerticalLayout> columnWellLayouts = wellLayouts.get(column);
      while (columnWellLayouts.size() < rows) {
        int row = columnWellLayouts.size();
        VerticalLayout layout = wellLayout();
        gridLayout.addComponent(layout, column + 1, row + 1);
        columnWellLayouts.add(layout);
      }
      while (columnWellLayouts.size() > rows) {
        columnWellLayouts.remove(columnWellLayouts.size() - 1);
      }
    });
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

  private Stream<VerticalLayout> wells() {
    return wellLayouts.stream().flatMap(list -> list.stream());
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
    wellLayouts.get(column).get(row).addComponent(component);
  }

  public Component getWellComponent(int column, int row, int index) {
    return wellLayouts.get(column).get(row).getComponent(index);
  }

  public void removeAllWellComponents(int column, int row) {
    wellLayouts.get(column).get(row).removeAllComponents();
  }

  public void removeWellComponent(Component component, int column, int row) {
    wellLayouts.get(column).get(row).removeComponent(component);
  }

  public void addWellClickListener(LayoutClickListener listener, int column, int row) {
    wellLayouts.get(column).get(row).addLayoutClickListener(listener);
  }

  public Collection<?> getWellListener(Class<?> eventType, int column, int row) {
    return wellLayouts.get(column).get(row).getListeners(eventType);
  }

  public void removeWellClickListener(LayoutClickListener listener, int column, int row) {
    wellLayouts.get(column).get(row).removeLayoutClickListener(listener);
  }

  public void addWellStyleName(String style, int column, int row) {
    wellLayouts.get(column).get(row).addStyleName(style);
  }

  public String getWellStyleName(int column, int row) {
    return wellLayouts.get(column).get(row).getStyleName();
  }

  public void removeWellStyleName(String style, int column, int row) {
    wellLayouts.get(column).get(row).removeStyleName(style);
  }

  public String getColumnHeaderCaption(int column) {
    return columnHeaders.get(column).getValue();
  }

  public void setColumnHeaderCaption(String caption, int column) {
    columnHeaders.get(column).setValue(caption);
  }

  public void addColumnHeaderClickListener(LayoutClickListener listener, int column) {
    columnHeaderLayouts.get(column).addLayoutClickListener(listener);
  }

  public Collection<?> getColumnHeaderListener(Class<?> eventType, int column) {
    return columnHeaderLayouts.get(column).getListeners(eventType);
  }

  public void removeColumnHeaderClickListener(LayoutClickListener listener, int column) {
    columnHeaderLayouts.get(column).removeLayoutClickListener(listener);
  }

  public String getRowHeaderCaption(int row) {
    return rowHeaders.get(row).getValue();
  }

  public void setRowHeaderCaption(String caption, int row) {
    rowHeaders.get(row).setValue(caption);
  }

  public void addRowHeaderClickListener(LayoutClickListener listener, int row) {
    rowHeaderLayouts.get(row).addLayoutClickListener(listener);
  }

  public Collection<?> getRowHeaderListener(Class<?> eventType, int row) {
    return rowHeaderLayouts.get(row).getListeners(eventType);
  }

  public void removeRowHeaderClickListener(LayoutClickListener listener, int row) {
    rowHeaderLayouts.get(row).removeLayoutClickListener(listener);
  }

  public int getColumns() {
    return gridLayout.getColumns() - 1;
  }

  public void setColumns(int columns) {
    changeGridSize(columns, gridLayout.getRows() - 1);
  }

  public int getRows() {
    return gridLayout.getRows() - 1;
  }

  public void setRows(int rows) {
    changeGridSize(gridLayout.getColumns() - 1, rows);
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
