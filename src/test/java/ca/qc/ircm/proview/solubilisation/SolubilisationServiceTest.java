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
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.security.AuthorizationService;
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
public class SolubilisationServiceTest {
  private SolubilisationService solubilisationServiceImpl;
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
    solubilisationServiceImpl = new SolubilisationService(entityManager, queryFactory,
        solubilisationActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private SampleContainer findContainer(Collection<SampleContainer> containers,
      SampleContainerType type, long id) {
    for (SampleContainer container : containers) {
      if (container.getId() == id && container.getType() == type) {
        return container;
      }
    }
    return null;
  }

  @Test
  public void get() {
    Solubilisation solubilisation = solubilisationServiceImpl.get(1L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(solubilisation);
    assertEquals((Long) 1L, solubilisation.getId());
    assertEquals(Treatment.Type.SOLUBILISATION, solubilisation.getType());
    assertEquals((Long) 4L, solubilisation.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 11, 45, 0).atZone(ZoneId.systemDefault()).toInstant(),
        solubilisation.getInsertTime());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionType());
    assertEquals(null, solubilisation.getDeletionJustification());
    List<SolubilisedSample> solubilisedSamples = solubilisation.getTreatmentSamples();
    assertEquals(1, solubilisedSamples.size());
    SolubilisedSample solubilisedSample = solubilisedSamples.get(0);
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, solubilisedSample.getContainer().getType());
    assertEquals((Long) 1L, solubilisedSample.getContainer().getId());
    assertEquals(null, solubilisedSample.getComments());
    assertEquals("Methanol", solubilisedSample.getSolvent());
    assertEquals(20.0, solubilisedSample.getSolventVolume(), 0.01);
  }

  @Test
  public void get_Null() {
    Solubilisation solubilisation = solubilisationServiceImpl.get(null);

    assertNull(solubilisation);
  }

  @Test
  public void all_Tube() {
    Sample sample = new SubmissionSample(1L);

    List<Solubilisation> solubilisations = solubilisationServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, solubilisations.size());
    Solubilisation solubilisation = solubilisations.get(0);
    assertEquals((Long) 1L, solubilisation.getId());
  }

  @Test
  public void all_Spot() {
    Sample sample = new SubmissionSample(589L);

    List<Solubilisation> solubilisations = solubilisationServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, solubilisations.size());
    Solubilisation solubilisation = solubilisations.get(0);
    assertEquals((Long) 236L, solubilisation.getId());
  }

  @Test
  public void all_Null() {
    List<Solubilisation> solubilisations = solubilisationServiceImpl.all(null);

    assertEquals(0, solubilisations.size());
  }

  @Test
  public void insert_Tube() {
    final List<SolubilisedSample> solubilisedSamples = new ArrayList<SolubilisedSample>();
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    SolubilisedSample solubilisedSample = new SolubilisedSample();
    solubilisedSample.setComments("unit test");
    solubilisedSample.setSample(sample);
    solubilisedSample.setContainer(tube);
    solubilisedSample.setSolvent("Methanol");
    solubilisedSample.setSolventVolume(20.0);
    solubilisedSamples.add(solubilisedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatmentSamples(solubilisedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    solubilisationServiceImpl.insert(solubilisation);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).insert(eq(solubilisation));
    verify(activityService).insert(eq(activity));
    assertNotNull(solubilisation.getId());
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionType());
    assertEquals(null, solubilisation.getDeletionJustification());
    assertEquals(user, solubilisation.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(solubilisation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(solubilisation.getInsertTime()));
    assertEquals(1, solubilisation.getTreatmentSamples().size());
    solubilisedSample = solubilisation.getTreatmentSamples().get(0);
    assertEquals("unit test", solubilisedSample.getComments());
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, solubilisedSample.getContainer().getType());
    assertEquals((Long) 1L, solubilisedSample.getContainer().getId());
    assertEquals("Methanol", solubilisedSample.getSolvent());
    assertEquals((Double) 20.0, solubilisedSample.getSolventVolume());
  }

  @Test
  public void insert_Spot() {
    final List<SolubilisedSample> solubilisedSamples = new ArrayList<SolubilisedSample>();
    SubmissionSample sample = new SubmissionSample(1L);
    PlateSpot spot = new PlateSpot(128L);
    SolubilisedSample solubilisedSample = new SolubilisedSample();
    solubilisedSample.setComments("unit test");
    solubilisedSample.setSample(sample);
    solubilisedSample.setContainer(spot);
    solubilisedSample.setSolvent("Methanol");
    solubilisedSample.setSolventVolume(20.0);
    solubilisedSamples.add(solubilisedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatmentSamples(solubilisedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    solubilisationServiceImpl.insert(solubilisation);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).insert(eq(solubilisation));
    verify(activityService).insert(eq(activity));
    assertNotNull(solubilisation.getId());
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionType());
    assertEquals(null, solubilisation.getDeletionJustification());
    assertEquals(user, solubilisation.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(solubilisation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(solubilisation.getInsertTime()));
    assertEquals(1, solubilisation.getTreatmentSamples().size());
    solubilisedSample = solubilisation.getTreatmentSamples().get(0);
    assertEquals("unit test", solubilisedSample.getComments());
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.SPOT, solubilisedSample.getContainer().getType());
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

    solubilisationServiceImpl.undoErroneous(solubilisation, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoErroneous(eq(solubilisation), eq("undo unit test"));
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, solubilisation.getDeletionType());
    assertEquals("undo unit test", solubilisation.getDeletionJustification());
  }

  @Test
  public void undoFailed_NoBan() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationServiceImpl.undoFailed(solubilisation, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionJustification());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 236L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationServiceImpl.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 992L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1004L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 992L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1004L));
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 237L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationServiceImpl.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 44L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 43L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 993L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1005L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 44L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 43L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 993L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1005L));
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 238L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationServiceImpl.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Solubilisation test = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 45L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 46L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 994L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1006L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1018L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1030L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 45L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 46L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 994L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1006L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1018L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1030L));
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 239L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationServiceImpl.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 47L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 48L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 995L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1007L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1088L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1100L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1112L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1124L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 47L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 48L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 995L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1007L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1088L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1100L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1112L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1124L));
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 240L);
    entityManager.detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    solubilisationServiceImpl.undoFailed(solubilisation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = solubilisationServiceImpl.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, solubilisation.getDeletionType());
    assertEquals("fail unit test", solubilisation.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 49L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 50L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 996L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1008L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1020L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1032L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1089L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1101L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1113L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1125L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 49L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 50L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 996L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1008L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1020L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1032L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1089L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1101L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1113L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1125L));
  }
}
