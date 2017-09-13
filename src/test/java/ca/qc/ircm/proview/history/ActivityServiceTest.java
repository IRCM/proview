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

package ca.qc.ircm.proview.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus;
import ca.qc.ircm.proview.msanalysis.AcquisitionMascotFile;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.ComparableUpdateActivity;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Tests {@link ActivityServiceDefault} class.<br>
 * TODO Add description tests for sample treatment and analysis deletion.<br>
 * TODO Add description tests for plate treatment and analysis.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ActivityServiceTest {
  private ActivityService activityService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(ActivityService.class, locale);

  @Before
  public void beforeTest() {
    activityService = create(false);
  }

  private ActivityService create(boolean useFailsafeDescription) {
    return new ActivityService(entityManager, queryFactory, authorizationService,
        useFailsafeDescription);
  }

  private <A extends Activity> A find(Collection<A> activities, long id) {
    for (A activity : activities) {
      if (activity.getId() == id) {
        return activity;
      }
    }
    return null;
  }

  @Test
  public void getRecord_Sample() {
    Activity activity = entityManager.find(Activity.class, 5549L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof Sample);
    Sample sample = (Sample) record;
    assertEquals((Long) 444L, sample.getId());
  }

  @Test
  public void getRecord_Submission() {
    Activity activity = entityManager.find(Activity.class, 5543L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof Submission);
    Submission submission = (Submission) record;
    assertEquals((Long) 1L, submission.getId());
  }

  @Test
  public void getRecord_Plate() {
    Activity activity = entityManager.find(Activity.class, 5714L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof Plate);
    Plate plate = (Plate) record;
    assertEquals((Long) 113L, plate.getId());
  }

  @Test
  public void getRecord_Protocol() {
    Activity activity = entityManager.find(Activity.class, 5545L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof Protocol);
    Protocol protocol = (Protocol) record;
    assertEquals((Long) 1L, protocol.getId());
  }

  @Test
  public void getRecord_Treatment() {
    Activity activity = entityManager.find(Activity.class, 5550L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof Treatment);
    Treatment<?> treatment = (Treatment<?>) record;
    assertEquals((Long) 1L, treatment.getId());
  }

  @Test
  public void getRecord_MsAnalysis() {
    Activity activity = entityManager.find(Activity.class, 5551L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof MsAnalysis);
    MsAnalysis msAnalysis = (MsAnalysis) record;
    assertEquals((Long) 12L, msAnalysis.getId());
  }

  @Test
  public void getRecord_MascotFile() {
    Activity activity = entityManager.find(Activity.class, 5567L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof AcquisitionMascotFile);
    AcquisitionMascotFile acquisitionMascotFile = (AcquisitionMascotFile) record;
    assertEquals((Long) 3L, acquisitionMascotFile.getId());
  }

  @Test
  public void getRecord_DataAnalysis() {
    Activity activity = entityManager.find(Activity.class, 5552L);

    Object record = activityService.getRecord(activity);

    assertTrue(record instanceof DataAnalysis);
    DataAnalysis dataAnalysis = (DataAnalysis) record;
    assertEquals((Long) 3L, dataAnalysis.getId());
  }

  @Test
  public void getRecord_Null() {
    Object record = activityService.getRecord(null);

    assertNull(record);
  }

  @Test
  public void search() throws Exception {
    ActivityFilter parameters = new ActivityFilter();
    parameters.actionType = Optional.of(ActionType.INSERT);
    parameters.tableName = Optional.of("submission");
    parameters.recordId = Optional.of(1L);

    List<Activity> activities = activityService.search(parameters);

    verify(authorizationService).checkAdminRole();
    assertFalse(activities.isEmpty());
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(null, activity.getJustification());
    assertEquals((Long) 1L, activity.getRecordId());
    assertEquals("submission", activity.getTableName());
    assertEquals(
        LocalDateTime.of(2011, 6, 14, 16, 32, 8).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals((Long) 3L, activity.getUser().getId());
  }

  @Test
  public void search_Null() throws Exception {
    List<Activity> activities = activityService.search(null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void all_Submission() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);

    List<Activity> activities = activityService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(9, activities.size());
    assertNotNull(find(activities, 5543));
    assertNotNull(find(activities, 5544));
    assertNotNull(find(activities, 5550));
    assertNotNull(find(activities, 5552));
    assertNotNull(find(activities, 5553));
    assertNotNull(find(activities, 5557));
    assertNotNull(find(activities, 5558));
    assertNotNull(find(activities, 5569));
    assertNotNull(find(activities, 5573));
  }

  @Test
  public void all_Submission_147() throws Exception {
    Submission submission = entityManager.find(Submission.class, 147L);

    List<Activity> activities = activityService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(3, activities.size());
    assertNotNull(find(activities, 5634));
    assertNotNull(find(activities, 5638));
    assertNotNull(find(activities, 5639));
  }

  @Test
  public void all_NullSubmission() throws Exception {
    List<Activity> activities = activityService.all((Submission) null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void allInsertActivities_Sample() throws Exception {
    Sample sample = new SubmissionSample(442L);

    List<Activity> activities = activityService.allInsertActivities(sample);

    verify(authorizationService).checkAdminRole();
    assertFalse(activities.isEmpty());
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(null, activity.getJustification());
    assertEquals((Long) 32L, activity.getRecordId());
    assertEquals("submission", activity.getTableName());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 10, 36, 33).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 3L, activity.getUser().getId());
  }

  @Test
  public void allInsertActivities_NullSample() throws Exception {
    List<Activity> activities = activityService.allInsertActivities((Sample) null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void allInsertActivities_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allInsertActivities(plate);

    verify(authorizationService).checkAdminRole();
    assertFalse(activities.isEmpty());
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(null, activity.getJustification());
    assertEquals((Long) 26L, activity.getRecordId());
    assertEquals("plate", activity.getTableName());
    assertEquals(
        LocalDateTime.of(2011, 11, 8, 13, 33, 21).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
  }

  @Test
  public void allInsertActivities_NullPlate() throws Exception {
    List<Activity> activities = activityService.allInsertActivities((Plate) null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void allUpdateActivities() throws Exception {
    SubmissionSample sample = new SubmissionSample(442L);

    List<Activity> activities = activityService.allUpdateActivities(sample);

    verify(authorizationService).checkAdminRole();
    assertFalse(activities.isEmpty());
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(null, activity.getJustification());
    assertEquals((Long) 4L, activity.getRecordId());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(
        LocalDateTime.of(2011, 10, 14, 14, 24, 23).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("sample", updateActivity.getTableName());
    assertEquals(sample.getId(), updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("status", updateActivity.getColumn());
    assertEquals(SampleStatus.ANALYSED.name(), updateActivity.getOldValue());
    assertEquals(SampleStatus.DATA_ANALYSIS.name(), updateActivity.getNewValue());
  }

  @Test
  public void allUpdateActivities_Null() throws Exception {
    List<Activity> activities = activityService.allUpdateActivities(null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void allUpdateSpotActivities() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allUpdateSpotActivities(plate);

    verify(authorizationService).checkAdminRole();
    // Ban.
    Activity activity = activities.get(0);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertEquals("problem with spots", activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 13, 53, 16, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(3, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("samplecontainer", updateActivity.getTableName());
    assertEquals((Long) 199L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("banned", updateActivity.getColumn());
    assertEquals("0", updateActivity.getOldValue());
    assertEquals("1", updateActivity.getNewValue());
  }

  @Test
  public void allUpdateSpotActivities_Null() throws Exception {
    List<Activity> activities = activityService.allUpdateSpotActivities(null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void allTreatmentActivities_Sample_Digestion() throws Exception {
    Sample sample = new SubmissionSample(559L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 195L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 8, 10, 42, 26).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
  }

  @Test
  public void allTreatmentActivities_Sample_Dilution() throws Exception {
    Sample sample = new SubmissionSample(569L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 210L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 9, 12, 20, 50).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
  }

  @Test
  public void allTreatmentActivities_Sample_Enrichment() throws Exception {
    Sample sample = new SubmissionSample(579L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 223L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 14, 14, 7, 16).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
  }

  @Test
  public void allTreatmentActivities_Sample_Solubilisation() throws Exception {
    Sample sample = new SubmissionSample(589L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 236L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 15, 9, 57, 51).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
  }

  @Test
  public void allTreatmentActivities_Sample_StandardAdition() throws Exception {
    Sample sample = new SubmissionSample(599L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 248L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 15, 13, 45, 42).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
  }

  @Test
  public void allTreatmentActivities_Sample_Fractionation() throws Exception {
    Sample sample = new SubmissionSample(628L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 300L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 22, 9, 55, 29).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals((Long) 4L, activity.getUser().getId());
    assertEquals(2, activity.getUpdates().size());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("samplecontainer", updateActivity.getTableName());
    assertEquals((Long) 1473L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("sampleId", updateActivity.getColumn());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals(sample.getId().toString(), updateActivity.getNewValue());
    updateActivity = activity.getUpdates().get(1);
    assertEquals("samplecontainer", updateActivity.getTableName());
    assertEquals((Long) 1485L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("sampleId", updateActivity.getColumn());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals(sample.getId().toString(), updateActivity.getNewValue());
  }

  @Test
  public void allTreatmentActivities_Sample_Transfer() throws Exception {
    Sample sample = new SubmissionSample(627L);

    List<Activity> activities = activityService.allTreatmentActivities(sample);

    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 298L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 22, 9, 51, 27).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("samplecontainer", updateActivity.getTableName());
    assertEquals((Long) 1472L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("sampleId", updateActivity.getColumn());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals(sample.getId().toString(), updateActivity.getNewValue());
  }

  @Test
  public void allTreatmentActivities_Sample_Null() throws Exception {
    List<Activity> activities = activityService.allTreatmentActivities((Sample) null);

    assertEquals(0, activities.size());
  }

  @Test
  public void allTreatmentActivities_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allTreatmentActivities(plate);

    verify(authorizationService).checkAdminRole();
    // Transfer.
    Activity activity = find(activities, 5573L);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 9L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 15, 07, 34, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("samplecontainer", updateActivity.getTableName());
    assertEquals((Long) 129L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("sampleId", updateActivity.getColumn());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals("1", updateActivity.getNewValue());
    // Fractionation.
    activity = find(activities, 5569L);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 8L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 13, 31, 13, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
    updateActivity = activity.getUpdates().get(0);
    assertEquals("samplecontainer", updateActivity.getTableName());
    assertEquals((Long) 128L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("sampleId", updateActivity.getColumn());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals("1", updateActivity.getNewValue());
  }

  @Test
  public void allTreatmentActivities_Plate_Null() throws Exception {
    List<Activity> activities = activityService.allTreatmentActivities((Plate) null);

    assertEquals(0, activities.size());
  }

  @Test
  public void allMsAnalysisActivities_Sample() throws Exception {
    Sample sample = new SubmissionSample(627L);

    List<Activity> activities = activityService.allMsAnalysisActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(0);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals((Long) 22L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 22, 9, 49, 9).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("sample", updateActivity.getTableName());
    assertEquals(sample.getId(), updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("status", updateActivity.getColumn());
    assertEquals(SampleStatus.TO_ANALYSE.name(), updateActivity.getOldValue());
    assertEquals(SampleStatus.ANALYSED.name(), updateActivity.getNewValue());
  }

  @Test
  public void allMsAnalysisActivities_Sample_Null() throws Exception {
    List<Activity> activities = activityService.allMsAnalysisActivities((Sample) null);

    assertEquals(0, activities.size());
  }

  @Test
  public void allMsAnalysisActivities_Plate() throws Exception {
    Plate plate = new Plate(115L);

    List<Activity> activities = activityService.allMsAnalysisActivities(plate);

    verify(authorizationService).checkAdminRole();
    Activity activity = find(activities, 5829L);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals((Long) 20L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2014, 10, 15, 15, 53, 34).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 4L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("sample", updateActivity.getTableName());
    assertEquals((Long) 612L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("status", updateActivity.getColumn());
    assertEquals(SampleStatus.RECEIVED.name(), updateActivity.getOldValue());
    assertEquals(SampleStatus.ANALYSED.name(), updateActivity.getNewValue());
  }

  @Test
  public void allMsAnalysisActivities_Plate_Null() throws Exception {
    List<Activity> activities = activityService.allMsAnalysisActivities((Plate) null);

    assertEquals(0, activities.size());
  }

  @Test
  public void allDataAnalysisActivities_Insert() throws Exception {
    Sample sample = new SubmissionSample(442L);

    List<Activity> activities = activityService.allDataAnalysisActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals((Long) 4L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 10, 14, 14, 24, 23, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("sample", updateActivity.getTableName());
    assertEquals(sample.getId(), updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("status", updateActivity.getColumn());
    assertEquals(SampleStatus.ANALYSED.name(), updateActivity.getOldValue());
    assertEquals(SampleStatus.DATA_ANALYSIS.name(), updateActivity.getNewValue());
  }

  @Test
  public void allDataAnalysisActivities_Update() throws Exception {
    Sample sample = new SubmissionSample(1L);

    List<Activity> activities = activityService.allDataAnalysisActivities(sample);

    Activity activity = activities.get(activities.size() - 2);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals((Long) 3L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 10, 14, 14, 24, 22, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("sample", updateActivity.getTableName());
    assertEquals(sample.getId(), updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("status", updateActivity.getColumn());
    assertEquals(SampleStatus.ANALYSED.name(), updateActivity.getOldValue());
    assertEquals(SampleStatus.DATA_ANALYSIS.name(), updateActivity.getNewValue());
    activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals((Long) 3L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 10, 14, 14, 24, 22, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals((Long) 2L, activity.getUser().getId());
    final Set<ComparableUpdateActivity> expecteds = new HashSet<>();
    UpdateActivity scoreUpdate = new UpdateActivity();
    scoreUpdate.setActionType(ActionType.UPDATE);
    scoreUpdate.setTableName("dataanalysis");
    scoreUpdate.setRecordId(3L);
    scoreUpdate.setColumn("score");
    scoreUpdate.setOldValue(null);
    scoreUpdate.setNewValue("123456: 95%");
    expecteds.add(new ComparableUpdateActivity(scoreUpdate));
    UpdateActivity statusUpdate = new UpdateActivity();
    statusUpdate.setActionType(ActionType.UPDATE);
    statusUpdate.setTableName("dataanalysis");
    statusUpdate.setRecordId(3L);
    statusUpdate.setColumn("status");
    statusUpdate.setOldValue(DataAnalysisStatus.TO_DO.name());
    statusUpdate.setNewValue(DataAnalysisStatus.ANALYSED.name());
    expecteds.add(new ComparableUpdateActivity(statusUpdate));
    UpdateActivity workTimeUpdate = new UpdateActivity();
    workTimeUpdate.setActionType(ActionType.UPDATE);
    workTimeUpdate.setTableName("dataanalysis");
    workTimeUpdate.setRecordId(3L);
    workTimeUpdate.setColumn("workTime");
    workTimeUpdate.setOldValue(null);
    workTimeUpdate.setNewValue("1.75");
    expecteds.add(new ComparableUpdateActivity(workTimeUpdate));
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(sample.getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SampleStatus.DATA_ANALYSIS.name());
    sampleStatusUpdate.setNewValue(SampleStatus.ANALYSED.name());
    expecteds.add(new ComparableUpdateActivity(sampleStatusUpdate));
    Set<ComparableUpdateActivity> actuals = new HashSet<>();
    for (UpdateActivity testUpdateActivity : activity.getUpdates()) {
      ComparableUpdateActivity comparableUpdateActivity =
          new ComparableUpdateActivity(testUpdateActivity);
      actuals.add(new ComparableUpdateActivity(testUpdateActivity));
      assertTrue("Activity " + comparableUpdateActivity + " not expected",
          expecteds.contains(new ComparableUpdateActivity(testUpdateActivity)));
    }
    for (ComparableUpdateActivity expected : expecteds) {
      assertTrue("Expected to find " + expected + " in sample update activity",
          actuals.contains(expected));
    }
  }

  @Test
  public void allDataAnalysisActivities_Null() throws Exception {
    List<Activity> activities = activityService.allDataAnalysisActivities(null);

    assertEquals(0, activities.size());
  }

  @Test
  public void allMascotFileActivities() {
    Sample sample = new SubmissionSample(442L);

    List<Activity> activities = activityService.allMascotFileActivities(sample);

    verify(authorizationService).checkAdminRole();
    Activity activity = activities.get(0);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("acquisition_to_mascotfile", activity.getTableName());
    assertEquals((Long) 1L, activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(
        LocalDateTime.of(2011, 10, 17, 11, 56, 18, 0).atZone(ZoneId.systemDefault()).toInstant(),
        activity.getTimestamp());
    assertEquals(1, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
    UpdateActivity updateActivity = activity.getUpdates().get(0);
    assertEquals("acquisition_to_mascotfile", updateActivity.getTableName());
    assertEquals((Long) 1L, updateActivity.getRecordId());
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("comments", updateActivity.getColumn());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals("complete report", updateActivity.getNewValue());
  }

  @Test
  public void allMascotFileActivities_Null() throws Exception {
    List<Activity> activities = activityService.allMascotFileActivities(null);

    assertEquals(0, activities.size());
  }

  @Test
  public void description_Submission_Insert() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5543L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(resources.message("Submission.INSERT"), description);
  }

  @Test
  public void description_Sample_Update() {
    Submission submission = entityManager.find(Submission.class, 147L);
    Sample sample = entityManager.find(Sample.class, 559L);
    Activity activity = entityManager.find(Activity.class, 5635L);
    UpdateActivity update = activity.getUpdates().get(0);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(resources.message("Sample.UPDATE", sample.getName(), update.getColumn(),
        update.getOldValue(), update.getNewValue()), description);
  }

  @Test
  public void description_Solubilisation_Insert() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5550L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Solubilisation\n"
        + "Sample FAM119A_band_01 in tube FAM119A_band_01 with 20 µl of Methanol", description);
  }

  @Test
  @SuppressWarnings("checkstyle:LineLength")
  public void description_Dilution_Insert() {
    Submission submission = entityManager.find(Submission.class, 32L);
    Activity activity = entityManager.find(Activity.class, 5561L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Dilution\n"
        + "Sample CAP_20111013_01 in tube CAP_20111013_01 with 10 µl of sample in 20 µl of Methanol",
        description);
  }

  @Test
  public void description_Digestion_Insert() {
    Submission submission = entityManager.find(Submission.class, 147L);
    Activity activity = entityManager.find(Activity.class, 5639L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Digestion\n" + "Sample POLR2A_20141008_1 on plate G_20141008_01 (A-1)\n"
        + "Sample POLR2A_20141008_2 on plate G_20141008_01 (B-1)", description);
  }

  @Test
  public void description_Enrichment_Insert() {
    Submission submission = entityManager.find(Submission.class, 150L);
    Activity activity = entityManager.find(Activity.class, 5717L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Enrichment\n" + "Sample POLR2A_20140914_01 on plate A_20141014_01 (A-1)\n"
        + "Sample POLR2A_20140914_02 on plate A_20141014_01 (B-1)", description);
  }

  @Test
  public void description_StandardAddition_Insert() {
    Submission submission = entityManager.find(Submission.class, 152L);
    Activity activity = entityManager.find(Activity.class, 5796L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Added standard\n"
        + "Standard adh (2 μg) added to sample POLR2A_20141015_11 on plate A_20141015_01 (A-6)\n"
        + "Standard adh (2 μg) added to sample POLR2A_20141015_12 on plate A_20141015_01 (B-6)",
        description);
  }

  @Test
  @SuppressWarnings("checkstyle:LineLength")
  public void description_Fractionation_Insert() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5557L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Fractionation\n"
        + "Sample FAM119A_band_01 from tube FAM119A_band_01 to tube FAM119A_band_01_F1 - fraction 1",
        description);
  }

  @Test
  public void description_Transfer_Insert() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5558L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(
        "Transfer\n"
            + "Sample FAM119A_band_01 from tube FAM119A_band_01 to tube FAM119A_band_01_T1",
        description);
  }

  @Test
  public void description_Transfer_Delete() {
    Submission submission = entityManager.find(Submission.class, 162L);
    Activity activity = entityManager.find(Activity.class, 5933L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(
        "Undone erroneous transfer\n"
            + "Sample POLR2B_20150527_02 from tube POLR2B_20150527_02 to plate A_20141022_02 (A-3)",
        description);
  }

  @Test
  public void description_MsAnalysis_Insert() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5544L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("MS analysis\n" + "Sample FAM119A_band_01 in tube FAM119A_band_01 - acquisition 1",
        description);
  }

  @Test
  @SuppressWarnings("checkstyle:LineLength")
  public void description_Mascot_Update() {
    Submission submission = entityManager.find(Submission.class, 32L);
    Activity activity = entityManager.find(Activity.class, 5555L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(
        "comments changed from null to complete report for Mascot file F006101.dat (2009-10-02) linked to acquisition CAP_20111013_01.A1",
        description);
  }

  @Test
  public void description_DataAnalysis_Insert() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5552L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(
        "Data analysis of protein requested - protein 123456, peptide null, max work time 2\n"
            + "Sample FAM119A_band_01 status changed from ANALYSED to DATA_ANALYSIS",
        description);
  }

  @Test
  @SuppressWarnings("checkstyle:LineLength")
  public void description_DataAnalysis_Update() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5553L);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(
        "Updated data analysis of protein requested - protein 123456, peptide null, max work time 2\n"
            + "Data analysis score changed from null to 123456: 95%\n"
            + "Data analysis workTime changed from null to 1.75\n"
            + "Data analysis status changed from TO_DO to ANALYSED\n"
            + "Sample FAM119A_band_01 status changed from DATA_ANALYSIS to ANALYSED",
        description);
    assertNotNull(description);
  }

  @Test
  public void description_Failsafe() {
    activityService = create(true);
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5544L);
    activity.setActionType(ActionType.UPDATE);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals("Updated msanalysis 1", description);
  }

  @Test
  public void description_Failsafe_Null() {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5544L);
    activity.setActionType(ActionType.UPDATE);

    String description = activityService.description(activity, submission, locale);

    verify(authorizationService).checkAdminRole();
    assertNull(description);
  }

  @Test
  public void description_NullActivity() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);

    String description = activityService.description(null, submission, locale);

    assertNull(description);
  }

  @Test
  public void description_NullSubmission() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5543L);

    String description = activityService.description(activity, null, locale);

    assertNull(description);
  }

  @Test
  public void description_NullLocale() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);
    Activity activity = entityManager.find(Activity.class, 5543L);

    String description = activityService.description(activity, submission, null);

    assertNull(description);
  }

  @Test
  public void plateDescription_Plate_Insert() {
    Plate plate = entityManager.find(Plate.class, 26L);
    Activity activity = entityManager.find(Activity.class, 5559L);

    String description = activityService.plateDescription(plate, activity, locale);

    verify(authorizationService).checkAdminRole();
    assertNotNull(description);
  }

  @Test
  public void plateDescription_Plate_Update() {
    Plate plate = entityManager.find(Plate.class, 26L);
    Activity activity = entityManager.find(Activity.class, 5570L);

    String description = activityService.plateDescription(plate, activity, locale);

    verify(authorizationService).checkAdminRole();
    assertNotNull(description);
  }

  @Test
  public void plateDescription_NullSample() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5543L);

    String description = activityService.plateDescription(null, activity, locale);

    assertNull(description);
  }

  @Test
  public void plateDescription_NullActivity() throws Exception {
    Plate plate = entityManager.find(Plate.class, 26L);

    String description = activityService.plateDescription(plate, null, locale);

    assertNull(description);
  }

  @Test
  public void plateDescription_NullLocale() throws Exception {
    Plate plate = entityManager.find(Plate.class, 26L);
    Activity activity = entityManager.find(Activity.class, 5543L);

    String description = activityService.plateDescription(plate, activity, null);

    assertNull(description);
  }

  @Test
  public void insertLogWithoutUpdates() throws Exception {
    User user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(45L);
    activity.setUser(user);
    activity.setTableName("sample");
    activity.setJustification("unit_test");
    activity.setUpdates(null);

    activityService.insert(activity);

    entityManager.flush();
    ActivityFilter parameters = new ActivityFilter();
    parameters.actionType = Optional.of(ActionType.INSERT);
    parameters.tableName = Optional.of("sample");
    parameters.recordId = Optional.of(45L);
    List<Activity> activities = activityService.search(parameters);
    assertFalse(activities.isEmpty());
    activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 45L, activity.getRecordId());
    assertEquals("sample", activity.getTableName());
    Instant beforeInsert =
        LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(activity.getTimestamp().isAfter(beforeInsert)
        || activity.getTimestamp().equals(beforeInsert));
    Instant afterInsert =
        LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(activity.getTimestamp().isBefore(afterInsert)
        || activity.getTimestamp().equals(afterInsert));
    assertEquals(true, activity.getUpdates().isEmpty());
    assertEquals(user, activity.getUser());
  }

  @Test
  public void insertLogWithUpdates() throws Exception {
    final User user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    final List<UpdateActivity> updateActivities = new LinkedList<>();
    UpdateActivity updateActivity = new UpdateActivity();
    updateActivity.setTableName("contaminant");
    updateActivity.setRecordId(12L);
    updateActivity.setActionType(ActionType.INSERT);
    updateActivity.setColumn(null);
    updateActivity.setOldValue(null);
    updateActivity.setNewValue(null);
    updateActivities.add(updateActivity);
    updateActivity = new UpdateActivity();
    updateActivity.setTableName("standard");
    updateActivity.setRecordId(25L);
    updateActivity.setActionType(ActionType.UPDATE);
    updateActivity.setColumn("name");
    updateActivity.setOldValue("old_name");
    updateActivity.setNewValue("new_name");
    updateActivities.add(updateActivity);
    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(45L);
    activity.setUser(user);
    activity.setTableName("sample");
    activity.setJustification("unit_test");
    activity.setUpdates(updateActivities);

    activityService.insert(activity);

    entityManager.flush();
    ActivityFilter parameters = new ActivityFilter();
    parameters.actionType = Optional.of(ActionType.INSERT);
    parameters.tableName = Optional.of("sample");
    parameters.recordId = Optional.of(45L);
    List<Activity> activities = activityService.search(parameters);
    assertFalse(activities.isEmpty());
    activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 45L, activity.getRecordId());
    assertEquals("sample", activity.getTableName());
    Instant beforeInsert =
        LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(activity.getTimestamp().isAfter(beforeInsert)
        || activity.getTimestamp().equals(beforeInsert));
    Instant afterInsert =
        LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(activity.getTimestamp().isBefore(afterInsert)
        || activity.getTimestamp().equals(afterInsert));
    assertEquals(user, activity.getUser());
    assertEquals(2, activity.getUpdates().size());
    updateActivity = activity.getUpdates().get(0);
    assertEquals(ActionType.INSERT, updateActivity.getActionType());
    assertEquals(null, updateActivity.getColumn());
    assertEquals(null, updateActivity.getNewValue());
    assertEquals(null, updateActivity.getOldValue());
    assertEquals((Long) 12L, updateActivity.getRecordId());
    assertEquals("contaminant", updateActivity.getTableName());
    updateActivity = activity.getUpdates().get(1);
    assertEquals(ActionType.UPDATE, updateActivity.getActionType());
    assertEquals("name", updateActivity.getColumn());
    assertEquals("new_name", updateActivity.getNewValue());
    assertEquals("old_name", updateActivity.getOldValue());
    assertEquals((Long) 25L, updateActivity.getRecordId());
    assertEquals("standard", updateActivity.getTableName());
  }
}
