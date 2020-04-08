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

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserRole;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class WellServiceTest {
  @Autowired
  private WellService service;

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void get() throws Exception {
    Well well = service.get(129L);

    assertEquals((Long) 129L, well.getId());
    assertEquals((Long) 26L, well.getPlate().getId());
    assertEquals((Long) 1L, well.getSample().getId());
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 7, 34), well.getTimestamp());
    assertEquals(false, well.isBanned());
    assertEquals(0, well.getRow());
    assertEquals(1, well.getColumn());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void get_Null() throws Exception {
    Well well = service.get(null);

    assertNull(well);
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void get_AccessDenied_Anonymous() throws Exception {
    service.get(129L);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void get_AccessDenied() throws Exception {
    service.get(129L);
  }
}
