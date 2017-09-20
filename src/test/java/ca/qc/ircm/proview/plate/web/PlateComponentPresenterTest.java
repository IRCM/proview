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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
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
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateComponentPresenterTest {
  private PlateComponentPresenter presenter;
  @Mock
  private PlateComponent view;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    presenter = new PlateComponentPresenter();
    view.spreadsheet = new Spreadsheet(getClass().getResourceAsStream("/Plate-Template.xlsx"));
  }

  @Test
  public void spreadsheet() {
    presenter.init(view);

    assertFalse(view.spreadsheet.isFunctionBarVisible());
    assertFalse(view.spreadsheet.isSheetSelectionBarVisible());
    assertFalse(view.spreadsheet.isRowColHeadingsVisible());
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
        if (rowIndex > 0 && column > 0) {
          assertEquals("", view.spreadsheet.getCellValue(cell));
        }
      }
    }
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

    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    presenter.setSelectedSpots(Arrays.asList(spot1, spot2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(6, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
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
        .filter(ref -> ref.getRow() - 1 == spot2.getRow() && ref.getCol() - 1 == spot2.getColumn())
        .findAny().isPresent());
    assertEquals(6, presenter.getSelectedSpots().size());
    assertTrue(presenter.getSelectedSpots().contains(spot1));
    assertTrue(presenter.getSelectedSpots().contains(plate.spot(0, 1)));
    assertTrue(presenter.getSelectedSpots().contains(plate.spot(0, 2)));
    assertTrue(presenter.getSelectedSpots().contains(plate.spot(1, 0)));
    assertTrue(presenter.getSelectedSpots().contains(plate.spot(1, 1)));
    assertTrue(presenter.getSelectedSpots().contains(spot2));
  }

  @Test
  public void getSelectedSpot_NotMulti() {
    presenter.init(view);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    presenter.setSelectedSpots(Arrays.asList(spot1, spot2));

    Well spot = presenter.getSelectedSpot();

    assertEquals(spot1, spot);
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getSelectedSpot_None() {
    presenter.init(view);

    Well spot = presenter.getSelectedSpot();

    assertNull(spot);
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(0, references.size());
  }

  @Test
  public void getSelectedSpot_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    presenter.setSelectedSpots(Arrays.asList(spot1, spot2));

    try {
      presenter.getSelectedSpot();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      // Success.
    }
  }

  @Test
  public void getSelectedSpots_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    presenter.setSelectedSpots(Arrays.asList(spot1, spot2));

    Collection<Well> spots = presenter.getSelectedSpots();

    assertEquals(6, spots.size());
    assertTrue(spots.contains(spot1));
    assertTrue(spots.contains(spot2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(6, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
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
        .filter(ref -> ref.getRow() - 1 == spot2.getRow() && ref.getCol() - 1 == spot2.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getSelectedSpots_NotMulti() {
    presenter.init(view);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    presenter.setSelectedSpots(Arrays.asList(spot1, spot2));

    Collection<Well> spots = presenter.getSelectedSpots();

    assertEquals(1, spots.size());
    assertTrue(spots.contains(spot1));
    assertFalse(spots.contains(spot2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getSelectedSpots_MultiThanNotMulti() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    presenter.setSelectedSpots(Arrays.asList(spot1, spot2));
    presenter.setMultiSelect(false);

    Collection<Well> spots = presenter.getSelectedSpots();

    assertEquals(0, spots.size());
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(0, references.size());
  }

  @Test
  public void setSelectedSpots_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    List<Well> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);

    presenter.setSelectedSpots(spots);

    Collection<Well> selectedSpots = presenter.getSelectedSpots();
    assertEquals(6, selectedSpots.size());
    assertTrue(selectedSpots.contains(spot1));
    assertTrue(selectedSpots.contains(plate.spot(0, 1)));
    assertTrue(selectedSpots.contains(plate.spot(0, 2)));
    assertTrue(selectedSpots.contains(plate.spot(1, 0)));
    assertTrue(selectedSpots.contains(plate.spot(1, 1)));
    assertTrue(selectedSpots.contains(spot2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(6, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
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
        .filter(ref -> ref.getRow() - 1 == spot2.getRow() && ref.getCol() - 1 == spot2.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void setSelectedSpots_NotMulti() {
    presenter.init(view);
    Plate plate = presenter.getPlate();
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 2);
    List<Well> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);

    presenter.setSelectedSpots(spots);

    Collection<Well> selectedSpots = presenter.getSelectedSpots();
    assertEquals(1, selectedSpots.size());
    assertTrue(selectedSpots.contains(spot1));
    assertFalse(selectedSpots.contains(spot2));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
        .findAny().isPresent());
  }

  @Test
  public void getPlate() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.initSpots();
    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
  }

  @Test
  public void setPlate() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.initSpots();
    Well spot1 = plate.spot(0, 0);
    spot1.setSample(new SubmissionSample(1L, "test 1"));
    Well spot2 = plate.spot(0, 1);
    spot2.setSample(new Control(1L, "test control 1"));
    Well spot3 = plate.spot(0, 2);
    spot3.setSample(new SubmissionSample(2L, "test control 2"));
    Well spot4 = plate.spot(1, 0);
    spot4.setSample(new SubmissionSample(4L, "test control 4"));

    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
    Sheet sheet = view.spreadsheet.getActiveSheet();
    assertEquals(spot1.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(spot1.getRow() + 1).getCell(spot1.getColumn() + 1)));
    assertEquals(spot2.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(spot2.getRow() + 1).getCell(spot2.getColumn() + 1)));
    assertEquals(spot3.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(spot3.getRow() + 1).getCell(spot3.getColumn() + 1)));
    assertEquals(spot4.getSample().getName(), view.spreadsheet
        .getCellValue(sheet.getRow(spot4.getRow() + 1).getCell(spot4.getColumn() + 1)));
  }

  @Test
  public void setPlate_DifferentSize() {
    presenter.init(view);
    Plate plate = new Plate();
    plate.setRowCount(13);
    plate.setColumnCount(15);
    plate.initSpots();

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
    plate.initSpots();

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
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 1);
    List<Well> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);
    presenter.setSelectedSpots(spots);
    Collection<Well> selectedSpots = presenter.getSelectedSpots();
    assertEquals(1, selectedSpots.size());
    assertTrue(selectedSpots.contains(spot1));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
        .findAny().isPresent());
    assertFalse(view.spreadsheet.isFunctionBarVisible());
    assertFalse(view.spreadsheet.isSheetSelectionBarVisible());
    assertFalse(view.spreadsheet.isRowColHeadingsVisible());
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
    Well spot1 = plate.spot(0, 0);
    Well spot2 = plate.spot(1, 1);
    List<Well> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);
    presenter.setSelectedSpots(spots);
    Collection<Well> selectedSpots = presenter.getSelectedSpots();
    assertEquals(1, selectedSpots.size());
    assertTrue(selectedSpots.contains(spot1));
    Set<CellReference> references = view.spreadsheet.getSelectedCellReferences();
    assertEquals(1, references.size());
    assertTrue(references.stream()
        .filter(ref -> ref.getRow() - 1 == spot1.getRow() && ref.getCol() - 1 == spot1.getColumn())
        .findAny().isPresent());
    assertFalse(view.spreadsheet.isFunctionBarVisible());
    assertFalse(view.spreadsheet.isSheetSelectionBarVisible());
    assertFalse(view.spreadsheet.isRowColHeadingsVisible());
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
    assertNotNull(presenter.getPlate().spot(0, 0).getSample());
    assertTrue(presenter.getPlate().spot(0, 0).getSample() instanceof SubmissionSample);
    assertEquals("test 1", presenter.getPlate().spot(0, 0).getSample().getName());
    assertNotNull(presenter.getPlate().spot(0, 1).getSample());
    assertTrue(presenter.getPlate().spot(0, 1).getSample() instanceof SubmissionSample);
    assertEquals("test 2", presenter.getPlate().spot(0, 1).getSample().getName());
  }
}
