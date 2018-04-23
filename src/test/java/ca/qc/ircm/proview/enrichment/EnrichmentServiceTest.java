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
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.ProtocolService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class EnrichmentServiceTest {
  private EnrichmentService enrichmentService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private ProtocolService protocolService;
  @Mock
  private EnrichmentActivityService enrichmentActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
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
    enrichmentService = new EnrichmentService(entityManager, queryFactory, protocolService,
        enrichmentActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Enrichment enrichment = enrichmentService.get(7L);

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
    Enrichment enrichment = enrichmentService.get(null);

    assertNull(enrichment);
  }

  @Test
  public void insert_Tube() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    entityManager.detach(sample);
    Tube tube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSamples.add(treatedSample);
    enrichment.setTreatedSamples(treatedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    enrichmentService.insert(enrichment);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = enrichmentService.get(enrichment.getId());
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
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.ENRICHED, sample.getStatus());
  }

  @Test
  public void insert_Well() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    entityManager.detach(sample);
    Well well = new Well(128L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(well);
    treatedSamples.add(treatedSample);
    enrichment.setTreatedSamples(treatedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    enrichmentService.insert(enrichment);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = enrichmentService.get(enrichment.getId());
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
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.ENRICHED, sample.getStatus());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
    Tube tube1 = entityManager.find(Tube.class, 3L);
    Tube tube2 = entityManager.find(Tube.class, 8L);
    entityManager.detach(tube1);
    entityManager.detach(tube1.getSample());
    entityManager.detach(tube2);
    entityManager.detach(tube2.getSample());
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
      enrichmentService.insert(enrichment);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new Protocol(2L));
    Tube tube1 = entityManager.find(Tube.class, 3L);
    Tube tube2 = entityManager.find(Tube.class, 4L);
    entityManager.detach(tube1);
    entityManager.detach(tube1.getSample());
    entityManager.detach(tube2);
    entityManager.detach(tube2.getSample());
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
      enrichmentService.insert(enrichment);
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
      entityManager.persist(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    enrichmentService.insert(enrichment);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(protocolService).insert(eq(protocol));
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = enrichmentService.get(enrichment.getId());
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
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    enrichment.getTreatedSamples().stream().forEach(ts -> entityManager.detach(ts));
    enrichment.setProtocol(entityManager.find(Protocol.class, 4L));
    enrichment.getTreatedSamples().get(0).setComment("test update");
    enrichment.getTreatedSamples().get(0).setContainer(new Well(248L));
    enrichment.getTreatedSamples().get(0).setSample(new Control(444L));
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    enrichmentService.update(enrichment, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).update(eq(enrichment), eq("test explanation"));
    verify(activityService).insert(activity);
    enrichment = entityManager.find(Enrichment.class, 223L);
    assertNotNull(enrichment);
    assertEquals((Long) 4L, enrichment.getProtocol().getId());
    assertEquals((Long) 248L, enrichment.getTreatedSamples().get(0).getContainer().getId());
    assertEquals((Long) 444L, enrichment.getTreatedSamples().get(0).getSample().getId());
    assertEquals("test update", enrichment.getTreatedSamples().get(0).getComment());
  }

  @Test
  public void update_NewProtocol() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    Protocol protocol = new Protocol(null, "test protocol");
    protocol.setType(Protocol.Type.ENRICHMENT);
    enrichment.setProtocol(protocol);
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.of(activity));
    doAnswer(i -> {
      entityManager.persist(i.getArgumentAt(0, Protocol.class));
      return null;
    }).when(protocolService).insert(any());

    enrichmentService.update(enrichment, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(protocolService).insert(eq(protocol));
    verify(enrichmentActivityService).update(eq(enrichment), eq("test explanation"));
    verify(activityService).insert(activity);
    enrichment = entityManager.find(Enrichment.class, 223L);
    assertNotNull(enrichment);
    assertNotNull(enrichment.getProtocol().getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveTreatedSample() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    enrichment.getTreatedSamples().stream().forEach(ts -> entityManager.detach(ts));
    enrichment.getTreatedSamples().remove(1);
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    enrichmentService.update(enrichment, "test explanation");
  }

  @Test
  public void update_NoActivity() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.update(any(), any())).thenReturn(Optional.empty());

    enrichmentService.update(enrichment, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).update(eq(enrichment), eq("test explanation"));
    verify(activityService, never()).insert(any());
    enrichment = entityManager.find(Enrichment.class, 223L);
    assertNotNull(enrichment);
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
  }

  @Test
  public void undo_NoBan() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentService.undo(enrichment, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentService.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Ban() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentService.undo(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentService.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 800L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 812L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 800L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 812L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 225L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentService.undo(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentService.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 801L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 813L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 896L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 908L);
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
    Enrichment enrichment = entityManager.find(Enrichment.class, 226L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentService.undo(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentService.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 825L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 837L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 897L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 909L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 921L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 933L);
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
    Enrichment enrichment = entityManager.find(Enrichment.class, 227L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentService.undo(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentService.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 849L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 861L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 898L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 910L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 803L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 815L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 827L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 839L);
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
    Enrichment enrichment = entityManager.find(Enrichment.class, 228L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentService.undo(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentService.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals("fail unit test", enrichment.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 873L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 885L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 899L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 911L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 923L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 935L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 802L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 814L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 826L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 838L);
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
