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

package ca.qc.ircm.proview.enrichment;

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
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.ProtocolActivityService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class EnrichmentProtocolServiceTest {
  private EnrichmentProtocolService enrichmentProtocolService;
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
  @Captor
  private ArgumentCaptor<EnrichmentProtocol> protocolCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    enrichmentProtocolService = new EnrichmentProtocolService(entityManager,
        queryFactory, protocolActivityService, activityService, authorizationService);
  }

  @Test
  public void get() throws Throwable {
    EnrichmentProtocol protocol = enrichmentProtocolService.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, protocol.getId());
    assertEquals("enrichment_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
  }

  @Test
  public void get_Null() throws Throwable {
    EnrichmentProtocol protocol = enrichmentProtocolService.get(null);

    assertNull(protocol);
  }

  @Test
  public void all() throws Throwable {
    List<EnrichmentProtocol> protocols = enrichmentProtocolService.all();

    verify(authorizationService).checkAdminRole();
    assertEquals(1, protocols.size());
    assertEquals(true, protocols.contains(enrichmentProtocolService.get(2L)));
  }

  @Test
  public void availableNameSecurity() throws Throwable {
    enrichmentProtocolService.availableName("enrichment_protocol_1");
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void availableName_False() throws Throwable {
    boolean available = enrichmentProtocolService.availableName("enrichment_protocol_1");

    verify(authorizationService).checkAdminRole();
    assertEquals(false, available);

    assertEquals(true, enrichmentProtocolService.availableName("some_random_name"));
  }

  @Test
  public void availableName_True() throws Throwable {
    boolean available = enrichmentProtocolService.availableName("some_random_name");

    verify(authorizationService).checkAdminRole();
    assertEquals(true, available);
  }

  @Test
  public void availableName_Null() throws Throwable {
    boolean available = enrichmentProtocolService.availableName(null);

    assertEquals(false, available);
  }

  @Test
  public void insert() throws Throwable {
    EnrichmentProtocol protocol = new EnrichmentProtocol();
    protocol.setName("unit_test_enrichment_protocol");
    when(protocolActivityService.insert(any(Protocol.class))).thenReturn(activity);

    enrichmentProtocolService.insert(protocol);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    assertNotNull(protocol.getId());
    protocol = enrichmentProtocolService.get(protocol.getId());
    assertEquals("unit_test_enrichment_protocol", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
    verify(protocolActivityService).insert(protocol);
    verify(activityService).insert(activity);
  }
}
