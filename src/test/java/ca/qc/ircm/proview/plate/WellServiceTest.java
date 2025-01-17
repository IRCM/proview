package ca.qc.ircm.proview.plate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserRole;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link WellService}.
 */
@ServiceTestAnnotations
public class WellServiceTest {
  @Autowired
  private WellService service;

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void get() {
    Well well = service.get(129L).orElseThrow();

    assertEquals((Long) 129L, well.getId());
    assertEquals((Long) 26L, well.getPlate().getId());
    assertNotNull(well.getSample());
    assertEquals((Long) 1L, well.getSample().getId());
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 7, 34), well.getTimestamp());
    assertEquals(false, well.isBanned());
    assertEquals(0, well.getRow());
    assertEquals(1, well.getColumn());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void get_0() {
    assertFalse(service.get(0).isPresent());
  }

  @Test
  @WithAnonymousUser
  public void get_AccessDenied_Anonymous() {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(129L);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void get_AccessDenied() {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(129L);
    });
  }
}
