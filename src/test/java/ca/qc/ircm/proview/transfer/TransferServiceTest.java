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

package ca.qc.ircm.proview.transfer;

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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
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
public class TransferServiceTest {
  private TransferService transferService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private TransferActivityService transferActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private PlateService plateService;
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
    transferService = new TransferService(entityManager, queryFactory, transferActivityService,
        activityService, plateService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Transfer transfer = transferService.get(3L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 3L, transfer.getId());
    assertEquals(TreatmentType.TRANSFER, transfer.getType());
    assertEquals((Long) 4L, transfer.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 15, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        transfer.getInsertTime());
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(1, transfer.getTreatmentSamples().size());
    TransferedSample transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 3L, transferedSample.getId());
    assertEquals(transfer, transferedSample.getTransfer());
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getContainer().getType());
    assertEquals((Long) 1L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getDestinationContainer().getType());
    assertEquals((Long) 7L, transferedSample.getDestinationContainer().getId());
    assertEquals(null, transferedSample.getComment());
  }

  @Test
  public void get_Null() {
    Transfer transfer = transferService.get(null);

    assertNull(transfer);
  }

  @Test
  public void insert_TubesToTubes() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Tube sourceTube = new Tube(1L);
    Tube destinationTube = new Tube();
    destinationTube.setName("unit_test_tube_" + sample.getName());
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceTube);
    transferedSample.setDestinationContainer(destinationTube);
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);

    transferService.insert(transfer);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).insert(eq(transfer));
    verify(activityService).insert(eq(activity));
    assertNotNull(transfer.getId());
    transfer = transferService.get(transfer.getId());
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(transfer.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(transfer.getInsertTime()));
    assertEquals(user, transfer.getUser());
    assertEquals(1, transfer.getTreatmentSamples().size());
    transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getContainer().getType());
    assertEquals((Long) 1L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getDestinationContainer().getType());
    assertNotNull(destinationTube.getId());
    assertEquals(destinationTube.getId(), transferedSample.getDestinationContainer().getId());
    assertTrue(before.isBefore(transferedSample.getDestinationContainer().getTimestamp()));
    assertTrue(after.isAfter(transferedSample.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_TubesToExistingWells() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    Well destinationWell = new Well(134L);
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceTube);
    transferedSample.setDestinationContainer(destinationWell);
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);

    transferService.insert(transfer);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).insert(eq(transfer));
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(user, transfer.getUser());
    assertEquals(1, transfer.getTreatmentSamples().size());
    transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getContainer().getType());
    assertEquals((Long) 1L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getDestinationContainer().getType());
    assertEquals((Long) 134L, transferedSample.getDestinationContainer().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(transferedSample.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(transferedSample.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_TubesToNewWells() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Plate destinationPlate = new Plate(null, "test_plate");
    destinationPlate.initWells();
    Well destinationWell = destinationPlate.well(0, 0);
    Tube sourceTube = new Tube(1L);
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceTube);
    transferedSample.setDestinationContainer(destinationWell);
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);
    doAnswer(i -> {
      Plate plate = i.getArgumentAt(0, Plate.class);
      plate.setInsertTime(Instant.now());
      plate.initWells();
      plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
      entityManager.persist(plate);
      entityManager.flush();
      return null;
    }).when(plateService).insert(any());

    transferService.insert(transfer);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).insert(eq(transfer));
    verify(activityService).insert(eq(activity));
    verify(plateService).insert(eq(destinationPlate));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(user, transfer.getUser());
    assertEquals(1, transfer.getTreatmentSamples().size());
    transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getContainer().getType());
    assertEquals((Long) 1L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getDestinationContainer().getType());
    assertNotNull(transferedSample.getDestinationContainer().getId());
    destinationWell = (Well) transferedSample.getDestinationContainer();
    assertNotNull(destinationWell.getPlate().getId());
    assertEquals("test_plate", destinationWell.getPlate().getName());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(transferedSample.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(transferedSample.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_WellsToTubes() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Well sourceWell = new Well(128L);
    Tube destinationTube = new Tube();
    destinationTube.setName("unit_test_tube_" + sample.getName());
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceWell);
    transferedSample.setDestinationContainer(destinationTube);
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);

    transferService.insert(transfer);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).insert(eq(transfer));
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(user, transfer.getUser());
    assertEquals(1, transfer.getTreatmentSamples().size());
    transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getContainer().getType());
    assertEquals((Long) 128L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, transferedSample.getDestinationContainer().getType());
    assertNotNull(destinationTube.getId());
    assertEquals(destinationTube.getId(), transferedSample.getDestinationContainer().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(transferedSample.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(transferedSample.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_WellsToExistingWells() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Well sourceWell = new Well(128L);
    Well destinationWell = new Well(134L);
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceWell);
    transferedSample.setDestinationContainer(destinationWell);
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);

    transferService.insert(transfer);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).insert(eq(transfer));
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(user, transfer.getUser());
    assertEquals(1, transfer.getTreatmentSamples().size());
    transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getContainer().getType());
    assertEquals((Long) 128L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getDestinationContainer().getType());
    assertEquals((Long) 134L, transferedSample.getDestinationContainer().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(transferedSample.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(transferedSample.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_WellsToNewWells() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Plate destinationPlate = new Plate(null, "test_plate");
    destinationPlate.initWells();
    Well destinationWell = destinationPlate.well(0, 0);
    Well sourceWell = new Well(128L);
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceWell);
    transferedSample.setDestinationContainer(destinationWell);
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);
    doAnswer(i -> {
      Plate plate = i.getArgumentAt(0, Plate.class);
      plate.setInsertTime(Instant.now());
      plate.initWells();
      plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
      entityManager.persist(plate);
      entityManager.flush();
      return null;
    }).when(plateService).insert(any());

    transferService.insert(transfer);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).insert(eq(transfer));
    verify(activityService).insert(eq(activity));
    verify(plateService).insert(eq(destinationPlate));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(user, transfer.getUser());
    assertEquals(1, transfer.getTreatmentSamples().size());
    transferedSample = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, transferedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getContainer().getType());
    assertEquals((Long) 128L, transferedSample.getContainer().getId());
    assertEquals(SampleContainerType.WELL, transferedSample.getDestinationContainer().getType());
    assertEquals(SampleContainerType.WELL, transferedSample.getDestinationContainer().getType());
    assertNotNull(transferedSample.getDestinationContainer().getId());
    destinationWell = (Well) transferedSample.getDestinationContainer();
    assertNotNull(destinationWell.getPlate().getId());
    assertEquals("test_plate", destinationWell.getPlate().getName());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(transferedSample.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(transferedSample.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    final Tube tube1 = entityManager.find(Tube.class, 3L);
    final Tube tube2 = entityManager.find(Tube.class, 8L);
    Tube destinationTube1 = new Tube();
    destinationTube1.setName("unit_test_tube_" + tube1.getSample().getName());
    TransferedSample transferedSample1 = new TransferedSample();
    transferedSample1.setSample(tube1.getSample());
    transferedSample1.setContainer(tube1);
    transferedSample1.setDestinationContainer(destinationTube1);
    transferedSamples.add(transferedSample1);
    Tube destinationTube2 = new Tube();
    destinationTube2.setName("unit_test_tube_" + tube2.getSample().getName());
    TransferedSample transferedSample2 = new TransferedSample();
    transferedSample2.setSample(tube2.getSample());
    transferedSample2.setContainer(tube2);
    transferedSample2.setDestinationContainer(destinationTube2);
    transferedSamples.add(transferedSample2);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);

    try {
      transferService.insert(transfer);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    final List<TransferedSample> transferedSamples = new ArrayList<>();
    final Tube tube1 = entityManager.find(Tube.class, 3L);
    final Tube tube2 = entityManager.find(Tube.class, 4L);
    Tube destinationTube1 = new Tube();
    destinationTube1.setName("unit_test_tube_" + tube1.getSample().getName());
    TransferedSample transferedSample1 = new TransferedSample();
    transferedSample1.setSample(tube1.getSample());
    transferedSample1.setContainer(tube1);
    transferedSample1.setDestinationContainer(destinationTube1);
    transferedSamples.add(transferedSample1);
    Tube destinationTube2 = new Tube();
    destinationTube2.setName("unit_test_tube_" + tube2.getSample().getName());
    TransferedSample transferedSample2 = new TransferedSample();
    transferedSample2.setSample(tube2.getSample());
    transferedSample2.setContainer(tube2);
    transferedSample2.setDestinationContainer(destinationTube2);
    transferedSamples.add(transferedSample2);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(transferedSamples);
    when(transferActivityService.insert(any(Transfer.class))).thenReturn(activity);
  
    try {
      transferService.insert(transfer);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void undoErroneous_TubeDestination() throws Throwable {
    Transfer transfer = entityManager.find(Transfer.class, 3L);
    entityManager.detach(transfer);
    when(transferActivityService.undoErroneous(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoErroneous(transfer, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoErroneous(eq(transfer), eq("undo unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, transfer.getDeletionType());
    assertEquals("undo unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 1L);
    assertEquals((Long) 1L, sourceTube.getSample().getId());
    Tube destinationTube = entityManager.find(Tube.class, 7L);
    assertNull(destinationTube.getSample());
    Collection<SampleContainer> samplesRemoved = containersCaptor.getValue();
    assertEquals(1, samplesRemoved.size());
    assertTrue(findContainer(samplesRemoved, SampleContainerType.TUBE, 7L).isPresent());
  }

  @Test
  public void undoErroneous_WellDestination() throws Throwable {
    Transfer transfer = entityManager.find(Transfer.class, 253L);
    entityManager.detach(transfer);
    when(transferActivityService.undoErroneous(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoErroneous(transfer, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoErroneous(eq(transfer), eq("undo unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, transfer.getDeletionType());
    assertEquals("undo unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 53L);
    assertEquals((Long) 601L, sourceTube.getSample().getId());
    sourceTube = entityManager.find(Tube.class, 54L);
    assertEquals((Long) 602L, sourceTube.getSample().getId());
    Well destinationWell = entityManager.find(Well.class, 998L);
    assertNull(destinationWell.getSample());
    destinationWell = entityManager.find(Well.class, 1010L);
    assertNull(destinationWell.getSample());
    Collection<SampleContainer> samplesRemoved = containersCaptor.getValue();
    assertEquals(2, samplesRemoved.size());
    assertTrue(findContainer(samplesRemoved, SampleContainerType.WELL, 998L).isPresent());
    assertTrue(findContainer(samplesRemoved, SampleContainerType.WELL, 1010L).isPresent());
  }

  @Test
  public void undoErroneous_UsedContainer_TubeDestination_Dilution() throws Throwable {
    Transfer transfer = entityManager.find(Transfer.class, 259L);
    entityManager.detach(transfer);

    try {
      transferService.undoErroneous(transfer, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(1, e.containers.size());
      assertTrue(findContainer(e.containers, SampleContainerType.TUBE, 65L).isPresent());
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoErroneous_UsedContainer_TubeDestination_MsAnalysis() throws Throwable {
    Transfer transfer = entityManager.find(Transfer.class, 260L);
    entityManager.detach(transfer);

    try {
      transferService.undoErroneous(transfer, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(1, e.containers.size());
      assertTrue(findContainer(e.containers, SampleContainerType.TUBE, 66L).isPresent());
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoErroneous_UsedContainer_WellDestination_Enrichment() throws Throwable {
    Transfer transfer = entityManager.find(Transfer.class, 261L);
    entityManager.detach(transfer);

    try {
      transferService.undoErroneous(transfer, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(1, e.containers.size());
      assertTrue(findContainer(e.containers, SampleContainerType.WELL, 1076L).isPresent());
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoErroneous_UsedContainer_WellDestination_MsAnalysis() throws Throwable {
    Transfer transfer = entityManager.find(Transfer.class, 262L);
    entityManager.detach(transfer);

    try {
      transferService.undoErroneous(transfer, "undo unit test");
      fail("Expected DestinationUsedInTreatmentException to be thrown");
    } catch (DestinationUsedInTreatmentException e) {
      assertEquals(1, e.containers.size());
      assertTrue(findContainer(e.containers, SampleContainerType.WELL, 1077).isPresent());
    }
    verify(authorizationService).checkAdminRole();
  }

  @Test
  public void undoFailed_NoBan_TubeDestination() {
    Transfer transfer = entityManager.find(Transfer.class, 3L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube destinationTube = entityManager.find(Tube.class, 7L);
    assertEquals(false, destinationTube.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_NoBan_WellDestination() {
    Transfer transfer = entityManager.find(Transfer.class, 253L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Well destinationWell = entityManager.find(Well.class, 998L);
    assertEquals(false, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1010L);
    assertEquals(false, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban_TubeDestination() {
    Transfer transfer = entityManager.find(Transfer.class, 3L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube destinationTube = entityManager.find(Tube.class, 7L);
    assertEquals(true, destinationTube.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 7L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination() {
    Transfer transfer = entityManager.find(Transfer.class, 253L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Well destinationWell = entityManager.find(Well.class, 998L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1010L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 998L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1010L).isPresent());
  }

  @Test
  public void undoFailed_Ban_TubeDestination_Transfer() {
    Transfer transfer = entityManager.find(Transfer.class, 265L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 67L);
    assertEquals(false, sourceTube.isBanned());
    Tube destinationTube = entityManager.find(Tube.class, 75L);
    assertEquals(true, destinationTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1208L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 67L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 75L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1208L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Transfer() {
    Transfer transfer = entityManager.find(Transfer.class, 269L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 68L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1184L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1160L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 68L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1184L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1160L).isPresent());
  }

  @Test
  public void undoFailed_Ban_TubeDestination_Fractionation() {
    Transfer transfer = entityManager.find(Transfer.class, 266L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 69L);
    assertEquals(false, sourceTube.isBanned());
    Tube destinationTube = entityManager.find(Tube.class, 76L);
    assertEquals(true, destinationTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1188L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1200L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(3, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 69L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 76L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1188L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1200L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Fractionation() {
    Transfer transfer = entityManager.find(Transfer.class, 270L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 70L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1185L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1161L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1173L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(3, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 70L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1185L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1161L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1173L).isPresent());
  }

  @Test
  public void undoFailed_Ban_TubeDestination_Transfer_Fractionation() {
    Transfer transfer = entityManager.find(Transfer.class, 267L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 71L);
    assertEquals(false, sourceTube.isBanned());
    Tube destinationTube = entityManager.find(Tube.class, 77L);
    assertEquals(true, destinationTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1189L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1163L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1175L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 71L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 77L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1189L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1163L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1175L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Transfer_Fractionation() {
    Transfer transfer = entityManager.find(Transfer.class, 271L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    transfer = transferService.get(transfer.getId());
    assertNotNull(transfer);
    assertEquals(true, transfer.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, transfer.getDeletionType());
    assertEquals("fail unit test", transfer.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 72L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1186L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1162L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1190L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1202L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 72L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1186L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1162L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1190L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1202L).isPresent());
  }

  @Test
  public void undoFailed_Ban_TubeDestination_Fractionation_Transfer() {
    Transfer transfer = entityManager.find(Transfer.class, 268L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Transfer test = transferService.get(transfer.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 73L);
    assertEquals(false, sourceTube.isBanned());
    Tube destinationTube = entityManager.find(Tube.class, 78L);
    assertEquals(true, destinationTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1191L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1203L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1165L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1177L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(5, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 73L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 78L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1191L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1203L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1165L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1177L).isPresent());
  }

  @Test
  public void undoFailed_Ban_WellDestination_Fractionation_Transfer() {
    Transfer transfer = entityManager.find(Transfer.class, 272L);
    entityManager.detach(transfer);
    when(transferActivityService.undoFailed(any(Transfer.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    transferService.undoFailed(transfer, "fail unit test", true);

    entityManager.flush();
    verify(transferActivityService).undoFailed(eq(transfer), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Transfer test = transferService.get(transfer.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube sourceTube = entityManager.find(Tube.class, 74L);
    assertEquals(false, sourceTube.isBanned());
    Well destinationWell = entityManager.find(Well.class, 1164L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1176L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1192L);
    assertEquals(true, destinationWell.isBanned());
    destinationWell = entityManager.find(Well.class, 1204L);
    assertEquals(true, destinationWell.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(5, bannedContainers.size());
    assertFalse(findContainer(bannedContainers, SampleContainerType.TUBE, 74L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1164L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1176L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1192L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1204L).isPresent());
  }
}
