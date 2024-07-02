package ca.qc.ircm.proview.tube;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link TubeService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class TubeServiceTest {
  private static final String READ = "read";
  @Autowired
  private TubeService service;
  @MockBean
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void beforeTest() {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get() throws Throwable {
    Tube tube = service.get(1L).get();

    verify(permissionEvaluator).hasPermission(any(), eq(tube.getSample()), eq(READ));
    assertEquals((Long) 1L, tube.getId());
    assertEquals("FAM119A_band_01", tube.getName());
    assertEquals((Long) 1L, tube.getSample().getId());
    assertEquals(SampleContainerType.TUBE, tube.getType());
    assertEquals(LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0), tube.getTimestamp());
  }

  @Test
  public void get_Null() throws Throwable {
    assertFalse(service.get((Long) null).isPresent());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void nameAvailable_True() throws Throwable {
    boolean available = service.nameAvailable("FAM119A_band_01");

    assertFalse(available);
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void nameAvailable_False() throws Throwable {
    boolean available = service.nameAvailable("unit_test");

    assertTrue(available);
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void nameAvailable_Null() throws Throwable {
    boolean available = service.nameAvailable(null);

    assertFalse(available);
  }

  @Test
  @WithAnonymousUser
  public void nameAvailable_AccessDenied_Anonymous() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.nameAvailable("FAM119A_band_01");
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void nameAvailable_AccessDenied() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.nameAvailable("FAM119A_band_01");
    });
  }

  @Test
  public void all() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    List<Tube> tubes = service.all(sample);

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertEquals(3, tubes.size());
    assertTrue(find(tubes, 1L).isPresent());
    assertTrue(find(tubes, 6L).isPresent());
    assertTrue(find(tubes, 7L).isPresent());
    assertFalse(find(tubes, 5L).isPresent());
  }

  @Test
  public void all_Null() throws Throwable {
    List<Tube> tubes = service.all(null);

    assertEquals(0, tubes.size());
  }
}
