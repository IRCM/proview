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
public class FractionationActivityServiceTest {
  private FractionationActivityService fractionationActivityService;
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
    fractionationActivityService =
        new FractionationActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert_Tube() {
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    final Tube sourceTube = new Tube(1L);
    Tube destinationTube = new Tube();
    destinationTube.setId(56456748L);
    destinationTube.setName(sample.getName() + "_2");
    destinationTube.setSample(sample);
    FractionationDetail detail = new FractionationDetail();
    detail.setSample(sample);
    detail.setContainer(sourceTube);
    detail.setDestinationContainer(destinationTube);
    detail.setNumber(1);
    List<FractionationDetail> details = new ArrayList<>();
    details.add(detail);
    Fractionation fractionation = new Fractionation();
    fractionation.setId(123456L);
    fractionation.setFractionationType(FractionationType.MUDPIT);
    fractionation.setTreatmentSamples(details);

    Activity activity = fractionationActivityService.insert(fractionation);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity tubeSampleActivity = new UpdateActivity();
    tubeSampleActivity.setActionType(ActionType.INSERT);
    tubeSampleActivity.setTableName("samplecontainer");
    tubeSampleActivity.setRecordId(destinationTube.getId());
    expecteds.add(tubeSampleActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void insert_Well() {
    Sample sample = new SubmissionSample(1L);
    Tube sourceTube = new Tube(1L);
    Well destinationWell = new Well(130L);
    destinationWell.setSample(sample);
    FractionationDetail detail = new FractionationDetail();
    detail.setSample(sample);
    detail.setContainer(sourceTube);
    detail.setDestinationContainer(destinationWell);
    detail.setNumber(1);
    List<FractionationDetail> details = new ArrayList<>();
    details.add(detail);
    Fractionation fractionation = new Fractionation();
    fractionation.setId(123456L);
    fractionation.setFractionationType(FractionationType.MUDPIT);
    fractionation.setTreatmentSamples(details);

    Activity activity = fractionationActivityService.insert(fractionation);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity wellSampleActivity = new UpdateActivity();
    wellSampleActivity.setActionType(ActionType.UPDATE);
    wellSampleActivity.setTableName("samplecontainer");
    wellSampleActivity.setRecordId(destinationWell.getId());
    wellSampleActivity.setColumn("sampleId");
    wellSampleActivity.setOldValue(null);
    wellSampleActivity.setNewValue(sample.getId().toString());
    expecteds.add(wellSampleActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undoErroneous_Tube() {
    Fractionation fractionation = new Fractionation(2L);
    Tube destinationTube = new Tube(6L);
    Collection<SampleContainer> samplesRemoved = new ArrayList<>();
    samplesRemoved.add(destinationTube);

    Activity activity =
        fractionationActivityService.undoErroneous(fractionation, "unit_test", samplesRemoved);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
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
  public void undoErroneous_Well() {
    final Sample sample = new SubmissionSample(1L);
    Fractionation fractionation = new Fractionation(8L);
    Well destinationWell = new Well(128L);
    Collection<SampleContainer> samplesRemoved = new ArrayList<>();
    samplesRemoved.add(destinationWell);

    Activity activity =
        fractionationActivityService.undoErroneous(fractionation, "unit_test", samplesRemoved);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity updateWellActivity = new UpdateActivity();
    updateWellActivity.setActionType(ActionType.UPDATE);
    updateWellActivity.setTableName("samplecontainer");
    updateWellActivity.setRecordId(destinationWell.getId());
    updateWellActivity.setColumn("sampleId");
    updateWellActivity.setOldValue(sample.getId().toString());
    updateWellActivity.setNewValue(null);
    expecteds.add(updateWellActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan_Tube() {
    Fractionation fractionation = new Fractionation(2L);

    Activity activity = fractionationActivityService.undoFailed(fractionation, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan_Well() {
    Fractionation fractionation = new Fractionation(8L);

    Activity activity = fractionationActivityService.undoFailed(fractionation, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban_Tube() {
    Fractionation fractionation = new Fractionation(2L);
    Tube destinationTube = new Tube(6L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(destinationTube);

    Activity activity =
        fractionationActivityService.undoFailed(fractionation, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
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
  public void undoFailed_Ban_Well() {
    Fractionation fractionation = new Fractionation(8L);
    Well destinationWell = new Well(128L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(destinationWell);

    Activity activity =
        fractionationActivityService.undoFailed(fractionation, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(fractionation.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
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

  @Test
  public void undoFailed_LongDescription() throws Throwable {
    Fractionation fractionation = new Fractionation(2L);
    Tube sourceTube = new Tube(1L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    Activity activity =
        fractionationActivityService.undoFailed(fractionation, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    assertEquals(255, activity.getJustification().length());
    assertEquals(reasonCutAt255Bytes, activity.getJustification());
  }
}
