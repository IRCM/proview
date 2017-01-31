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

package ca.qc.ircm.proview.digestion;

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
public class DigestionProtocolServiceTest {
  private DigestionProtocolService digestionProtocolService;
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
  private ArgumentCaptor<DigestionProtocol> protocolCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    digestionProtocolService = new DigestionProtocolService(entityManager, queryFactory,
        protocolActivityService, activityService, authorizationService);
  }

  @Test
  public void get() throws Throwable {
    DigestionProtocol protocol = digestionProtocolService.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, protocol.getId());
    assertEquals("digestion_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
  }

  @Test
  public void get_Null() throws Throwable {
    DigestionProtocol protocol = digestionProtocolService.get(null);

    assertNull(protocol);
  }

  @Test
  public void all() throws Throwable {
    List<DigestionProtocol> protocols = digestionProtocolService.all();

    verify(authorizationService).checkAdminRole();
    assertEquals(1, protocols.size());
    assertEquals(true, protocols.contains(digestionProtocolService.get(1L)));
  }

  @Test
  public void availableName_True() throws Throwable {
    boolean availableName = digestionProtocolService.availableName("digestion_protocol_1");

    verify(authorizationService).checkAdminRole();
    assertEquals(false, availableName);
  }

  @Test
  public void availableName_False() throws Throwable {
    boolean availableName = digestionProtocolService.availableName("some_random_name");

    verify(authorizationService).checkAdminRole();
    assertEquals(true, availableName);
  }

  @Test
  public void availableName_Null() throws Throwable {
    boolean availableName = digestionProtocolService.availableName(null);

    assertEquals(false, availableName);
  }

  @Test
  public void insert() throws Throwable {
    DigestionProtocol protocol = new DigestionProtocol();
    protocol.setName("unit_test_digestion_protocol");
    when(protocolActivityService.insert(any(Protocol.class))).thenReturn(activity);

    digestionProtocolService.insert(protocol);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    assertNotNull(protocol.getId());
    protocol = digestionProtocolService.get(protocol.getId());
    assertEquals("unit_test_digestion_protocol", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
    verify(protocolActivityService).insert(protocol);
    verify(activityService).insert(activity);
  }
}
