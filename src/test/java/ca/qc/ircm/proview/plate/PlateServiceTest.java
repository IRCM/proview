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
import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.time.TimeConverter.toInstant;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.inject.Inject;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateServiceTest extends AbstractServiceTestCase {
  @Inject
  private PlateService service;
  @Inject
  private PlateRepository repository;
  @Inject
  private WellRepository wellRepository;
  @Inject
  private SampleRepository sampleRepository;
  @MockBean
  private PlateActivityService plateActivityService;
  @MockBean
  private ActivityService activityService;
  @MockBean
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Collection<Well>> wellsCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get() throws Exception {
    Plate plate = service.get(26L);

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
    Plate plate = service.get((Long) null);

    assertNull(plate);
  }

  @Test
  public void all_Filter() throws Throwable {
    PlateFilter filter = mock(PlateFilter.class);
    when(filter.predicate()).thenReturn(plate.isNotNull());

    List<Plate> plates = service.all(filter);

    verify(authorizationService).checkAdminRole();
    verify(filter).predicate();
    assertEquals(18, plates.size());
  }

  @Test
  public void all_ContainsAnySamples() throws Exception {
    PlateFilter filter = new PlateFilter();
    Sample sample1 = sampleRepository.findOne(629L);
    Sample sample2 = sampleRepository.findOne(444L);
    filter.containsAnySamples = Arrays.asList(sample1, sample2);

    List<Plate> plates = service.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(3, plates.size());
    assertTrue(find(plates, 107L).isPresent());
    assertTrue(find(plates, 120L).isPresent());
    assertTrue(find(plates, 121L).isPresent());
  }

  @Test
  public void all_SubmissionFalse() throws Exception {
    PlateFilter filter = new PlateFilter();
    filter.submission = false;

    List<Plate> plates = service.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(17, plates.size());
    assertFalse(find(plates, 123L).isPresent());
  }

  @Test
  public void all_SubmissionTrue() throws Exception {
    PlateFilter filter = new PlateFilter();
    filter.submission = true;

    List<Plate> plates = service.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, plates.size());
    assertTrue(find(plates, 123L).isPresent());
  }

  @Test
  public void all_MinimumEmptyCount() throws Exception {
    PlateFilter filter = new PlateFilter();
    filter.minimumEmptyCount = 94;

    List<Plate> plates = service.all(filter);

    verify(authorizationService).checkAdminRole();
    assertEquals(3, plates.size());
    assertTrue(find(plates, 26L).isPresent());
    assertTrue(find(plates, 111L).isPresent());
    assertTrue(find(plates, 122L).isPresent());
  }

  @Test
  public void all_Null() throws Exception {
    List<Plate> plates = service.all(null);

    verify(authorizationService).checkAdminRole();
    assertEquals(18, plates.size());
  }

  @Test
  public void nameAvailable_ProteomicTrue() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean available = service.nameAvailable("unit_test");

    verify(authorizationService).checkUserRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicFalse() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean available = service.nameAvailable("A_20111108");

    verify(authorizationService).checkUserRole();
    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionTrue() throws Exception {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = service.nameAvailable("unit_test");

    verify(authorizationService).checkUserRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_SubmissionFalse() throws Exception {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = service.nameAvailable("Andrew-20171108");

    verify(authorizationService).checkUserRole();
    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_OtherUser() throws Exception {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = service.nameAvailable("Andrew-20171108");

    verify(authorizationService).checkUserRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicNull() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean available = service.nameAvailable(null);

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionNull() throws Exception {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean available = service.nameAvailable(null);

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

    Workbook workbook = service.workbook(plate, locale);

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

    Workbook workbook = service.workbook(null, locale);

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

    Workbook workbook = service.workbook(plate, null);

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

    String content = service.print(plate, locale);

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
  public void lastTreatmentOrAnalysisDate() {
    assertEquals(toInstant(LocalDateTime.of(2011, 11, 16, 15, 7, 34)),
        service.lastTreatmentOrAnalysisDate(repository.findOne(26L)));
    assertEquals(toInstant(LocalDateTime.of(2014, 10, 15, 15, 53, 34)),
        service.lastTreatmentOrAnalysisDate(repository.findOne(115L)));
    assertEquals(toInstant(LocalDateTime.of(2014, 10, 17, 11, 54, 22)),
        service.lastTreatmentOrAnalysisDate(repository.findOne(118L)));
    assertEquals(toInstant(LocalDateTime.of(2014, 10, 22, 9, 57, 18)),
        service.lastTreatmentOrAnalysisDate(repository.findOne(121L)));
    assertNull(service.lastTreatmentOrAnalysisDate(repository.findOne(122L)));
    assertNull(service.lastTreatmentOrAnalysisDate(repository.findOne(123L)));
  }

  @Test
  public void lastTreatmentOrAnalysisDate_Null() {
    assertNull(service.lastTreatmentOrAnalysisDate(null));
  }

  @Test
  public void insert() throws Exception {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    service.insert(plate);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).insert(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = service.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test
  public void update() throws Exception {
    Plate plate = repository.findOne(26L);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    service.update(plate);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).update(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = service.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test
  public void ban_OneWell() {
    Plate plate = repository.findOne(26L);
    detach(plate);
    WellLocation location = new WellLocation(0, 0);
    when(plateActivityService.ban(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    service.ban(plate, location, location, "unit test");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).ban(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = wellRepository.findOne(128L);
    assertEquals(true, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(1, loggedWells.size());
    assertTrue(find(loggedWells, 128L).isPresent());
  }

  @Test
  public void ban_MultipleWells() {
    Plate plate = repository.findOne(26L);
    detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    service.ban(plate, from, to, "unit test");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).ban(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    List<Well> bannedWells = service.get(plate.getId()).wells(from, to);
    for (Well bannedWell : bannedWells) {
      Well well = wellRepository.findOne(bannedWell.getId());
      assertEquals(true, well.isBanned());
    }
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(bannedWells, loggedWells);
  }

  @Test
  public void activate_OneWell() {
    Plate plate = repository.findOne(26L);
    detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    service.activate(plate, location, location, "unit test");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).activate(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = wellRepository.findOne(211L);
    assertEquals(false, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(1, loggedWells.size());
    assertTrue(find(loggedWells, 211L).isPresent());
  }

  @Test
  public void activate_MultipleWells() {
    Plate plate = repository.findOne(26L);
    detach(plate);
    WellLocation from = new WellLocation(5, 11);
    WellLocation to = new WellLocation(7, 11);
    when(plateActivityService.activate(anyCollectionOf(Well.class), any(String.class)))
        .thenReturn(activity);

    service.activate(plate, from, to, "unit test");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).activate(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = wellRepository.findOne(199L);
    assertEquals(false, well.isBanned());
    well = wellRepository.findOne(211L);
    assertEquals(false, well.isBanned());
    well = wellRepository.findOne(223L);
    assertEquals(false, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(3, loggedWells.size());
    assertTrue(find(loggedWells, 199L).isPresent());
    assertTrue(find(loggedWells, 211L).isPresent());
    assertTrue(find(loggedWells, 223L).isPresent());
  }
}
