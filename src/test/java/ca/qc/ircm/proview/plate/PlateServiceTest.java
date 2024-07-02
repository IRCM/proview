package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link PlateService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class PlateServiceTest extends AbstractServiceTestCase {
  private static final String READ = "read";
  @Autowired
  private PlateService service;
  @Autowired
  private PlateRepository repository;
  @Autowired
  private WellRepository wellRepository;
  @MockBean
  private PlateActivityService plateActivityService;
  @MockBean
  private ActivityService activityService;
  @MockBean
  private AuthenticatedUser authenticatedUser;
  @MockBean
  private PermissionEvaluator permissionEvaluator;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Collection<Well>> wellsCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    optionalActivity = Optional.of(activity);
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get() throws Exception {
    Plate plate = service.get(26L).orElse(null);

    verify(permissionEvaluator).hasPermission(any(), eq(plate), eq(READ));
    assertEquals((Long) 26L, plate.getId());
    assertEquals("A_20111108", plate.getName());
    assertNull(plate.getSubmission());
    final List<Well> wells = plate.getWells();
    assertEquals(96, wells.size());
    final int rowCount = plate.getRowCount();
    List<Well> someWells = plate.wells(new WellLocation(0, 1), new WellLocation(rowCount, 1));
    assertEquals(plate.getRowCount(), someWells.size());
    for (Well testWell : someWells) {
      assertEquals(1, testWell.getColumn());
    }
    Well well = plate.well(2, 3);
    assertEquals(2, well.getRow());
    assertEquals(3, well.getColumn());
    assertEquals(91, plate.getEmptyWellCount());
    assertEquals(2, plate.getSampleCount());
  }

  @Test
  public void get_NullId() throws Exception {
    assertFalse(service.get((Long) null).isPresent());
  }

  @Test
  public void get_Submission() throws Exception {
    Submission submission = new Submission(163L);

    Plate plate = service.get(submission).orElse(null);

    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(READ));
    assertEquals((Long) 123L, plate.getId());
    assertEquals("Andrew-20171108", plate.getName());
    assertEquals((Long) 163L, plate.getSubmission().getId());
  }

  @Test
  public void get_NullSubmission() throws Exception {
    assertFalse(service.get((Submission) null).isPresent());
  }

  @Test
  public void nameAvailable_ProteomicTrue() throws Exception {
    User user = new User(1L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);

    boolean available = service.nameAvailable("unit_test");

    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicFalse() throws Exception {
    User user = new User(1L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);

    boolean available = service.nameAvailable("A_20111108");

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionTrue() throws Exception {
    User user = new User(10L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable("unit_test");

    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_SubmissionFalse() throws Exception {
    User user = new User(10L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable("Andrew-20171108");

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_OtherUser() throws Exception {
    User user = new User(3L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable("Andrew-20171108");

    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicNull() throws Exception {
    User user = new User(1L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);

    boolean available = service.nameAvailable(null);

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionNull() throws Exception {
    User user = new User(3L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable(null);

    assertEquals(false, available);
  }

  @Test
  @WithAnonymousUser
  public void nameAvailable_AccessDenied_Anonymous() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.nameAvailable("unit_test");
    });
  }

  private Plate plateForPrint() {
    Plate plate = new Plate();
    plate.setName("my plate");
    plate.initWells();
    plate.getWells().stream().limit(20).forEach(well -> {
      SubmissionSample sample = new SubmissionSample();
      sample.setName("s_" + well.getName());
      well.setSample(sample);
    });
    plate.getWells().stream().skip(18).limit(2).forEach(well -> well.setBanned(true));
    plate.getWells().stream().skip(20).limit(2).forEach(well -> {
      Control control = new Control();
      control.setName("c_" + well.getName());
      well.setSample(control);
    });
    return plate;
  }

  @Test
  public void print() throws Exception {
    Plate plate = plateForPrint();
    Locale locale = Locale.getDefault();

    String content = service.print(plate, locale);

    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"plate-information section"));
    assertTrue(content.contains("class=\"plate-name\""));
    assertTrue(content.contains(plate.getName()));
    assertTrue(content.contains("class=\"well active\""));
    assertTrue(content.contains("class=\"well banned\""));
    assertTrue(content.contains("class=\"well-sample-name\""));
    for (Well well : plate.getWells()) {
      if (well.getSample() != null) {
        assertTrue(content.contains(well.getSample().getName()));
      }
    }
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void lastTreatmentOrAnalysisDate() {
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 7, 34),
        service.lastTreatmentOrAnalysisDate(repository.findById(26L).orElse(null)).orElse(null));
    assertEquals(LocalDateTime.of(2014, 10, 15, 15, 53, 34),
        service.lastTreatmentOrAnalysisDate(repository.findById(115L).orElse(null)).orElse(null));
    assertEquals(LocalDateTime.of(2014, 10, 17, 11, 54, 22),
        service.lastTreatmentOrAnalysisDate(repository.findById(118L).orElse(null)).orElse(null));
    assertEquals(LocalDateTime.of(2014, 10, 22, 9, 57, 18),
        service.lastTreatmentOrAnalysisDate(repository.findById(121L).orElse(null)).orElse(null));
    assertFalse(
        service.lastTreatmentOrAnalysisDate(repository.findById(122L).orElse(null)).isPresent());
    assertFalse(
        service.lastTreatmentOrAnalysisDate(repository.findById(123L).orElse(null)).isPresent());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void lastTreatmentOrAnalysisDate_Null() {
    assertFalse(service.lastTreatmentOrAnalysisDate(null).isPresent());
  }

  @Test
  @WithAnonymousUser
  public void lastTreatmentOrAnalysisDate_AccessDenied_Anonymous() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.lastTreatmentOrAnalysisDate(repository.findById(26L).orElse(null));
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void lastTreatmentOrAnalysisDate_AccessDenied() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      service.lastTreatmentOrAnalysisDate(repository.findById(26L).orElse(null));
    });
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void insert() throws Exception {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    service.insert(plate);

    repository.flush();
    verify(plateActivityService).insert(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = service.get(plate.getId()).get();
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test
  @WithAnonymousUser
  public void insert_AccessDenied_Anonymous() throws Throwable {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    assertThrows(AccessDeniedException.class, () -> {
      service.insert(plate);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void insert_AccessDenied() throws Throwable {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    assertThrows(AccessDeniedException.class, () -> {
      service.insert(plate);
    });
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void update() throws Exception {
    Plate plate = repository.findById(26L).orElse(null);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    service.update(plate);

    repository.flush();
    verify(plateActivityService).update(plate);
    verify(activityService).insert(activity);
    assertNotNull(plate.getId());
    plate = service.get(plate.getId()).get();
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test
  @WithAnonymousUser
  public void update_AccessDenied_Anonymous() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    assertThrows(AccessDeniedException.class, () -> {
      service.update(plate);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void update_AccessDenied() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    assertThrows(AccessDeniedException.class, () -> {
      service.update(plate);
    });
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void ban_OneWell() {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation location = new WellLocation(0, 0);
    when(plateActivityService.ban(anyCollection(), any(String.class))).thenReturn(activity);

    service.ban(plate, location, location, "unit test");

    repository.flush();
    verify(plateActivityService).ban(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = wellRepository.findById(128L).orElse(null);
    assertEquals(true, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(1, loggedWells.size());
    assertTrue(find(loggedWells, 128L).isPresent());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void ban_MultipleWells() {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollection(), any(String.class))).thenReturn(activity);

    service.ban(plate, from, to, "unit test");

    repository.flush();
    verify(plateActivityService).ban(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    List<Well> bannedWells = service.get(plate.getId()).get().wells(from, to);
    for (Well bannedWell : bannedWells) {
      Well well = wellRepository.findById(bannedWell.getId()).orElse(null);
      assertEquals(true, well.isBanned());
    }
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(bannedWells.size(), loggedWells.size());
    for (Well banned : bannedWells) {
      assertTrue(find(loggedWells, banned.getId()).isPresent());
    }
  }

  @Test
  @WithAnonymousUser
  public void ban_AccessDenied_Anonymous() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollection(), any(String.class))).thenReturn(activity);

    assertThrows(AccessDeniedException.class, () -> {
      service.ban(plate, from, to, "unit test");
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void ban_AccessDenied() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollection(), any(String.class))).thenReturn(activity);

    assertThrows(AccessDeniedException.class, () -> {
      service.ban(plate, from, to, "unit test");
    });
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void activate_OneWell() {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollection(), any(String.class))).thenReturn(activity);

    service.activate(plate, location, location, "unit test");

    repository.flush();
    verify(plateActivityService).activate(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = wellRepository.findById(211L).orElse(null);
    assertEquals(false, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(1, loggedWells.size());
    assertTrue(find(loggedWells, 211L).isPresent());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void activate_MultipleWells() {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation from = new WellLocation(5, 11);
    WellLocation to = new WellLocation(7, 11);
    when(plateActivityService.activate(anyCollection(), any(String.class))).thenReturn(activity);

    service.activate(plate, from, to, "unit test");

    repository.flush();
    verify(plateActivityService).activate(wellsCaptor.capture(), eq("unit test"));
    verify(activityService).insert(activity);
    Well well = wellRepository.findById(199L).orElse(null);
    assertEquals(false, well.isBanned());
    well = wellRepository.findById(211L).orElse(null);
    assertEquals(false, well.isBanned());
    well = wellRepository.findById(223L).orElse(null);
    assertEquals(false, well.isBanned());
    Collection<Well> loggedWells = wellsCaptor.getValue();
    assertEquals(3, loggedWells.size());
    assertTrue(find(loggedWells, 199L).isPresent());
    assertTrue(find(loggedWells, 211L).isPresent());
    assertTrue(find(loggedWells, 223L).isPresent());
  }

  @Test
  @WithAnonymousUser
  public void activate_AccessDenied_Anonymous() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollection(), any(String.class))).thenReturn(activity);

    assertThrows(AccessDeniedException.class, () -> {
      service.activate(plate, location, location, "unit test");
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void activate_AccessDenied() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollection(), any(String.class))).thenReturn(activity);

    assertThrows(AccessDeniedException.class, () -> {
      service.activate(plate, location, location, "unit test");
    });
  }
}
