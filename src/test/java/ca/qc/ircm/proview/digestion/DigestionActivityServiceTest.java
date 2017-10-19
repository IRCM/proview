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

package ca.qc.ircm.proview.digestion;

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
public class DigestionActivityServiceTest {
  private DigestionActivityService digestionActivityService;
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
    digestionActivityService = new DigestionActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    final DigestionProtocol protocol = new DigestionProtocol(1L);
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(352L);
    DigestedSample digestedSample = new DigestedSample();
    digestedSample.setSample(sample);
    digestedSample.setContainer(sourceTube);
    List<DigestedSample> digestedSamples = new ArrayList<>();
    digestedSamples.add(digestedSample);
    Digestion digestion = new Digestion();
    digestion.setId(123456L);
    digestion.setProtocol(protocol);
    digestion.setTreatmentSamples(digestedSamples);

    Activity activity = digestionActivityService.insert(digestion);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Digestion digestion = entityManager.find(Digestion.class, 195L);
    entityManager.detach(digestion);
    digestion.getTreatmentSamples().forEach(ts -> entityManager.detach(ts));
    digestion.setProtocol(entityManager.find(DigestionProtocol.class, 3L));
    digestion.getTreatmentSamples().get(0).setContainer(new Well(248L));
    digestion.getTreatmentSamples().get(0).setSample(new Control(444L));
    digestion.getTreatmentSamples().get(0).setComment("test");
    DigestedSample newDigestedSample = new DigestedSample();
    newDigestedSample.setId(400L);
    newDigestedSample.setContainer(new Tube(14L));
    newDigestedSample.setSample(new SubmissionSample(562L));
    digestion.getTreatmentSamples().add(newDigestedSample);

    Optional<Activity> optionalActivity =
        digestionActivityService.update(digestion, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity protocolActivity = new UpdateActivity();
    protocolActivity.setActionType(ActionType.UPDATE);
    protocolActivity.setTableName("treatment");
    protocolActivity.setRecordId(digestion.getId());
    protocolActivity.setColumn("protocol");
    protocolActivity.setOldValue("1");
    protocolActivity.setNewValue("3");
    expecteds.add(protocolActivity);
    UpdateActivity newDigestedSampleActivity = new UpdateActivity();
    newDigestedSampleActivity.setActionType(ActionType.INSERT);
    newDigestedSampleActivity.setTableName("treatmentsample");
    newDigestedSampleActivity.setRecordId(400L);
    expecteds.add(newDigestedSampleActivity);
    UpdateActivity updateDigestedSampleSampleActivity = new UpdateActivity();
    updateDigestedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateDigestedSampleSampleActivity.setTableName("treatmentsample");
    updateDigestedSampleSampleActivity.setRecordId(196L);
    updateDigestedSampleSampleActivity.setColumn("sampleId");
    updateDigestedSampleSampleActivity.setOldValue("559");
    updateDigestedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateDigestedSampleSampleActivity);
    UpdateActivity updateDigestedSampleContainerActivity = new UpdateActivity();
    updateDigestedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateDigestedSampleContainerActivity.setTableName("treatmentsample");
    updateDigestedSampleContainerActivity.setRecordId(196L);
    updateDigestedSampleContainerActivity.setColumn("containerId");
    updateDigestedSampleContainerActivity.setOldValue("224");
    updateDigestedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateDigestedSampleContainerActivity);
    UpdateActivity updateDigestedSampleCommentActivity = new UpdateActivity();
    updateDigestedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateDigestedSampleCommentActivity.setTableName("treatmentsample");
    updateDigestedSampleCommentActivity.setRecordId(196L);
    updateDigestedSampleCommentActivity.setColumn("comment");
    updateDigestedSampleCommentActivity.setOldValue(null);
    updateDigestedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateDigestedSampleCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    entityManager.detach(digestion);

    Optional<Activity> optionalActivity =
        digestionActivityService.update(digestion, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    Digestion digestion = new Digestion(6L);

    Activity activity = digestionActivityService.undoErroneous(digestion, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    Digestion digestion = new Digestion(6L);

    Activity activity = digestionActivityService.undoFailed(digestion, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    Digestion digestion = new Digestion(6L);
    Tube sourceTube = new Tube(4L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);

    Activity activity =
        digestionActivityService.undoFailed(digestion, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
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
    Digestion digestion = new Digestion(6L);
    Tube sourceTube = new Tube(352L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    Activity activity = digestionActivityService.undoFailed(digestion, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    assertEquals(255, activity.getExplanation().length());
    assertEquals(reasonCutAt255Bytes, activity.getExplanation());
  }
}
