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

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Plate component that allows selection and drag and drop.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateComponentPresenter {
  public static final String SELECTED_STYLE = "selected";
  private PlateComponent view;
  private boolean multiSelect = false;
  private boolean readOnly = false;
  private Plate plate;
  private Set<PlateSpot> selectedSpots = new HashSet<>();

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlateComponent view) {
    this.view = view;
    addListeners();
  }

  private void updateSelectionMode() {
    if (!multiSelect && getSelectedSpots().size() > 1) {
      deselectAllWells();
    }
  }

  private void updatePlate() {
    clearPlate();
    view.plateLayout.setColumns(plate.getColumnCount());
    view.plateLayout.setRows(plate.getRowCount());
    setWellsContent();
  }

  private void forEachSpot(BiConsumer<Integer, Integer> consumer) {
    IntStream.range(0, view.plateLayout.getColumns())
        .forEach(column -> IntStream.range(0, view.plateLayout.getRows()).forEach(row -> {
          consumer.accept(column, row);
        }));
  }

  private IntStream columnsStream() {
    return IntStream.range(0, view.plateLayout.getColumns());
  }

  private void clearPlate() {
    view.plateLayout.removeAllComponents();
  }

  private void setWellsContent() {
    forEachSpot((column, row) -> {
      PlateSpot well = plate.spot(row, column);
      Label sampleName = new Label();
      if (well != null && well.getSample() != null) {
        sampleName.setValue(well.getSample().getName());
      }
      view.plateLayout.addComponent(sampleName, column, row);
    });
  }

  private void addListeners() {
    view.plateLayout.addWellClickListener(e -> toggleWell(e.getColumn(), e.getRow()));
    view.plateLayout.addColumnHeaderClickListener(e -> toggleColumn(e.getColumn()));
    view.plateLayout.addRowHeaderClickListener(e -> toggleRow(e.getRow()));
  }

  private void toggleColumn(int column) {
    if (multiSelect) {
      List<PlateSpot> spots = plate.column(column);
      if (!selectedSpots.containsAll(spots)) {
        selectColumn(column);
      } else {
        deselectColumn(column);
      }
    }
  }

  private List<PlateSpot> plateRow(int row) {
    return columnsStream().mapToObj(column -> plate.spot(row, column)).collect(Collectors.toList());
  }

  private void toggleRow(int row) {
    if (multiSelect) {
      List<PlateSpot> spots = plateRow(row);
      if (!selectedSpots.containsAll(spots)) {
        selectRow(row);
      } else {
        deselectRow(row);
      }
    }
  }

  private void toggleWell(int column, int row) {
    toggleWell(plate.spot(row, column));
  }

  private void toggleWell(PlateSpot spot) {
    if (!selectedSpots.contains(spot)) {
      selectWell(spot);
    } else {
      deselectWell(spot);
    }
  }

  /**
   * Select a well.
   *
   * @param spot
   *          spot associated with well
   */
  public void selectWell(PlateSpot spot) {
    if (readOnly) {
      return;
    }

    if (!multiSelect) {
      deselectAllWells();
    }
    selectedSpots.add(spot);
    view.plateLayout.addWellStyleName(spot.getColumn(), spot.getRow(), SELECTED_STYLE);
  }

  /**
   * Deselect a well.
   *
   * @param spot
   *          spot associated with well
   */
  public void deselectWell(PlateSpot spot) {
    if (readOnly) {
      return;
    }

    selectedSpots.remove(spot);
    view.plateLayout.removeWellStyleName(spot.getColumn(), spot.getRow(), SELECTED_STYLE);
  }

  /**
   * Deselect all wells.
   */
  public void deselectAllWells() {
    new ArrayList<>(selectedSpots).forEach(spot -> deselectWell(spot));
  }

  /**
   * Select all wells in column.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param column
   *          column
   */
  public void selectColumn(int column) {
    if (multiSelect) {
      List<PlateSpot> spots = plate.column(column);
      spots.forEach(spot -> selectWell(spot));
    }
  }

  /**
   * Deselect all wells in column.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param column
   *          column
   */
  public void deselectColumn(int column) {
    if (multiSelect) {
      List<PlateSpot> spots = plate.column(column);
      spots.forEach(spot -> deselectWell(spot));
    }
  }

  /**
   * Select all wells in row.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param row
   *          row
   */
  public void selectRow(int row) {
    if (multiSelect) {
      List<PlateSpot> spots = plateRow(row);
      spots.forEach(spot -> selectWell(spot));
    }
  }

  /**
   * Deselect all wells in row.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param row
   *          row
   */
  public void deselectRow(int row) {
    if (multiSelect) {
      List<PlateSpot> spots = plateRow(row);
      spots.forEach(spot -> deselectWell(spot));
    }
  }

  public boolean isMultiSelect() {
    return multiSelect;
  }

  public void setMultiSelect(boolean multiSelect) {
    this.multiSelect = multiSelect;
    updateSelectionMode();
  }

  /**
   * Returns select spot in single selection mode or null if no selection was made.
   *
   * @return select spot in single selection mode or null if no selection was made
   * @throws IllegalStateException
   *           multi selection mode is active with more than one selection
   */
  public PlateSpot getSelectedSpot() {
    if (selectedSpots.size() == 0) {
      return null;
    } else if (selectedSpots.size() == 1) {
      return selectedSpots.iterator().next();
    } else {
      throw new IllegalStateException("getSelectedSpot in multi select mode");
    }
  }

  public Collection<PlateSpot> getSelectedSpots() {
    return new ArrayList<>(selectedSpots);
  }

  /**
   * Set selected spots.
   *
   * @param selectedSpots
   *          selected spots
   */
  public void setSelectedSpots(Collection<PlateSpot> selectedSpots) {
    if (readOnly) {
      return;
    }

    new ArrayList<>(this.selectedSpots).forEach(spot -> deselectWell(spot));
    selectedSpots.forEach(spot -> selectWell(spot));
  }

  public Plate getPlate() {
    return plate;
  }

  /**
   * Sets plate, cannot be null.
   *
   * @param plate
   *          plate, cannot be null
   */
  public void setPlate(Plate plate) {
    if (plate == null) {
      throw new NullPointerException();
    }
    this.plate = plate;
    updatePlate();
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
}
