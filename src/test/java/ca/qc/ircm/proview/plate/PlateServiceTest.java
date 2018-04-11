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

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.plate.PlateService.PLATE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateServiceTest {
  private PlateService plateService;
  @Inject
  private TemplateEngine emailTemplateEngine;
  @Mock
  private PlateActivityService plateActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Collection<Well>> wellsCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    plateService = new PlateService(entityManager, queryFactory, plateActivityService,
        activityService, authorizationService, emailTemplateEngine, applicationConfiguration);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get() throws Exception {
    Plate plate = plateService.get(26L);

    verify(authorizationService).checkPlateReadPermission(plate);
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertFalse(plate.isSubmission());
    final List<Well> wells = plate.getWells();
    assertEquals(96, wells.size());
    final int rowCount = plate.getRowCount();
    List<Well> someWells = plate.wells(new WellLocation(0, 1), new WellLocation(rowCount, 1));
    assertEquals(plate.getRowCount(), someWells.size());
    for (Well testWell : someWells) {
      assertEquals(1, testWell.getColumn());
    }
    Well well = plate.well(2, 3);
    assertEquals(2, well.getRow());
    assertEquals(3, well.getColumn());
    assertEquals(91, plate.getEmptyWellCount());
    assertEquals(2, plate.getSampleCount());
  }

  @Test
  public void get_NullId() throws Exception {
    Plate plate = plateService.get((Long) null);

    assertNull(plate);
  }

  @Test
  public void all() throws Exception {
    PlateFilter filter = new PlateFilter();

    List<Plate> plates = plateService.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(18, plates.size());
  }

  @Test
  public void all_ContainsAnySamples() throws Exception {
    PlateFilter filter = new PlateFilter();
    Sample sample1 = entityManager.find(Sample.class, 629L);
    Sample sample2 = entityManager.find(Sample.class, 444L);
    filter.containsAnySamples = Arrays.asList(sample1, sample2);

    List<Plate> plates = plateService.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(3, plates.size());
    assertTrue(find(plates, 107L).isPresent());
    assertTrue(find(plates, 120L).isPresent());
    assertTrue(find(plates, 121L).isPresent());
  }

  @Test
  public void all_OnlyProteomicPlates() throws Exception {
    PlateFilter filter = new PlateFilter();
    filter.onlyProteomicPlates();

    List<Plate> plates = plateService.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(17, plates.size());
    assertFalse(find(plates, 123L).isPresent());
  }

  @Test
  public void all_Null() throws Exception {
    List<Plate> plates = plateService.all(null);

    verify(authorizationService).checkAdminRole();
    assertEquals(18, plates.size());
  }

  @Test
  public void nameAvailable_ProteomicTrue() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean available = plateService.nameAvailable("unit_test");

    verify(authorizationService).checkUserRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicFalse() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean available = plateService.nameAvailable("A_20111108");

    verify(authorizationService).checkUserRole();
    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionTrue() throws Exception {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = plateService.nameAvailable("unit_test");

    verify(authorizationService).checkUserRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_SubmissionFalse() throws Exception {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = plateService.nameAvailable("Andrew-20171108");

    verify(authorizationService).checkUserRole();
    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_OtherUser() throws Exception {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = plateService.nameAvailable("Andrew-20171108");

    verify(authorizationService).checkUserRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicNull() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean available = plateService.nameAvailable(null);

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionNull() throws Exception {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = plateService.nameAvailable(null);

    assertEquals(false, available);
  }

  @SuppressWarnings("deprecation")
  private String cellValue(Cell cell) {
    return cell.getCellTypeEnum() == CellType.NUMERIC
        ? String.format("%1.0f", cell.getNumericCellValue())
        : cell.getStringCellValue();
  }

  private void workbook(Locale locale) throws Exception {
    Plate plate = new Plate();
    plate.initWells();
    Well well1 = plate.well(0, 0);
    well1.setSample(new SubmissionSample(1L, "test 1"));
    Well well2 = plate.well(0, 1);
    well2.setSample(new Control(1L, "test control 1"));
    well2.setBanned(true);
    Well well3 = plate.well(0, 2);
    well3.setSample(new SubmissionSample(2L, "test control 2"));
    well3.setBanned(true);
    Well well4 = plate.well(1, 0);
    well4.setSample(new SubmissionSample(4L, "test control 4"));
    well4.setBanned(true);
    MessageResource resources = new MessageResource(PlateService.class, locale);

    Workbook workbook = plateService.workbook(plate, locale);

    Sheet sheet = workbook.getSheetAt(0);
    assertTrue(plate.getRowCount() + 1 <= sheet.getLastRowNum());
    Row firstRow = sheet.getRow(0);
    assertTrue(plate.getColumnCount() + 1 <= sheet.getLastRowNum());
    assertEquals(resources.message(PLATE), cellValue(firstRow.getCell(0)));
    for (int column = 1; column < firstRow.getLastCellNum(); column++) {
      assertEquals(Plate.columnLabel(column - 1), cellValue(firstRow.getCell(column)));
    }
    for (int row = 1; row < sheet.getLastRowNum(); row++) {
      assertTrue(plate.getColumnCount() + 1 <= sheet.getLastRowNum());
      Row sheetRow = sheet.getRow(row);
      assertEquals(Plate.rowLabel(row - 1), cellValue(sheetRow.getCell(0)));
    }
    for (int row = 0; row < plate.getRowCount(); row++) {
      Row sheetRow = sheet.getRow(row + 1);
      for (int column = 0; column < plate.getColumnCount(); column++) {
        Well well = plate.well(row, column);
        Sample sample = well.getSample();
        Cell cell = sheetRow.getCell(column + 1);
        assertEquals(sample != null ? sample.getName() : "", cellValue(cell));
        CellStyle style = cell.getCellStyle();
        assertEquals(well.isBanned() ? HSSFColor.RED.index : HSSFColor.WHITE.index,
            style.getFillBackgroundColor());
        assertEquals(well.isBanned() ? HSSFColor.WHITE.index : HSSFColor.BLACK.index,
            workbook.getFontAt(style.getFontIndex()).getColor());
      }
    }
    assertEquals(well1.getSample().getName(),
        cellValue(sheet.getRow(well1.getRow() + 1).getCell(well1.getColumn() + 1)));
    CellStyle style =
        sheet.getRow(well1.getRow() + 1).getCell(well1.getColumn() + 1).getCellStyle();
    assertEquals(HSSFColor.WHITE.index, style.getFillBackgroundColor());
    assertEquals(HSSFColor.BLACK.index, workbook.getFontAt(style.getFontIndex()).getColor());
    assertEquals(well2.getSample().getName(),
        cellValue(sheet.getRow(well2.getRow() + 1).getCell(well2.getColumn() + 1)));
    style = sheet.getRow(well2.getRow() + 1).getCell(well2.getColumn() + 1).getCellStyle();
    assertEquals(HSSFColor.RED.index, style.getFillBackgroundColor());
    assertEquals(HSSFColor.WHITE.index, workbook.getFontAt(style.getFontIndex()).getColor());
  }

  @Test
  public void workbook() throws Exception {
    workbook(Locale.CANADA);
  }

  @Test
  public void workbook_French() throws Exception {
    workbook(Locale.CANADA_FRENCH);
  }

  @Test
  public void workbook_NullPlate() throws Exception {
    Locale locale = Locale.CANADA;
    MessageResource resources = new MessageResource(PlateService.class, locale);

    Workbook workbook = plateService.workbook(null, locale);

    Plate plate = new Plate();
    Sheet sheet = workbook.getSheetAt(0);
    assertTrue(plate.getRowCount() + 1 <= sheet.getLastRowNum());
    Row firstRow = sheet.getRow(0);
    assertTrue(plate.getColumnCount() + 1 <= sheet.getLastRowNum());
    assertEquals(resources.message(PLATE), cellValue(firstRow.getCell(0)));
    for (int column = 1; column < firstRow.getLastCellNum(); column++) {
      assertEquals(Plate.columnLabel(column - 1), cellValue(firstRow.getCell(column)));
    }
    for (int row = 1; row < sheet.getLastRowNum(); row++) {
      assertTrue(plate.getColumnCount() + 1 <= sheet.getLastRowNum());
      Row sheetRow = sheet.getRow(row);
      assertEquals(Plate.rowLabel(row - 1), cellValue(sheetRow.getCell(0)));
    }
    for (int row = 1; row < sheet.getLastRowNum(); row++) {
      Row sheetRow = sheet.getRow(row);
      for (int column = 1; column < sheetRow.getLastCellNum(); column++) {
        Cell cell = sheetRow.getCell(column);
        assertEquals("", cellValue(cell));
        CellStyle style = cell.getCellStyle();
        assertEquals(HSSFColor.WHITE.index, style.getFillBackgroundColor());
        assertEquals(HSSFColor.BLACK.index, workbook.getFontAt(style.getFontIndex()).getColor());
      }
    }
  }

  @Test
  public void workbook_NullLocale() throws Exception {
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
    Locale locale = Locale.CANADA;
    MessageResource resources = new MessageResource(PlateService.class, locale);

    Workbook workbook = plateService.workbook(plate, null);

    Sheet sheet = workbook.getSheetAt(0);
    assertTrue(plate.getRowCount() + 1 <= sheet.getLastRowNum());
    Row firstRow = sheet.getRow(0);
    assertTrue(plate.getColumnCount() + 1 <= sheet.getLastRowNum());
    assertEquals(resources.message(PLATE), cellValue(firstRow.getCell(0)));
    for (int column = 1; column < firstRow.getLastCellNum(); column++) {
      assertEquals(Plate.columnLabel(column - 1), cellValue(firstRow.getCell(column)));
    }
    for (int row = 1; row < sheet.getLastRowNum(); row++) {
      assertTrue(plate.getColumnCount() + 1 <= sheet.getLastRowNum());
      Row sheetRow = sheet.getRow(row);
      assertEquals(Plate.rowLabel(row - 1), cellValue(sheetRow.getCell(0)));
    }
    for (int row = 0; row < plate.getRowCount(); row++) {
      Row sheetRow = sheet.getRow(row + 1);
      for (int column = 0; column < plate.getColumnCount(); column++) {
        Well well = plate.well(row, column);
        Sample sample = well.getSample();
        Cell cell = sheetRow.getCell(column + 1);
        assertEquals(sample != null ? sample.getName() : "", cellValue(cell));
        CellStyle style = cell.getCellStyle();
        assertEquals(HSSFColor.WHITE.index, style.getFillBackgroundColor());
        assertEquals(HSSFColor.BLACK.index, workbook.getFontAt(style.getFontIndex()).getColor());
      }
    }
  }

  private Plate plateForPrint() {
    Plate plate = new Plate();
    plate.setName("my plate");
    plate.initWells();
    plate.getWells().stream().limit(20).forEach(well -> {
      SubmissionSample sample = new SubmissionSample();
      sample.setName("s_" + well.getName());
      well.setSample(sample);
    });
    plate.getWells().stream().skip(18).limit(2).forEach(well -> well.setBanned(true));
    plate.getWells().stream().skip(20).limit(2).forEach(well -> {
      Control control = new Control();
      control.setName("c_" + well.getName());
      well.setSample(control);
    });
    return plate;
  }

  @Test
  public void print() throws Exception {
    Plate plate = plateForPrint();
    Locale locale = Locale.getDefault();

    String content = plateService.print(plate, locale);

    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"plate-information section"));
    assertTrue(content.contains("class=\"plate-name\""));
    assertTrue(content.contains(plate.getName()));
    assertTrue(content.contains("class=\"well active\""));
    assertTrue(content.contains("class=\"well banned\""));
    assertTrue(content.contains("class=\"well-sample-name\""));
    for (Well well : plate.getWells()) {
      if (well.getSample() != null) {
        assertTrue(content.contains(well.getSample().getName()));
      }
    }
  }

  @Test
  public void insert() throws Exception {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    plateService.insert(plate);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).insert(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = plateService.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test
  public void update() throws Exception {
    Plate plate = entityManager.find(Plate.class, 26L);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    plateService.update(plate);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).update(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = plateService.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test
  public void ban_OneWell() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    WellLocation location = new WellLocation(0, 0);
    when(plateActivityService.ban(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    plateService.ban(plate, location, location, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).ban(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = entityManager.find(Well.class, 128L);
    assertEquals(true, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(1, loggedWells.size());
    assertTrue(find(loggedWells, 128L).isPresent());
  }

  @Test
  public void ban_MultipleWells() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    plateService.ban(plate, from, to, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).ban(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    List<Well> bannedWells = plateService.get(plate.getId()).wells(from, to);
    for (Well bannedWell : bannedWells) {
      Well well = entityManager.find(Well.class, bannedWell.getId());
      assertEquals(true, well.isBanned());
    }
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(bannedWells, loggedWells);
  }

  @Test
  public void activate_OneWell() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    plateService.activate(plate, location, location, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).activate(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = entityManager.find(Well.class, 211L);
    assertEquals(false, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(1, loggedWells.size());
    assertTrue(find(loggedWells, 211L).isPresent());
  }

  @Test
  public void activate_MultipleWells() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    WellLocation from = new WellLocation(5, 11);
    WellLocation to = new WellLocation(7, 11);
    when(plateActivityService.activate(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    plateService.activate(plate, from, to, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).activate(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = entityManager.find(Well.class, 199L);
    assertEquals(false, well.isBanned());
    well = entityManager.find(Well.class, 211L);
    assertEquals(false, well.isBanned());
    well = entityManager.find(Well.class, 223L);
    assertEquals(false, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(3, loggedWells.size());
    assertTrue(find(loggedWells, 199L).isPresent());
    assertTrue(find(loggedWells, 211L).isPresent());
    assertTrue(find(loggedWells, 223L).isPresent());
  }
}
