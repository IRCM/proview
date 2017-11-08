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

package ca.qc.ircm.proview.standard;

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
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
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
public class StandardAdditionServiceTest {
  private StandardAdditionService standardAdditionService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private StandardAdditionActivityService standardAdditionActivityService;
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
    standardAdditionService = new StandardAdditionService(entityManager, queryFactory,
        standardAdditionActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    StandardAddition standardAddition = standardAdditionService.get(5L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(standardAddition);
    assertEquals((Long) 5L, standardAddition.getId());
    assertEquals(TreatmentType.STANDARD_ADDITION, standardAddition.getType());
    assertEquals((Long) 2L, standardAddition.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 12, 2).atZone(ZoneId.systemDefault()).toInstant(),
        standardAddition.getInsertTime());
    assertEquals(false, standardAddition.isDeleted());
    assertEquals(null, standardAddition.getDeletionExplanation());
    List<AddedStandard> addedStandards = standardAddition.getTreatmentSamples();
    assertEquals(1, addedStandards.size());
    AddedStandard addedStandard = addedStandards.get(0);
    assertEquals(standardAddition, addedStandard.getStandardAddition());
    assertEquals((Long) 444L, addedStandard.getSample().getId());
    assertEquals(SampleContainerType.TUBE, addedStandard.getContainer().getType());
    assertEquals((Long) 4L, addedStandard.getContainer().getId());
    assertEquals(null, addedStandard.getComment());
    assertEquals("unit_test_added_standard", addedStandard.getName());
    assertEquals("20.0 μg", addedStandard.getQuantity());
  }

  @Test
  public void get_Null() {
    StandardAddition standardAddition = standardAdditionService.get(null);

    assertNull(standardAddition);
  }

  @Test
  public void insert_Tube() {
    final List<AddedStandard> addedStandards = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    AddedStandard addedStandard = new AddedStandard();
    addedStandard.setComment("unit test");
    addedStandard.setSample(sample);
    addedStandard.setContainer(tube);
    addedStandard.setName("unit_test_added_standard");
    addedStandard.setQuantity("20.0 μg");
    addedStandards.add(addedStandard);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setTreatmentSamples(addedStandards);
    when(standardAdditionActivityService.insert(any(StandardAddition.class))).thenReturn(activity);

    standardAdditionService.insert(standardAddition);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).insert(eq(standardAddition));
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(false, standardAddition.isDeleted());
    assertEquals(null, standardAddition.getDeletionExplanation());
    assertEquals(user, standardAddition.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(standardAddition.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(standardAddition.getInsertTime()));
    assertEquals(1, standardAddition.getTreatmentSamples().size());
    addedStandard = standardAddition.getTreatmentSamples().get(0);
    assertEquals("unit test", addedStandard.getComment());
    assertEquals((Long) 1L, addedStandard.getSample().getId());
    assertEquals(SampleContainerType.TUBE, addedStandard.getContainer().getType());
    assertEquals((Long) 1L, addedStandard.getContainer().getId());
    assertEquals("unit_test_added_standard", addedStandard.getName());
    assertEquals("20.0 μg", addedStandard.getQuantity());
  }

