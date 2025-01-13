package ca.qc.ircm.proview.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link SampleContainerService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class SampleContainerServiceTest {
  private static final String READ = "read";
  @Autowired
  private SampleContainerService service;
  @MockBean
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void beforeTest() {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get_Id() throws Throwable {
    SampleContainer container = service.get(1L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(container.getSample()), eq(READ));
    assertEquals((Long) 1L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(SampleContainerType.TUBE, container.getType());
    assertEquals(LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0), container.getTimestamp());
  }

  @Test
  public void get_0() throws Throwable {
    assertFalse(service.get(0).isPresent());
  }

  @Test
  public void last() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    SampleContainer container = service.last(sample).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertEquals((Long) 129L, container.getId());
    assertNotNull(container.getSample());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(SampleContainerType.WELL, container.getType());
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 7, 34, 0), container.getTimestamp());
  }
}
