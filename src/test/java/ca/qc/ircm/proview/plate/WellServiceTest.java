package ca.qc.ircm.proview.plate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
  public void get() throws Exception {
    Well well = service.get(129L).orElse(null);

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
    assertFalse(service.get(null).isPresent());
  }

  @Test
  @WithAnonymousUser
  public void get_AccessDenied_Anonymous() throws Exception {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(129L);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void get_AccessDenied() throws Exception {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(129L);
    });
  }
}
