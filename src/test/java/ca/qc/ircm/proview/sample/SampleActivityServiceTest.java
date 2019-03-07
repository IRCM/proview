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

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleActivityServiceTest extends AbstractServiceTestCase {
  private static final QSubmissionSample qsubmissionSample = QSubmissionSample.submissionSample;
  private static final QSubmission qsubmission = QSubmission.submission;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private SubmissionSampleRepository submissionSampleRepository;
  @Inject
  private ControlRepository controlRepository;
  @MockBean
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    user = new User(4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insertControl() {
    Control control = new Control();
    control.setId(123456L);
    control.setName("unit_test_control");
    control.setQuantity("200.0 μg");
    control.setType(SampleType.SOLUTION);
    control.setControlType(ControlType.NEGATIVE_CONTROL);
    control.setVolume("300.0 μl");

    Activity activity = sampleActivityService.insertControl(control);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void updateStatus() {
    SubmissionSample sample = submissionSampleRepository.findOne(584L);
    detach(sample);
    sample.setStatus(SampleStatus.ANALYSED);
    Submission submission = sample.getSubmission();
    detach(submission);
    LocalDate sampleDeliveryDate = LocalDate.now().minusDays(2);
    LocalDate digestionDate = LocalDate.now();
    LocalDate analysisDate = LocalDate.now().plusDays(1);
    submission.setSampleDeliveryDate(sampleDeliveryDate);
    submission.setDigestionDate(digestionDate);
    submission.setAnalysisDate(analysisDate);

    Optional<Activity> optionalActivity = sampleActivityService.updateStatus(sample);

    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(sample.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity statusActivity = new UpdateActivity();
    statusActivity.setActionType(ActionType.UPDATE);
    statusActivity.setTableName(Sample.TABLE_NAME);
    statusActivity.setRecordId(sample.getId());
    statusActivity.setColumn(qname(qsubmissionSample.status));
    statusActivity.setOldValue(SampleStatus.ENRICHED.name());
    statusActivity.setNewValue(SampleStatus.ANALYSED.name());
    expectedUpdateActivities.add(statusActivity);
    UpdateActivity sampleDeliveryDateActivity = new UpdateActivity();
    sampleDeliveryDateActivity.setActionType(ActionType.UPDATE);
    sampleDeliveryDateActivity.setTableName(Submission.TABLE_NAME);
    sampleDeliveryDateActivity.setRecordId(submission.getId());
    sampleDeliveryDateActivity.setColumn(qname(qsubmission.sampleDeliveryDate));
    sampleDeliveryDateActivity.setOldValue("2014-10-14");
    sampleDeliveryDateActivity.setNewValue(formatter.format(sampleDeliveryDate));
    expectedUpdateActivities.add(sampleDeliveryDateActivity);
    UpdateActivity digestionDateActivity = new UpdateActivity();
    digestionDateActivity.setActionType(ActionType.UPDATE);
    digestionDateActivity.setTableName(Submission.TABLE_NAME);
    digestionDateActivity.setRecordId(submission.getId());
    digestionDateActivity.setColumn(qname(qsubmission.digestionDate));
    digestionDateActivity.setOldValue(null);
    digestionDateActivity.setNewValue(formatter.format(digestionDate));
    expectedUpdateActivities.add(digestionDateActivity);
    UpdateActivity analysisDateActivity = new UpdateActivity();
    analysisDateActivity.setActionType(ActionType.UPDATE);
    analysisDateActivity.setTableName(Submission.TABLE_NAME);
    analysisDateActivity.setRecordId(submission.getId());
    analysisDateActivity.setColumn(qname(qsubmission.analysisDate));
    analysisDateActivity.setOldValue(null);
    analysisDateActivity.setNewValue(formatter.format(analysisDate));
    expectedUpdateActivities.add(analysisDateActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample() {
    SubmissionSample submissionSample = submissionSampleRepository.findOne(442L);
    detach(submissionSample);
    submissionSample.setName("new_solution_tag_0001");
    submissionSample.setType(SampleType.DRY);
    submissionSample.setQuantity("12 pmol");
    submissionSample.setVolume("70.0 μl");
    submissionSample.setNumberProtein(2);
    submissionSample.setMolecularWeight(20.0);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName(Sample.TABLE_NAME);
    nameActivity.setRecordId(submissionSample.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("CAP_20111013_01");
    nameActivity.setNewValue("new_solution_tag_0001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName(Sample.TABLE_NAME);
    supportActivity.setRecordId(submissionSample.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(SampleType.SOLUTION.name());
    supportActivity.setNewValue(SampleType.DRY.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName(Sample.TABLE_NAME);
    quantityActivity.setRecordId(submissionSample.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("1.5 μg");
    quantityActivity.setNewValue("12 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity volumeActivity = new UpdateActivity();
    volumeActivity.setActionType(ActionType.UPDATE);
    volumeActivity.setTableName(Sample.TABLE_NAME);
    volumeActivity.setRecordId(submissionSample.getId());
    volumeActivity.setColumn("volume");
    volumeActivity.setOldValue("50 μl");
    volumeActivity.setNewValue("70.0 μl");
    expectedUpdateActivities.add(volumeActivity);
    UpdateActivity sampleNumberProteinActivity = new UpdateActivity();
    sampleNumberProteinActivity.setActionType(ActionType.UPDATE);
    sampleNumberProteinActivity.setTableName(Sample.TABLE_NAME);
    sampleNumberProteinActivity.setRecordId(submissionSample.getId());
    sampleNumberProteinActivity.setColumn("numberProtein");
    sampleNumberProteinActivity.setOldValue(null);
    sampleNumberProteinActivity.setNewValue("2");
    expectedUpdateActivities.add(sampleNumberProteinActivity);
    UpdateActivity molecularWeightActivity = new UpdateActivity();
    molecularWeightActivity.setActionType(ActionType.UPDATE);
    molecularWeightActivity.setTableName(Sample.TABLE_NAME);
    molecularWeightActivity.setRecordId(submissionSample.getId());
    molecularWeightActivity.setColumn("molecularWeight");
    molecularWeightActivity.setOldValue(null);
    molecularWeightActivity.setNewValue("20.0");
    expectedUpdateActivities.add(molecularWeightActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_AddContaminants() {
    SubmissionSample submissionSample = submissionSampleRepository.findOne(442L);
    detach(submissionSample);
    Contaminant contaminant = new Contaminant();
    contaminant.setId(57894121L);
    contaminant.setName("my_new_contaminant");
    contaminant.setQuantity("3 μg");
    contaminant.setComment("some_comment");
    submissionSample.getContaminants().add(contaminant);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    SubmissionSample submissionSample = submissionSampleRepository.findOne(447L);
    detach(submissionSample);
    for (Contaminant contaminant : submissionSample.getContaminants()) {
      detach(contaminant);
    }
    Contaminant contaminant = submissionSample.getContaminants().get(0);
    contaminant.setName("new_contaminant_name");
    contaminant.setQuantity("1 pmol");
    contaminant.setComment("new_comment");

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    UpdateActivity commentActivity = new UpdateActivity();
    commentActivity.setActionType(ActionType.UPDATE);
    commentActivity.setTableName("contaminant");
    commentActivity.setRecordId(contaminant.getId());
    commentActivity.setColumn("comment");
    commentActivity.setOldValue("some_comment");
    commentActivity.setNewValue("new_comment");
    expectedUpdateActivities.add(commentActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_RemoveContaminant() {
    SubmissionSample submissionSample = submissionSampleRepository.findOne(447L);
    detach(submissionSample);
    final Contaminant contaminant = submissionSample.getContaminants().get(0);
    submissionSample.getContaminants().remove(0);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    SubmissionSample submissionSample = submissionSampleRepository.findOne(442L);
    detach(submissionSample);
    Standard standard = new Standard();
    standard.setId(57894121L);
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComment("some_comment");
    submissionSample.getStandards().add(standard);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    SubmissionSample submissionSample = submissionSampleRepository.findOne(447L);
    detach(submissionSample);
    for (Standard standard : submissionSample.getStandards()) {
      detach(standard);
    }
    Standard standard = submissionSample.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComment("new_comment");

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    UpdateActivity commentActivity = new UpdateActivity();
    commentActivity.setActionType(ActionType.UPDATE);
    commentActivity.setTableName("standard");
    commentActivity.setRecordId(standard.getId());
    commentActivity.setColumn("comment");
    commentActivity.setOldValue("some_comment");
    commentActivity.setNewValue("new_comment");
    expectedUpdateActivities.add(commentActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_SubmissionSample_RemoveStandard() {
    SubmissionSample submissionSample = submissionSampleRepository.findOne(447L);
    detach(submissionSample);
    final Standard standard = submissionSample.getStandards().get(0);
    submissionSample.getStandards().remove(0);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(submissionSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    Control control = controlRepository.findOne(444L);
    detach(control);
    control.setName("nc_test_000001");
    control.setControlType(ControlType.POSITIVE_CONTROL);
    control.setType(SampleType.SOLUTION);
    control.setVolume("2.0 μl");
    control.setQuantity("40 μg");

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName(Sample.TABLE_NAME);
    nameActivity.setRecordId(control.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("control_01");
    nameActivity.setNewValue("nc_test_000001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity controlTypeActivity = new UpdateActivity();
    controlTypeActivity.setActionType(ActionType.UPDATE);
    controlTypeActivity.setTableName(Sample.TABLE_NAME);
    controlTypeActivity.setRecordId(control.getId());
    controlTypeActivity.setColumn("controlType");
    controlTypeActivity.setOldValue("NEGATIVE_CONTROL");
    controlTypeActivity.setNewValue("POSITIVE_CONTROL");
    expectedUpdateActivities.add(controlTypeActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName(Sample.TABLE_NAME);
    supportActivity.setRecordId(control.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(SampleType.GEL.name());
    supportActivity.setNewValue(SampleType.SOLUTION.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity volumeActivity = new UpdateActivity();
    volumeActivity.setActionType(ActionType.UPDATE);
    volumeActivity.setTableName(Sample.TABLE_NAME);
    volumeActivity.setRecordId(control.getId());
    volumeActivity.setColumn("volume");
    volumeActivity.setOldValue(null);
    volumeActivity.setNewValue("2.0 μl");
    expectedUpdateActivities.add(volumeActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName(Sample.TABLE_NAME);
    quantityActivity.setRecordId(control.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue(null);
    quantityActivity.setNewValue("40 μg");
    expectedUpdateActivities.add(quantityActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_AddStandard() {
    Control control = controlRepository.findOne(444L);
    detach(control);
    Standard standard = new Standard();
    standard.setId(57894121L);
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComment("some_comment");
    control.getStandards().add(standard);

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    Control control = controlRepository.findOne(448L);
    detach(control);
    for (Standard standard : control.getStandards()) {
      detach(standard);
    }
    Standard standard = control.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComment("new_comment");

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
    UpdateActivity commentActivity = new UpdateActivity();
    commentActivity.setActionType(ActionType.UPDATE);
    commentActivity.setTableName("standard");
    commentActivity.setRecordId(standard.getId());
    commentActivity.setColumn("comment");
    commentActivity.setOldValue("some_comment");
    commentActivity.setNewValue("new_comment");
    expectedUpdateActivities.add(commentActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_RemoveStandard() {
    Control control = controlRepository.findOne(448L);
    detach(control);
    final Standard standard = control.getStandards().get(0);
    control.getStandards().remove(0);

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
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
  public void update_Submission_SampleDeliveryAndDigestionDateAndAnalysisDate() {
    SubmissionSample sample = submissionSampleRepository.findOne(584L);
    Submission submission = sample.getSubmission();
    detach(sample);
    detach(submission);
    sample.setStatus(SampleStatus.ANALYSED);
    LocalDate sampleDeliveryDate = LocalDate.now().minusDays(2);
    LocalDate digestionDate = LocalDate.now();
    LocalDate analysisDate = LocalDate.now().plusDays(1);
    submission.setSampleDeliveryDate(sampleDeliveryDate);
    submission.setDigestionDate(digestionDate);
    submission.setAnalysisDate(analysisDate);

    Optional<Activity> optionalActivity = sampleActivityService.update(sample, null);

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Sample.TABLE_NAME, activity.getTableName());
    assertEquals(sample.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity statusActivity = new UpdateActivity();
    statusActivity.setActionType(ActionType.UPDATE);
    statusActivity.setTableName(Sample.TABLE_NAME);
    statusActivity.setRecordId(sample.getId());
    statusActivity.setColumn(qname(qsubmissionSample.status));
    statusActivity.setOldValue(SampleStatus.ENRICHED.name());
    statusActivity.setNewValue(SampleStatus.ANALYSED.name());
    expectedUpdateActivities.add(statusActivity);
    // Submission dates updates are not recorded.
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    Control control = controlRepository.findOne(448L);
    detach(control);

    Optional<Activity> optionalActivity = sampleActivityService.update(control, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
