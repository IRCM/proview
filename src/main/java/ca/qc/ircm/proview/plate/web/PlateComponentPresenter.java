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
import ca.qc.ircm.proview.sample.SubmissionSample;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.function.Consumer;
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

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlateComponent view) {
    this.view = view;
    view.spreadsheet.setFunctionBarVisible(false);
    view.spreadsheet.setSheetSelectionBarVisible(false);
    view.spreadsheet.setRowColHeadingsVisible(false);
    plate = new Plate();
    plate.initSpots();
    updatePlate();
    updateReadOnly();
    view.spreadsheet.addCellValueChangeListener(e -> e.getChangedCells().stream().forEach(ref -> {
      Cell cell = view.spreadsheet.getActiveSheet().getRow(ref.getRow()).getCell(ref.getCol());
      PlateSpot well = plate.spot(ref.getRow() - 1, ref.getCol() - 1);
      if (well.getSample() == null) {
        well.setSample(new SubmissionSample());
      }
      well.getSample().setName(view.spreadsheet.getCellValue(cell));
    }));
  }

  private void updateSelectionMode() {
    if (!multiSelect && getSelectedSpots().size() > 1) {
      deselectAllWells();
    }
  }

  private void updatePlate() {
    clearPlate();
    view.spreadsheet.setMaxColumns(plate.getColumnCount() + 1);
    view.spreadsheet.setMaxRows(plate.getRowCount() + 1);
    setWellsContent();
  }

  private void updateReadOnly() {
    CellStyle locked = view.spreadsheet.getWorkbook().createCellStyle();
    locked.setLocked(readOnly);
    forEachCell(cell -> cell.setCellStyle(locked));
  }

  private void forEachCell(Consumer<Cell> consumer) {
    Sheet sheet = view.spreadsheet.getActiveSheet();
    IntStream.range(1, view.spreadsheet.getRows()).mapToObj(rowIndex -> {
      Row row = sheet.getRow(rowIndex);
      if (row == null) {
        sheet.createRow(rowIndex);
      }
      return row;
    }).forEach(row -> IntStream.range(1, view.spreadsheet.getColumns()).forEach(col -> {
      consumer.accept(row.getCell(col, MissingCellPolicy.CREATE_NULL_AS_BLANK));
    }));
  }

  private void clearPlate() {
    forEachCell(cell -> cell.setCellValue(""));
  }

  private void setWellsContent() {
    forEachCell(cell -> {
      int row = cell.getRowIndex() - 1;
      int col = cell.getColumnIndex() - 1;
      PlateSpot well = plate.spot(row, col);
      if (well.getSample() != null) {
        cell.setCellValue(well.getSample().getName());
      }
    });
  }

  private void deselectAllWells() {
    view.spreadsheet.getCellSelectionManager().clear();
  }

  boolean isMultiSelect() {
    return multiSelect;
  }

  void setMultiSelect(boolean multiSelect) {
    this.multiSelect = multiSelect;
    updateSelectionMode();
  }

  /**
   * Returns selected spot.
   *
   * @return selected spot
   */
  PlateSpot getSelectedSpot() {
    if (multiSelect) {
      throw new IllegalStateException("getSelectedSpot cannot be called in multi select mode");
    }
    CellReference reference = view.spreadsheet.getSelectedCellReference();
    if (reference == null) {
      return null;
    } else {
      return plate.spot(reference.getRow() - 1, reference.getCol() - 1);
    }
  }

  /**
   * Returns select spots.
   *
   * @return select spots
   */
  Collection<PlateSpot> getSelectedSpots() {
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    return references.stream().map(ref -> plate.spot(ref.getRow() - 1, ref.getCol() - 1))
        .collect(Collectors.toList());
  }

  /**
   * Set selected wells.
   * <p>
   * Since only ranges can be selected, all wells in range from min / max row to min / max column of
   * all wells.
   * </p>
   *
   * @param selectedSpots
   *          selected wells
   */
  void setSelectedSpots(Collection<PlateSpot> selectedSpots) {
    IntSummaryStatistics rowSummary =
        selectedSpots.stream().mapToInt(spot -> spot.getRow()).summaryStatistics();
    IntSummaryStatistics columnSummary =
        selectedSpots.stream().mapToInt(spot -> spot.getColumn()).summaryStatistics();
    if (multiSelect) {
      view.spreadsheet.setSelectionRange(rowSummary.getMin() + 1, columnSummary.getMin() + 1,
          rowSummary.getMax() + 1, columnSummary.getMax() + 1);
    } else if (!selectedSpots.isEmpty()) {
      PlateSpot well = selectedSpots.iterator().next();
      view.spreadsheet.setSelection(well.getRow() + 1, well.getColumn() + 1);
    }
  }

  Plate getPlate() {
    return plate;
  }

  /**
   * Sets plate, cannot be null.
   *
   * @param plate
   *          plate, cannot be null
   */
  void setPlate(Plate plate) {
    if (plate == null) {
      throw new NullPointerException("plate cannot be null");
    }
    this.plate = plate;
    updatePlate();
  }

  boolean isReadOnly() {
    return readOnly;
  }

  void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    updateReadOnly();
  }
}
