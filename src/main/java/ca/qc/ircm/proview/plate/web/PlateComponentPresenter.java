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
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.utils.MessageResource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;

/**
 * Plate component presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateComponentPresenter {
  public static final String PLATE = "plate";
  public static final String PLATE_EXCEPTION = "plate.exception";
  private PlateComponent view;
  private boolean multiSelect = false;
  private boolean readOnly = false;
  private Plate plate;
  @Inject
  private PlateService plateService;

  protected PlateComponentPresenter() {
  }

  protected PlateComponentPresenter(PlateService plateService) {
    this.plateService = plateService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlateComponent view) {
    this.view = view;
    view.addStyleName(PLATE);
    view.spreadsheet.addStyleName(PLATE);
    plate = new Plate();
    plate.initWells();
    updatePlate();
    view.spreadsheet.setFunctionBarVisible(false);
    view.spreadsheet.setSheetSelectionBarVisible(false);
    view.spreadsheet.setSelection(1, 1);
    updateReadOnly();
    view.spreadsheet.addCellValueChangeListener(e -> e.getChangedCells().stream().forEach(ref -> {
      Cell cell = view.spreadsheet.getActiveSheet().getRow(ref.getRow()).getCell(ref.getCol());
      Well well = plate.well(ref.getRow() - 1, ref.getCol() - 1);
      if (well.getSample() == null) {
        well.setSample(new SubmissionSample());
      }
      well.getSample().setName(view.spreadsheet.getCellValue(cell));
    }));
  }

  private void updateSelectionMode() {
    if (!multiSelect && getSelectedWells().size() > 1) {
      deselectAllWells();
    }
  }

  private void updatePlate() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    try {
      view.spreadsheet.setWorkbook(plateService.workbook(plate, locale));
    } catch (IOException e) {
      view.showWarning(resources.message(PLATE_EXCEPTION));
    }
    view.spreadsheet.setMaxColumns(plate.getColumnCount() + 1);
    view.spreadsheet.setMaxRows(plate.getRowCount() + 1);
  }

  private void updateReadOnly() {
    CellStyle locked = view.spreadsheet.getWorkbook().createCellStyle();
    locked.setLocked(readOnly);
    cells().forEach(cell -> cell.setCellStyle(locked));
  }

  private Stream<Cell> cells() {
    Sheet sheet = view.spreadsheet.getActiveSheet();
    return IntStream.range(1, view.spreadsheet.getRows()).mapToObj(rowIndex -> {
      Row row = sheet.getRow(rowIndex);
      if (row == null) {
        sheet.createRow(rowIndex);
      }
      return row;
    }).flatMap(row -> IntStream.range(1, view.spreadsheet.getColumns())
        .mapToObj(col -> row.getCell(col, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
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
   * Returns selected well.
   *
   * @return selected well
   */
  Well getSelectedWell() {
    if (multiSelect) {
      throw new IllegalStateException("getSelectedWell cannot be called in multi select mode");
    }
    CellReference reference = view.spreadsheet.getSelectedCellReference();
    if (reference == null) {
      return null;
    } else {
      return plate.well(reference.getRow() - 1, reference.getCol() - 1);
    }
  }

  /**
   * Returns select wells.
   *
   * @return select wells
   */
  Collection<Well> getSelectedWells() {
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    return references.stream().map(ref -> plate.well(ref.getRow() - 1, ref.getCol() - 1))
        .collect(Collectors.toList());
  }

  /**
   * Set selected wells.
   * <p>
   * Since only ranges can be selected, all wells in range from min / max row to min / max column of
   * all wells.
   * </p>
   *
   * @param selectedWells
   *          selected wells
   */
  void setSelectedWells(Collection<Well> selectedWells) {
    IntSummaryStatistics rowSummary =
        selectedWells.stream().mapToInt(well -> well.getRow()).summaryStatistics();
    IntSummaryStatistics columnSummary =
        selectedWells.stream().mapToInt(well -> well.getColumn()).summaryStatistics();
    if (multiSelect) {
      view.spreadsheet.setSelectionRange(rowSummary.getMin() + 1, columnSummary.getMin() + 1,
          rowSummary.getMax() + 1, columnSummary.getMax() + 1);
    } else if (!selectedWells.isEmpty()) {
      Well well = selectedWells.iterator().next();
      view.spreadsheet.setSelection(well.getRow() + 1, well.getColumn() + 1);
    }
  }

  Plate getValue() {
    return plate;
  }

  void setValue(Plate plate) {
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
