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

import static ca.qc.ircm.proview.treatment.Protocol.Type.DIGESTION;
import static ca.qc.ircm.proview.treatment.Protocol.Type.ENRICHMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ProtocolServiceTest {
  private ProtocolService protocolService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private ProtocolActivityService protocolActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;

  @Before
  public void beforeTest() {
    protocolService = new ProtocolService(entityManager, queryFactory, protocolActivityService,
        activityService, authorizationService);
  }

  @Test
  public void get_DigestionProtocol() throws Throwable {
    Protocol protocol = protocolService.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, protocol.getId());
    assertEquals("digestion_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
  }

  @Test
  public void get_EnrichmentProtocol() throws Throwable {
    Protocol protocol = protocolService.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, protocol.getId());
    assertEquals("enrichment_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
  }

  @Test
  public void get_Null() throws Throwable {
    Protocol protocol = protocolService.get(null);

    assertNull(protocol);
  }

  @Test
  public void all_Digestion() throws Throwable {
    List<Protocol> protocols = protocolService.all(DIGESTION);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, protocols.size());
    assertEquals(true, protocols.contains(protocolService.get(1L)));
    assertEquals(true, protocols.contains(protocolService.get(3L)));
  }

  @Test
  public void all_Enrichment() throws Throwable {
    List<Protocol> protocols = protocolService.all(ENRICHMENT);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, protocols.size());
    assertEquals(true, protocols.contains(protocolService.get(2L)));
    assertEquals(true, protocols.contains(protocolService.get(4L)));
  }

  @Test
  public void insert() throws Throwable {
    Protocol protocol = new Protocol();
    protocol.setName("unit_test_protocol");
    protocol.setType(DIGESTION);
    when(protocolActivityService.insert(any(Protocol.class))).thenReturn(activity);

    protocolService.insert(protocol);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    assertNotNull(protocol.getId());
    protocol = protocolService.get(protocol.getId());
    assertEquals("unit_test_protocol", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
    verify(protocolActivityService).insert(protocol);
    verify(activityService).insert(activity);
  }
}
