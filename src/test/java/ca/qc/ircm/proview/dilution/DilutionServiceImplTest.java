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
public class DilutionServiceImplTest {
  private DilutionServiceImpl dilutionServiceImpl;
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
    dilutionServiceImpl = new DilutionServiceImpl(entityManager, queryFactory,
        dilutionActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private SampleContainer findContainer(Collection<SampleContainer> containers,
      SampleContainer.Type type, long id) {
    for (SampleContainer container : containers) {
      if (container.getId() == id && container.getType() == type) {
        return container;
      }
    }
    return null;
  }

  @Test
  public void get() {
    Dilution dilution = dilutionServiceImpl.get(4L);

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
    assertEquals(null, dilution.getDeletionJustification());
    List<DilutedSample> dilutedSamples = dilution.getTreatmentSamples();
    assertEquals(1, dilutedSamples.size());
    DilutedSample dilutedSample = dilutedSamples.get(0);
    assertEquals((Long) 442L, dilutedSample.getSample().getId());
    assertEquals(SampleContainer.Type.TUBE, dilutedSample.getContainer().getType());
    assertEquals((Long) 2L, dilutedSample.getContainer().getId());
    assertEquals(null, dilutedSample.getComments());
  }

  @Test
  public void get_Null() {
    Dilution dilution = dilutionServiceImpl.get(null);

    assertNull(dilution);
  }

  @Test
  public void all_Tube() {
    Sample sample = new SubmissionSample(442L);

    List<Dilution> dilutions = dilutionServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, dilutions.size());
    Dilution dilution = dilutions.get(0);
    assertEquals((Long) 4L, dilution.getId());
  }

  @Test
  public void all_Spot() {
    Sample sample = new SubmissionSample(569L);

    List<Dilution> dilutions = dilutionServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, dilutions.size());
    Dilution dilution = dilutions.get(0);
    assertEquals((Long) 210L, dilution.getId());
  }

  @Test
  public void all_Null() {
    List<Dilution> dilutions = dilutionServiceImpl.all(null);

    assertEquals(0, dilutions.size());
  }

  @Test
  public void insert_Tube() {
    SubmissionSample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    final List<DilutedSample> dilutedSamples = new ArrayList<DilutedSample>();
    DilutedSample dilutedSample = new DilutedSample();
    dilutedSample.setComments("unit test");
    dilutedSample.setSample(sample);
    dilutedSample.setContainer(tube);
    dilutedSample.setSourceVolume(10.0);
    dilutedSample.setSolvent("Methanol");
    dilutedSample.setSolventVolume(20.0);
    dilutedSamples.add(dilutedSample);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(dilutedSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    dilutionServiceImpl.insert(dilution);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).insert(eq(dilution));
    verify(activityService).insert(eq(activity));
    assertNotNull(dilution.getId());
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionType());
    assertEquals(null, dilution.getDeletionJustification());
    assertEquals(user, dilution.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(dilution.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(dilution.getInsertTime()));
    assertEquals(1, dilution.getTreatmentSamples().size());
    dilutedSample = dilution.getTreatmentSamples().get(0);
    assertEquals("unit test", dilutedSample.getComments());
    assertEquals((Long) 1L, dilutedSample.getSample().getId());
    assertEquals(SampleContainer.Type.TUBE, dilutedSample.getContainer().getType());
    assertEquals((Long) 1L, dilutedSample.getContainer().getId());
    assertEquals((Double) 10.0, dilutedSample.getSourceVolume());
    assertEquals("Methanol", dilutedSample.getSolvent());
    assertEquals((Double) 20.0, dilutedSample.getSolventVolume());
  }

  @Test
  public void insert_Spot() {
    SubmissionSample sample = new SubmissionSample(1L);
    PlateSpot spot = new PlateSpot(128L);
    final List<DilutedSample> dilutedSamples = new ArrayList<DilutedSample>();
    DilutedSample dilutedSample = new DilutedSample();
    dilutedSample.setComments("unit test");
    dilutedSample.setSample(sample);
    dilutedSample.setContainer(spot);
    dilutedSample.setSourceVolume(10.0);
    dilutedSample.setSolvent("Methanol");
    dilutedSample.setSolventVolume(20.0);
    dilutedSamples.add(dilutedSample);
    Dilution dilution = new Dilution();
    dilution.setTreatmentSamples(dilutedSamples);
    when(dilutionActivityService.insert(any(Dilution.class))).thenReturn(activity);

    dilutionServiceImpl.insert(dilution);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).insert(eq(dilution));
    verify(activityService).insert(eq(activity));
    assertNotNull(dilution.getId());
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertEquals(false, dilution.isDeleted());
    assertEquals(null, dilution.getDeletionType());
    assertEquals(null, dilution.getDeletionJustification());
    assertEquals(user, dilution.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(dilution.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(dilution.getInsertTime()));
    assertEquals(1, dilution.getTreatmentSamples().size());
    dilutedSample = dilution.getTreatmentSamples().get(0);
    assertEquals("unit test", dilutedSample.getComments());
    assertEquals((Long) 1L, dilutedSample.getSample().getId());
    assertEquals(SampleContainer.Type.SPOT, dilutedSample.getContainer().getType());
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

    dilutionServiceImpl.undoErroneous(dilution, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoErroneous(eq(dilution), eq("undo unit test"));
    verify(activityService).insert(eq(activity));
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, dilution.getDeletionType());
    assertEquals("undo unit test", dilution.getDeletionJustification());
  }

  @Test
  public void undoFailed_NoBan() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionServiceImpl.undoFailed(dilution, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
    assertEquals("fail unit test", dilution.getDeletionJustification());
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

    dilutionServiceImpl.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
    assertEquals("fail unit test", dilution.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 608L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 620L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 608L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 620L));
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    Dilution dilution = entityManager.find(Dilution.class, 211L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionServiceImpl.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
    assertEquals("fail unit test", dilution.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 24L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 23L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 513L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 525L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 24L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 23L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 513L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 525L));
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 213L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionServiceImpl.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 26L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 25L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 515L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 527L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 539L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 551L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 26L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 25L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 515L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 527L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 539L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 551L));
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 216L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    dilutionServiceImpl.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    dilution = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(dilution);
    assertEquals(true, dilution.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, dilution.getDeletionType());
    assertEquals("fail unit test", dilution.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 28L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 27L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 516L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 528L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 704L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 716L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 728L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 740L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 28L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 27L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 516L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 528L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 704L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 716L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 728L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 740L));
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    Dilution dilution = entityManager.find(Dilution.class, 219L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Dilution failed.
    dilutionServiceImpl.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 29L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 30L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 517L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 529L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 541L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 553L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 705L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 717L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 729L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 741L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 29L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 30L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 517L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 529L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 541L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 553L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 705L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 717L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 729L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 741L));
  }

  @Test
  public void undoFailed_NotBanErroneousFractionation() {
    Dilution dilution = entityManager.find(Dilution.class, 322L);
    entityManager.detach(dilution);
    when(dilutionActivityService.undoFailed(any(Dilution.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Dilution failed.
    dilutionServiceImpl.undoFailed(dilution, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(dilutionActivityService).undoFailed(eq(dilution), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Dilution test = dilutionServiceImpl.get(dilution.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 2278L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1583L);
    assertEquals(false, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1571L);
    assertEquals(false, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 2278L));
  }
}
