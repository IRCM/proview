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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ActivityServiceTest extends AbstractServiceTestCase {
  private static final QActivity qactivity = QActivity.activity;
  @Inject
  private ActivityService activityService;
  @Inject
  private ActivityRepository repository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private PlateRepository plateRepository;
  @MockBean
  private AuthorizationService authorizationService;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(ActivityService.class, locale);

  @Test
  public void record_DataAnalysis() throws Exception {
    Activity activity = repository.findOne(5566L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof DataAnalysis);
    DataAnalysis dataAnalysis = (DataAnalysis) object;
    assertEquals((Long) 5L, dataAnalysis.getId());
  }

  @Test
  public void record_Digestion() throws Exception {
    Activity activity = repository.findOne(5639L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Digestion);
    Digestion digestion = (Digestion) object;
    assertEquals((Long) 195L, digestion.getId());
  }

  @Test
  public void record_Dilution() throws Exception {
    Activity activity = repository.findOne(5680L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Dilution);
    Dilution dilution = (Dilution) object;
    assertEquals((Long) 210L, dilution.getId());
  }

  @Test
  public void record_Enrichment() throws Exception {
    Activity activity = repository.findOne(5719L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Enrichment);
    Enrichment enrichment = (Enrichment) object;
    assertEquals((Long) 225L, enrichment.getId());
  }

  @Test
  public void record_Fractionation() throws Exception {
    Activity activity = repository.findOne(5659L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Fractionation);
    Fractionation fractionation = (Fractionation) object;
    assertEquals((Long) 203L, fractionation.getId());
  }

  @Test
  public void record_Acquisition() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Acquisition.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Acquisition);
    Acquisition acquisition = (Acquisition) object;
    assertEquals((Long) 1L, acquisition.getId());
  }

  @Test
  public void record_MsAnalysis() throws Exception {
    Activity activity = repository.findOne(5828L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof MsAnalysis);
    MsAnalysis msAnalysis = (MsAnalysis) object;
    assertEquals((Long) 19L, msAnalysis.getId());
  }

  @Test
  public void record_Plate() throws Exception {
    Activity activity = repository.findOne(5559L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Plate);
    Plate plate = (Plate) object;
    assertEquals((Long) 26L, plate.getId());
  }

  @Test
  public void record_Well() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(128L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Well);
    Well well = (Well) object;
    assertEquals((Long) 128L, well.getId());
  }

  @Test
  public void record_Contaminant() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Contaminant.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(2L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Contaminant);
    Contaminant contaminant = (Contaminant) object;
    assertEquals((Long) 2L, contaminant.getId());
  }

  @Test
  public void record_Control() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Sample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(444L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Control);
    Control control = (Control) object;
    assertEquals((Long) 444L, control.getId());
  }

  @Test
  public void record_Sample() throws Exception {
    Activity activity = repository.findOne(5635L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof SubmissionSample);
    SubmissionSample sample = (SubmissionSample) object;
    assertEquals((Long) 559L, sample.getId());
  }

  @Test
  public void record_SampleContainer() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof SampleContainer);
    SampleContainer container = (SampleContainer) object;
    assertEquals((Long) 1L, container.getId());
  }

  @Test
  public void record_Standard() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Standard.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(4L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Standard);
    Standard standard = (Standard) object;
    assertEquals((Long) 4L, standard.getId());
  }

  @Test
  public void record_SubmissionSample() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Sample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof SubmissionSample);
    SubmissionSample sample = (SubmissionSample) object;
    assertEquals((Long) 1L, sample.getId());
  }

  @Test
  public void record_Solubilisation() throws Exception {
    Activity activity = repository.findOne(5763L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Solubilisation);
    Solubilisation solubilisation = (Solubilisation) object;
    assertEquals((Long) 236L, solubilisation.getId());
  }

  @Test
  public void record_StandardAddition() throws Exception {
    Activity activity = repository.findOne(5796L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof StandardAddition);
    StandardAddition standardAddition = (StandardAddition) object;
    assertEquals((Long) 248L, standardAddition.getId());
  }

  @Test
  public void record_Submission() throws Exception {
    Activity activity = repository.findOne(5543L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Submission);
    Submission submission = (Submission) object;
    assertEquals((Long) 1L, submission.getId());
  }

  @Test
  public void record_SubmissionFile() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SubmissionFile.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof SubmissionFile);
    SubmissionFile file = (SubmissionFile) object;
    assertEquals((Long) 1L, file.getId());
  }

  @Test
  public void record_Protocol() throws Exception {
    Activity activity = repository.findOne(5545L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Protocol);
    Protocol protocol = (Protocol) object;
    assertEquals((Long) 1L, protocol.getId());
  }

  @Test
  public void record_TreatedSample() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(TreatedSample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof TreatedSample);
    TreatedSample ts = (TreatedSample) object;
    assertEquals((Long) 1L, ts.getId());
  }

  @Test
  public void record_Treatment() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Treatment.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Treatment);
    Treatment treatment = (Treatment) object;
    assertEquals((Long) 1L, treatment.getId());
  }

  @Test
  public void record_Transfer() throws Exception {
    Activity activity = repository.findOne(5657L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Transfer);
    Transfer transfer = (Transfer) object;
    assertEquals((Long) 201L, transfer.getId());
  }

  @Test
  public void record_Tube() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Tube);
    Tube tube = (Tube) object;
    assertEquals((Long) 1L, tube.getId());
  }

  @Test
  public void record_ForgotPassword() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(ForgotPassword.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(7L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof ForgotPassword);
    ForgotPassword forgotPassword = (ForgotPassword) object;
    assertEquals((Long) 7L, forgotPassword.getId());
  }

  @Test
  public void record_Address() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Address.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Address);
    Address address = (Address) object;
    assertEquals((Long) 1L, address.getId());
  }

  @Test
  public void record_Laboratory() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Laboratory.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(2L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof Laboratory);
    Laboratory laboratory = (Laboratory) object;
    assertEquals((Long) 2L, laboratory.getId());
  }

  @Test
  public void record_PhoneNumber() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(PhoneNumber.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof PhoneNumber);
    PhoneNumber phoneNumber = (PhoneNumber) object;
    assertEquals((Long) 1L, phoneNumber.getId());
  }

  @Test
  public void record_User() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(User.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    verify(authorizationService).checkAdminRole();
    assertTrue(object instanceof User);
    User user = (User) object;
    assertEquals((Long) 1L, user.getId());
  }

  @Test
  public void record_Null() throws Exception {
    assertNull(activityService.record(null));
  }

  @Test
  public void record_TableNameNull() throws Exception {
    Activity activity = repository.findOne(5566L);
    activity.setTableName(null);
    assertNull(activityService.record(activity));
  }

  @Test
  public void all_Submission() throws Exception {
    Submission submission = submissionRepository.findOne(1L);

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
    Submission submission = submissionRepository.findOne(147L);

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
  public void description_Insert() {
    Submission submission = submissionRepository.findOne(1L);
    Activity activity = repository.findOne(5543L);

    String description = activityService.description(activity, locale);

    verify(authorizationService).checkAdminRole();
    assertEquals(resources.message("activity", activity.getActionType().ordinal(),
        activity.getTableName(), submission.getName(), activity.getRecordId()), description);
  }

  @Test
  public void description_Update() {
    Submission submission = submissionRepository.findOne(163L);
    Activity activity = repository.findOne(5936L);

    String description = activityService.description(activity, locale);

    verify(authorizationService).checkAdminRole();
    String[] descriptionLines = description.split("\n", -1);
    assertEquals(resources.message("activity", activity.getActionType().ordinal(),
        activity.getTableName(), submission.getName(), activity.getRecordId()),
        descriptionLines[0]);
    for (int i = 0; i < activity.getUpdates().size(); i++) {
      UpdateActivity update = activity.getUpdates().get(i);
      String name = null;
      if (update.getTableName().equals(Submission.TABLE_NAME)) {
        name = submissionRepository.findOne(update.getRecordId()).getName();
      } else if (update.getTableName().equals(Sample.TABLE_NAME)) {
        name = sampleRepository.findOne(update.getRecordId()).getName();
      } else if (update.getTableName().equals(Plate.TABLE_NAME)) {
        name = plateRepository.findOne(update.getRecordId()).getName();
      }
      assertEquals(
          resources.message("update", update.getActionType().ordinal(), update.getTableName(), name,
              update.getRecordId(), update.getColumn(), update.getOldValue(), update.getNewValue()),
          descriptionLines[i + 1]);
    }
  }

  @Test
  public void insertLogWithoutUpdates() throws Exception {
    User user = new User(4L);
    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(45L);
    activity.setUser(user);
    activity.setTableName("sample");
    activity.setExplanation("unit_test");
    activity.setUpdates(null);

    activityService.insert(activity);

    repository.flush();
    BooleanExpression predicate = qactivity.actionType.eq(ActionType.INSERT)
        .and(qactivity.tableName.eq(Sample.TABLE_NAME)).and(qactivity.recordId.eq(45L));
    List<Activity> activities = Lists.newArrayList(repository.findAll(predicate));
    assertFalse(activities.isEmpty());
    activity = activities.get(activities.size() - 1);
    refresh(activity);
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
    assertEquals(user.getId(), activity.getUser().getId());
  }

  @Test
  public void insertLogWithUpdates() throws Exception {
    final User user = new User(4L);
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

    repository.flush();
    BooleanExpression predicate = qactivity.actionType.eq(ActionType.INSERT)
        .and(qactivity.tableName.eq(Sample.TABLE_NAME)).and(qactivity.recordId.eq(45L));
    List<Activity> activities = Lists.newArrayList(repository.findAll(predicate));
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
    assertEquals(user.getId(), activity.getUser().getId());
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
