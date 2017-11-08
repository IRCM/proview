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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class WellServiceTest {
  private WellService wellService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    wellService = new WellService(entityManager, authorizationService);
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
}
