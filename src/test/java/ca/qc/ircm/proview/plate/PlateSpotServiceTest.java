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
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateSpotServiceTest {
  private PlateSpotService plateSpotServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    plateSpotServiceImpl = new PlateSpotService(entityManager, queryFactory, authorizationService);
  }

  private PlateSpot find(Collection<PlateSpot> spots, long id) {
    for (PlateSpot spot : spots) {
      if (spot.getId() == id) {
        return spot;
      }
    }
    return null;
  }

  @Test
  public void get() throws Exception {
    PlateSpot spot = plateSpotServiceImpl.get(129L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 129L, spot.getId());
    assertEquals((Long) 26L, spot.getPlate().getId());
    assertEquals((Long) 1L, spot.getSample().getId());
    assertEquals((Long) 9L, spot.getTreatmentSample().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 15, 7, 34).atZone(ZoneId.systemDefault()).toInstant(),
        spot.getTimestamp());
    assertEquals(false, spot.isBanned());
    assertEquals(0, spot.getRow());
    assertEquals(1, spot.getColumn());
  }

  @Test
  public void get_Null() throws Exception {
    PlateSpot spot = plateSpotServiceImpl.get(null);

    assertNull(spot);
  }

  @Test
  public void get_Location() throws Exception {
    Plate plate = new Plate(26L);
    SpotLocation location = new SpotLocation(2, 3);

    PlateSpot spot = plateSpotServiceImpl.get(plate, location);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 155L, spot.getId());
    assertEquals((Long) 26L, spot.getPlate().getId());
    assertEquals(null, spot.getSample());
    assertEquals(null, spot.getTreatmentSample());
    assertEquals(
        LocalDateTime.of(2011, 11, 8, 13, 33, 21).atZone(ZoneId.systemDefault()).toInstant(),
        spot.getTimestamp());
    assertEquals(false, spot.isBanned());
    assertEquals(2, spot.getRow());
    assertEquals(3, spot.getColumn());
  }

  @Test
  public void get_LocationNullPlate() throws Exception {
    SpotLocation location = new SpotLocation(2, 3);

    PlateSpot spot = plateSpotServiceImpl.get(null, location);

    assertNull(spot);
  }

  @Test
  public void get_LocationNullLocation() throws Exception {
    Plate plate = new Plate(26L);

    PlateSpot spot = plateSpotServiceImpl.get(plate, null);

    assertNull(spot);
  }

  @Test
  public void last() throws Exception {
    Sample sample = new SubmissionSample(1L);

    PlateSpot spot = plateSpotServiceImpl.last(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 129L, spot.getId());
  }

  @Test
  public void last_Null() throws Exception {
    PlateSpot spot = plateSpotServiceImpl.last(null);

    assertNull(spot);
  }

  @Test
  public void all_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<PlateSpot> spots = plateSpotServiceImpl.all(plate);

    verify(authorizationService).checkAdminRole();
    assertEquals(96, spots.size());
    for (long i = 128; i <= 223; i++) {
      assertNotNull(find(spots, i));
    }
  }

  @Test
  public void all_NullPlate() throws Exception {
    List<PlateSpot> spots = plateSpotServiceImpl.all((Plate) null);

    assertEquals(0, spots.size());
  }

  @Test
  public void all_Sample() throws Exception {
    Sample sample = new SubmissionSample(629L);

    List<PlateSpot> spots = plateSpotServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(3, spots.size());
    assertNotNull(find(spots, 1474));
    assertNotNull(find(spots, 1568));
    assertNotNull(find(spots, 1580));
  }

  @Test
  public void all_NullSample() throws Exception {
    List<PlateSpot> spots = plateSpotServiceImpl.all((Sample) null);

    assertEquals(0, spots.size());
  }

  @Test
  public void location() throws Exception {
    Plate plate = new Plate(108L);
    Sample sample = new SubmissionSample(564L);

    List<PlateSpot> spots = plateSpotServiceImpl.location(sample, plate);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, spots.size());
    assertNotNull(find(spots, 322L));
    assertNotNull(find(spots, 334L));
  }

  @Test
  public void location_NullSample() throws Exception {
    Plate plate = new Plate(108L);

    List<PlateSpot> spots = plateSpotServiceImpl.location(null, plate);

    assertEquals(0, spots.size());
  }

  @Test
  public void location_NullPlate() throws Exception {
    Sample sample = new SubmissionSample(564L);

    List<PlateSpot> spots = plateSpotServiceImpl.location(sample, null);

    assertEquals(0, spots.size());
  }
}
