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
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link SampleActivityService}.
 */
@ServiceTestAnnotations
public class SampleActivityServiceTest extends AbstractServiceTestCase {
  private static final QSubmissionSample qsubmissionSample = QSubmissionSample.submissionSample;
  private static final QSubmission qsubmission = QSubmission.submission;
  @Autowired
  private SampleActivityService sampleActivityService;
  @Autowired
  private SubmissionSampleRepository submissionSampleRepository;
  @MockBean
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    user = new User(4L);
    when(authorizationService.getCurrentUser()).thenReturn(Optional.of(user));
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
    assertEquals(user.getId(), activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void updateStatus() {
    SubmissionSample sample = submissionSampleRepository.findById(584L).orElse(null);
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
    assertEquals(user.getId(), activity.getUser().getId());
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
    SubmissionSample submissionSample = submissionSampleRepository.findById(442L).orElse(null);
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
    assertEquals(user.getId(), activity.getUser().getId());
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
  public void update_Submission_SampleDeliveryAndDigestionDateAndAnalysisDate() {
    SubmissionSample sample = submissionSampleRepository.findById(584L).orElse(null);
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
    assertEquals(user.getId(), activity.getUser().getId());
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
    SubmissionSample submissionSample = submissionSampleRepository.findById(442L).orElse(null);
    detach(submissionSample);

    Optional<Activity> optionalActivity =
        sampleActivityService.update(submissionSample, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
