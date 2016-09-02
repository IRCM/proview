package ca.qc.ircm.proview.treatment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.digestion.DigestionProtocol;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocol;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ProtocolServiceImplTest {
  private ProtocolServiceImpl protocolServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    protocolServiceImpl = new ProtocolServiceImpl(entityManager, authorizationService);
  }

  @Test
  public void get_DigestionProtocol() throws Throwable {
    Protocol protocol = protocolServiceImpl.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, protocol.getId());
    assertEquals("digestion_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
    assertTrue(protocol instanceof DigestionProtocol);
  }

  @Test
  public void get_EnrichmentProtocol() throws Throwable {
    Protocol protocol = protocolServiceImpl.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, protocol.getId());
    assertEquals("enrichment_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
    assertTrue(protocol instanceof EnrichmentProtocol);
  }

  @Test
  public void get_Null() throws Throwable {
    Protocol protocol = protocolServiceImpl.get(null);

    assertNull(protocol);
  }
}
