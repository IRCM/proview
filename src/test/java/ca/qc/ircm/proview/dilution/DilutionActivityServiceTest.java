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
import ca.qc.ircm.proview.treatment.TreatedSample;
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
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setSample(sample);
    treatedSample.setContainer(sourceTube);
    treatedSample.setSolvent("Methanol");
    treatedSample.setSolventVolume(20.0);
    treatedSample.setSourceVolume(10.0);
    List<TreatedSample> treatedSamples = new ArrayList<>();
    treatedSamples.add(treatedSample);
    Dilution dilution = new Dilution();
    dilution.setId(123456L);
    dilution.setTreatedSamples(treatedSamples);

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
    dilution.getTreatedSamples().forEach(ts -> entityManager.detach(ts));
    dilution.getTreatedSamples().get(0).setContainer(new Well(248L));
    dilution.getTreatedSamples().get(0).setSample(new Control(444L));
    dilution.getTreatedSamples().get(0).setSourceVolume(3.5);
    dilution.getTreatedSamples().get(0).setSolvent("ch3oh");
    dilution.getTreatedSamples().get(0).setSolventVolume(7.0);
    dilution.getTreatedSamples().get(0).setComment("test");
    TreatedSample newTreatedSample = new TreatedSample();
    newTreatedSample.setId(400L);
    newTreatedSample.setContainer(new Tube(14L));
    newTreatedSample.setSample(new SubmissionSample(562L));
    dilution.getTreatedSamples().add(newTreatedSample);

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
    UpdateActivity newTreatedSampleActivity = new UpdateActivity();
    newTreatedSampleActivity.setActionType(ActionType.INSERT);
    newTreatedSampleActivity.setTableName("treatmentsample");
    newTreatedSampleActivity.setRecordId(400L);
    expecteds.add(newTreatedSampleActivity);
    UpdateActivity updateTreatedSampleSampleActivity = new UpdateActivity();
    updateTreatedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSampleActivity.setTableName("treatmentsample");
    updateTreatedSampleSampleActivity.setRecordId(236L);
    updateTreatedSampleSampleActivity.setColumn("sampleId");
    updateTreatedSampleSampleActivity.setOldValue("569");
    updateTreatedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateTreatedSampleSampleActivity);
    UpdateActivity updateTreatedSampleContainerActivity = new UpdateActivity();
    updateTreatedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleContainerActivity.setTableName("treatmentsample");
    updateTreatedSampleContainerActivity.setRecordId(236L);
    updateTreatedSampleContainerActivity.setColumn("containerId");
    updateTreatedSampleContainerActivity.setOldValue("608");
    updateTreatedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateTreatedSampleContainerActivity);
    UpdateActivity updateTreatedSampleSourceVolumeActivity = new UpdateActivity();
    updateTreatedSampleSourceVolumeActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSourceVolumeActivity.setTableName("treatmentsample");
    updateTreatedSampleSourceVolumeActivity.setRecordId(236L);
    updateTreatedSampleSourceVolumeActivity.setColumn("sourceVolume");
    updateTreatedSampleSourceVolumeActivity.setOldValue("2.0");
    updateTreatedSampleSourceVolumeActivity.setNewValue("3.5");
    expecteds.add(updateTreatedSampleSourceVolumeActivity);
    UpdateActivity updateTreatedSampleSolventActivity = new UpdateActivity();
    updateTreatedSampleSolventActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSolventActivity.setTableName("treatmentsample");
    updateTreatedSampleSolventActivity.setRecordId(236L);
    updateTreatedSampleSolventActivity.setColumn("solvent");
    updateTreatedSampleSolventActivity.setOldValue("Methanol");
    updateTreatedSampleSolventActivity.setNewValue("ch3oh");
    expecteds.add(updateTreatedSampleSolventActivity);
    UpdateActivity updateTreatedSampleSolventVolumeActivity = new UpdateActivity();
    updateTreatedSampleSolventVolumeActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSolventVolumeActivity.setTableName("treatmentsample");
    updateTreatedSampleSolventVolumeActivity.setRecordId(236L);
    updateTreatedSampleSolventVolumeActivity.setColumn("solventVolume");
    updateTreatedSampleSolventVolumeActivity.setOldValue("5.0");
    updateTreatedSampleSolventVolumeActivity.setNewValue("7.0");
    expecteds.add(updateTreatedSampleSolventVolumeActivity);
    UpdateActivity updateTreatedSampleCommentActivity = new UpdateActivity();
    updateTreatedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleCommentActivity.setTableName("treatmentsample");
    updateTreatedSampleCommentActivity.setRecordId(236L);
    updateTreatedSampleCommentActivity.setColumn("comment");
    updateTreatedSampleCommentActivity.setOldValue(null);
    updateTreatedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateTreatedSampleCommentActivity);
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
}
