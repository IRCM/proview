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

import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.DestinationUsedInTreatmentException;
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
public class FractionationServiceTest {
  private FractionationService fractionationService;
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
    fractionationService = new FractionationService(entityManager, queryFactory,
        fractionationActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Fractionation fractionation = fractionationService.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, fractionation.getId());
    assertEquals(TreatmentType.FRACTIONATION, fractionation.getType());
    assertEquals(FractionationType.MUDPIT, fractionation.getFractionationType());
    assertEquals((Long) 4L, fractionation.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 12, 20, 33, 0).atZone(ZoneId.systemDefault()).toInstant(),
        fractionation.getInsertTime());
    assertEquals(false, fractionation.isDeleted());
    assertEquals(null, fractionation.getDeletionType());
    assertEquals(null, fractionation.getDeletionExplanation());
    Fraction fraction = fractionation.getTreatmentSamples().get(0);
    assertEquals((Long) 2L, fraction.getId());
    assertEquals(fractionation, fraction.getFractionation());
    assertEquals(SampleContainerType.TUBE, fraction.getContainer().getType());
    assertEquals((Long) 1L, fraction.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, fraction.getDestinationContainer().getType());
    assertEquals((Long) 6L, fraction.getDestinationContainer().getId());
    assertEquals(null, fraction.getComment());
    assertEquals((Integer) 1, fraction.getPosition());
    assertEquals((Integer) 1, fraction.getNumber());
    assertEquals(null, fraction.getPiInterval());
  }

  @Test
  public void get_Null() {
    Fractionation fractionation = fractionationService.get(null);

    assertNull(fractionation);
  }

  @Test
  public void search() {
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(6L);
    tube.setSample(sample);

    Fraction detail = fractionationService.search(tube);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(detail);
    assertEquals((Long) 2L, detail.getId());
    assertEquals("FAM119A_band_01.F1", detail.getName());
  }

  @Test
  public void search_None() {
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    tube.setSample(sample);

    Fraction detail = fractionationService.search(tube);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNull(detail);
  }

  @Test
  public void search_Null() {
    Fraction detail = fractionationService.search(null);

    assertNull(detail);
  }

  @Test
  public void insert_Tube() {
    Fractionation fractionation = new Fractionation();
    fractionation.setFractionationType(FractionationType.MUDPIT);
    final List<Fraction> fractions = new ArrayList<>();
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Tube sourceTube = new Tube(1L);
    Tube destinationTube = new Tube();
    destinationTube.setSample(sample);
    destinationTube.setName("unit_test_tube_" + sample.getName());
    Fraction fraction = new Fraction();
    fraction.setSample(sample);
    fraction.setContainer(sourceTube);
    fraction.setDestinationContainer(destinationTube);
    fractions.add(fraction);
    fractionation.setTreatmentSamples(fractions);

    try {
      fractionationService.insert(fractionation);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }

  @Test
  public void insert_Well() {
    final List<Fraction> fractions = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    final Tube sourceTube = new Tube(1L);
    Well destinationWell1 = new Well(134L);
    Well destinationWell2 = new Well(135L);
    destinationWell1.setSample(sample);
    destinationWell2.setSample(sample);
    Fraction fraction = new Fraction();
    fraction.setSample(sample);
    fraction.setContainer(sourceTube);
    fraction.setDestinationContainer(destinationWell1);
    fraction.setNumber(1);
    fractions.add(fraction);
    fraction = new Fraction();
    fraction.setSample(sample);
    fraction.setContainer(sourceTube);
    fraction.setDestinationContainer(destinationWell2);
    fraction.setNumber(2);
    fractions.add(fraction);
    Fractionation fractionation = new Fractionation();
    fractionation.setTreatmentSamples(fractions);
    when(fractionationActivityService.insert(any(Fractionation.class))).thenReturn(activity);

    fractionationService.insert(fractionation);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).insert(eq(fractionation));
    verify(activityService).insert(eq(activity));
    fractionation = fractionationService.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(false, fractionation.isDeleted());
    assertEquals(null, fractionation.getDeletionType());
    assertEquals(null, fractionation.getDeletionExplanation());
    assertEquals(user, fractionation.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(fractionation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(fractionation.getInsertTime()));
    assertEquals(2, fractionation.getTreatmentSamples().size());
    fraction = fractionation.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, fraction.getSample().getId());
    assertEquals(SampleContainerType.TUBE, fraction.getContainer().getType());
    assertEquals((Long) 1L, fraction.getContainer().getId());
    assertEquals(SampleContainerType.WELL, fraction.getDestinationContainer().getType());
    assertEquals((Long) 134L, fraction.getDestinationContainer().getId());
    assertEquals((Integer) 1, fraction.getNumber());
    assertEquals((Integer) 3, fraction.getPosition());
    fraction = fractionation.getTreatmentSamples().get(1);
    assertEquals((Long) 1L, fraction.getSample().getId());
    assertEquals(SampleContainerType.TUBE, fraction.getContainer().getType());
    assertEquals((Long) 1L, fraction.getContainer().getId());
    assertEquals(SampleContainerType.WELL, fraction.getDestinationContainer().getType());
    assertEquals((Long) 135L, fraction.getDestinationContainer().getId());
    assertEquals((Integer) 2, fraction.getNumber());
    assertEquals((Integer) 4, fraction.getPosition());
    assertTrue(before.isBefore(fraction.getDestinationContainer().getTimestamp()));
    assertTrue(after.isAfter(fraction.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void undoErroneous_WellDestination() throws Throwable {
    Fractionation fractionation = entityManager.find(Fractionation.class, 8L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoErroneous(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoErroneous(fractionation, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoErroneous(eq(fractionation), eq("undo unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationService.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, fractionation.getDeletionType());
    assertEquals("undo unit test", fractionation.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 1L);
    assertEquals((Long) 1L, sourceTube.getSample().getId());
    Well destinationWell = entityManager.find(Well.class, 128L);
    assertNull(destinationWell.getSample());
    Collection<SampleContainer> samplesRemoved = containersCaptor.getValue();
    assertEquals(1, samplesRemoved.size());
    assertTrue(findContainer(samplesRemoved, SampleContainerType.WELL, 128L).isPresent());
  }

  @Test
  public void undoErroneous_UsedContainer_WellDestination_Enrichment() throws Throwable {
    Fractionation fractionation = entityManager.find(Fractionation.class, 285L);
    entityManager.detach(fractionation);

    try {
      fractionationService.undoErroneous(fractionation, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(2, e.containers.size());
      assertTrue(findContainer(e.containers, SampleContainerType.WELL, 1280L).isPresent());
      assertTrue(findContainer(e.containers, SampleContainerType.WELL, 1292L).isPresent());
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoErroneous_UsedContainer_WellDestination_MsAnalysis() throws Throwable {
    Fractionation fractionation = entityManager.find(Fractionation.class, 286L);
    entityManager.detach(fractionation);

    try {
      fractionationService.undoErroneous(fractionation, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(2, e.containers.size());
      assertTrue(findContainer(e.containers, SampleContainerType.WELL, 1281L).isPresent());
      assertTrue(findContainer(e.containers, SampleContainerType.WELL, 1293L).isPresent());
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoFailed_NoBan_Well() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 8L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoFailed(fractionation, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationService.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban_WellDestination() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 8L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Fractionation test = fractionationService.get(fractionation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 1L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 128L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 1L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 128L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Transfer() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 288L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationService.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 81L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1282L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1294L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1376L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1388L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 81L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1282L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1294L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1376L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1388L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Fractionation() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 289L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationService.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 82L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1283L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1295L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1378L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1390L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1402L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1414L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 82L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1283L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1295L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1378L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1390L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1402L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1414L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Transfer_Fractionation() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 290L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    fractionation = fractionationService.get(fractionation.getId());
    assertNotNull(fractionation);
    assertEquals(true, fractionation.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, fractionation.getDeletionType());
    assertEquals("fail unit test", fractionation.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 83L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1284L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1296L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1377L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1389L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1328L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1340L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1352L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1364L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 83L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1284L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1296L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1377L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1389L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1328L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1340L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1352L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1364L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Fractionation_Transfer() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 291L);
    entityManager.detach(fractionation);
    when(fractionationActivityService.undoFailed(any(Fractionation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    fractionationService.undoFailed(fractionation, "fail unit test", true);

    entityManager.flush();
    verify(fractionationActivityService).undoFailed(eq(fractionation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Fractionation test = fractionationService.get(fractionation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 84L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1285L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1297L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1379L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1391L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1403L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1415L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1329L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1341L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1353L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1365L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 84L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1285L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1297L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1379L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1391L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1403L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1415L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1329L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1341L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1353L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1365L).isPresent());
  }
}
