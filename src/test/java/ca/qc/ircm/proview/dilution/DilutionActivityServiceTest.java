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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
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
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DilutionActivityServiceTest {
  private DilutionActivityService dilutionActivityService;
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
    dilutionActivityService = new DilutionActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(352L);
    DilutedSample dilutedSample = new DilutedSample();
    dilutedSample.setSample(sample);
    dilutedSample.setContainer(sourceTube);
    dilutedSample.setSolvent("Methanol");
    dilutedSample.setSolventVolume(20.0);
    dilutedSample.setSourceVolume(10.0);
    List<DilutedSample> dilutedSamples = new ArrayList<>();
    dilutedSamples.add(dilutedSample);
    Dilution dilution = new Dilution();
    dilution.setId(123456L);
    dilution.setTreatmentSamples(dilutedSamples);

    Activity activity = dilutionActivityService.insert(dilution);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(dilution.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Dilution dilution = entityManager.find(Dilution.class, 210L);
    entityManager.detach(dilution);
    dilution.getTreatmentSamples().forEach(ts -> entityManager.detach(ts));
    dilution.getTreatmentSamples().get(0).setContainer(new Well(248L));
    dilution.getTreatmentSamples().get(0).setSample(new Control(444L));
    dilution.getTreatmentSamples().get(0).setSourceVolume(3.5);
    dilution.getTreatmentSamples().get(0).setSolvent("ch3oh");
    dilution.getTreatmentSamples().get(0).setSolventVolume(7.0);
    dilution.getTreatmentSamples().get(0).setComment("test");
    DilutedSample newDilutedSample = new DilutedSample();
    newDilutedSample.setId(400L);
    newDilutedSample.setContainer(new Tube(14L));
    newDilutedSample.setSample(new SubmissionSample(562L));
    dilution.getTreatmentSamples().add(newDilutedSample);

    Optional<Activity> optionalActivity =
        dilutionActivityService.update(dilution, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(dilution.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity newDilutedSampleActivity = new UpdateActivity();
    newDilutedSampleActivity.setActionType(ActionType.INSERT);
    newDilutedSampleActivity.setTableName("treatmentsample");
    newDilutedSampleActivity.setRecordId(400L);
    expecteds.add(newDilutedSampleActivity);
    UpdateActivity updateDilutedSampleSampleActivity = new UpdateActivity();
    updateDilutedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateDilutedSampleSampleActivity.setTableName("treatmentsample");
    updateDilutedSampleSampleActivity.setRecordId(236L);
    updateDilutedSampleSampleActivity.setColumn("sampleId");
    updateDilutedSampleSampleActivity.setOldValue("569");
    updateDilutedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateDilutedSampleSampleActivity);
    UpdateActivity updateDilutedSampleContainerActivity = new UpdateActivity();
    updateDilutedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateDilutedSampleContainerActivity.setTableName("treatmentsample");
    updateDilutedSampleContainerActivity.setRecordId(236L);
    updateDilutedSampleContainerActivity.setColumn("containerId");
    updateDilutedSampleContainerActivity.setOldValue("608");
    updateDilutedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateDilutedSampleContainerActivity);
    UpdateActivity updateDilutedSampleSourceVolumeActivity = new UpdateActivity();
    updateDilutedSampleSourceVolumeActivity.setActionType(ActionType.UPDATE);
    updateDilutedSampleSourceVolumeActivity.setTableName("treatmentsample");
    updateDilutedSampleSourceVolumeActivity.setRecordId(236L);
    updateDilutedSampleSourceVolumeActivity.setColumn("sourceVolume");
    updateDilutedSampleSourceVolumeActivity.setOldValue("2.0");
    updateDilutedSampleSourceVolumeActivity.setNewValue("3.5");
    expecteds.add(updateDilutedSampleSourceVolumeActivity);
    UpdateActivity updateDilutedSampleSolventActivity = new UpdateActivity();
    updateDilutedSampleSolventActivity.setActionType(ActionType.UPDATE);
    updateDilutedSampleSolventActivity.setTableName("treatmentsample");
    updateDilutedSampleSolventActivity.setRecordId(236L);
    updateDilutedSampleSolventActivity.setColumn("solvent");
    updateDilutedSampleSolventActivity.setOldValue("Methanol");
    updateDilutedSampleSolventActivity.setNewValue("ch3oh");
    expecteds.add(updateDilutedSampleSolventActivity);
    UpdateActivity updateDilutedSampleSolventVolumeActivity = new UpdateActivity();
    updateDilutedSampleSolventVolumeActivity.setActionType(ActionType.UPDATE);
    updateDilutedSampleSolventVolumeActivity.setTableName("treatmentsample");
    updateDilutedSampleSolventVolumeActivity.setRecordId(236L);
    updateDilutedSampleSolventVolumeActivity.setColumn("solventVolume");
    updateDilutedSampleSolventVolumeActivity.setOldValue("5.0");
    updateDilutedSampleSolventVolumeActivity.setNewValue("7.0");
    expecteds.add(updateDilutedSampleSolventVolumeActivity);
    UpdateActivity updateDilutedSampleCommentActivity = new UpdateActivity();
    updateDilutedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateDilutedSampleCommentActivity.setTableName("treatmentsample");
    updateDilutedSampleCommentActivity.setRecordId(236L);
    updateDilutedSampleCommentActivity.setColumn("comment");
    updateDilutedSampleCommentActivity.setOldValue(null);
    updateDilutedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateDilutedSampleCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    entityManager.detach(dilution);

    Optional<Activity> optionalActivity =
        dilutionActivityService.update(dilution, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    Dilution dilution = new Dilution(4L);

    Activity activity = dilutionActivityService.undoErroneous(dilution, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(dilution.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    Dilution dilution = new Dilution(4L);

    Activity activity = dilutionActivityService.undoFailed(dilution, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(dilution.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    Dilution dilution = new Dilution(4L);
    Tube sourceTube = new Tube(2L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);

    Activity activity = dilutionActivityService.undoFailed(dilution, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(dilution.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity bannedTubeActivity = new UpdateActivity();
    bannedTubeActivity.setActionType(ActionType.UPDATE);
    bannedTubeActivity.setTableName("samplecontainer");
    bannedTubeActivity.setRecordId(sourceTube.getId());
    bannedTubeActivity.setColumn("banned");
    bannedTubeActivity.setOldValue("0");
    bannedTubeActivity.setNewValue("1");
    expecteds.add(bannedTubeActivity);
    UpdateActivity bannedWellActivity = new UpdateActivity();
    bannedWellActivity.setActionType(ActionType.UPDATE);
    bannedWellActivity.setTableName("samplecontainer");
    bannedWellActivity.setRecordId(well.getId());
    bannedWellActivity.setColumn("banned");
    bannedWellActivity.setOldValue("0");
    bannedWellActivity.setNewValue("1");
    expecteds.add(bannedWellActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undoFailed_LongDescription() throws Throwable {
    Dilution dilution = new Dilution(4L);
    Tube sourceTube = new Tube(2L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    Activity activity = dilutionActivityService.undoFailed(dilution, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    assertEquals(255, activity.getExplanation().length());
    assertEquals(reasonCutAt255Bytes, activity.getExplanation());
  }
}