  @Test
  public void insert_Well() {
    final List<AddedStandard> addedStandards = new ArrayList<>();
    SubmissionSample sample = new SubmissionSample(1L);
    Well well = new Well(128L);
    AddedStandard addedStandard = new AddedStandard();
    addedStandard.setComment("unit test");
    addedStandard.setSample(sample);
    addedStandard.setContainer(well);
    addedStandard.setName("unit_test_added_standard");
    addedStandard.setQuantity("20.0 μg");
    addedStandards.add(addedStandard);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setTreatmentSamples(addedStandards);
    when(standardAdditionActivityService.insert(any(StandardAddition.class))).thenReturn(activity);

    standardAdditionService.insert(standardAddition);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).insert(eq(standardAddition));
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(false, standardAddition.isDeleted());
    assertEquals(null, standardAddition.getDeletionExplanation());
    assertEquals(user, standardAddition.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(standardAddition.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(standardAddition.getInsertTime()));
    assertEquals(1, standardAddition.getTreatmentSamples().size());
    addedStandard = standardAddition.getTreatmentSamples().get(0);
    assertEquals("unit test", addedStandard.getComment());
    assertEquals((Long) 1L, addedStandard.getSample().getId());
    assertEquals(SampleContainerType.WELL, addedStandard.getContainer().getType());
    assertEquals((Long) 128L, addedStandard.getContainer().getId());
    assertEquals("unit_test_added_standard", addedStandard.getName());
    assertEquals("20.0 μg", addedStandard.getQuantity());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    final List<AddedStandard> addedStandards = new ArrayList<>();
    final Tube tube1 = entityManager.find(Tube.class, 3L);
    final Tube tube2 = entityManager.find(Tube.class, 8L);
    AddedStandard addedStandard1 = new AddedStandard();
    addedStandard1.setComment("unit test");
    addedStandard1.setSample(tube1.getSample());
    addedStandard1.setContainer(tube1);
    addedStandard1.setName("unit_test_added_standard");
    addedStandard1.setQuantity("20.0 μg");
    addedStandards.add(addedStandard1);
    AddedStandard addedStandard2 = new AddedStandard();
    addedStandard2.setComment("unit test");
    addedStandard2.setSample(tube2.getSample());
    addedStandard2.setContainer(tube2);
    addedStandard2.setName("unit_test_added_standard");
    addedStandard2.setQuantity("20.0 μg");
    addedStandards.add(addedStandard2);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setTreatmentSamples(addedStandards);
    when(standardAdditionActivityService.insert(any(StandardAddition.class))).thenReturn(activity);

    try {
      standardAdditionService.insert(standardAddition);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    final List<AddedStandard> addedStandards = new ArrayList<>();
    final Tube tube1 = entityManager.find(Tube.class, 3L);
    final Tube tube2 = entityManager.find(Tube.class, 4L);
    AddedStandard addedStandard1 = new AddedStandard();
    addedStandard1.setComment("unit test");
    addedStandard1.setSample(tube1.getSample());
    addedStandard1.setContainer(tube1);
    addedStandard1.setName("unit_test_added_standard");
    addedStandard1.setQuantity("20.0 μg");
    addedStandards.add(addedStandard1);
    AddedStandard addedStandard2 = new AddedStandard();
    addedStandard2.setComment("unit test");
    addedStandard2.setSample(tube2.getSample());
    addedStandard2.setContainer(tube2);
    addedStandard2.setName("unit_test_added_standard");
    addedStandard2.setQuantity("20.0 μg");
    addedStandards.add(addedStandard2);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setTreatmentSamples(addedStandards);
    when(standardAdditionActivityService.insert(any(StandardAddition.class))).thenReturn(activity);

    try {
      standardAdditionService.insert(standardAddition);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void update() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 248L);
    entityManager.detach(standardAddition);
    standardAddition.getTreatmentSamples().stream().forEach(ts -> entityManager.detach(ts));
    standardAddition.getTreatmentSamples().get(0).setName("std1");
    standardAddition.getTreatmentSamples().get(0).setQuantity("10 μg");
    standardAddition.getTreatmentSamples().get(0).setComment("test update");
    standardAddition.getTreatmentSamples().get(0).setContainer(new Well(248L));
    standardAddition.getTreatmentSamples().get(0).setSample(new Control(444L));
    when(standardAdditionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    standardAdditionService.update(standardAddition, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).update(eq(standardAddition), eq("test explanation"));
    verify(activityService).insert(activity);
    standardAddition = entityManager.find(StandardAddition.class, 248L);
    assertNotNull(standardAddition);
    assertEquals((Long) 248L, standardAddition.getTreatmentSamples().get(0).getContainer().getId());
    assertEquals((Long) 444L, standardAddition.getTreatmentSamples().get(0).getSample().getId());
    assertEquals("std1", standardAddition.getTreatmentSamples().get(0).getName());
    assertEquals("10 μg", standardAddition.getTreatmentSamples().get(0).getQuantity());
    assertEquals("test update", standardAddition.getTreatmentSamples().get(0).getComment());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveAddedStandard() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 248L);
    entityManager.detach(standardAddition);
    standardAddition.getTreatmentSamples().stream().forEach(ts -> entityManager.detach(ts));
    standardAddition.getTreatmentSamples().remove(1);
    when(standardAdditionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    standardAdditionService.update(standardAddition, "test explanation");
  }

  @Test
  public void undo_NoBan() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionService.undo(standardAddition, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals("fail unit test", standardAddition.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Ban() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 248L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionService.undo(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals("fail unit test", standardAddition.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 997L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1009L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 997L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1009L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 249L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionService.undo(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals("fail unit test", standardAddition.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 53L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 54L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 998L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1010L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 53L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 54L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 998L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1010L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 250L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionService.undo(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    StandardAddition test = standardAdditionService.get(standardAddition.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 55L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 56L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 999L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1011L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1023L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1035L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 55L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 56L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 999L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1011L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1023L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1035L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer_Fractionation() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 251L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionService.undo(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals("fail unit test", standardAddition.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 57L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 58L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1000L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1012L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1090L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1102L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1114L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1126L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 57L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 58L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1000L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1012L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1090L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1102L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1114L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1126L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation_Transfer() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 252L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionService.undo(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionService.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals("fail unit test", standardAddition.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 60L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 59L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1001L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1013L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1025L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1037L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1091L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1103L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1115L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1127L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 60L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 59L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1001L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1013L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1025L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1037L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1091L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1103L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1115L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1127L).isPresent());
  }
}
