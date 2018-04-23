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
public class SolubilisationActivityServiceTest {
  private SolubilisationActivityService solubilisationActivityService;
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
    solubilisationActivityService =
        new SolubilisationActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setSample(sample);
    treatedSample.setSolvent("Methanol");
    treatedSample.setSolventVolume(20.0);
    treatedSample.setContainer(sourceTube);
    List<TreatedSample> treatedSamples = new ArrayList<>();
    treatedSamples.add(treatedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setId(123456L);
    solubilisation.setTreatedSamples(treatedSamples);

    Activity activity = solubilisationActivityService.insert(solubilisation);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(solubilisation.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 236L);
    entityManager.detach(solubilisation);
    solubilisation.getTreatedSamples().forEach(ts -> entityManager.detach(ts));
    solubilisation.getTreatedSamples().get(0).setContainer(new Well(248L));
    solubilisation.getTreatedSamples().get(0).setSample(new Control(444L));
    solubilisation.getTreatedSamples().get(0).setSolvent("ch3oh");
    solubilisation.getTreatedSamples().get(0).setSolventVolume(7.0);
    solubilisation.getTreatedSamples().get(0).setComment("test");
    TreatedSample newSolubilisedSample = new TreatedSample();
    newSolubilisedSample.setId(400L);
    newSolubilisedSample.setContainer(new Tube(14L));
    newSolubilisedSample.setSample(new SubmissionSample(562L));
    solubilisation.getTreatedSamples().add(newSolubilisedSample);

    Optional<Activity> optionalActivity =
        solubilisationActivityService.update(solubilisation, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(solubilisation.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity newSolubilisedSampleActivity = new UpdateActivity();
    newSolubilisedSampleActivity.setActionType(ActionType.INSERT);
    newSolubilisedSampleActivity.setTableName("treatedsample");
    newSolubilisedSampleActivity.setRecordId(400L);
    expecteds.add(newSolubilisedSampleActivity);
    UpdateActivity updateSolubilisedSampleSampleActivity = new UpdateActivity();
    updateSolubilisedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateSolubilisedSampleSampleActivity.setTableName("treatedsample");
    updateSolubilisedSampleSampleActivity.setRecordId(312L);
    updateSolubilisedSampleSampleActivity.setColumn("sampleId");
    updateSolubilisedSampleSampleActivity.setOldValue("589");
    updateSolubilisedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateSolubilisedSampleSampleActivity);
    UpdateActivity updateSolubilisedSampleContainerActivity = new UpdateActivity();
    updateSolubilisedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateSolubilisedSampleContainerActivity.setTableName("treatedsample");
    updateSolubilisedSampleContainerActivity.setRecordId(312L);
    updateSolubilisedSampleContainerActivity.setColumn("containerId");
    updateSolubilisedSampleContainerActivity.setOldValue("992");
    updateSolubilisedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateSolubilisedSampleContainerActivity);
    UpdateActivity updateSolubilisedSampleSolventActivity = new UpdateActivity();
    updateSolubilisedSampleSolventActivity.setActionType(ActionType.UPDATE);
    updateSolubilisedSampleSolventActivity.setTableName("treatedsample");
    updateSolubilisedSampleSolventActivity.setRecordId(312L);
    updateSolubilisedSampleSolventActivity.setColumn("solvent");
    updateSolubilisedSampleSolventActivity.setOldValue("Methanol");
    updateSolubilisedSampleSolventActivity.setNewValue("ch3oh");
    expecteds.add(updateSolubilisedSampleSolventActivity);
    UpdateActivity updateSolubilisedSampleSolventVolumeActivity = new UpdateActivity();
    updateSolubilisedSampleSolventVolumeActivity.setActionType(ActionType.UPDATE);
    updateSolubilisedSampleSolventVolumeActivity.setTableName("treatedsample");
    updateSolubilisedSampleSolventVolumeActivity.setRecordId(312L);
    updateSolubilisedSampleSolventVolumeActivity.setColumn("solventVolume");
    updateSolubilisedSampleSolventVolumeActivity.setOldValue("20.0");
    updateSolubilisedSampleSolventVolumeActivity.setNewValue("7.0");
    expecteds.add(updateSolubilisedSampleSolventVolumeActivity);
    UpdateActivity updateSolubilisedSampleCommentActivity = new UpdateActivity();
    updateSolubilisedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateSolubilisedSampleCommentActivity.setTableName("treatedsample");
    updateSolubilisedSampleCommentActivity.setRecordId(312L);
    updateSolubilisedSampleCommentActivity.setColumn("comment");
    updateSolubilisedSampleCommentActivity.setOldValue(null);
    updateSolubilisedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateSolubilisedSampleCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    entityManager.detach(solubilisation);

    Optional<Activity> optionalActivity =
        solubilisationActivityService.update(solubilisation, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    Solubilisation solubilisation = new Solubilisation(1L);

    Activity activity = solubilisationActivityService.undoErroneous(solubilisation, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(solubilisation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    Solubilisation solubilisation = new Solubilisation(1L);

    Activity activity = solubilisationActivityService.undoFailed(solubilisation, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(solubilisation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    Solubilisation solubilisation = new Solubilisation(1L);
    Tube sourceTube = new Tube(1L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);

    Activity activity =
        solubilisationActivityService.undoFailed(solubilisation, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(solubilisation.getId(), activity.getRecordId());
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
