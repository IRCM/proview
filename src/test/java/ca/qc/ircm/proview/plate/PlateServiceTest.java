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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Data;
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
  private ArgumentCaptor<Collection<PlateSpot>> spotsCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    plateService = new PlateService(entityManager, queryFactory, plateActivityService,
        activityService, authorizationService);
  }

  private <D extends Data> D find(Collection<D> datas, long id) {
    for (D data : datas) {
      if (data.getId() == id) {
        return data;
      }
    }
    return null;
  }

  @Test
  public void get() throws Exception {
    Plate plate = plateService.get(26L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertEquals(PlateType.A, plate.getType());
    assertEquals((Long) 26L, plate.getId());
    final List<PlateSpot> spots = plate.getSpots();
    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertEquals(PlateType.A, plate.getType());
    assertEquals(96, spots.size());
    final int rowCount = plate.getType().getRowCount();
    List<PlateSpot> someSpots = plate.spots(new SpotLocation(0, 1), new SpotLocation(rowCount, 1));
    assertEquals(plate.getType().getRowCount(), someSpots.size());
    for (PlateSpot testSpot : someSpots) {
      assertEquals(1, testSpot.getColumn());
    }
    PlateSpot spot = plate.spot(2, 3);
    assertEquals(2, spot.getRow());
    assertEquals(3, spot.getColumn());
    assertEquals(91, plate.getEmptySpotCount());
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

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertEquals(PlateType.A, plate.getType());
    assertEquals((Long) 26L, plate.getId());
    final List<PlateSpot> spots = plate.getSpots();
    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertEquals(PlateType.A, plate.getType());
    assertEquals(96, spots.size());
    final int rowCount = plate.getType().getRowCount();
    List<PlateSpot> someSpots = plate.spots(new SpotLocation(0, 1), new SpotLocation(rowCount, 1));
    assertEquals(plate.getType().getRowCount(), someSpots.size());
    for (PlateSpot testSpot : someSpots) {
      assertEquals(1, testSpot.getColumn());
    }
    PlateSpot spot = plate.spot(2, 3);
    assertEquals(2, spot.getRow());
    assertEquals(3, spot.getColumn());
    assertEquals(91, plate.getEmptySpotCount());
    assertEquals(2, plate.getSampleCount());
  }

  @Test
  public void get_NullName() throws Exception {
    Plate plate = plateService.get((String) null);

    assertNull(plate);
  }

  @Test
  public void all() throws Exception {
    PlateFilterBuilder filterBuilder = new PlateFilterBuilder();

    List<Plate> plates = plateService.all(filterBuilder.build());

    verify(authorizationService).checkAdminRole();
    assertEquals(17, plates.size());
  }

  @Test
  public void all_Type() throws Exception {
    PlateFilterBuilder filterBuilder = new PlateFilterBuilder();
    filterBuilder.type(PlateType.A);

    List<Plate> plates = plateService.all(filterBuilder.build());

    verify(authorizationService).checkAdminRole();
    assertEquals(15, plates.size());
    assertNotNull(find(plates, 26L));
    assertNotNull(find(plates, 122L));
    assertNull(find(plates, 107L));
    assertNull(find(plates, 111L));
  }

  @Test
  public void all_ContainsAnySamples() throws Exception {
    PlateFilterBuilder filterBuilder = new PlateFilterBuilder();
    Sample sample1 = entityManager.find(Sample.class, 629L);
    Sample sample2 = entityManager.find(Sample.class, 444L);
    filterBuilder.containsAnySamples(sample1, sample2);

    List<Plate> plates = plateService.all(filterBuilder.build());

    verify(authorizationService).checkAdminRole();
    assertEquals(3, plates.size());
    assertNotNull(find(plates, 107L));
    assertNotNull(find(plates, 120L));
    assertNotNull(find(plates, 121L));
  }

  @Test
  public void all_Null() throws Exception {
    List<Plate> plates = plateService.all(null);

    verify(authorizationService).checkAdminRole();
    assertEquals(17, plates.size());
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
    plate.setType(PlateType.A);
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    plateService.insert(plate);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).insert(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = plateService.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
    assertEquals(PlateType.A, plate.getType());
  }

  @Test
  public void ban_OneSpot() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    SpotLocation location = new SpotLocation(0, 0);
    when(plateActivityService.ban(anyCollectionOf(PlateSpot.class), any(String.class)))
        .thenReturn(activity);

    plateService.ban(plate, location, location, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).ban(spotsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    PlateSpot spot = entityManager.find(PlateSpot.class, 128L);
    assertEquals(true, spot.isBanned());
    Collection<PlateSpot> loggedSpots = spotsCaptor.getValue();
    assertEquals(1, loggedSpots.size());
    assertNotNull(find(loggedSpots, 128L));
  }

  @Test
  public void ban_MultipleSpots() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    SpotLocation from = new SpotLocation(3, 3);
    SpotLocation to = new SpotLocation(5, 4);
    when(plateActivityService.ban(anyCollectionOf(PlateSpot.class), any(String.class)))
        .thenReturn(activity);

    plateService.ban(plate, from, to, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).ban(spotsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    List<PlateSpot> bannedSpots = plateService.get(plate.getId()).spots(from, to);
    for (PlateSpot bannedSpot : bannedSpots) {
      PlateSpot spot = entityManager.find(PlateSpot.class, bannedSpot.getId());
      assertEquals(true, spot.isBanned());
    }
    Collection<PlateSpot> loggedSpots = spotsCaptor.getValue();
    assertEquals(bannedSpots, loggedSpots);
  }

  @Test
  public void activate_OneSpot() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    SpotLocation location = new SpotLocation(6, 11);
    when(plateActivityService.activate(anyCollectionOf(PlateSpot.class), any(String.class)))
        .thenReturn(activity);

    plateService.activate(plate, location, location, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).activate(spotsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    PlateSpot spot = entityManager.find(PlateSpot.class, 211L);
    assertEquals(false, spot.isBanned());
    Collection<PlateSpot> loggedSpots = spotsCaptor.getValue();
    assertEquals(1, loggedSpots.size());
    assertNotNull(find(loggedSpots, 211L));
  }

  @Test
  public void activate_MultipleSpots() {
    Plate plate = entityManager.find(Plate.class, 26L);
    entityManager.detach(plate);
    SpotLocation from = new SpotLocation(5, 11);
    SpotLocation to = new SpotLocation(7, 11);
    when(plateActivityService.activate(anyCollectionOf(PlateSpot.class), any(String.class)))
        .thenReturn(activity);

    plateService.activate(plate, from, to, "unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(plateActivityService).activate(spotsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    PlateSpot spot = entityManager.find(PlateSpot.class, 199L);
    assertEquals(false, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 211L);
    assertEquals(false, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 223L);
    assertEquals(false, spot.isBanned());
    Collection<PlateSpot> loggedSpots = spotsCaptor.getValue();
    assertEquals(3, loggedSpots.size());
    assertNotNull(find(loggedSpots, 199L));
    assertNotNull(find(loggedSpots, 211L));
    assertNotNull(find(loggedSpots, 223L));
  }
}
