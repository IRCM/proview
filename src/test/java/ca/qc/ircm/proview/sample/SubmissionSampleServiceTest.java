package ca.qc.ircm.proview.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SubmissionSampleService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class SubmissionSampleServiceTest extends AbstractServiceTestCase {
  private static final String READ = "read";
  @Autowired
  private SubmissionSampleService service;
  @Autowired
  private SubmissionSampleRepository repository;
  @MockitoBean
  private SampleActivityService sampleActivityService;
  @MockitoBean
  private ActivityService activityService;
  @MockitoBean
  private AuthenticatedUser authenticatedUser;
  @MockitoBean
  private PermissionEvaluator permissionEvaluator;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<SubmissionSample> sampleCaptor;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get_Gel() {
    SubmissionSample sample = service.get(1L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertEquals((Long) 1L, sample.getId());
    assertEquals("FAM119A_band_01", sample.getName());
    assertEquals(SampleType.GEL, sample.getType());
    assertEquals(Sample.Category.SUBMISSION, sample.getCategory());
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals((Long) 1L, sample.getSubmission().getId());
    assertEquals(0, sample.getVersion());
  }

  @Test
  public void get() {
    SubmissionSample sample = service.get(442L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertEquals((Long) 442L, sample.getId());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals(SampleType.SOLUTION, sample.getType());
    assertEquals(Sample.Category.SUBMISSION, sample.getCategory());
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertEquals((Long) 32L, sample.getSubmission().getId());
    assertEquals("1.5 μg", sample.getQuantity());
    assertEquals("50 μl", sample.getVolume());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(0, sample.getVersion());
  }

  @Test
  public void get_0() {
    assertFalse(service.get(0).isPresent());
  }

  @Test
  public void exists_True() {
    User user = new User(3L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    boolean exists = service.exists("CAP_20111013_05");

    assertEquals(true, exists);
  }

  @Test
  public void exists_False() {
    User user = new User(3L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    boolean exists = service.exists("CAP_20111013_80");

    assertEquals(false, exists);
  }

  @Test
  public void exists_OtherUser() {
    User user = new User(10L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    boolean exists = service.exists("CAP_20111013_05");

    assertEquals(false, exists);
  }

  @Test
  public void exists_ControlName() {
    User user = new User(3L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    boolean exists = service.exists("control_01");

    assertEquals(false, exists);
  }

  @Test
  @WithAnonymousUser
  public void exists_AccessDenied() {
    assertThrows(AccessDeniedException.class, () -> service.exists("CAP_20111013_05"));
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus() {
    SubmissionSample sample1 = repository.findById(443L).orElseThrow();
    detach(sample1);
    sample1.setStatus(SampleStatus.DIGESTED);
    SubmissionSample sample2 = repository.findById(445L).orElseThrow();
    detach(sample2);
    sample2.setStatus(SampleStatus.RECEIVED);
    Collection<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample1);
    samples.add(sample2);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(samples);

    repository.flush();
    SubmissionSample testSample1 = repository.findById(443L).orElseThrow();
    SubmissionSample testSample2 = repository.findById(445L).orElseThrow();
    assertEquals(SampleStatus.DIGESTED, testSample1.getStatus());
    assertEquals(1, testSample1.getVersion());
    assertEquals(SampleStatus.RECEIVED, testSample2.getStatus());
    assertEquals(1, testSample2.getVersion());
    verify(sampleActivityService, times(2)).updateStatus(sampleCaptor.capture());
    verify(activityService, times(2)).insert(activity);
    SubmissionSample newTestSample1 = sampleCaptor.getAllValues().get(0);
    assertEquals(SampleStatus.DIGESTED, newTestSample1.getStatus());
    SubmissionSample newTestSample2 = sampleCaptor.getAllValues().get(1);
    assertEquals(SampleStatus.RECEIVED, newTestSample2.getStatus());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Name() {
    SubmissionSample sample = repository.findById(443L).orElseThrow();
    detach(sample);
    final String name = sample.getName();
    sample.setName("unit_test");
    sample.setStatus(SampleStatus.DIGESTED);
    Collection<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(samples);

    repository.flush();
    sample = repository.findById(443L).orElseThrow();
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
    assertEquals(name, sample.getName());
    assertEquals(1, sample.getVersion());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Received_SampleDeliveryDate_UpdatedNull() {
    SubmissionSample sample = repository.findById(443L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.RECEIVED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(443L).orElseThrow();
    assertTrue(
        LocalDate.now().minusDays(2).isBefore(sample.getSubmission().getSampleDeliveryDate()));
    assertTrue(LocalDate.now().plusDays(2).isAfter(sample.getSubmission().getSampleDeliveryDate()));
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Received_SampleDeliveryDate_NotUpdated() {
    SubmissionSample sample = repository.findById(559L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.RECEIVED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(559L).orElseThrow();
    assertEquals(LocalDate.of(2014, 10, 8), sample.getSubmission().getSampleDeliveryDate());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Digested_SampleDeliveryDate_NotUpdated() {
    SubmissionSample sample = repository.findById(443L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.DIGESTED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(443L).orElseThrow();
    assertNull(sample.getSubmission().getSampleDeliveryDate());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Digested_SubmissionDigestionDate_UpdatedNull() {
    SubmissionSample sample = repository.findById(443L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.DIGESTED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(443L).orElseThrow();
    assertTrue(LocalDate.now().minusDays(2).isBefore(sample.getSubmission().getDigestionDate()));
    assertTrue(LocalDate.now().plusDays(2).isAfter(sample.getSubmission().getDigestionDate()));
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Digested_SubmissionDigestionDate_NotUpdated() {
    SubmissionSample sample = repository.findById(559L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.DIGESTED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(559L).orElseThrow();
    assertEquals(LocalDate.of(2014, 10, 8), sample.getSubmission().getDigestionDate());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Analysed_SubmissionDigestionDate_NotUpdated() {
    SubmissionSample sample = repository.findById(443L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.ANALYSED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(443L).orElseThrow();
    assertNull(sample.getSubmission().getDigestionDate());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Analysed_SubmissionAnalysisDate_UpdatedNull() {
    SubmissionSample sample = repository.findById(443L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.ANALYSED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(443L).orElseThrow();
    assertTrue(LocalDate.now().minusDays(2).isBefore(sample.getSubmission().getAnalysisDate()));
    assertTrue(LocalDate.now().plusDays(2).isAfter(sample.getSubmission().getAnalysisDate()));
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void updateStatus_Analysed_SubmissionAnalysisDate_NotUpdated() {
    SubmissionSample sample = repository.findById(621L).orElseThrow();
    detach(sample);
    sample.setStatus(SampleStatus.ANALYSED);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    service.updateStatus(List.of(sample));

    repository.flush();
    sample = repository.findById(621L).orElseThrow();
    assertEquals(LocalDate.of(2014, 10, 17), sample.getSubmission().getAnalysisDate());
  }

  @Test
  @WithAnonymousUser
  public void updateStatus_AccessDenied_Anonymous() {
    SubmissionSample sample1 = repository.findById(443L).orElseThrow();
    detach(sample1);
    sample1.setStatus(SampleStatus.DIGESTED);
    SubmissionSample sample2 = repository.findById(445L).orElseThrow();
    detach(sample2);
    sample2.setStatus(SampleStatus.RECEIVED);
    Collection<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample1);
    samples.add(sample2);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    assertThrows(AccessDeniedException.class, () -> service.updateStatus(samples));
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void updateStatus_AccessDenied() {
    SubmissionSample sample1 = repository.findById(443L).orElseThrow();
    detach(sample1);
    sample1.setStatus(SampleStatus.DIGESTED);
    SubmissionSample sample2 = repository.findById(445L).orElseThrow();
    detach(sample2);
    sample2.setStatus(SampleStatus.RECEIVED);
    Collection<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample1);
    samples.add(sample2);
    when(sampleActivityService.updateStatus(any())).thenReturn(Optional.of(activity));

    assertThrows(AccessDeniedException.class, () -> service.updateStatus(samples));
  }
}
