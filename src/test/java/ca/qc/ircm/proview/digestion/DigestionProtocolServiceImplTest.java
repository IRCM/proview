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
public class DigestionProtocolServiceImplTest {
  private DigestionProtocolServiceImpl digestionProtocolServiceImpl;
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
    digestionProtocolServiceImpl = new DigestionProtocolServiceImpl(entityManager, queryFactory,
        protocolActivityService, activityService, authorizationService);
  }

  @Test
  public void get() throws Throwable {
    DigestionProtocol protocol = digestionProtocolServiceImpl.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, protocol.getId());
    assertEquals("digestion_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
  }

  @Test
  public void get_Null() throws Throwable {
    DigestionProtocol protocol = digestionProtocolServiceImpl.get(null);

    assertNull(protocol);
  }

  @Test
  public void all() throws Throwable {
    List<DigestionProtocol> protocols = digestionProtocolServiceImpl.all();

    verify(authorizationService).checkAdminRole();
    assertEquals(1, protocols.size());
    assertEquals(true, protocols.contains(digestionProtocolServiceImpl.get(1L)));
  }

  @Test
  public void availableName_True() throws Throwable {
    boolean availableName = digestionProtocolServiceImpl.availableName("digestion_protocol_1");

    verify(authorizationService).checkAdminRole();
    assertEquals(false, availableName);
  }

  @Test
  public void availableName_False() throws Throwable {
    boolean availableName = digestionProtocolServiceImpl.availableName("some_random_name");

    verify(authorizationService).checkAdminRole();
    assertEquals(true, availableName);
  }

  @Test
  public void availableName_Null() throws Throwable {
    boolean availableName = digestionProtocolServiceImpl.availableName(null);

    assertEquals(false, availableName);
  }

  @Test
  public void insert() throws Throwable {
    DigestionProtocol protocol = new DigestionProtocol();
    protocol.setName("unit_test_digestion_protocol");
    when(protocolActivityService.insert(any(Protocol.class))).thenReturn(activity);

    digestionProtocolServiceImpl.insert(protocol);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    assertNotNull(protocol.getId());
    protocol = digestionProtocolServiceImpl.get(protocol.getId());
    assertEquals("unit_test_digestion_protocol", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
    verify(protocolActivityService).insert(protocol);
    verify(activityService).insert(activity);
  }
}
