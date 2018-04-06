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

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.digestion.DigestionProtocol;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocol;
import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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

  @Test
  public void record_Submission() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5543L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Submission);
    Submission submission = (Submission) object;
    assertEquals((Long) 1L, submission.getId());
  }

  @Test
  public void record_Sample() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5635L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof SubmissionSample);
    SubmissionSample sample = (SubmissionSample) object;
    assertEquals((Long) 559L, sample.getId());
  }

  @Test
  public void record_Plate() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5559L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Plate);
    Plate plate = (Plate) object;
    assertEquals((Long) 26L, plate.getId());
  }

  @Test
  public void record_DigestionProtocol() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5545L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof DigestionProtocol);
    DigestionProtocol protocol = (DigestionProtocol) object;
    assertEquals((Long) 1L, protocol.getId());
  }

  @Test
  public void record_EnrichmentProtocol() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5546L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof EnrichmentProtocol);
    EnrichmentProtocol protocol = (EnrichmentProtocol) object;
    assertEquals((Long) 2L, protocol.getId());
  }

  @Test
  public void record_Digestion() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5639L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Digestion);
    Digestion digestion = (Digestion) object;
    assertEquals((Long) 195L, digestion.getId());
  }

  @Test
  public void record_Dilution() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5680L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Dilution);
    Dilution dilution = (Dilution) object;
    assertEquals((Long) 210L, dilution.getId());
  }

  @Test
  public void record_Enrichment() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5719L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Enrichment);
    Enrichment enrichment = (Enrichment) object;
    assertEquals((Long) 225L, enrichment.getId());
  }

  @Test
  public void record_Fractionation() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5659L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Fractionation);
    Fractionation fractionation = (Fractionation) object;
    assertEquals((Long) 203L, fractionation.getId());
  }

  @Test
  public void record_Solubilisation() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5763L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Solubilisation);
    Solubilisation solubilisation = (Solubilisation) object;
    assertEquals((Long) 236L, solubilisation.getId());
  }

  @Test
  public void record_StandardAddition() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5796L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof StandardAddition);
    StandardAddition standardAddition = (StandardAddition) object;
    assertEquals((Long) 248L, standardAddition.getId());
  }

  @Test
  public void record_Transfer() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5657L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Transfer);
    Transfer transfer = (Transfer) object;
    assertEquals((Long) 201L, transfer.getId());
  }

  @Test
  public void record_MsAnalysis() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5828L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof MsAnalysis);
    MsAnalysis msAnalysis = (MsAnalysis) object;
    assertEquals((Long) 19L, msAnalysis.getId());
  }

  @Test
  public void record_DataAnalysis() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5566L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof DataAnalysis);
    DataAnalysis dataAnalysis = (DataAnalysis) object;
    assertEquals((Long) 5L, dataAnalysis.getId());
  }

  @Test
  public void record_Null() throws Exception {
    assertNull(activityService.record(null));
  }

  @Test
  public void record_TableNameNull() throws Exception {
    Activity activity = entityManager.find(Activity.class, 5566L);
    activity.setTableName(null);
    assertNull(activityService.record(activity));
  }

  @Test
  public void all_Submission() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);

    List<Activity> activities = activityService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(9, activities.size());
    assertTrue(find(activities, 5543).isPresent());
    assertTrue(find(activities, 5544).isPresent());
    assertTrue(find(activities, 5550).isPresent());
    assertTrue(find(activities, 5552).isPresent());
    assertTrue(find(activities, 5553).isPresent());
    assertTrue(find(activities, 5557).isPresent());
    assertTrue(find(activities, 5558).isPresent());
    assertTrue(find(activities, 5569).isPresent());
    assertTrue(find(activities, 5573).isPresent());
  }

  @Test
  public void all_Submission_147() throws Exception {
    Submission submission = entityManager.find(Submission.class, 147L);

    List<Activity> activities = activityService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(7, activities.size());
    assertTrue(find(activities, 5634).isPresent());
    assertTrue(find(activities, 5635).isPresent());
    assertTrue(find(activities, 5636).isPresent());
    assertTrue(find(activities, 5638).isPresent());
    assertTrue(find(activities, 5639).isPresent());
    assertTrue(find(activities, 5640).isPresent());
    assertTrue(find(activities, 5641).isPresent());
  }

  @Test
  public void all_NullSubmission() throws Exception {
    List<Activity> activities = activityService.all((Submission) null);

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
    assertEquals(null, activity.getExplanation());
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
  public void allUpdateWellActivities() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allUpdateWellActivities(plate);

    verify(authorizationService).checkAdminRole();
    // Ban.
    Activity activity = activities.get(0);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertEquals("problem with wells", activity.getExplanation());
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
  public void allUpdateWellActivities_Null() throws Exception {
    List<Activity> activities = activityService.allUpdateWellActivities(null);

    assertTrue(activities.isEmpty());
  }

  @Test
  public void allTreatmentActivities_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allTreatmentActivities(plate);

    verify(authorizationService).checkAdminRole();
    // Transfer.
    assertTrue(find(activities, 5573L).isPresent());
    Activity activity = find(activities, 5573L).get();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 9L, activity.getRecordId());
    assertEquals(null, activity.getExplanation());
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
    assertTrue(find(activities, 5569L).isPresent());
    activity = find(activities, 5569L).get();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 8L, activity.getRecordId());
    assertEquals(null, activity.getExplanation());
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
  public void allMsAnalysisActivities_Plate() throws Exception {
    Plate plate = new Plate(115L);

    List<Activity> activities = activityService.allMsAnalysisActivities(plate);

    verify(authorizationService).checkAdminRole();
    assertTrue(find(activities, 5829L).isPresent());
    Activity activity = find(activities, 5829L).get();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals((Long) 20L, activity.getRecordId());
    assertEquals(null, activity.getExplanation());
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
        "Undone transfer\n"
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
    activity.setExplanation("unit_test");
    activity.setUpdates(null);

    activityService.insert(activity);

    entityManager.flush();
    JPAQuery<Activity> query = queryFactory.select(QActivity.activity);
    query.from(QActivity.activity);
    query.where(QActivity.activity.actionType.eq(ActionType.INSERT));
    query.where(QActivity.activity.tableName.eq("sample"));
    query.where(QActivity.activity.recordId.eq(45L));
    List<Activity> activities = query.fetch();
    assertFalse(activities.isEmpty());
    activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("unit_test", activity.getExplanation());
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
    activity.setExplanation("unit_test");
    activity.setUpdates(updateActivities);

    activityService.insert(activity);

    entityManager.flush();
    JPAQuery<Activity> query = queryFactory.select(QActivity.activity);
    query.from(QActivity.activity);
    query.where(QActivity.activity.actionType.eq(ActionType.INSERT));
    query.where(QActivity.activity.tableName.eq("sample"));
    query.where(QActivity.activity.recordId.eq(45L));
    List<Activity> activities = query.fetch();
    assertFalse(activities.isEmpty());
    activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("unit_test", activity.getExplanation());
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
