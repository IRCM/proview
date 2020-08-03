/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
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
  private AuthorizationService authorizationService;
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
  @Before
  public void beforeTest() {
    optionalActivity = Optional.of(activity);
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get() throws Exception {
    Plate plate = service.get(26L);

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
    Plate plate = service.get((Long) null);

    assertNull(plate);
  }

  @Test
  public void get_Submission() throws Exception {
    Submission submission = new Submission(163L);

    Plate plate = service.get(submission);

    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(READ));
    assertEquals((Long) 123L, plate.getId());
    assertEquals("Andrew-20171108", plate.getName());
    assertEquals((Long) 163L, plate.getSubmission().getId());
  }

  @Test
  public void get_NullSubmission() throws Exception {
    Plate plate = service.get((Submission) null);

    assertNull(plate);
  }

  @Test
  public void nameAvailable_ProteomicTrue() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);

    boolean available = service.nameAvailable("unit_test");

    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicFalse() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);

    boolean available = service.nameAvailable("A_20111108");

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionTrue() throws Exception {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable("unit_test");

    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_SubmissionFalse() throws Exception {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable("Andrew-20171108");

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_OtherUser() throws Exception {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable("Andrew-20171108");

    assertEquals(true, available);
  }

  @Test
  public void nameAvailable_ProteomicNull() throws Exception {
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);

    boolean available = service.nameAvailable(null);

    assertEquals(false, available);
  }

  @Test
  public void nameAvailable_SubmissionNull() throws Exception {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(false);

    boolean available = service.nameAvailable(null);

    assertEquals(false, available);
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void nameAvailable_AccessDenied_Anonymous() throws Throwable {
    service.nameAvailable("unit_test");
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
        service.lastTreatmentOrAnalysisDate(repository.findById(26L).orElse(null)));
    assertEquals(LocalDateTime.of(2014, 10, 15, 15, 53, 34),
        service.lastTreatmentOrAnalysisDate(repository.findById(115L).orElse(null)));
    assertEquals(LocalDateTime.of(2014, 10, 17, 11, 54, 22),
        service.lastTreatmentOrAnalysisDate(repository.findById(118L).orElse(null)));
    assertEquals(LocalDateTime.of(2014, 10, 22, 9, 57, 18),
        service.lastTreatmentOrAnalysisDate(repository.findById(121L).orElse(null)));
    assertNull(service.lastTreatmentOrAnalysisDate(repository.findById(122L).orElse(null)));
    assertNull(service.lastTreatmentOrAnalysisDate(repository.findById(123L).orElse(null)));
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void lastTreatmentOrAnalysisDate_Null() {
    assertNull(service.lastTreatmentOrAnalysisDate(null));
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void lastTreatmentOrAnalysisDate_AccessDenied_Anonymous() throws Throwable {
    service.lastTreatmentOrAnalysisDate(repository.findById(26L).orElse(null));
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void lastTreatmentOrAnalysisDate_AccessDenied() throws Throwable {
    service.lastTreatmentOrAnalysisDate(repository.findById(26L).orElse(null));
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
    plate = service.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void insert_AccessDenied_Anonymous() throws Throwable {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    service.insert(plate);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void insert_AccessDenied() throws Throwable {
    Plate plate = new Plate();
    plate.setName("test_plate_4896415");
    when(plateActivityService.insert(any(Plate.class))).thenReturn(activity);

    service.insert(plate);
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
    plate = service.get(plate.getId());
    assertEquals("test_plate_4896415", plate.getName());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void update_AccessDenied_Anonymous() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    service.update(plate);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void update_AccessDenied() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    plate.setName("test_plate_4896415");
    when(plateActivityService.update(any(Plate.class))).thenReturn(optionalActivity);

    service.update(plate);
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
    List<Well> bannedWells = service.get(plate.getId()).wells(from, to);
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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void ban_AccessDenied_Anonymous() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollection(), any(String.class))).thenReturn(activity);

    service.ban(plate, from, to, "unit test");
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void ban_AccessDenied() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation from = new WellLocation(3, 3);
    WellLocation to = new WellLocation(5, 4);
    when(plateActivityService.ban(anyCollection(), any(String.class))).thenReturn(activity);

    service.ban(plate, from, to, "unit test");
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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void activate_AccessDenied_Anonymous() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollection(), any(String.class))).thenReturn(activity);

    service.activate(plate, location, location, "unit test");
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void activate_AccessDenied() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    detach(plate);
    WellLocation location = new WellLocation(6, 11);
    when(plateActivityService.activate(anyCollection(), any(String.class))).thenReturn(activity);

    service.activate(plate, location, location, "unit test");
  }
}
