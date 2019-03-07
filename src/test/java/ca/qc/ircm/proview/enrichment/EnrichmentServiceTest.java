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

package ca.qc.ircm.proview.enrichment;

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
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
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
public class EnrichmentServiceTest extends AbstractServiceTestCase {
  @Inject
  private EnrichmentService service;
  @Inject
  private EnrichmentRepository repository;
  @Inject
  private ProtocolRepository protocolRepository;
  @Inject
  private SubmissionSampleRepository submissionSampleRepository;
  @Inject
  private TubeRepository tubeRepository;
  @Inject
  private WellRepository wellRepository;
  @MockBean
  private ProtocolService protocolService;
  @MockBean
  private EnrichmentActivityService enrichmentActivityService;
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
    Enrichment enrichment = service.get(7L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(enrichment);
    assertEquals((Long) 7L, enrichment.getId());
    assertEquals(TreatmentType.ENRICHMENT, enrichment.getType());
    assertEquals((Long) 2L, enrichment.getUser().getId());
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 20, 21).atZone(ZoneId.systemDefault()).toInstant(),
        enrichment.getInsertTime());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionExplanation());
    List<TreatedSample> treatedSamples = enrichment.getTreatedSamples();
    assertEquals(1, treatedSamples.size());
    TreatedSample treatedSample = treatedSamples.get(0);
    assertEquals(enrichment, treatedSample.getTreatment());
    assertEquals((Long) 444L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 4L, treatedSample.getContainer().getId());
    assertEquals(null, treatedSample.getComment());
  }

  @Test
  public void get_Null() {
    Enrichment enrichment = service.get(null);

    assertNull(enrichment);
  }

  @Test
  public void insert_Tube() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Tube tube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    enrichment.setTreatedSamples(treatedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    service.insert(enrichment);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = service.get(enrichment.getId());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionExplanation());
    assertEquals(user, enrichment.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(enrichment.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(enrichment.getInsertTime()));
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
    assertEquals(1, enrichment.getTreatedSamples().size());
    treatedSample = enrichment.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.ENRICHED, sample.getStatus());
  }

  @Test
  public void insert_Well() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    detach(sample);
    Well well = new Well(128L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(well);
    treatedSamples.add(treatedSample);
    enrichment.setTreatedSamples(treatedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    service.insert(enrichment);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = service.get(enrichment.getId());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionExplanation());
    assertEquals(user, enrichment.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(enrichment.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(enrichment.getInsertTime()));
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
    assertEquals(1, enrichment.getTreatedSamples().size());
    treatedSample = enrichment.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, treatedSample.getContainer().getType());
    assertEquals((Long) 128L, treatedSample.getContainer().getId());
    sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.ENRICHED, sample.getStatus());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
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
    enrichment.setTreatedSamples(treatedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    try {
      service.insert(enrichment);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
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
    enrichment.setTreatedSamples(treatedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    try {
      service.insert(enrichment);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void insert_NewProtocol() {
    Enrichment enrichment = new Enrichment();
    Protocol protocol = new Protocol(null, "test protocol");
    protocol.setType(Protocol.Type.ENRICHMENT);
    enrichment.setProtocol(protocol);
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    enrichment.setTreatedSamples(treatedSamples);
    doAnswer(i -> {
      protocolRepository.save(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    service.insert(enrichment);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(protocolService).insert(eq(protocol));
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = service.get(enrichment.getId());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionExplanation());
    assertEquals(user, enrichment.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(enrichment.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(enrichment.getInsertTime()));
    assertNotNull(enrichment.getProtocol().getId());
    assertEquals(protocol.getName(), enrichment.getProtocol().getName());
    assertEquals(1, enrichment.getTreatedSamples().size());
    treatedSample = enrichment.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
  }

  @Test
  public void update() {
    Enrichment enrichment = repository.findOne(223L);
    detach(enrichment);
    enrichment.getTreatedSamples().stream().forEach(ts -> detach(ts));
    enrichment.setProtocol(protocolRepository.findOne(4L));
    enrichment.getTreatedSamples().get(0).setComment("test update");
    enrichment.getTreatedSamples().get(0).setContainer(new Well(248L));
    enrichment.getTreatedSamples().get(0).setSample(new Control(444L));
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(enrichment, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).update(eq(enrichment), eq("test explanation"));
    verify(activityService).insert(activity);
    enrichment = repository.findOne(223L);
    assertNotNull(enrichment);
    assertEquals((Long) 4L, enrichment.getProtocol().getId());
    assertEquals((Long) 248L, enrichment.getTreatedSamples().get(0).getContainer().getId());
    assertEquals((Long) 444L, enrichment.getTreatedSamples().get(0).getSample().getId());
    assertEquals("test update", enrichment.getTreatedSamples().get(0).getComment());
  }

  @Test
  public void update_NewProtocol() {
    Enrichment enrichment = repository.findOne(223L);
    detach(enrichment);
    Protocol protocol = new Protocol(null, "test protocol");
    protocol.setType(Protocol.Type.ENRICHMENT);
    enrichment.setProtocol(protocol);
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.of(activity));
    doAnswer(i -> {
      protocolRepository.save(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());

    service.update(enrichment, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(protocolService).insert(eq(protocol));
    verify(enrichmentActivityService).update(eq(enrichment), eq("test explanation"));
    verify(activityService).insert(activity);
    enrichment = repository.findOne(223L);
    assertNotNull(enrichment);
    assertNotNull(enrichment.getProtocol().getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveTreatedSample() {
    Enrichment enrichment = repository.findOne(223L);
    detach(enrichment);
    enrichment.getTreatedSamples().stream().forEach(ts -> detach(ts));
    enrichment.getTreatedSamples().remove(1);
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(enrichment, "test explanation");
  }

  @Test
  public void update_NoActivity() {
    Enrichment enrichment = repository.findOne(223L);
    detach(enrichment);
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.empty());

    service.update(enrichment, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).update(eq(enrichment), eq("test explanation"));
    verify(activityService, never()).insert(any());
    enrichment = repository.findOne(223L);
    assertNotNull(enrichment);
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
  }

  @Test
  public void undo_NoBan() {
    Enrichment enrichment = repository.findOne(223L);
    detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(enrichment, "fail unit test", false);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = service.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Ban() {
    Enrichment enrichment = repository.findOne(223L);
    detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(enrichment, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = service.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = wellRepository.findOne(800L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(812L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 800L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 812L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer() {
    Enrichment enrichment = repository.findOne(225L);
    detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(enrichment, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = service.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = wellRepository.findOne(801L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(813L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(896L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(908L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 801L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 813L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 896L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 908L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation() {
    Enrichment enrichment = repository.findOne(226L);
    detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(enrichment, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = service.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = wellRepository.findOne(825L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(837L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(897L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(909L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(921L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(933L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 825L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 837L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 897L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 909L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 921L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 933L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer_Fractionation() {
    Enrichment enrichment = repository.findOne(227L);
    detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(enrichment, "fail unit test", true);

    repository.flush();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = service.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = wellRepository.findOne(849L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(861L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(898L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(910L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(803L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(815L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(827L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(839L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 849L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 861L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 898L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 910L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 803L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 815L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 827L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 839L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation_Transfer() {
    Enrichment enrichment = repository.findOne(228L);
    detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(enrichment, "fail unit test", true);

    repository.flush();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = service.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = wellRepository.findOne(873L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(885L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(899L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(911L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(923L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(935L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(802L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(814L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(826L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(838L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 873L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 885L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 899L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 911L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 923L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 935L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 802L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 814L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 826L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 838L).isPresent());
  }
}
