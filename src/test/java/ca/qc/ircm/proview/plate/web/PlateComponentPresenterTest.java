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

import static ca.qc.ircm.proview.plate.web.PlateComponentPresenter.PLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.addon.spreadsheet.Spreadsheet.CellValueChangeEvent;
import com.vaadin.addon.spreadsheet.Spreadsheet.CellValueChangeListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateComponentPresenterTest {
  private PlateComponentPresenter presenter;
  @Mock
  private PlateComponent view;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlateComponent.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    presenter = new PlateComponentPresenter();
    view.spreadsheet = new Spreadsheet(getClass().getResourceAsStream("/Plate-Template.xlsx"));
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);

    verify(view).addStyleName(PLATE);
    assertTrue(view.spreadsheet.getStyleName().contains(PLATE));
  }

  @Test
  public void spreadsheet() {
    presenter.init(view);

    assertFalse(view.spreadsheet.isFunctionBarVisible());
    assertFalse(view.spreadsheet.isSheetSelectionBarVisible());
    assertTrue(view.spreadsheet.isRowColHeadingsVisible());
    Plate plate = presenter.getPlate();
    assertEquals(plate.getRowCount() + 1, view.spreadsheet.getRows());
    assertEquals(plate.getColumnCount() + 1, view.spreadsheet.getColumns());
    Sheet sheet = view.spreadsheet.getActiveSheet();
    for (int rowIndex = 0; rowIndex < plate.getRowCount() + 1; rowIndex++) {
      Row row = sheet.getRow(rowIndex);
      for (int column = 0; column < plate.getColumnCount() + 1; column++) {
        Cell cell = row.getCell(column);
        CellStyle style = cell.getCellStyle();
        assertEquals(rowIndex + "-" + column, rowIndex == 0 || column == 0, style.getLocked());
        if (rowIndex == 0 && column == 0) {
          assertEquals(resources.message(PLATE), view.spreadsheet.getCellValue(cell));
        } else if (rowIndex == 0) {
          assertEquals(Plate.columnLabel(column - 1), view.spreadsheet.getCellValue(cell));
        } else if (column == 0) {
          assertEquals(Plate.rowLabel(rowIndex - 1), view.spreadsheet.getCellValue(cell));
        } else if (rowIndex > 0 && column > 0) {
          assertEquals("", view.spreadsheet.getCellValue(cell));
        }
      }
    }
    assertEquals(1, view.spreadsheet.getSelectedCellReference().getCol());
    assertEquals(1, view.spreadsheet.getSelectedCellReference().getRow());
  }

  @Test
  public void isMultiSelect() {
    presenter.init(view);

    assertFalse(presenter.isMultiSelect());

    presenter.setMultiSelect(true);
    assertTrue(presenter.isMultiSelect());

    presenter.setMultiSelect(false);
    assertFalse(presenter.isMultiSelect());
  }

  @Test
  public void setMultiSelect() {
    presenter.init(view);
    Plate plate = presenter.getPlate();

    presenter.setMultiSelect(true);

    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    presenter.setSelectedWells(Arrays.asList(well1, well2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(6, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 0 && ref.getCol() - 1 == 1)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 0 && ref.getCol() - 1 == 2)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 1 && ref.getCol() - 1 == 0)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 1 && ref.getCol() - 1 == 1)
        .findAny().isPresent());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well2.getRow() && ref.getCol() - 1 == well2.getColumn())
        .findAny().isPresent());
    assertEquals(6, presenter.getSelectedWells().size());
    assertTrue(presenter.getSelectedWells().contains(well1));
    assertTrue(presenter.getSelectedWells().contains(plate.well(0, 1)));
    assertTrue(presenter.getSelectedWells().contains(plate.well(0, 2)));
    assertTrue(presenter.getSelectedWells().contains(plate.well(1, 0)));
    assertTrue(presenter.getSelectedWells().contains(plate.well(1, 1)));
    assertTrue(presenter.getSelectedWells().contains(well2));
  }

  @Test
  public void getSelectedWell_NotMulti() {
    presenter.init(view);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    presenter.setSelectedWells(Arrays.asList(well1, well2));

    Well well = presenter.getSelectedWell();

    assertEquals(well1, well);
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getSelectedWell_None() {
    presenter.init(view);
    view.spreadsheet.getCellSelectionManager().clear();

    Well well = presenter.getSelectedWell();

    assertNull(well);
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(0, references.size());
  }

  @Test
  public void getSelectedWell_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    presenter.setSelectedWells(Arrays.asList(well1, well2));

    try {
      presenter.getSelectedWell();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      // Success.
    }
  }

  @Test
  public void getSelectedWells_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    presenter.setSelectedWells(Arrays.asList(well1, well2));

    Collection<Well> wells = presenter.getSelectedWells();

    assertEquals(6, wells.size());
    assertTrue(wells.contains(well1));
    assertTrue(wells.contains(well2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(6, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 0 && ref.getCol() - 1 == 1)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 0 && ref.getCol() - 1 == 2)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 1 && ref.getCol() - 1 == 0)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 1 && ref.getCol() - 1 == 1)
        .findAny().isPresent());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well2.getRow() && ref.getCol() - 1 == well2.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getSelectedWells_NotMulti() {
    presenter.init(view);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    presenter.setSelectedWells(Arrays.asList(well1, well2));

    Collection<Well> wells = presenter.getSelectedWells();

    assertEquals(1, wells.size());
    assertTrue(wells.contains(well1));
    assertFalse(wells.contains(well2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getSelectedWells_MultiThanNotMulti() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    presenter.setSelectedWells(Arrays.asList(well1, well2));
    presenter.setMultiSelect(false);

    Collection<Well> wells = presenter.getSelectedWells();

    assertEquals(0, wells.size());
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(0, references.size());
  }

  @Test
  public void setSelectedWells_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    List<Well> wells = new ArrayList<>();
    wells.add(well1);
    wells.add(well2);

    presenter.setSelectedWells(wells);

    Collection<Well> selectedWells = presenter.getSelectedWells();
    assertEquals(6, selectedWells.size());
    assertTrue(selectedWells.contains(well1));
    assertTrue(selectedWells.contains(plate.well(0, 1)));
    assertTrue(selectedWells.contains(plate.well(0, 2)));
    assertTrue(selectedWells.contains(plate.well(1, 0)));
    assertTrue(selectedWells.contains(plate.well(1, 1)));
    assertTrue(selectedWells.contains(well2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(6, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 0 && ref.getCol() - 1 == 1)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 0 && ref.getCol() - 1 == 2)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 1 && ref.getCol() - 1 == 0)
        .findAny().isPresent());
    assertTrue(references.stream().filter(ref -> ref.getRow() - 1 == 1 && ref.getCol() - 1 == 1)
        .findAny().isPresent());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well2.getRow() && ref.getCol() - 1 == well2.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void setSelectedWells_NotMulti() {
    presenter.init(view);
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 2);
    List<Well> wells = new ArrayList<>();
    wells.add(well1);
    wells.add(well2);

    presenter.setSelectedWells(wells);

    Collection<Well> selectedWells = presenter.getSelectedWells();
    assertEquals(1, selectedWells.size());
    assertTrue(selectedWells.contains(well1));
    assertFalse(selectedWells.contains(well2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getPlate() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.initWells();
    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
  }

  @Test
  public void setPlate() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.initWells();
    Well well1 = plate.well(0, 0);
    well1.setSample(new SubmissionSample(1L, "test 1"));
    Well well2 = plate.well(0, 1);
    well2.setSample(new Control(1L, "test control 1"));
    Well well3 = plate.well(0, 2);
    well3.setSample(new SubmissionSample(2L, "test control 2"));
    Well well4 = plate.well(1, 0);
    well4.setSample(new SubmissionSample(4L, "test control 4"));

    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
    Sheet sheet = view.spreadsheet.getActiveSheet();
    assertEquals(well1.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(well1.getRow() + 1).getCell(well1.getColumn() + 1)));
    assertEquals(well2.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(well2.getRow() + 1).getCell(well2.getColumn() + 1)));
    assertEquals(well3.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(well3.getRow() + 1).getCell(well3.getColumn() + 1)));
    assertEquals(well4.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(well4.getRow() + 1).getCell(well4.getColumn() + 1)));
  }

  @Test
  public void setPlate_DifferentSize() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.setRowCount(13);
    plate.setColumnCount(15);
    plate.initWells();

    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
    assertEquals(14, view.spreadsheet.getRows());
    assertEquals(16, view.spreadsheet.getColumns());
  }

  @Test
  public void setPlate_NoWells() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.setType(PlateType.A);
    plate.initWells();

    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
  }

  @Test
  public void isReadOnly() {
    presenter.init(view);

    assertFalse(presenter.isReadOnly());
  }

  @Test
  public void setReadOnly_False() {
    presenter.init(view);

    presenter.setReadOnly(false);

    assertFalse(presenter.isReadOnly());
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 1);
    List<Well> wells = new ArrayList<>();
    wells.add(well1);
    wells.add(well2);
    presenter.setSelectedWells(wells);
    Collection<Well> selectedWells = presenter.getSelectedWells();
    assertEquals(1, selectedWells.size());
    assertTrue(selectedWells.contains(well1));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
    assertFalse(view.spreadsheet.isFunctionBarVisible());
    assertFalse(view.spreadsheet.isSheetSelectionBarVisible());
    assertTrue(view.spreadsheet.isRowColHeadingsVisible());
    Sheet sheet = view.spreadsheet.getActiveSheet();
    for (int rowIndex = 0; rowIndex < plate.getRowCount() + 1; rowIndex++) {
      Row row = sheet.getRow(rowIndex);
      for (int column = 0; column < plate.getColumnCount() + 1; column++) {
        Cell cell = row.getCell(column, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        CellStyle style = cell.getCellStyle();
        assertEquals(rowIndex + "-" + column, rowIndex == 0 || column == 0, style.getLocked());
      }
    }
  }

  @Test
  public void setReadOnly_True() {
    presenter.init(view);

    presenter.setReadOnly(true);

    assertTrue(presenter.isReadOnly());
    Plate plate = presenter.getPlate();
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 1);
    List<Well> wells = new ArrayList<>();
    wells.add(well1);
    wells.add(well2);
    presenter.setSelectedWells(wells);
    Collection<Well> selectedWells = presenter.getSelectedWells();
    assertEquals(1, selectedWells.size());
    assertTrue(selectedWells.contains(well1));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == well1.getRow() && ref.getCol() - 1 == well1.getColumn())
        .findAny().isPresent());
    assertFalse(view.spreadsheet.isFunctionBarVisible());
    assertFalse(view.spreadsheet.isSheetSelectionBarVisible());
    assertTrue(view.spreadsheet.isRowColHeadingsVisible());
    Sheet sheet = view.spreadsheet.getActiveSheet();
    for (int rowIndex = 0; rowIndex < plate.getRowCount() + 1; rowIndex++) {
      Row row = sheet.getRow(rowIndex);
      for (int column = 0; column < plate.getColumnCount() + 1; column++) {
        Cell cell = row.getCell(column, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        CellStyle style = cell.getCellStyle();
        assertTrue(style.getLocked());
      }
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void updateCell() {
    presenter.init(view);

    Sheet sheet = view.spreadsheet.getActiveSheet();
    sheet.getRow(1).getCell(1).setCellValue("test 1");
    sheet.getRow(1).getCell(2).setCellValue("test 2");
    Collection<CellValueChangeListener> listeners =
        (Collection<CellValueChangeListener>) view.spreadsheet
            .getListeners(CellValueChangeEvent.class);
    CellValueChangeEvent event = new CellValueChangeEvent(view.spreadsheet,
        new HashSet<>(Arrays.asList(new CellReference(1, 1), new CellReference(1, 2))));
    listeners.stream().forEach(lis -> lis.onCellValueChange(event));
    assertNotNull(presenter.getPlate().well(0, 0).getSample());
    assertTrue(presenter.getPlate().well(0, 0).getSample() instanceof SubmissionSample);
    assertEquals("test 1", presenter.getPlate().well(0, 0).getSample().getName());
    assertNotNull(presenter.getPlate().well(0, 1).getSample());
    assertTrue(presenter.getPlate().well(0, 1).getSample() instanceof SubmissionSample);
    assertEquals("test 2", presenter.getPlate().well(0, 1).getSample().getName());
  }
}
