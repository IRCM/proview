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

package ca.qc.ircm.proview.digestion;

import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellRepository;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.ProtocolRepository;
import ca.qc.ircm.proview.treatment.ProtocolService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeRepository;
import ca.qc.ircm.proview.user.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DigestionServiceTest extends AbstractServiceTestCase {
  @Inject
  private DigestionService service;
  @Inject
  private DigestionRepository repository;
  @Inject
  private ProtocolRepository protocolRepository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private SubmissionSampleRepository submissionSampleRepository;
  @Inject
  private TubeRepository tubeRepository;
  @Inject
  private WellRepository wellRepository;
  @MockBean
  private ProtocolService protocolService;
  @MockBean
  private DigestionActivityService digestionActivityService;
  @MockBean
  private ActivityService activityService;
  @MockBean
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Collection<SampleContainer>> containersCaptor;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    user = new User(4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Digestion digestion = service.get(6L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(digestion);
    assertEquals((Long) 6L, digestion.getId());
    assertEquals(TreatmentType.DIGESTION, digestion.getType());
    assertEquals((Long) 2L, digestion.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 15, 20).atZone(ZoneId.systemDefault()).toInstant(),
        digestion.getInsertTime());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionExplanation());
    List<TreatedSample> treatedSamples = digestion.getTreatedSamples();
    assertEquals(1, treatedSamples.size());
    TreatedSample treatedSample = treatedSamples.get(0);
    assertEquals(digestion, treatedSample.getTreatment());
    assertEquals((Long) 444L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 4L, treatedSample.getContainer().getId());
    assertEquals(null, treatedSample.getComment());
  }

  @Test
  public void get_Null() {
    Digestion digestion = service.get(null);

    assertNull(digestion);
  }

  @Test
  public void insert_Tube() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(protocolRepository.findOne(1L));
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Tube tube = new Tube(1L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = repository.findOne(digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionExplanation());
    assertEquals(user, digestion.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(digestion.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(digestion.getInsertTime()));
    assertEquals((Long) 1L, digestion.getProtocol().getId());
    assertEquals(1, digestion.getTreatedSamples().size());
    treatedSample = digestion.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
  }

  @Test
  public void insert_Well() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(protocolRepository.findOne(1L));
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Well well = new Well(128L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(well);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = service.get(digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionExplanation());
    assertEquals(user, digestion.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(digestion.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(digestion.getInsertTime()));
    assertEquals((Long) 1L, digestion.getProtocol().getId());
    assertEquals(1, digestion.getTreatedSamples().size());
    treatedSample = digestion.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, treatedSample.getContainer().getType());
    assertEquals((Long) 128L, treatedSample.getContainer().getId());
    sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(protocolRepository.findOne(1L));
    Tube tube1 = tubeRepository.findOne(3L);
    Tube tube2 = tubeRepository.findOne(8L);
    detach(tube1, tube1.getSample(), tube2, tube2.getSample());
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample1 = new TreatedSample();
    treatedSample1.setComment("unit test");
    treatedSample1.setSample(tube1.getSample());
    treatedSample1.setContainer(tube1);
    treatedSamples.add(treatedSample1);
    TreatedSample treatedSample2 = new TreatedSample();
    treatedSample2.setComment("unit test");
    treatedSample2.setSample(tube2.getSample());
    treatedSample2.setContainer(tube2);
    treatedSamples.add(treatedSample2);
    digestion.setTreatedSamples(treatedSamples);
    when(digestionActivityService.insert(any())).thenReturn(activity);

    try {
      service.insert(digestion);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(protocolRepository.findOne(1L));
    Tube tube1 = tubeRepository.findOne(3L);
    Tube tube2 = tubeRepository.findOne(4L);
    detach(tube1, tube1.getSample(), tube2, tube2.getSample());
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample1 = new TreatedSample();
    treatedSample1.setComment("unit test");
    treatedSample1.setSample(tube1.getSample());
    treatedSample1.setContainer(tube1);
    treatedSamples.add(treatedSample1);
    TreatedSample treatedSample2 = new TreatedSample();
    treatedSample2.setComment("unit test");
    treatedSample2.setSample(tube2.getSample());
    treatedSample2.setContainer(tube2);
    treatedSamples.add(treatedSample2);
    digestion.setTreatedSamples(treatedSamples);
    when(digestionActivityService.insert(any())).thenReturn(activity);

    try {
      service.insert(digestion);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void insert_NewProtocol() {
    Digestion digestion = new Digestion();
    Protocol protocol = new Protocol(null, "test protocol");
    protocol.setType(Protocol.Type.DIGESTION);
    digestion.setProtocol(protocol);
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Tube tube = new Tube(1L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    doAnswer(i -> {
      protocolRepository.save(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(protocolService).insert(eq(protocol));
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = repository.findOne(digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionExplanation());
    assertEquals(user, digestion.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(digestion.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(digestion.getInsertTime()));
    assertNotNull(digestion.getProtocol().getId());
    assertEquals(protocol.getName(), digestion.getProtocol().getName());
    assertEquals(1, digestion.getTreatedSamples().size());
    treatedSample = digestion.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void insert_WrongProtocolType() {
    Digestion digestion = new Digestion();
    Protocol protocol = protocolRepository.findOne(4L);
    digestion.setProtocol(protocol);
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Tube tube = new Tube(1L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    doAnswer(i -> {
      protocolRepository.save(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);
  }

  @Test(expected = IllegalArgumentException.class)
  public void insert_NullProtocolType() {
    Digestion digestion = new Digestion();
    Protocol protocol = new Protocol(null, "test protocol");
    digestion.setProtocol(protocol);
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Tube tube = new Tube(1L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    doAnswer(i -> {
      protocolRepository.save(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);
  }

  @Test
  public void insert_SubmissionDigestionDate_NotUpdated() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(protocolRepository.findOne(1L));
    SubmissionSample sample = submissionSampleRepository.findOne(559L);
    detach(sample, sample.getSubmission());
    Tube tube = new Tube(11L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);

    repository.flush();
    Submission submission = submissionRepository.findOne(147L);
    assertEquals(LocalDate.of(2014, 10, 8), submission.getDigestionDate());
  }

  @Test
  public void insert_SubmissionDigestionDate_UpdatedNull() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(protocolRepository.findOne(1L));
    SubmissionSample sample = submissionSampleRepository.findOne(442L);
    detach(sample, sample.getSubmission());
    Tube tube = new Tube(2L);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    digestion.setTreatedSamples(treatedSamples);
    when(digestionActivityService.insert(any())).thenReturn(activity);

    service.insert(digestion);

    repository.flush();
    Submission submission = submissionRepository.findOne(32L);
    assertTrue(LocalDate.now().minusDays(1).isBefore(submission.getDigestionDate()));
    assertTrue(LocalDate.now().plusDays(1).isAfter(submission.getDigestionDate()));
  }

  @Test
  public void update() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    digestion.getTreatedSamples().stream().forEach(ts -> detach(ts));
    digestion.setProtocol(protocolRepository.findOne(3L));
    digestion.getTreatedSamples().get(0).setComment("test update");
    digestion.getTreatedSamples().get(0).setContainer(new Well(248L));
    digestion.getTreatedSamples().get(0).setSample(new Control(444L));
    when(digestionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(digestion, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).update(eq(digestion), eq("test explanation"));
    verify(activityService).insert(activity);
    digestion = repository.findOne(195L);
    assertNotNull(digestion);
    assertEquals((Long) 3L, digestion.getProtocol().getId());
    assertEquals((Long) 248L, digestion.getTreatedSamples().get(0).getContainer().getId());
    assertEquals((Long) 444L, digestion.getTreatedSamples().get(0).getSample().getId());
    assertEquals("test update", digestion.getTreatedSamples().get(0).getComment());
  }

  @Test
  public void update_NewProtocol() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    Protocol protocol = new Protocol(null, "test protocol");
    protocol.setType(Protocol.Type.DIGESTION);
    digestion.setProtocol(protocol);
    when(digestionActivityService.update(any(), any())).thenReturn(Optional.of(activity));
    doAnswer(i -> {
      protocolRepository.save(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());

    service.update(digestion, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(protocolService).insert(eq(protocol));
    verify(digestionActivityService).update(eq(digestion), eq("test explanation"));
    verify(activityService).insert(activity);
    digestion = repository.findOne(195L);
    assertNotNull(digestion);
    assertNotNull(digestion.getProtocol().getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveTreatedSample() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    digestion.getTreatedSamples().stream().forEach(ts -> detach(ts));
    digestion.getTreatedSamples().remove(1);
    when(digestionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(digestion, "test explanation");
  }

  @Test
  public void update_NoActivity() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    when(digestionActivityService.update(any(), any())).thenReturn(Optional.empty());

    service.update(digestion, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).update(eq(digestion), eq("test explanation"));
    verify(activityService, never()).insert(any());
    digestion = repository.findOne(195L);
    assertNotNull(digestion);
    assertEquals((Long) 1L, digestion.getProtocol().getId());
  }

  @Test
  public void undo_NoBan() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(digestion, "fail unit test", false);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    digestion = service.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Ban() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(digestion, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    digestion = service.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Well well = wellRepository.findOne(224L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(236L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 224L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 236L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer() {
    Digestion digestion = repository.findOne(196L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(digestion, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    Digestion test = service.get(digestion.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(13L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(14L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(320L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(332L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 13L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 14L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 320L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 332L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation() {
    Digestion digestion = repository.findOne(197L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(digestion, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    Digestion test = service.get(digestion.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(15L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(16L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(322L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(334L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(346L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(358L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 15L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 16L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 322L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 334L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 346L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 358L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer_Fractionation() {
    Digestion digestion = repository.findOne(198L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(digestion, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    // Test that digestion failed.
    digestion = service.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(17L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(18L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(321L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(333L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(416L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(428L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(440L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(452L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 17L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 18L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 321L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 333L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 416L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 428L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 440L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 452L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation_Transfer() {
    Digestion digestion = repository.findOne(199L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(digestion, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    // Test that digestion failed.
    digestion = service.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(19L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(20L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(323L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(335L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(347L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(359L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(417L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(429L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(441L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(453L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 19L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 20L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 323L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 335L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 347L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 359L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 417L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 429L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 441L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 453L).isPresent());
  }

  @Test
  public void undo_NotBanErroneousTransfer() {
    Digestion digestion = repository.findOne(321L);
    detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Digestion failed.
    service.undo(digestion, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    digestion = service.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(2279L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(1570L);
    assertEquals(false, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 2279L).isPresent());
  }
}
