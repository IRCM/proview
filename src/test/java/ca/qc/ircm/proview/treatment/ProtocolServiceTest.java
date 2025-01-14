package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.treatment.Protocol.Type.DIGESTION;
import static ca.qc.ircm.proview.treatment.Protocol.Type.ENRICHMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link ProtocolService}.
 */
@ServiceTestAnnotations
@WithMockUser(authorities = UserRole.ADMIN)
public class ProtocolServiceTest {
  @Autowired
  private ProtocolService service;

  @Test
  public void get_DigestionProtocol() throws Throwable {
    Protocol protocol = service.get(1L).orElseThrow();

    assertEquals((Long) 1L, protocol.getId());
    assertEquals("digestion_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.DIGESTION, protocol.getType());
  }

  @Test
  public void get_EnrichmentProtocol() throws Throwable {
    Protocol protocol = service.get(2L).orElseThrow();

    assertEquals((Long) 2L, protocol.getId());
    assertEquals("enrichment_protocol_1", protocol.getName());
    assertEquals(Protocol.Type.ENRICHMENT, protocol.getType());
  }

  @Test
  public void get_0() throws Throwable {
    assertFalse(service.get(0).isPresent());
  }

  @Test
  @WithAnonymousUser
  public void get_AccessDenied_Anonymous() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(1L);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void get_AccessDenied() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(1L);
    });
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

  @Test
  @WithAnonymousUser
  public void all_AccessDenied_Anonymous() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.all(DIGESTION);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void all_AccessDenied() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.all(DIGESTION);
    });
  }
}
