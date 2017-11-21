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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateComponentTest {
  private PlateComponent view;
  @Mock
  private PlateComponentPresenter presenter;
  @Mock
  private ValueChangeListener<Plate> valueChangeListener;
  @Captor
  private ArgumentCaptor<ValueChangeEvent<Plate>> valueChangeEventCaptor;
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  private Plate plate;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    view = new PlateComponent(presenter, applicationConfiguration);
    view.init();
    plate = new Plate();
    plate.initWells();
  }

  @Test
  public void init() throws Throwable {
    try (Workbook workbook = new XSSFWorkbook(applicationConfiguration.getPlateTemplate())) {
      Sheet sheet = workbook.getSheetAt(0);
      Sheet viewSheet = view.spreadsheet.getActiveSheet();
      Row firstRow = sheet.getRow(0);
      for (int column = 1; column < firstRow.getLastCellNum(); column++) {
        int value = (int) firstRow.getCell(column).getNumericCellValue();
        assertEquals(String.valueOf(value),
            view.spreadsheet.getCellValue(viewSheet.getRow(0).getCell(column)));
      }
      for (int row = 0; row < sheet.getLastRowNum(); row++) {
        String value = sheet.getRow(row).getCell(0).getStringCellValue();
        assertEquals(value, view.spreadsheet.getCellValue(viewSheet.getRow(row).getCell(0)));
      }
    }
  }

  @Test
  public void isMultiSelect_True() {
    when(presenter.isMultiSelect()).thenReturn(true);

    assertTrue(view.isMultiSelect());

    verify(presenter).isMultiSelect();
  }

  @Test
  public void isMultiSelect_False() {
    when(presenter.isMultiSelect()).thenReturn(false);

    assertFalse(view.isMultiSelect());

    verify(presenter).isMultiSelect();
  }

  @Test
  public void setMultiSelect_True() {
    view.setMultiSelect(true);

    verify(presenter).setMultiSelect(true);
  }

  @Test
  public void setMultiSelect_False() {
    view.setMultiSelect(false);

    verify(presenter).setMultiSelect(false);
  }

  @Test
  public void getSelectedWell() {
    Well well = plate.well(1, 1);
    when(presenter.getSelectedWell()).thenReturn(well);

    assertEquals(well, view.getSelectedWell());

    verify(presenter).getSelectedWell();
  }

  @Test
  public void getSelectedWells() {
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 1);
    when(presenter.getSelectedWells()).thenReturn(Arrays.asList(well1, well2));

    Collection<Well> wells = view.getSelectedWells();

    assertTrue(wells.contains(well1));
    assertTrue(wells.contains(well2));
    verify(presenter).getSelectedWells();
  }

  @Test
  public void setSelectedWells() {
    Well well1 = plate.well(0, 0);
    Well well2 = plate.well(1, 1);
    List<Well> wells = Arrays.asList(well1, well2);

    view.setSelectedWells(wells);

    verify(presenter).setSelectedWells(wells);
  }

  @Test
  public void getValue() {
    when(presenter.getValue()).thenReturn(plate);

    assertEquals(plate, view.getValue());

    verify(presenter).getValue();
  }

  @Test
  public void setValue() {
    view.setValue(plate);

    verify(presenter).setValue(plate);
  }

  @Test
  public void isReadOnly_True() {
    when(presenter.isReadOnly()).thenReturn(true);

    assertTrue(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void isReadOnly_False() {
    when(presenter.isReadOnly()).thenReturn(false);

    assertFalse(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void setReadOnly_True() {
    view.setReadOnly(true);

    verify(presenter).setReadOnly(true);
  }

  @Test
  public void setReadOnly_False() {
    view.setReadOnly(false);

    verify(presenter).setReadOnly(false);
  }
}
