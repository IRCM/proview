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
public class EnrichmentProtocolServiceImplTest {
  private EnrichmentProtocolServiceImpl enrichmentProtocolServiceImpl;
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
    enrichmentProtocolServiceImpl = new EnrichmentProtocolServiceImpl(entityManager,
        queryFactory, protocolActivityService, activityService, authorizationService);
  }

  @Test
  public void get() throws Throwable {
    EnrichmentProtocol protocol = enrichmentProtocolServiceImpl.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, protocol.getId());
    assertEquals("enrichment_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
  }

  @Test
  public void get_Null() throws Throwable {
    EnrichmentProtocol protocol = enrichmentProtocolServiceImpl.get(null);

    assertNull(protocol);
  }

  @Test
  public void all() throws Throwable {
    List<EnrichmentProtocol> protocols = enrichmentProtocolServiceImpl.all();

    verify(authorizationService).checkAdminRole();
    assertEquals(1, protocols.size());
    assertEquals(true, protocols.contains(enrichmentProtocolServiceImpl.get(2L)));
  }

  @Test
  public void availableNameSecurity() throws Throwable {
    enrichmentProtocolServiceImpl.availableName("enrichment_protocol_1");
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void availableName_False() throws Throwable {
    boolean available = enrichmentProtocolServiceImpl.availableName("enrichment_protocol_1");

    verify(authorizationService).checkAdminRole();
    assertEquals(false, available);

    assertEquals(true, enrichmentProtocolServiceImpl.availableName("some_random_name"));
  }

  @Test
  public void availableName_True() throws Throwable {
    boolean available = enrichmentProtocolServiceImpl.availableName("some_random_name");

    verify(authorizationService).checkAdminRole();
    assertEquals(true, available);
  }

  @Test
  public void availableName_Null() throws Throwable {
    boolean available = enrichmentProtocolServiceImpl.availableName(null);

    assertEquals(false, available);
  }

  @Test
  public void insert() throws Throwable {
    EnrichmentProtocol protocol = new EnrichmentProtocol();
    protocol.setName("unit_test_enrichment_protocol");
    when(protocolActivityService.insert(any(Protocol.class))).thenReturn(activity);

    enrichmentProtocolServiceImpl.insert(protocol);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    assertNotNull(protocol.getId());
    protocol = enrichmentProtocolServiceImpl.get(protocol.getId());
    assertEquals("unit_test_enrichment_protocol", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
    verify(protocolActivityService).insert(protocol);
    verify(activityService).insert(activity);
  }
}
