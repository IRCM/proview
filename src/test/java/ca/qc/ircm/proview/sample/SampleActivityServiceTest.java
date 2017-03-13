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

package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleActivityServiceTest {
  private SampleActivityService sampleActivityService;
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
    sampleActivityService = new SampleActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insertControl() {
    Control control = new Control();
    control.setId(123456L);
    control.setName("unit_test_control");
    control.setQuantity("200.0 μg");
    control.setSupport(SampleSupport.SOLUTION);
    control.setControlType(ControlType.NEGATIVE_CONTROL);
    control.setVolume(300.0);

    Activity activity = sampleActivityService.insertControl(control);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 442L);
    entityManager.detach(submissionSample);
    submissionSample.setName("new_solution_tag_0001");
    submissionSample.setSupport(SampleSupport.DRY);
    submissionSample.setQuantity("12 pmol");
    submissionSample.setVolume(70.0);
    submissionSample.setNumberProtein(2);
    submissionSample.setMolecularWeight(20.0);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("sample");
    nameActivity.setRecordId(submissionSample.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("CAP_20111013_01");
    nameActivity.setNewValue("new_solution_tag_0001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName("sample");
    supportActivity.setRecordId(submissionSample.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(SampleSupport.SOLUTION.name());
    supportActivity.setNewValue(SampleSupport.DRY.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("sample");
    quantityActivity.setRecordId(submissionSample.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("1.5 μg");
    quantityActivity.setNewValue("12 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity volumeActivity = new UpdateActivity();
    volumeActivity.setActionType(ActionType.UPDATE);
    volumeActivity.setTableName("sample");
    volumeActivity.setRecordId(submissionSample.getId());
    volumeActivity.setColumn("volume");
    volumeActivity.setOldValue("50.0");
    volumeActivity.setNewValue("70.0");
    expectedUpdateActivities.add(volumeActivity);
    UpdateActivity sampleNumberProteinActivity = new UpdateActivity();
    sampleNumberProteinActivity.setActionType(ActionType.UPDATE);
    sampleNumberProteinActivity.setTableName("sample");
    sampleNumberProteinActivity.setRecordId(submissionSample.getId());
    sampleNumberProteinActivity.setColumn("numberProtein");
    sampleNumberProteinActivity.setOldValue(null);
    sampleNumberProteinActivity.setNewValue("2");
    expectedUpdateActivities.add(sampleNumberProteinActivity);
    UpdateActivity molecularWeightActivity = new UpdateActivity();
    molecularWeightActivity.setActionType(ActionType.UPDATE);
    molecularWeightActivity.setTableName("sample");
    molecularWeightActivity.setRecordId(submissionSample.getId());
    molecularWeightActivity.setColumn("molecularWeight");
    molecularWeightActivity.setOldValue(null);
    molecularWeightActivity.setNewValue("20.0");
    expectedUpdateActivities.add(molecularWeightActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_AddContaminants() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 442L);
    entityManager.detach(submissionSample);
    Contaminant contaminant = new Contaminant();
    contaminant.setId(57894121L);
    contaminant.setName("my_new_contaminant");
    contaminant.setQuantity("3 μg");
    contaminant.setComments("some_comments");
    submissionSample.getContaminants().add(contaminant);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity addContaminantActivity = new UpdateActivity();
    addContaminantActivity.setActionType(ActionType.INSERT);
    addContaminantActivity.setTableName("contaminant");
    addContaminantActivity.setRecordId(contaminant.getId());
    expectedUpdateActivities.add(addContaminantActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_UpdateContaminants() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 447L);
    entityManager.detach(submissionSample);
    for (Contaminant contaminant : submissionSample.getContaminants()) {
      entityManager.detach(contaminant);
    }
    Contaminant contaminant = submissionSample.getContaminants().get(0);
    contaminant.setName("new_contaminant_name");
    contaminant.setQuantity("1 pmol");
    contaminant.setComments("new_comments");

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("contaminant");
    nameActivity.setRecordId(contaminant.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("cap_contaminant");
    nameActivity.setNewValue("new_contaminant_name");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("contaminant");
    quantityActivity.setRecordId(contaminant.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("3 μg");
    quantityActivity.setNewValue("1 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("contaminant");
    commentsActivity.setRecordId(contaminant.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("some_comments");
    commentsActivity.setNewValue("new_comments");
    expectedUpdateActivities.add(commentsActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_RemoveContaminant() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 447L);
    entityManager.detach(submissionSample);
    final Contaminant contaminant = submissionSample.getContaminants().get(0);
    submissionSample.getContaminants().remove(0);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity removeActivity = new UpdateActivity();
    removeActivity.setActionType(ActionType.DELETE);
    removeActivity.setTableName("contaminant");
    removeActivity.setRecordId(contaminant.getId());
    expectedUpdateActivities.add(removeActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_AddStandard() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 442L);
    entityManager.detach(submissionSample);
    Standard standard = new Standard();
    standard.setId(57894121L);
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComments("some_comments");
    submissionSample.getStandards().add(standard);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity addStandardActivity = new UpdateActivity();
    addStandardActivity.setActionType(ActionType.INSERT);
    addStandardActivity.setTableName("standard");
    addStandardActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(addStandardActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_UpdateStandard() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 447L);
    entityManager.detach(submissionSample);
    for (Standard standard : submissionSample.getStandards()) {
      entityManager.detach(standard);
    }
    Standard standard = submissionSample.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComments("new_comments");

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("standard");
    nameActivity.setRecordId(standard.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("cap_standard");
    nameActivity.setNewValue("new_standard_name");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("standard");
    quantityActivity.setRecordId(standard.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("3 μg");
    quantityActivity.setNewValue("1 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("standard");
    commentsActivity.setRecordId(standard.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("some_comments");
    commentsActivity.setNewValue("new_comments");
    expectedUpdateActivities.add(commentsActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_RemoveStandard() {
    SubmissionSample submissionSample = entityManager.find(SubmissionSample.class, 447L);
    entityManager.detach(submissionSample);
    final Standard standard = submissionSample.getStandards().get(0);
    submissionSample.getStandards().remove(0);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity removeActivity = new UpdateActivity();
    removeActivity.setActionType(ActionType.DELETE);
    removeActivity.setTableName("standard");
    removeActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(removeActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    control.setName("nc_test_000001");
    control.setControlType(ControlType.POSITIVE_CONTROL);
    control.setSupport(SampleSupport.SOLUTION);
    control.setVolume(2.0);
    control.setQuantity("40 μg");

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("sample");
    nameActivity.setRecordId(control.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("control_01");
    nameActivity.setNewValue("nc_test_000001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity controlTypeActivity = new UpdateActivity();
    controlTypeActivity.setActionType(ActionType.UPDATE);
    controlTypeActivity.setTableName("sample");
    controlTypeActivity.setRecordId(control.getId());
    controlTypeActivity.setColumn("controlType");
    controlTypeActivity.setOldValue("NEGATIVE_CONTROL");
    controlTypeActivity.setNewValue("POSITIVE_CONTROL");
    expectedUpdateActivities.add(controlTypeActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName("sample");
    supportActivity.setRecordId(control.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(SampleSupport.GEL.name());
    supportActivity.setNewValue(SampleSupport.SOLUTION.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity volumeActivity = new UpdateActivity();
    volumeActivity.setActionType(ActionType.UPDATE);
    volumeActivity.setTableName("sample");
    volumeActivity.setRecordId(control.getId());
    volumeActivity.setColumn("volume");
    volumeActivity.setOldValue(null);
    volumeActivity.setNewValue("2.0");
    expectedUpdateActivities.add(volumeActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("sample");
    quantityActivity.setRecordId(control.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue(null);
    quantityActivity.setNewValue("40 μg");
    expectedUpdateActivities.add(quantityActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_AddStandard() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    Standard standard = new Standard();
    standard.setId(57894121L);
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComments("some_comments");
    control.getStandards().add(standard);

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity addStandardActivity = new UpdateActivity();
    addStandardActivity.setActionType(ActionType.INSERT);
    addStandardActivity.setTableName("standard");
    addStandardActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(addStandardActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_UpdateStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    for (Standard standard : control.getStandards()) {
      entityManager.detach(standard);
    }
    Standard standard = control.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComments("new_comments");

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("standard");
    nameActivity.setRecordId(standard.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("cap_standard");
    nameActivity.setNewValue("new_standard_name");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("standard");
    quantityActivity.setRecordId(standard.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("3 μg");
    quantityActivity.setNewValue("1 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("standard");
    commentsActivity.setRecordId(standard.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("some_comments");
    commentsActivity.setNewValue("new_comments");
    expectedUpdateActivities.add(commentsActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_RemoveStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    final Standard standard = control.getStandards().get(0);
    control.getStandards().remove(0);

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity removeActivity = new UpdateActivity();
    removeActivity.setActionType(ActionType.DELETE);
    removeActivity.setTableName("standard");
    removeActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(removeActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
