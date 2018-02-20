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

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.Well;
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
    transferActivityService = new TransferActivityService(entityManager, authorizationService);
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
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceTube);
    transferedSample.setDestinationContainer(destinationTube);
    List<TransferedSample> transferedSamples = new ArrayList<>();
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setId(123456L);
    transfer.setTreatmentSamples(transferedSamples);

    Activity activity = transferActivityService.insert(transfer);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity insertTubeActivity = new UpdateActivity();
    insertTubeActivity.setActionType(ActionType.INSERT);
    insertTubeActivity.setTableName("samplecontainer");
    insertTubeActivity.setRecordId(destinationTube.getId());
    expecteds.add(insertTubeActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void logInsert_DestinationWell() {
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    Well destinationWell = new Well(130L);
    destinationWell.setSample(sample);
    TransferedSample transferedSample = new TransferedSample();
    transferedSample.setSample(sample);
    transferedSample.setContainer(sourceTube);
    transferedSample.setDestinationContainer(destinationWell);
    List<TransferedSample> transferedSamples = new ArrayList<>();
    transferedSamples.add(transferedSample);
    Transfer transfer = new Transfer();
    transfer.setId(123456L);
    transfer.setTreatmentSamples(transferedSamples);

    Activity activity = transferActivityService.insert(transfer);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity updateWellActivity = new UpdateActivity();
    updateWellActivity.setActionType(ActionType.UPDATE);
    updateWellActivity.setTableName("samplecontainer");
    updateWellActivity.setRecordId(destinationWell.getId());
    updateWellActivity.setColumn("sampleId");
    updateWellActivity.setOldValue(null);
    updateWellActivity.setNewValue(sample.getId().toString());
    expecteds.add(updateWellActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undo_RemoveSamplesTube() {
    Transfer transfer = new Transfer(3L);
    Tube destinationTube = new Tube(7L);
    Collection<SampleContainer> samplesRemoved = new ArrayList<>();
    samplesRemoved.add(destinationTube);

    Activity activity = transferActivityService.undo(transfer, "unit_test", samplesRemoved, null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity deleteTubeActivity = new UpdateActivity();
    deleteTubeActivity.setActionType(ActionType.DELETE);
    deleteTubeActivity.setTableName("samplecontainer");
    deleteTubeActivity.setRecordId(destinationTube.getId());
    expecteds.add(deleteTubeActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undo_RemoveSamplesWell() {
    Transfer transfer = new Transfer(9L);
    Well destinationWell = new Well(129L);
    Collection<SampleContainer> samplesRemoved = new ArrayList<>();
    samplesRemoved.add(destinationWell);

    Activity activity = transferActivityService.undo(transfer, "unit_test", samplesRemoved, null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity updateWellActivity = new UpdateActivity();
    updateWellActivity.setActionType(ActionType.UPDATE);
    updateWellActivity.setTableName("samplecontainer");
    updateWellActivity.setRecordId(destinationWell.getId());
    updateWellActivity.setColumn("sampleId");
    updateWellActivity.setOldValue("1");
    updateWellActivity.setNewValue(null);
    expecteds.add(updateWellActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undo_NoBan_Tube() {
    Transfer transfer = new Transfer(3L);

    Activity activity = transferActivityService.undo(transfer, "unit_test", null, null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undo_NoBan_Well() {
    Transfer transfer = new Transfer(9L);

    Activity activity = transferActivityService.undo(transfer, "unit_test", null, null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undo_Ban_Tube() {
    Transfer transfer = new Transfer(3L);
    Tube destinationTube = new Tube(7L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(destinationTube);

    Activity activity = transferActivityService.undo(transfer, "unit_test", null, bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
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
  public void undo_Ban_Well() {
    Transfer transfer = new Transfer(9L);
    Well destinationWell = new Well(129L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(destinationWell);

    Activity activity = transferActivityService.undo(transfer, "unit_test", null, bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(transfer.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity updateWellActivity = new UpdateActivity();
    updateWellActivity.setActionType(ActionType.UPDATE);
    updateWellActivity.setTableName("samplecontainer");
    updateWellActivity.setRecordId(destinationWell.getId());
    updateWellActivity.setColumn("banned");
    updateWellActivity.setOldValue("0");
    updateWellActivity.setNewValue("1");
    expecteds.add(updateWellActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }
}
