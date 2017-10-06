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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class WellServiceTest {
  private WellService wellService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    wellService = new WellService(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get() throws Exception {
    Well well = wellService.get(129L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 129L, well.getId());
    assertEquals((Long) 26L, well.getPlate().getId());
    assertEquals((Long) 1L, well.getSample().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 15, 7, 34).atZone(ZoneId.systemDefault()).toInstant(),
        well.getTimestamp());
    assertEquals(false, well.isBanned());
    assertEquals(0, well.getRow());
    assertEquals(1, well.getColumn());
  }

  @Test
  public void get_Null() throws Exception {
    Well well = wellService.get(null);

    assertNull(well);
  }

  @Test
  public void get_Location() throws Exception {
    Plate plate = new Plate(26L);
    WellLocation location = new WellLocation(2, 3);

    Well well = wellService.get(plate, location);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 155L, well.getId());
    assertEquals((Long) 26L, well.getPlate().getId());
    assertEquals(null, well.getSample());
    assertEquals(
        LocalDateTime.of(2011, 11, 8, 13, 33, 21).atZone(ZoneId.systemDefault()).toInstant(),
        well.getTimestamp());
    assertEquals(false, well.isBanned());
    assertEquals(2, well.getRow());
    assertEquals(3, well.getColumn());
  }

  @Test
  public void get_LocationNullPlate() throws Exception {
    WellLocation location = new WellLocation(2, 3);

    Well well = wellService.get(null, location);

    assertNull(well);
  }

  @Test
  public void get_LocationNullLocation() throws Exception {
    Plate plate = new Plate(26L);

    Well well = wellService.get(plate, null);

    assertNull(well);
  }

  @Test
  public void last() throws Exception {
    Sample sample = new SubmissionSample(1L);

    Well well = wellService.last(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 129L, well.getId());
  }

  @Test
  public void last_Null() throws Exception {
    Well well = wellService.last(null);

    assertNull(well);
  }

  @Test
  public void all_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<Well> wells = wellService.all(plate);

    verify(authorizationService).checkAdminRole();
    assertEquals(96, wells.size());
    for (long i = 128; i <= 223; i++) {
      assertTrue(find(wells, i).isPresent());
    }
  }

  @Test
  public void all_NullPlate() throws Exception {
    List<Well> wells = wellService.all((Plate) null);

    assertEquals(0, wells.size());
  }

  @Test
  public void all_Sample() throws Exception {
    Sample sample = new SubmissionSample(629L);

    List<Well> wells = wellService.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(3, wells.size());
    assertTrue(find(wells, 1474).isPresent());
    assertTrue(find(wells, 1568).isPresent());
    assertTrue(find(wells, 1580).isPresent());
  }

  @Test
  public void all_NullSample() throws Exception {
    List<Well> wells = wellService.all((Sample) null);

    assertEquals(0, wells.size());
  }

  @Test
  public void location() throws Exception {
    Plate plate = new Plate(108L);
    Sample sample = new SubmissionSample(564L);

    List<Well> wells = wellService.location(sample, plate);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, wells.size());
    assertTrue(find(wells, 322L).isPresent());
    assertTrue(find(wells, 334L).isPresent());
  }

  @Test
  public void location_NullSample() throws Exception {
    Plate plate = new Plate(108L);

    List<Well> wells = wellService.location(null, plate);

    assertEquals(0, wells.size());
  }

  @Test
  public void location_NullPlate() throws Exception {
    Sample sample = new SubmissionSample(564L);

    List<Well> wells = wellService.location(sample, null);

    assertEquals(0, wells.size());
  }
}
