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

package ca.qc.ircm.proview.fractionation;

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
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.DestinationUsedInTreatmentException;
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
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class FractionationServiceTest {
  private FractionationService fractionationServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private FractionationActivityService fractionationActivityService;
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
    fractionationServiceImpl = new FractionationService(entityManager, queryFactory,
        fractionationActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private Optional<Fractionation> findFractionation(Collection<Fractionation> fractionations,
      long id) {
    return fractionations.stream().filter(f -> f.getId() == id).findFirst();
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
    Fractionation fractionation = fractionationServiceImpl.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, fractionation.getId());
    assertEquals(Treatment.Type.FRACTIONATION, fractionation.getType());
    assertEquals(Fractionation.FractionationType.MUDPIT, fractionation.getFractionationType());
    assertEquals((Long) 4L, fractionation.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 12, 20, 33, 0).atZone(ZoneId.systemDefault()).toInstant(),
        fractionation.getInsertTime());
    assertEquals(false, fractionation.isDeleted());
    assertEquals(null, fractionation.getDeletionType());
    assertEquals(null, fractionation.getDeletionJustification());
    FractionationDetail fractionationDetail = fractionation.getTreatmentSamples().get(0);
    assertEquals((Long) 2L, fractionationDetail.getId());
    assertEquals(SampleContainerType.TUBE, fractionationDetail.getContainer().getType());
    assertEquals((Long) 1L, fractionationDetail.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, fractionationDetail.getDestinationContainer().getType());
    assertEquals((Long) 6L, fractionationDetail.getDestinationContainer().getId());
    assertEquals(null, fractionationDetail.getComments());
    assertEquals((Integer) 1, fractionationDetail.getPosition());
    assertEquals((Integer) 1, fractionationDetail.getNumber());
    assertEquals(null, fractionationDetail.getPiInterval());
  }

  @Test
  public void get_Null() {
    Fractionation fractionation = fractionationServiceImpl.get(null);

    assertNull(fractionation);
  }

  @Test
  public void find() {
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(6L);
    tube.setSample(sample);

    FractionationDetail detail = fractionationServiceImpl.find(tube);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(detail);
    assertEquals((Long) 2L, detail.getId());
    assertEquals("FAM119A_band_01.F1", detail.getName());
  }

  @Test
  public void find_None() {
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    tube.setSample(sample);

    FractionationDetail detail = fractionationServiceImpl.find(tube);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNull(detail);
  }

  @Test
  public void find_Null() {
    FractionationDetail detail = fractionationServiceImpl.find(null);

    assertNull(detail);
  }

  @Test
  public void all() {
    Sample sample = new SubmissionSample(1L);

    List<Fractionation> fractionations = fractionationServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, fractionations.size());
    assertTrue(findFractionation(fractionations, 2).isPresent());
    assertTrue(findFractionation(fractionations, 8).isPresent());
  }

  @Test
  public void all_Null() {
    List<Fractionation> fractionations = fractionationServiceImpl.all(null);

    assertEquals(0, fractionations.size());
  }

  @Test
  public void insert_Tube() {
    Fractionation fractionation = new Fractionation();
    fractionation.setFractionationType(Fractionation.FractionationType.MUDPIT);
    final List<FractionationDetail> fractionationDetails = new ArrayList<>();
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Tube sourceTube = new Tube(1L);
    Tube destinationTube = new Tube();
    destinationTube.setSample(sample);
    destinationTube.setName("unit_test_tube_" + sample.getName());
    FractionationDetail fractionationDetail = new FractionationDetail();
    fractionationDetail.setSample(sample);
    fractionationDetail.setContainer(sourceTube);
    fractionationDetail.setDestinationContainer(destinationTube);
    fractionationDetails.add(fractionationDetail);
    fractionation.setTreatmentSamples(fractionationDetails);

    try {
      fractionationServiceImpl.insert(fractionation);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }

  @Test
  public void insert_Spot() {
    final List<FractionationDetail> fractionationDetails = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    final Tube sourceTube = new Tube(1L);
    PlateSpot destinationSpot1 = new PlateSpot(134L);
    PlateSpot destinationSpot2 = new PlateSpot(135L);
    destinationSpot1.setSample(sample);
    destinationSpot2.setSample(sample);
    FractionationDetail fractionationDetail = new FractionationDetail();
    fractionationDetail.setSample(sample);
    fractionationDetail.setContainer(sourceTube);
    fractionationDetail.setDestinationContainer(destinationSpot1);
    fractionationDetail.setNumber(1);
    fractionationDetails.add(fractionationDetail);
    fractionationDetail = new FractionationDetail();
    fractionationDetail.setSample(sample);
    fractionationDetail.setContainer(sourceTube);
    fractionationDetail.setDestinationContainer(destinationSpot2);
    fractionationDetail.setNumber(2);
    fractionationDetails.add(fractionationDetail);
    Fractionation fractionation = new Fractionation();
    fractionation.setTreatmentSamples(fractionationDetails);
    when(fractionationActivityService.insert(any(Fractionation.class))).thenReturn(activity);

    fractionationServiceImpl.insert(fractionation);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).insert(eq(fractionation));
    verify(activityService).insert(eq(activity));
    fractionation = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(false, fractionation.isDeleted());
    assertEquals(null, fractionation.getDeletionType());
    assertEquals(null, fractionation.getDeletionJustification());
    assertEquals(user, fractionation.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(fractionation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(fractionation.getInsertTime()));
    assertEquals(2, fractionation.getTreatmentSamples().size());
    fractionationDetail = fractionation.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, fractionationDetail.getSample().getId());
    assertEquals(SampleContainerType.TUBE, fractionationDetail.getContainer().getType());
    assertEquals((Long) 1L, fractionationDetail.getContainer().getId());
    assertEquals(SampleContainerType.SPOT, fractionationDetail.getDestinationContainer().getType());
    assertEquals((Long) 134L, fractionationDetail.getDestinationContainer().getId());
    assertEquals(fractionationDetail.getId(),
        fractionationDetail.getDestinationContainer().getTreatmentSample().getId());
    assertEquals((Integer) 1, fractionationDetail.getNumber());
    assertEquals((Integer) 3, fractionationDetail.getPosition());
    fractionationDetail = fractionation.getTreatmentSamples().get(1);
    assertEquals((Long) 1L, fractionationDetail.getSample().getId());
    assertEquals(SampleContainerType.TUBE, fractionationDetail.getContainer().getType());
    assertEquals((Long) 1L, fractionationDetail.getContainer().getId());
    assertEquals(SampleContainerType.SPOT, fractionationDetail.getDestinationContainer().getType());
    assertEquals((Long) 135L, fractionationDetail.getDestinationContainer().getId());
    assertEquals(fractionationDetail.getId(),
        fractionationDetail.getDestinationContainer().getTreatmentSample().getId());
    assertEquals((Integer) 2, fractionationDetail.getNumber());
    assertEquals((Integer) 4, fractionationDetail.getPosition());
    assertTrue(before.isBefore(fractionationDetail.getDestinationContainer().getTimestamp()));
    assertTrue(after.isAfter(fractionationDetail.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void undoErroneous_SpotDestination() throws Throwable {
    Fractionation fractionation = entityManager.find(Fractionation.class, 8L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoErroneous(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoErroneous(fractionation, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoErroneous(eq(fractionation), eq("undo unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, fractionation.getDeletionType());
    assertEquals("undo unit test", fractionation.getDeletionJustification());
    Tube sourceTube = entityManager.find(Tube.class, 1L);
    assertEquals((Long) 1L, sourceTube.getSample().getId());
    PlateSpot destinationSpot = entityManager.find(PlateSpot.class, 128L);
    assertNull(destinationSpot.getSample());
    assertNull(destinationSpot.getTreatmentSample());
    Collection<SampleContainer> samplesRemoved = containersCaptor.getValue();
    assertEquals(1, samplesRemoved.size());
    assertNotNull(findContainer(samplesRemoved, SampleContainerType.SPOT, 128L));
  }

  @Test
  public void undoErroneous_UsedContainer_SpotDestination_Enrichment() throws Throwable {
    Fractionation fractionation = entityManager.find(Fractionation.class, 285L);
    entityManager.detach(fractionation);

    try {
      fractionationServiceImpl.undoErroneous(fractionation, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(2, e.containers.size());
      assertNotNull(findContainer(e.containers, SampleContainerType.SPOT, 1280L));
      assertNotNull(findContainer(e.containers, SampleContainerType.SPOT, 1292L));
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoErroneous_UsedContainer_SpotDestination_MsAnalysis() throws Throwable {
    Fractionation fractionation = entityManager.find(Fractionation.class, 286L);
    entityManager.detach(fractionation);

    try {
      fractionationServiceImpl.undoErroneous(fractionation, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(2, e.containers.size());
      assertNotNull(findContainer(e.containers, SampleContainerType.SPOT, 1281L));
      assertNotNull(findContainer(e.containers, SampleContainerType.SPOT, 1293L));
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoFailed_NoBan_Spot() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 8L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoFailed(fractionation, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionJustification());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban_SpotDestination() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 8L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Fractionation test = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube sourceTube = entityManager.find(Tube.class, 1L);
    assertEquals(false, sourceTube.isBanned());
    PlateSpot destinationSpot = entityManager.find(PlateSpot.class, 128L);
    assertEquals(true, destinationSpot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 1L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 128L));
  }

  @Test
  public void undoFailed_Ban_SpotDestination_Transfer() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 288L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionJustification());
    Tube sourceTube = entityManager.find(Tube.class, 81L);
    assertEquals(false, sourceTube.isBanned());
    PlateSpot destinationSpot = entityManager.find(PlateSpot.class, 1282L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1294L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1376L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1388L);
    assertEquals(true, destinationSpot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 81L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1282L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1294L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1376L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1388L));
  }

  @Test
  public void undoFailed_Ban_SpotDestination_Fractionation() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 289L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionJustification());
    Tube sourceTube = entityManager.find(Tube.class, 82L);
    assertEquals(false, sourceTube.isBanned());
    PlateSpot destinationSpot = entityManager.find(PlateSpot.class, 1283L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1295L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1378L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1390L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1402L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1414L);
    assertEquals(true, destinationSpot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 82L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1283L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1295L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1378L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1390L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1402L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1414L));
  }

  @Test
  public void undoFailed_Ban_SpotDestination_Transfer_Fractionation() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 290L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionJustification());
    Tube sourceTube = entityManager.find(Tube.class, 83L);
    assertEquals(false, sourceTube.isBanned());
    PlateSpot destinationSpot = entityManager.find(PlateSpot.class, 1284L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1296L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1377L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1389L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1328L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1340L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1352L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1364L);
    assertEquals(true, destinationSpot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 83L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1284L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1296L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1377L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1389L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1328L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1340L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1352L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1364L));
  }

  @Test
  public void undoFailed_Ban_SpotDestination_Fractionation_Transfer() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 291L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationServiceImpl.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Fractionation test = fractionationServiceImpl.get(fractionation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube sourceTube = entityManager.find(Tube.class, 84L);
    assertEquals(false, sourceTube.isBanned());
    PlateSpot destinationSpot = entityManager.find(PlateSpot.class, 1285L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1297L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1379L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1391L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1403L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1415L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1329L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1341L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1353L);
    assertEquals(true, destinationSpot.isBanned());
    destinationSpot = entityManager.find(PlateSpot.class, 1365L);
    assertEquals(true, destinationSpot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 84L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1285L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1297L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1379L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1391L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1403L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1415L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1329L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1341L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1353L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 1365L));
  }
}
