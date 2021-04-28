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

package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.treatment.Protocol.Type.DIGESTION;
import static ca.qc.ircm.proview.treatment.Protocol.Type.ENRICHMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserRole;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@WithMockUser(authorities = UserRole.ADMIN)
public class ProtocolServiceTest {
  @Autowired
  private ProtocolService service;

  @Test
  public void get_DigestionProtocol() throws Throwable {
    Protocol protocol = service.get(1L).get();

    assertEquals((Long) 1L, protocol.getId());
    assertEquals("digestion_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
  }

  @Test
  public void get_EnrichmentProtocol() throws Throwable {
    Protocol protocol = service.get(2L).get();

    assertEquals((Long) 2L, protocol.getId());
    assertEquals("enrichment_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
  }

  @Test
  public void get_Null() throws Throwable {
    assertFalse(service.get(null).isPresent());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void get_AccessDenied_Anonymous() throws Throwable {
    service.get(1L);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void get_AccessDenied() throws Throwable {
    service.get(1L);
  }

  @Test
  public void all_Digestion() throws Throwable {
    List<Protocol> protocols = service.all(DIGESTION);

    assertEquals(2, protocols.size());
    assertTrue(find(protocols, 1L).isPresent());
    assertTrue(find(protocols, 3L).isPresent());
  }

  @Test
  public void all_Enrichment() throws Throwable {
    List<Protocol> protocols = service.all(ENRICHMENT);

    assertEquals(2, protocols.size());
    assertTrue(find(protocols, 2L).isPresent());
    assertTrue(find(protocols, 4L).isPresent());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void all_AccessDenied_Anonymous() throws Throwable {
    service.all(DIGESTION);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void all_AccessDenied() throws Throwable {
    service.all(DIGESTION);
  }
}
