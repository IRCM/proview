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

package ca.qc.ircm.proview.dilution;

import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatmentSample;
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
public class DilutionServiceTest {
  private DilutionService dilutionService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private DilutionActivityService dilutionActivityService;
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
    dilutionService = new DilutionService(entityManager, queryFactory, dilutionActivityService,
        activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Dilution dilution = dilutionService.get(4L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(dilution);
    assertEquals((Long) 4L, dilution.getId());
    assertEquals(TreatmentType.DILUTION, dilution.getType());
    assertEquals((Long) 2L, dilution.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 3, 54).atZone(ZoneId.systemDefault()).toInstant(),
        dilution.getInsertTime());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionExplanation());
    List<TreatmentSample> treatmentSamples = dilution.getTreatmentSamples();
    assertEquals(1, treatmentSamples.size());
    TreatmentSample treatmentSample = treatmentSamples.get(0);
    assertEquals(dilution, treatmentSample.getTreatment());
    assertEquals((Long) 442L, treatmentSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatmentSample.getContainer().getType());
    assertEquals((Long) 2L, treatmentSample.getContainer().getId());
    assertEquals(null, treatmentSample.getComment());
  }

  @Test
  public void get_Null() {
    Dilution dilution = dilutionService.get(null);

    assertNull(dilution);
  }

  @Test
  public void insert_Tube() {
    SubmissionSample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    final List<TreatmentSample> treatmentSamples = new ArrayList<>();
    TreatmentSample treatmentSample = new TreatmentSample();
    treatmentSample.setComment("unit test");
    treatmentSample.setSample(sample);
    treatmentSample.setContainer(tube);
    treatmentSample.setSourceVolume(10.0);
    treatmentSample.setSolvent("Methanol");
    treatmentSample.setSolventVolume(20.0);
    treatmentSamples.add(treatmentSample);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(treatmentSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    dilutionService.insert(dilution);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).insert(eq(dilution));
    verify(activityService).insert(eq(activity));
    assertNotNull(dilution.getId());
    dilution = dilutionService.get(dilution.getId());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionExplanation());
    assertEquals(user, dilution.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(dilution.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(dilution.getInsertTime()));
    assertEquals(1, dilution.getTreatmentSamples().size());
    treatmentSample = dilution.getTreatmentSamples().get(0);
    assertEquals("unit test", treatmentSample.getComment());
    assertEquals((Long) 1L, treatmentSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatmentSample.getContainer().getType());
    assertEquals((Long) 1L, treatmentSample.getContainer().getId());
    assertEquals((Double) 10.0, treatmentSample.getSourceVolume());
    assertEquals("Methanol", treatmentSample.getSolvent());
    assertEquals((Double) 20.0, treatmentSample.getSolventVolume());
  }

  @Test
  public void insert_Well() {
    SubmissionSample sample = new SubmissionSample(1L);
    Well well = new Well(128L);
    final List<TreatmentSample> treatmentSamples = new ArrayList<>();
    TreatmentSample treatmentSample = new TreatmentSample();
    treatmentSample.setComment("unit test");
    treatmentSample.setSample(sample);
    treatmentSample.setContainer(well);
    treatmentSample.setSourceVolume(10.0);
    treatmentSample.setSolvent("Methanol");
    treatmentSample.setSolventVolume(20.0);
    treatmentSamples.add(treatmentSample);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(treatmentSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    dilutionService.insert(dilution);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).insert(eq(dilution));
    verify(activityService).insert(eq(activity));
    assertNotNull(dilution.getId());
    dilution = dilutionService.get(dilution.getId());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionExplanation());
    assertEquals(user, dilution.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(dilution.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(dilution.getInsertTime()));
    assertEquals(1, dilution.getTreatmentSamples().size());
    treatmentSample = dilution.getTreatmentSamples().get(0);
    assertEquals("unit test", treatmentSample.getComment());
    assertEquals((Long) 1L, treatmentSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, treatmentSample.getContainer().getType());
    assertEquals((Long) 128L, treatmentSample.getContainer().getId());
    assertEquals((Double) 10.0, treatmentSample.getSourceVolume());
    assertEquals("Methanol", treatmentSample.getSolvent());
    assertEquals((Double) 20.0, treatmentSample.getSolventVolume());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    final Tube tube1 = entityManager.find(Tube.class, 3L);
    final Tube tube2 = entityManager.find(Tube.class, 8L);
    final List<TreatmentSample> treatmentSamples = new ArrayList<>();
    TreatmentSample treatmentSample1 = new TreatmentSample();
    treatmentSample1.setComment("unit test");
    treatmentSample1.setSample(tube1.getSample());
    treatmentSample1.setContainer(tube1);
    treatmentSample1.setSourceVolume(10.0);
    treatmentSample1.setSolvent("Methanol");
    treatmentSample1.setSolventVolume(20.0);
    treatmentSamples.add(treatmentSample1);
    TreatmentSample treatmentSample2 = new TreatmentSample();
    treatmentSample2.setComment("unit test");
    treatmentSample2.setSample(tube2.getSample());
    treatmentSample2.setContainer(tube2);
    treatmentSample2.setSourceVolume(10.0);
    treatmentSample2.setSolvent("Methanol");
    treatmentSample2.setSolventVolume(20.0);
    treatmentSamples.add(treatmentSample2);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(treatmentSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    try {
      dilutionService.insert(dilution);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    final Tube tube1 = entityManager.find(Tube.class, 3L);
    final Tube tube2 = entityManager.find(Tube.class, 4L);
    final List<TreatmentSample> treatmentSamples = new ArrayList<>();
    TreatmentSample treatmentSample1 = new TreatmentSample();
    treatmentSample1.setComment("unit test");
    treatmentSample1.setSample(tube1.getSample());
    treatmentSample1.setContainer(tube1);
    treatmentSample1.setSourceVolume(10.0);
    treatmentSample1.setSolvent("Methanol");
    treatmentSample1.setSolventVolume(20.0);
    treatmentSamples.add(treatmentSample1);
    TreatmentSample treatmentSample2 = new TreatmentSample();
    treatmentSample2.setComment("unit test");
    treatmentSample2.setSample(tube2.getSample());
    treatmentSample2.setContainer(tube2);
    treatmentSample2.setSourceVolume(10.0);
    treatmentSample2.setSolvent("Methanol");
    treatmentSample2.setSolventVolume(20.0);
    treatmentSamples.add(treatmentSample2);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(treatmentSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    try {
      dilutionService.insert(dilution);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void update() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    dilution.getTreatmentSamples().stream().forEach(ts -> entityManager.detach(ts));
    dilution.getTreatmentSamples().get(0).setSourceVolume(3.5);
    dilution.getTreatmentSamples().get(0).setSolvent("ch3oh");
    dilution.getTreatmentSamples().get(0).setSolventVolume(7.0);
    dilution.getTreatmentSamples().get(0).setComment("test update");
    dilution.getTreatmentSamples().get(0).setContainer(new Well(248L));
    dilution.getTreatmentSamples().get(0).setSample(new Control(444L));
    when(dilutionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    dilutionService.update(dilution, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).update(eq(dilution), eq("test explanation"));
    verify(activityService).insert(activity);
    dilution = entityManager.find(Dilution.class, 210L);
    assertNotNull(dilution);
    assertEquals((Long) 248L, dilution.getTreatmentSamples().get(0).getContainer().getId());
    assertEquals((Long) 444L, dilution.getTreatmentSamples().get(0).getSample().getId());
    assertEquals(3.5, dilution.getTreatmentSamples().get(0).getSourceVolume(), 0.0001);
    assertEquals("ch3oh", dilution.getTreatmentSamples().get(0).getSolvent());
    assertEquals(7.0, dilution.getTreatmentSamples().get(0).getSolventVolume(), 0.0001);
    assertEquals("test update", dilution.getTreatmentSamples().get(0).getComment());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveTreatmentSample() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    dilution.getTreatmentSamples().stream().forEach(ts -> entityManager.detach(ts));
    dilution.getTreatmentSamples().remove(1);
    when(dilutionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    dilutionService.update(dilution, "test explanation");
  }

  @Test
  public void undo_NoBan() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undo(dilution, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals("fail unit test", dilution.getDeletionExplanation());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Ban() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undo(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals("fail unit test", dilution.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 608L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 620L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 608L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 620L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer() {
    Dilution dilution = entityManager.find(Dilution.class, 211L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undo(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals("fail unit test", dilution.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 24L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 23L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 513L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 525L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 24L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 23L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 513L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 525L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 213L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undo(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionService.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 26L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 25L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 515L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 527L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 539L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 551L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 26L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 25L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 515L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 527L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 539L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 551L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer_Fractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 216L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undo(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals("fail unit test", dilution.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 28L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 27L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 516L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 528L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 704L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 716L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 728L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 740L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 28L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 27L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 516L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 528L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 704L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 716L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 728L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 740L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation_Transfer() {
    Dilution dilution = entityManager.find(Dilution.class, 219L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Dilution failed.
    dilutionService.undo(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionService.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 29L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 30L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 517L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 529L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 541L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 553L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 705L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 717L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 729L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 741L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 29L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 30L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 517L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 529L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 541L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 553L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 705L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 717L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 729L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 741L).isPresent());
  }

  @Test
  public void undo_NotBanErroneousFractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 322L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Dilution failed.
    dilutionService.undo(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionService.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 2278L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1583L);
    assertEquals(false, well.isBanned());
    well = entityManager.find(Well.class, 1571L);
    assertEquals(false, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 2278L).isPresent());
  }
}
