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

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateServiceTest {
  private PlateService plateService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
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

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    plateService = new PlateService(entityManager, queryFactory, plateActivityService,
        activityService, authorizationService);
  }

  @Test
  public void get() throws Exception {
    Plate plate = plateService.get(26L);

    verify(authorizationService).checkPlateReadPermission(plate);
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertEquals((Long) 26L, plate.getId());
    final List<Well> wells = plate.getWells();
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
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
  public void get_Name() throws Exception {
    Plate plate = plateService.get("A_20111108");

    verify(authorizationService).checkPlateReadPermission(plate);
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertEquals((Long) 26L, plate.getId());
    final List<Well> wells = plate.getWells();
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
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
  public void get_NullName() throws Exception {
    Plate plate = plateService.get((String) null);

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
  public void all_Null() throws Exception {
    List<Plate> plates = plateService.all(null);

    verify(authorizationService).checkAdminRole();
    assertEquals(18, plates.size());
  }

  @Test
  public void available_True() throws Exception {
    Plate plate = new Plate(26L);

    boolean available = plateService.available(plate);

    verify(authorizationService).checkAdminRole();
    assertEquals(true, available);
  }

  @Test
  public void available_New() throws Exception {
    Plate plate = new Plate(122L);

    boolean available = plateService.available(plate);

    assertEquals(true, available);
  }

  @Test
  public void available_False() throws Exception {
    Plate plate = new Plate(108L);

    boolean available = plateService.available(plate);

    verify(authorizationService).checkAdminRole();
    assertEquals(false, available);
  }

  @Test
  public void available_Null() throws Exception {
    boolean available = plateService.available(null);

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_True() throws Exception {
    boolean available = plateService.nameAvailable("unit_test");

    verify(authorizationService).checkAdminRole();
    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_False() throws Exception {
    boolean available = plateService.nameAvailable("A_20111108");

    verify(authorizationService).checkAdminRole();
    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_Null() throws Exception {
    boolean available = plateService.nameAvailable(null);

    assertEquals(false, available);
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
