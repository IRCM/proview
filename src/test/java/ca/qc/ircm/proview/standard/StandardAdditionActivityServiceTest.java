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

package ca.qc.ircm.proview.standard;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class StandardAdditionActivityServiceTest {
  private StandardAdditionActivityService standardAdditionActivityService;
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
    standardAdditionActivityService =
        new StandardAdditionActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setSample(sample);
    treatedSample.setName("unit_test_standard");
    treatedSample.setQuantity("20 μg");
    treatedSample.setContainer(sourceTube);
    List<TreatedSample> treatedSamples = new ArrayList<>();
    treatedSamples.add(treatedSample);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setId(123456L);
    standardAddition.setTreatedSamples(treatedSamples);

    Activity activity = standardAdditionActivityService.insert(standardAddition);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(standardAddition.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 248L);
    entityManager.detach(standardAddition);
    standardAddition.getTreatedSamples().forEach(ts -> entityManager.detach(ts));
    standardAddition.getTreatedSamples().get(0).setContainer(new Well(248L));
    standardAddition.getTreatedSamples().get(0).setSample(new Control(444L));
    standardAddition.getTreatedSamples().get(0).setName("std1");
    standardAddition.getTreatedSamples().get(0).setQuantity("10 μg");
    standardAddition.getTreatedSamples().get(0).setComment("test");
    TreatedSample newTreatedSample = new TreatedSample();
    newTreatedSample.setId(400L);
    newTreatedSample.setContainer(new Tube(14L));
    newTreatedSample.setSample(new SubmissionSample(562L));
    standardAddition.getTreatedSamples().add(newTreatedSample);

    Optional<Activity> optionalActivity =
        standardAdditionActivityService.update(standardAddition, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(standardAddition.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity newTreatedSampleActivity = new UpdateActivity();
    newTreatedSampleActivity.setActionType(ActionType.INSERT);
    newTreatedSampleActivity.setTableName("treatedsample");
    newTreatedSampleActivity.setRecordId(400L);
    expecteds.add(newTreatedSampleActivity);
    UpdateActivity updateTreatedSampleSampleActivity = new UpdateActivity();
    updateTreatedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSampleActivity.setTableName("treatedsample");
    updateTreatedSampleSampleActivity.setRecordId(344L);
    updateTreatedSampleSampleActivity.setColumn("sampleId");
    updateTreatedSampleSampleActivity.setOldValue("599");
    updateTreatedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateTreatedSampleSampleActivity);
    UpdateActivity updateTreatedSampleContainerActivity = new UpdateActivity();
    updateTreatedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleContainerActivity.setTableName("treatedsample");
    updateTreatedSampleContainerActivity.setRecordId(344L);
    updateTreatedSampleContainerActivity.setColumn("containerId");
    updateTreatedSampleContainerActivity.setOldValue("997");
    updateTreatedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateTreatedSampleContainerActivity);
    UpdateActivity updateTreatedSampleNameActivity = new UpdateActivity();
    updateTreatedSampleNameActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleNameActivity.setTableName("treatedsample");
    updateTreatedSampleNameActivity.setRecordId(344L);
    updateTreatedSampleNameActivity.setColumn("name");
    updateTreatedSampleNameActivity.setOldValue("adh");
    updateTreatedSampleNameActivity.setNewValue("std1");
    expecteds.add(updateTreatedSampleNameActivity);
    UpdateActivity updateTreatedSampleQuantityActivity = new UpdateActivity();
    updateTreatedSampleQuantityActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleQuantityActivity.setTableName("treatedsample");
    updateTreatedSampleQuantityActivity.setRecordId(344L);
    updateTreatedSampleQuantityActivity.setColumn("quantity");
    updateTreatedSampleQuantityActivity.setOldValue("2 μg");
    updateTreatedSampleQuantityActivity.setNewValue("10 μg");
    expecteds.add(updateTreatedSampleQuantityActivity);
    UpdateActivity updateTreatedSampleCommentActivity = new UpdateActivity();
    updateTreatedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleCommentActivity.setTableName("treatedsample");
    updateTreatedSampleCommentActivity.setRecordId(344L);
    updateTreatedSampleCommentActivity.setColumn("comment");
    updateTreatedSampleCommentActivity.setOldValue(null);
    updateTreatedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateTreatedSampleCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    entityManager.detach(standardAddition);

    Optional<Activity> optionalActivity =
        standardAdditionActivityService.update(standardAddition, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    StandardAddition standardAddition = new StandardAddition(5L);

    Activity activity =
        standardAdditionActivityService.undoErroneous(standardAddition, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(standardAddition.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    StandardAddition standardAddition = new StandardAddition(5L);

    Activity activity =
        standardAdditionActivityService.undoFailed(standardAddition, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(standardAddition.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    StandardAddition standardAddition = new StandardAddition(5L);
    Tube sourceTube = new Tube(1L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);

    Activity activity =
        standardAdditionActivityService.undoFailed(standardAddition, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(standardAddition.getId(), activity.getRecordId());
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
