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
import ca.qc.ircm.proview.sample.Sample;
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
        activityService, authorizationService);
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
    Transfer transfer = transferService.get(3L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 3L, transfer.getId());
    assertEquals(Treatment.Type.TRANSFER, transfer.getType());
    assertEquals((Long) 4L, transfer.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 15, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        transfer.getInsertTime());
    assertEquals(false, transfer.isDeleted());
    assertEquals(null, transfer.getDeletionType());
    assertEquals(null, transfer.getDeletionExplanation());
    assertEquals(1, transfer.getTreatmentSamples().size());
    SampleTransfer sampleTransfer = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 3L, sampleTransfer.getId());
    assertEquals(transfer, sampleTransfer.getTransfer());
    assertEquals((Long) 1L, sampleTransfer.getSample().getId());
    assertEquals(SampleContainerType.TUBE, sampleTransfer.getContainer().getType());
    assertEquals((Long) 1L, sampleTransfer.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, sampleTransfer.getDestinationContainer().getType());
    assertEquals((Long) 7L, sampleTransfer.getDestinationContainer().getId());
    assertEquals(null, sampleTransfer.getComments());
  }

  @Test
  public void get_Null() {
    Transfer transfer = transferService.get(null);

    assertNull(transfer);
  }

  @Test
  public void all() {
    Submission submission = entityManager.find(Submission.class, 1L);

    List<Transfer> transfers = transferService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, transfers.size());
    Transfer transfer = transfers.get(0);
    assertEquals((Long) 3L, transfer.getId());
  }

  @Test
  public void all_Null() {
    List<Transfer> transfers = transferService.all(null);

    assertEquals(0, transfers.size());
  }

  @Test
  public void insert_TubeToTube() {
    final List<SampleTransfer> sampleTransfers = new ArrayList<>();
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Tube sourceTube = new Tube(1L);
    Tube destinationTube = new Tube();
    destinationTube.setSample(sample);
    destinationTube.setName("unit_test_tube_" + sample.getName());
    SampleTransfer sampleTransfer = new SampleTransfer();
    sampleTransfer.setSample(sample);
    sampleTransfer.setContainer(sourceTube);
    sampleTransfer.setDestinationContainer(destinationTube);
    sampleTransfers.add(sampleTransfer);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(sampleTransfers);
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
    sampleTransfer = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, sampleTransfer.getSample().getId());
    assertEquals(SampleContainerType.TUBE, sampleTransfer.getContainer().getType());
    assertEquals((Long) 1L, sampleTransfer.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, sampleTransfer.getDestinationContainer().getType());
    assertNotNull(destinationTube.getId());
    assertEquals(destinationTube.getId(), sampleTransfer.getDestinationContainer().getId());
    assertEquals(sampleTransfer.getId(),
        sampleTransfer.getDestinationContainer().getTreatmentSample().getId());
    assertTrue(before.isBefore(sampleTransfer.getDestinationContainer().getTimestamp()));
    assertTrue(after.isAfter(sampleTransfer.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_TubeToWell() {
    final List<SampleTransfer> sampleTransfers = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    Well destinationWell = new Well(134L);
    destinationWell.setSample(sample);
    SampleTransfer sampleTransfer = new SampleTransfer();
    sampleTransfer.setSample(sample);
    sampleTransfer.setContainer(sourceTube);
    sampleTransfer.setDestinationContainer(destinationWell);
    sampleTransfers.add(sampleTransfer);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(sampleTransfers);
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
    sampleTransfer = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, sampleTransfer.getSample().getId());
    assertEquals(SampleContainerType.TUBE, sampleTransfer.getContainer().getType());
    assertEquals((Long) 1L, sampleTransfer.getContainer().getId());
    assertEquals(SampleContainerType.WELL, sampleTransfer.getDestinationContainer().getType());
    assertEquals((Long) 134L, sampleTransfer.getDestinationContainer().getId());
    assertEquals(sampleTransfer.getId(),
        sampleTransfer.getDestinationContainer().getTreatmentSample().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(sampleTransfer.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(sampleTransfer.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_WellToTube() {
    final List<SampleTransfer> sampleTransfers = new ArrayList<>();
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Well sourceWell = new Well(128L);
    Tube destinationTube = new Tube();
    destinationTube.setSample(sample);
    destinationTube.setName("unit_test_tube_" + sample.getName());
    SampleTransfer sampleTransfer = new SampleTransfer();
    sampleTransfer.setSample(sample);
    sampleTransfer.setContainer(sourceWell);
    sampleTransfer.setDestinationContainer(destinationTube);
    sampleTransfers.add(sampleTransfer);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(sampleTransfers);
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
    sampleTransfer = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, sampleTransfer.getSample().getId());
    assertEquals(SampleContainerType.WELL, sampleTransfer.getContainer().getType());
    assertEquals((Long) 128L, sampleTransfer.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, sampleTransfer.getDestinationContainer().getType());
    assertNotNull(destinationTube.getId());
    assertEquals(destinationTube.getId(), sampleTransfer.getDestinationContainer().getId());
    assertEquals(sampleTransfer.getId(),
        sampleTransfer.getDestinationContainer().getTreatmentSample().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(sampleTransfer.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(sampleTransfer.getDestinationContainer().getTimestamp()));
  }

  @Test
  public void insert_WellToWell() {
    final List<SampleTransfer> sampleTransfers = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Well sourceWell = new Well(128L);
    Well destinationWell = new Well(134L);
    destinationWell.setSample(sample);
    SampleTransfer sampleTransfer = new SampleTransfer();
    sampleTransfer.setSample(sample);
    sampleTransfer.setContainer(sourceWell);
    sampleTransfer.setDestinationContainer(destinationWell);
    sampleTransfers.add(sampleTransfer);
    Transfer transfer = new Transfer();
    transfer.setTreatmentSamples(sampleTransfers);
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
    sampleTransfer = transfer.getTreatmentSamples().get(0);
    assertEquals((Long) 1L, sampleTransfer.getSample().getId());
    assertEquals(SampleContainerType.WELL, sampleTransfer.getContainer().getType());
    assertEquals((Long) 128L, sampleTransfer.getContainer().getId());
    assertEquals(SampleContainerType.WELL, sampleTransfer.getDestinationContainer().getType());
    assertEquals((Long) 134L, sampleTransfer.getDestinationContainer().getId());
    assertEquals(sampleTransfer.getId(),
        sampleTransfer.getDestinationContainer().getTreatmentSample().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(sampleTransfer.getDestinationContainer().getTimestamp()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(sampleTransfer.getDestinationContainer().getTimestamp()));
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
    assertNull(destinationTube.getTreatmentSample());
    Collection<SampleContainer> samplesRemoved = containersCaptor.getValue();
    assertEquals(1, samplesRemoved.size());
    assertNotNull(findContainer(samplesRemoved, SampleContainerType.TUBE, 7L));
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
    assertNull(destinationWell.getTreatmentSample());
    Collection<SampleContainer> samplesRemoved = containersCaptor.getValue();
    assertEquals(2, samplesRemoved.size());
    assertNotNull(findContainer(samplesRemoved, SampleContainerType.WELL, 998L));
    assertNotNull(findContainer(samplesRemoved, SampleContainerType.WELL, 1010L));
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
      assertNotNull(findContainer(e.containers, SampleContainerType.TUBE, 65L));
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
      assertNotNull(findContainer(e.containers, SampleContainerType.TUBE, 66L));
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
      assertNotNull(findContainer(e.containers, SampleContainerType.WELL, 1076L));
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
      assertNotNull(findContainer(e.containers, SampleContainerType.WELL, 1077));
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
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 7L));
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
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 998L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1010L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 67L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 75L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1208L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 68L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1184L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1160L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 69L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 76L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1188L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1200L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 70L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1185L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1161L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1173L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 71L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 77L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1189L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1163L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1175L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 72L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1186L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1162L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1190L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1202L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 73L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 78L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1191L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1203L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1165L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1177L));
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
    assertNull(findContainer(bannedContainers, SampleContainerType.TUBE, 74L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1164L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1176L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1192L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.WELL, 1204L));
  }
}
