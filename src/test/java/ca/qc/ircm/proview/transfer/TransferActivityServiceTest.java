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
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TransferActivityServiceTest {
  private TransferActivityService transferActivityService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    transferActivityService =
        new TransferActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void logInsert_DestinationTube() {
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Tube sourceTube = new Tube(1L);
    Tube destinationTube = new Tube();
    destinationTube.setId(56456748L);
    destinationTube.setName(sample.getName() + "_2");
    destinationTube.setSample(sample);
    SampleTransfer sampleTransfer = new SampleTransfer();
    sampleTransfer.setSample(sample);
    sampleTransfer.setContainer(sourceTube);
    sampleTransfer.setDestinationContainer(destinationTube);
    List<SampleTransfer> sampleTransfers = new ArrayList<SampleTransfer>();
    sampleTransfers.add(sampleTransfer);
    Transfer transfer = new Transfer();
    transfer.setId(123456L);
    transfer.setTreatmentSamples(sampleTransfers);

    Activity activity = transferActivityService.insert(transfer);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity insertTubeActivity = new UpdateActivity();
    insertTubeActivity.setActionType(ActionType.INSERT);
    insertTubeActivity.setTableName("samplecontainer");
    insertTubeActivity.setRecordId(destinationTube.getId());
    expecteds.add(insertTubeActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logInsert_DestinationSpot() {
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    PlateSpot destinationSpot = new PlateSpot(130L);
    destinationSpot.setSample(sample);
    SampleTransfer sampleTransfer = new SampleTransfer();
    sampleTransfer.setSample(sample);
    sampleTransfer.setContainer(sourceTube);
    sampleTransfer.setDestinationContainer(destinationSpot);
    List<SampleTransfer> sampleTransfers = new ArrayList<SampleTransfer>();
    sampleTransfers.add(sampleTransfer);
    Transfer transfer = new Transfer();
    transfer.setId(123456L);
    transfer.setTreatmentSamples(sampleTransfers);

    Activity activity = transferActivityService.insert(transfer);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity updateSpotActivity = new UpdateActivity();
    updateSpotActivity.setActionType(ActionType.UPDATE);
    updateSpotActivity.setTableName("samplecontainer");
    updateSpotActivity.setRecordId(destinationSpot.getId());
    updateSpotActivity.setColumn("sampleId");
    updateSpotActivity.setOldValue(null);
    updateSpotActivity.setNewValue(sample.getId().toString());
    expecteds.add(updateSpotActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logUndoErroneous_Tube() {
    Transfer transfer = new Transfer(3L);
    Tube destinationTube = new Tube(7L);
    Collection<SampleContainer> samplesRemoved = new ArrayList<SampleContainer>();
    samplesRemoved.add(destinationTube);

    Activity activity =
        transferActivityService.undoErroneous(transfer, "unit_test", samplesRemoved);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity deleteTubeActivity = new UpdateActivity();
    deleteTubeActivity.setActionType(ActionType.DELETE);
    deleteTubeActivity.setTableName("samplecontainer");
    deleteTubeActivity.setRecordId(destinationTube.getId());
    expecteds.add(deleteTubeActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logUndoErroneous_Spot() {
    Transfer transfer = new Transfer(9L);
    PlateSpot destinationSpot = new PlateSpot(129L);
    Collection<SampleContainer> samplesRemoved = new ArrayList<SampleContainer>();
    samplesRemoved.add(destinationSpot);

    Activity activity =
        transferActivityService.undoErroneous(transfer, "unit_test", samplesRemoved);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity updateSpotActivity = new UpdateActivity();
    updateSpotActivity.setActionType(ActionType.UPDATE);
    updateSpotActivity.setTableName("samplecontainer");
    updateSpotActivity.setRecordId(destinationSpot.getId());
    updateSpotActivity.setColumn("sampleId");
    updateSpotActivity.setOldValue("1");
    updateSpotActivity.setNewValue(null);
    expecteds.add(updateSpotActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logUndoFailed_NoBan_Tube() {
    Transfer transfer = new Transfer(3L);

    Activity activity = transferActivityService.undoFailed(transfer, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void logUndoFailed_NoBan_Spot() {
    Transfer transfer = new Transfer(9L);

    Activity activity = transferActivityService.undoFailed(transfer, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void logUndoFailed_Ban_Tube() {
    Transfer transfer = new Transfer(3L);
    Tube destinationTube = new Tube(7L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(destinationTube);

    Activity activity =
        transferActivityService.undoFailed(transfer, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity updateTubeActivity = new UpdateActivity();
    updateTubeActivity.setActionType(ActionType.UPDATE);
    updateTubeActivity.setTableName("samplecontainer");
    updateTubeActivity.setRecordId(destinationTube.getId());
    updateTubeActivity.setColumn("banned");
    updateTubeActivity.setOldValue("0");
    updateTubeActivity.setNewValue("1");
    expecteds.add(updateTubeActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logUndoFailed_Ban_Spot() {
    Transfer transfer = new Transfer(9L);
    PlateSpot destinationSpot = new PlateSpot(129L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(destinationSpot);

    Activity activity =
        transferActivityService.undoFailed(transfer, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity updateSpotActivity = new UpdateActivity();
    updateSpotActivity.setActionType(ActionType.UPDATE);
    updateSpotActivity.setTableName("samplecontainer");
    updateSpotActivity.setRecordId(destinationSpot.getId());
    updateSpotActivity.setColumn("banned");
    updateSpotActivity.setOldValue("0");
    updateSpotActivity.setNewValue("1");
    expecteds.add(updateSpotActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logUndoFailed_LongDescription() throws Throwable {
    Transfer transfer = new Transfer(9L);
    Tube sourceTube = new Tube(1L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(sourceTube);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    Activity activity = transferActivityService.undoFailed(transfer, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    assertEquals(255, activity.getJustification().length());
    assertEquals(reasonCutAt255Bytes, activity.getJustification());
  }
}
