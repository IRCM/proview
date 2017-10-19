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

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Treatment;
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
    assertEquals(Treatment.Type.DILUTION, dilution.getType());
    assertEquals((Long) 2L, dilution.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 3, 54).atZone(ZoneId.systemDefault()).toInstant(),
        dilution.getInsertTime());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionType());
    assertEquals(null, dilution.getDeletionExplanation());
    List<DilutedSample> dilutedSamples = dilution.getTreatmentSamples();
    assertEquals(1, dilutedSamples.size());
    DilutedSample dilutedSample = dilutedSamples.get(0);
    assertEquals(dilution, dilutedSample.getDilution());
    assertEquals((Long) 442L, dilutedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, dilutedSample.getContainer().getType());
    assertEquals((Long) 2L, dilutedSample.getContainer().getId());
    assertEquals(null, dilutedSample.getComment());
  }

  @Test
  public void get_Null() {
    Dilution dilution = dilutionService.get(null);

    assertNull(dilution);
  }

  @Test
  public void all() {
    Submission submission = entityManager.find(Submission.class, 149L);

    List<Dilution> dilutions = dilutionService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(5, dilutions.size());
    assertTrue(find(dilutions, 210).isPresent());
    assertTrue(find(dilutions, 211).isPresent());
    assertTrue(find(dilutions, 213).isPresent());
    assertTrue(find(dilutions, 216).isPresent());
    assertTrue(find(dilutions, 219).isPresent());
  }

  @Test
  public void all_Null() {
    List<Dilution> dilutions = dilutionService.all(null);

    assertEquals(0, dilutions.size());
  }

  @Test
  public void insert_Tube() {
    SubmissionSample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    final List<DilutedSample> dilutedSamples = new ArrayList<>();
    DilutedSample dilutedSample = new DilutedSample();
    dilutedSample.setComment("unit test");
    dilutedSample.setSample(sample);
    dilutedSample.setContainer(tube);
    dilutedSample.setSourceVolume(10.0);
    dilutedSample.setSolvent("Methanol");
    dilutedSample.setSolventVolume(20.0);
    dilutedSamples.add(dilutedSample);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(dilutedSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    dilutionService.insert(dilution);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).insert(eq(dilution));
    verify(activityService).insert(eq(activity));
    assertNotNull(dilution.getId());
    dilution = dilutionService.get(dilution.getId());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionType());
    assertEquals(null, dilution.getDeletionExplanation());
    assertEquals(user, dilution.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(dilution.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(dilution.getInsertTime()));
    assertEquals(1, dilution.getTreatmentSamples().size());
    dilutedSample = dilution.getTreatmentSamples().get(0);
    assertEquals("unit test", dilutedSample.getComment());
    assertEquals((Long) 1L, dilutedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, dilutedSample.getContainer().getType());
    assertEquals((Long) 1L, dilutedSample.getContainer().getId());
    assertEquals((Double) 10.0, dilutedSample.getSourceVolume());
    assertEquals("Methanol", dilutedSample.getSolvent());
    assertEquals((Double) 20.0, dilutedSample.getSolventVolume());
  }

  @Test
  public void insert_Well() {
    SubmissionSample sample = new SubmissionSample(1L);
    Well well = new Well(128L);
    final List<DilutedSample> dilutedSamples = new ArrayList<>();
    DilutedSample dilutedSample = new DilutedSample();
    dilutedSample.setComment("unit test");
    dilutedSample.setSample(sample);
    dilutedSample.setContainer(well);
    dilutedSample.setSourceVolume(10.0);
    dilutedSample.setSolvent("Methanol");
    dilutedSample.setSolventVolume(20.0);
    dilutedSamples.add(dilutedSample);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(dilutedSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    dilutionService.insert(dilution);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).insert(eq(dilution));
    verify(activityService).insert(eq(activity));
    assertNotNull(dilution.getId());
    dilution = dilutionService.get(dilution.getId());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionType());
    assertEquals(null, dilution.getDeletionExplanation());
    assertEquals(user, dilution.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(dilution.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(dilution.getInsertTime()));
    assertEquals(1, dilution.getTreatmentSamples().size());
    dilutedSample = dilution.getTreatmentSamples().get(0);
    assertEquals("unit test", dilutedSample.getComment());
    assertEquals((Long) 1L, dilutedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, dilutedSample.getContainer().getType());
    assertEquals((Long) 128L, dilutedSample.getContainer().getId());
    assertEquals((Double) 10.0, dilutedSample.getSourceVolume());
    assertEquals("Methanol", dilutedSample.getSolvent());
    assertEquals((Double) 20.0, dilutedSample.getSolventVolume());
  }

  @Test
  public void undoErroneous() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoErroneous(any(Dilution.class), any(String.class)))
        .thenReturn(activity);

    dilutionService.undoErroneous(dilution, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoErroneous(eq(dilution), eq("undo unit test"));
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, dilution.getDeletionType());
    assertEquals("undo unit test", dilution.getDeletionExplanation());
  }

  @Test
  public void undoFailed_NoBan() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undoFailed(dilution, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
    assertEquals("fail unit test", dilution.getDeletionExplanation());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
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
  public void undoFailed_Ban_Transfer() {
    Dilution dilution = entityManager.find(Dilution.class, 211L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
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
  public void undoFailed_Ban_Fractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 213L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionService.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
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
  public void undoFailed_Ban_Transfer_Fractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 216L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionService.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionService.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
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
  public void undoFailed_Ban_Fractionation_Transfer() {
    Dilution dilution = entityManager.find(Dilution.class, 219L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Dilution failed.
    dilutionService.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionService.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
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
  public void undoFailed_NotBanErroneousFractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 322L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Dilution failed.
    dilutionService.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionService.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
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
