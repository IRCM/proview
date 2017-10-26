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

package ca.qc.ircm.proview.solubilisation;

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
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Treatment;
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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SolubilisationServiceTest {
  private SolubilisationService solubilisationService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private SolubilisationActivityService solubilisationActivityService;
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
    solubilisationService = new SolubilisationService(entityManager, queryFactory,
        solubilisationActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Solubilisation solubilisation = solubilisationService.get(1L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(solubilisation);
    assertEquals((Long) 1L, solubilisation.getId());
    assertEquals(TreatmentType.SOLUBILISATION, solubilisation.getType());
    assertEquals((Long) 4L, solubilisation.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 11, 45, 0).atZone(ZoneId.systemDefault()).toInstant(),
        solubilisation.getInsertTime());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionType());
    assertEquals(null, solubilisation.getDeletionExplanation());
    List<SolubilisedSample> solubilisedSamples = solubilisation.getTreatmentSamples();
    assertEquals(1, solubilisedSamples.size());
    SolubilisedSample solubilisedSample = solubilisedSamples.get(0);
    assertEquals(solubilisation, solubilisedSample.getSolubilisation());
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, solubilisedSample.getContainer().getType());
    assertEquals((Long) 1L, solubilisedSample.getContainer().getId());
    assertEquals(null, solubilisedSample.getComment());
    assertEquals("Methanol", solubilisedSample.getSolvent());
    assertEquals(20.0, solubilisedSample.getSolventVolume(), 0.01);
  }

  @Test
  public void get_Null() {
    Solubilisation solubilisation = solubilisationService.get(null);

    assertNull(solubilisation);
  }

  @Test
  public void insert_Tube() {
    final List<SolubilisedSample> solubilisedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    SolubilisedSample solubilisedSample = new SolubilisedSample();
    solubilisedSample.setComment("unit test");
    solubilisedSample.setSample(sample);
    solubilisedSample.setContainer(tube);
    solubilisedSample.setSolvent("Methanol");
    solubilisedSample.setSolventVolume(20.0);
    solubilisedSamples.add(solubilisedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatmentSamples(solubilisedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    solubilisationService.insert(solubilisation);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).insert(eq(solubilisation));
    verify(activityService).insert(eq(activity));
    assertNotNull(solubilisation.getId());
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionType());
    assertEquals(null, solubilisation.getDeletionExplanation());
    assertEquals(user, solubilisation.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(solubilisation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(solubilisation.getInsertTime()));
    assertEquals(1, solubilisation.getTreatmentSamples().size());
    solubilisedSample = solubilisation.getTreatmentSamples().get(0);
    assertEquals("unit test", solubilisedSample.getComment());
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, solubilisedSample.getContainer().getType());
    assertEquals((Long) 1L, solubilisedSample.getContainer().getId());
    assertEquals("Methanol", solubilisedSample.getSolvent());
    assertEquals((Double) 20.0, solubilisedSample.getSolventVolume());
  }

  @Test
  public void insert_Well() {
    final List<SolubilisedSample> solubilisedSamples = new ArrayList<>();
    SubmissionSample sample = new SubmissionSample(1L);
    Well well = new Well(128L);
    SolubilisedSample solubilisedSample = new SolubilisedSample();
    solubilisedSample.setComment("unit test");
    solubilisedSample.setSample(sample);
    solubilisedSample.setContainer(well);
    solubilisedSample.setSolvent("Methanol");
    solubilisedSample.setSolventVolume(20.0);
    solubilisedSamples.add(solubilisedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatmentSamples(solubilisedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    solubilisationService.insert(solubilisation);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).insert(eq(solubilisation));
    verify(activityService).insert(eq(activity));
    assertNotNull(solubilisation.getId());
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionType());
    assertEquals(null, solubilisation.getDeletionExplanation());
    assertEquals(user, solubilisation.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(solubilisation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(solubilisation.getInsertTime()));
    assertEquals(1, solubilisation.getTreatmentSamples().size());
    solubilisedSample = solubilisation.getTreatmentSamples().get(0);
    assertEquals("unit test", solubilisedSample.getComment());
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, solubilisedSample.getContainer().getType());
    assertEquals((Long) 128L, solubilisedSample.getContainer().getId());
    assertEquals("Methanol", solubilisedSample.getSolvent());
    assertEquals((Double) 20.0, solubilisedSample.getSolventVolume());
  }

  @Test
  public void undoErroneous() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoErroneous(any(Solubilisation.class), any(String.class)))
        .thenReturn(activity);

    solubilisationService.undoErroneous(solubilisation, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoErroneous(eq(solubilisation), eq("undo unit test"));
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, solubilisation.getDeletionType());
    assertEquals("undo unit test", solubilisation.getDeletionExplanation());
  }

  @Test
  public void undoFailed_NoBan() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationService.undoFailed(solubilisation, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 236L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationService.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 992L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1004L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 992L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1004L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 237L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationService.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 44L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 43L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 993L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1005L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 44L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 43L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 993L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1005L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 238L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationService.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Solubilisation test = solubilisationService.get(solubilisation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 45L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 46L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 994L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1006L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1018L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1030L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 45L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 46L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 994L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1006L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1018L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1030L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 239L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationService.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 47L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 48L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 995L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1007L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1088L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1100L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1112L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1124L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 47L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 48L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 995L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1007L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1088L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1100L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1112L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1124L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 240L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationService.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationService.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 49L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 50L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 996L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1008L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1020L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1032L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1089L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1101L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1113L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1125L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 49L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 50L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 996L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1008L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1020L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1032L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1089L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1101L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1113L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1125L).isPresent());
  }
}
