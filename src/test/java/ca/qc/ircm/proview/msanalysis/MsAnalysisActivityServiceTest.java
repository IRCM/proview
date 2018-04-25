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

package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.QSubmissionSample;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisActivityServiceTest {
  private static final QSubmission qsubmission = QSubmission.submission;
  private static final QSubmissionSample qsubmissionSample = QSubmissionSample.submissionSample;
  private MsAnalysisActivityService msAnalysisActivityService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    msAnalysisActivityService = new MsAnalysisActivityService(entityManager, authorizationService);
  }

  @Test
  public void insert() {
    final SubmissionSample sample = new SubmissionSample(443L);
    final Tube sourceTube = new Tube(348L);
    final MsAnalysis msAnalysis = new MsAnalysis();
    msAnalysis.setId(123456L);
    msAnalysis.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    msAnalysis.setSource(MassDetectionInstrumentSource.LDTD);
    Acquisition acquisition = new Acquisition();
    acquisition.setAcquisitionFile("unit_test_acquisition_file");
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSample(sample);
    acquisition.setSampleListName("unit_test_sample_list_name");
    acquisition.setContainer(sourceTube);
    final List<Acquisition> acquisitions = new ArrayList<>();
    acquisitions.add(acquisition);
    msAnalysis.setAcquisitions(acquisitions);
    sample.setStatus(SampleStatus.ANALYSED);
    LocalDate date = LocalDate.now();
    Submission submission = new Submission(33L);
    submission.setAnalysisDate(date);
    submission.setAnalysisDateExpected(true);
    sample.setSubmission(submission);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = msAnalysisActivityService.insert(msAnalysis);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(MsAnalysis.TABLE_NAME, activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity sampleStatusActivity = new UpdateActivity();
    sampleStatusActivity.setActionType(ActionType.UPDATE);
    sampleStatusActivity.setTableName(Sample.TABLE_NAME);
    sampleStatusActivity.setRecordId(sample.getId());
    sampleStatusActivity.setColumn(qname(qsubmissionSample.status));
    sampleStatusActivity.setOldValue(SampleStatus.TO_APPROVE.name());
    sampleStatusActivity.setNewValue(SampleStatus.ANALYSED.name());
    expectedUpdateActivities.add(sampleStatusActivity);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    UpdateActivity submissionAnalysisDateActivity = new UpdateActivity();
    submissionAnalysisDateActivity.setActionType(ActionType.UPDATE);
    submissionAnalysisDateActivity.setTableName(Submission.TABLE_NAME);
    submissionAnalysisDateActivity.setRecordId(submission.getId());
    submissionAnalysisDateActivity.setColumn(qname(qsubmission.analysisDate));
    submissionAnalysisDateActivity.setOldValue(null);
    submissionAnalysisDateActivity.setNewValue(dateFormatter.format(date));
    expectedUpdateActivities.add(submissionAnalysisDateActivity);
    UpdateActivity submissionAnalysisDateExpectedActivity = new UpdateActivity();
    submissionAnalysisDateExpectedActivity.setActionType(ActionType.UPDATE);
    submissionAnalysisDateExpectedActivity.setTableName(Submission.TABLE_NAME);
    submissionAnalysisDateExpectedActivity.setRecordId(submission.getId());
    submissionAnalysisDateExpectedActivity.setColumn(qname(qsubmission.analysisDateExpected));
    submissionAnalysisDateExpectedActivity.setOldValue("0");
    submissionAnalysisDateExpectedActivity.setNewValue("1");
    expectedUpdateActivities.add(submissionAnalysisDateExpectedActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    entityManager.detach(msAnalysis);
    msAnalysis.getAcquisitions().forEach(ac -> entityManager.detach(ac));
    msAnalysis.setMassDetectionInstrument(MassDetectionInstrument.ORBITRAP_FUSION);
    msAnalysis.setSource(MassDetectionInstrumentSource.ESI);
    msAnalysis.getAcquisitions().get(0).setContainer(new Tube(8L));
    msAnalysis.getAcquisitions().get(0).setSample(new SubmissionSample(446L));
    msAnalysis.getAcquisitions().get(0).setSampleListName("new_sample_list");
    msAnalysis.getAcquisitions().get(0).setAcquisitionFile("new_acquisition_file");
    msAnalysis.getAcquisitions().get(0).setComment("test");
    Acquisition newAcquisition = new Acquisition();
    newAcquisition.setId(400L);
    newAcquisition.setContainer(new Tube(14L));
    newAcquisition.setSample(new SubmissionSample(562L));
    msAnalysis.getAcquisitions().add(newAcquisition);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        msAnalysisActivityService.update(msAnalysis, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity massDetectionInstrumentActivity = new UpdateActivity();
    massDetectionInstrumentActivity.setActionType(ActionType.UPDATE);
    massDetectionInstrumentActivity.setTableName("msanalysis");
    massDetectionInstrumentActivity.setRecordId(msAnalysis.getId());
    massDetectionInstrumentActivity.setColumn("massDetectionInstrument");
    massDetectionInstrumentActivity.setOldValue("LTQ_ORBI_TRAP");
    massDetectionInstrumentActivity.setNewValue("ORBITRAP_FUSION");
    expecteds.add(massDetectionInstrumentActivity);
    UpdateActivity sourceActivity = new UpdateActivity();
    sourceActivity.setActionType(ActionType.UPDATE);
    sourceActivity.setTableName("msanalysis");
    sourceActivity.setRecordId(msAnalysis.getId());
    sourceActivity.setColumn("source");
    sourceActivity.setOldValue("LDTD");
    sourceActivity.setNewValue("ESI");
    expecteds.add(sourceActivity);
    UpdateActivity newAcquisitionActivity = new UpdateActivity();
    newAcquisitionActivity.setActionType(ActionType.INSERT);
    newAcquisitionActivity.setTableName("acquisition");
    newAcquisitionActivity.setRecordId(400L);
    expecteds.add(newAcquisitionActivity);
    UpdateActivity updateAcquisitionSampleActivity = new UpdateActivity();
    updateAcquisitionSampleActivity.setActionType(ActionType.UPDATE);
    updateAcquisitionSampleActivity.setTableName("acquisition");
    updateAcquisitionSampleActivity.setRecordId(411L);
    updateAcquisitionSampleActivity.setColumn("sampleId");
    updateAcquisitionSampleActivity.setOldValue("444");
    updateAcquisitionSampleActivity.setNewValue("446");
    expecteds.add(updateAcquisitionSampleActivity);
    UpdateActivity updateAcquisitionContainerActivity = new UpdateActivity();
    updateAcquisitionContainerActivity.setActionType(ActionType.UPDATE);
    updateAcquisitionContainerActivity.setTableName("acquisition");
    updateAcquisitionContainerActivity.setRecordId(411L);
    updateAcquisitionContainerActivity.setColumn("containerId");
    updateAcquisitionContainerActivity.setOldValue("4");
    updateAcquisitionContainerActivity.setNewValue("8");
    expecteds.add(updateAcquisitionContainerActivity);
    UpdateActivity updateAcquisitionSampleListNameActivity = new UpdateActivity();
    updateAcquisitionSampleListNameActivity.setActionType(ActionType.UPDATE);
    updateAcquisitionSampleListNameActivity.setTableName("acquisition");
    updateAcquisitionSampleListNameActivity.setRecordId(411L);
    updateAcquisitionSampleListNameActivity.setColumn("sampleListName");
    updateAcquisitionSampleListNameActivity.setOldValue("XL_20111115_01");
    updateAcquisitionSampleListNameActivity.setNewValue("new_sample_list");
    expecteds.add(updateAcquisitionSampleListNameActivity);
    UpdateActivity updateAcquisitionAcquisitionFileActivity = new UpdateActivity();
    updateAcquisitionAcquisitionFileActivity.setActionType(ActionType.UPDATE);
    updateAcquisitionAcquisitionFileActivity.setTableName("acquisition");
    updateAcquisitionAcquisitionFileActivity.setRecordId(411L);
    updateAcquisitionAcquisitionFileActivity.setColumn("acquisitionFile");
    updateAcquisitionAcquisitionFileActivity.setOldValue("XL_20111115_COU_01");
    updateAcquisitionAcquisitionFileActivity.setNewValue("new_acquisition_file");
    expecteds.add(updateAcquisitionAcquisitionFileActivity);
    UpdateActivity updateAcquisitionCommentActivity = new UpdateActivity();
    updateAcquisitionCommentActivity.setActionType(ActionType.UPDATE);
    updateAcquisitionCommentActivity.setTableName("acquisition");
    updateAcquisitionCommentActivity.setRecordId(411L);
    updateAcquisitionCommentActivity.setColumn("comment");
    updateAcquisitionCommentActivity.setOldValue(null);
    updateAcquisitionCommentActivity.setNewValue("test");
    expecteds.add(updateAcquisitionCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 1L);
    entityManager.detach(msAnalysis);

    Optional<Activity> optionalActivity =
        msAnalysisActivityService.update(msAnalysis, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    MsAnalysis msAnalysis = new MsAnalysis(1L);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = msAnalysisActivityService.undoErroneous(msAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    MsAnalysis msAnalysis = new MsAnalysis(1L);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = msAnalysisActivityService.undoFailed(msAnalysis, "unit_test", null);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    final MsAnalysis msAnalysis = new MsAnalysis(1L);
    Tube sourceTube = new Tube(1L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity =
        msAnalysisActivityService.undoFailed(msAnalysis, "unit_test", bannedContainers);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
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
