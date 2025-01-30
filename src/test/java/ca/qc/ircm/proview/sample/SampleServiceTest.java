package ca.qc.ircm.proview.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SampleService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class SampleServiceTest {
  private static final String READ = "read";
  @Autowired
  private SampleService service;
  @MockitoBean
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void beforeTest() {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get_SubmissionSample_Gel() {
    Sample sample = service.get(1L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertInstanceOf(SubmissionSample.class, sample);
    SubmissionSample gelSample = (SubmissionSample) sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals(SampleType.GEL, gelSample.getType());
    assertEquals(Sample.Category.SUBMISSION, gelSample.getCategory());
    assertEquals(SampleStatus.ANALYSED, gelSample.getStatus());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
  }

  @Test
  public void get_SubmissionSample() {
    Sample sample = service.get(442L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertInstanceOf(SubmissionSample.class, sample);
    SubmissionSample eluateSample = (SubmissionSample) sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("CAP_20111013_01", eluateSample.getName());
    assertEquals(SampleType.SOLUTION, eluateSample.getType());
    assertEquals(Sample.Category.SUBMISSION, eluateSample.getCategory());
    assertEquals(SampleStatus.ANALYSED, eluateSample.getStatus());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals("1.5 μg", eluateSample.getQuantity());
    assertEquals("50 μl", eluateSample.getVolume());
  }

  @Test
  public void get_Control() {
    Sample sample = service.get(444L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertInstanceOf(Control.class, sample);
    Control control = (Control) sample;
    assertEquals((Long) 444L, control.getId());
    assertEquals("control_01", control.getName());
    assertEquals(SampleType.GEL, control.getType());
    assertEquals(Sample.Category.CONTROL, control.getCategory());
    assertEquals(ControlType.NEGATIVE_CONTROL, control.getControlType());
    assertNull(control.getVolume());
    assertNull(control.getQuantity());
  }

  @Test
  public void get_0() {
    assertFalse(service.get(0).isPresent());
  }
}
