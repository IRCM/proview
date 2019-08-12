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
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.text.MessageResource;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@WithMockUser(authorities = { UserRole.ADMIN })
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
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(ActivityService.class, locale);

  @Test
  public void record_Digestion() throws Exception {
    Activity activity = repository.findById(5639L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment digestion = (Treatment) object;
    assertEquals((Long) 195L, digestion.getId());
  }

  @Test
  public void record_Dilution() throws Exception {
    Activity activity = repository.findById(5680L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment dilution = (Treatment) object;
    assertEquals((Long) 210L, dilution.getId());
  }

  @Test
  public void record_Enrichment() throws Exception {
    Activity activity = repository.findById(5719L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment enrichment = (Treatment) object;
    assertEquals((Long) 225L, enrichment.getId());
  }

  @Test
  public void record_Fractionation() throws Exception {
    Activity activity = repository.findById(5659L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment fractionation = (Treatment) object;
    assertEquals((Long) 203L, fractionation.getId());
  }

  @Test
  public void record_Acquisition() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Acquisition.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Acquisition);
    Acquisition acquisition = (Acquisition) object;
    assertEquals((Long) 1L, acquisition.getId());
  }

  @Test
  public void record_MsAnalysis() throws Exception {
    Activity activity = repository.findById(5828L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof MsAnalysis);
    MsAnalysis msAnalysis = (MsAnalysis) object;
    assertEquals((Long) 19L, msAnalysis.getId());
  }

  @Test
  public void record_Plate() throws Exception {
    Activity activity = repository.findById(5559L).orElse(null);

    Object object = activityService.record(activity);

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

    assertTrue(object instanceof Well);
    Well well = (Well) object;
    assertEquals((Long) 128L, well.getId());
  }

  @Test
  public void record_Control() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Sample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(444L);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Control);
    Control control = (Control) object;
    assertEquals((Long) 444L, control.getId());
  }

  @Test
  public void record_Sample() throws Exception {
    Activity activity = repository.findById(5635L).orElse(null);

    Object object = activityService.record(activity);

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

    assertTrue(object instanceof SampleContainer);
    SampleContainer container = (SampleContainer) object;
    assertEquals((Long) 1L, container.getId());
  }

  @Test
  public void record_SubmissionSample() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Sample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

    assertTrue(object instanceof SubmissionSample);
    SubmissionSample sample = (SubmissionSample) object;
    assertEquals((Long) 1L, sample.getId());
  }

  @Test
  public void record_Solubilisation() throws Exception {
    Activity activity = repository.findById(5763L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment solubilisation = (Treatment) object;
    assertEquals((Long) 236L, solubilisation.getId());
  }

  @Test
  public void record_StandardAddition() throws Exception {
    Activity activity = repository.findById(5796L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment standardAddition = (Treatment) object;
    assertEquals((Long) 248L, standardAddition.getId());
  }

  @Test
  public void record_Submission() throws Exception {
    Activity activity = repository.findById(5543L).orElse(null);

    Object object = activityService.record(activity);

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

    assertTrue(object instanceof SubmissionFile);
    SubmissionFile file = (SubmissionFile) object;
    assertEquals((Long) 1L, file.getId());
  }

  @Test
  public void record_Protocol() throws Exception {
    Activity activity = repository.findById(5545L).orElse(null);

    Object object = activityService.record(activity);

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

    assertTrue(object instanceof Treatment);
    Treatment treatment = (Treatment) object;
    assertEquals((Long) 1L, treatment.getId());
  }

  @Test
  public void record_Transfer() throws Exception {
    Activity activity = repository.findById(5657L).orElse(null);

    Object object = activityService.record(activity);

    assertTrue(object instanceof Treatment);
    Treatment transfer = (Treatment) object;
    assertEquals((Long) 201L, transfer.getId());
  }

  @Test
  public void record_Tube() throws Exception {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Object object = activityService.record(activity);

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
    Activity activity = repository.findById(5650L).orElse(null);
    activity.setTableName(null);
    assertNull(activityService.record(activity));
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void record_AccessDenied_Anonymous() throws Exception {
    Activity activity = repository.findById(5639L).orElse(null);

    activityService.record(activity);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void record_AccessDenied() throws Exception {
    Activity activity = repository.findById(5639L).orElse(null);

    activityService.record(activity);
  }

  @Test
  public void all_Submission() throws Exception {
    Submission submission = submissionRepository.findById(1L).orElse(null);

    List<Activity> activities = activityService.all(submission);

    assertEquals(7, activities.size());
    assertTrue(find(activities, 5543).isPresent());
    assertTrue(find(activities, 5544).isPresent());
    assertTrue(find(activities, 5550).isPresent());
    assertTrue(find(activities, 5557).isPresent());
    assertTrue(find(activities, 5558).isPresent());
    assertTrue(find(activities, 5569).isPresent());
    assertTrue(find(activities, 5573).isPresent());
  }

  @Test
  public void all_Submission_147() throws Exception {
    Submission submission = submissionRepository.findById(147L).orElse(null);

    List<Activity> activities = activityService.all(submission);

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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void all_AccessDenied_Anonymous() throws Exception {
    Submission submission = submissionRepository.findById(1L).orElse(null);

    activityService.all(submission);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void all_AccessDenied() throws Exception {
    Submission submission = submissionRepository.findById(1L).orElse(null);

    activityService.all(submission);
  }

  @Test
  public void allInsertActivities_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allInsertActivities(plate);

    assertFalse(activities.isEmpty());
    Activity activity = activities.get(activities.size() - 1);
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(null, activity.getExplanation());
    assertEquals((Long) 26L, activity.getRecordId());
    assertEquals("plate", activity.getTableName());
    assertEquals(LocalDateTime.of(2011, 11, 8, 13, 33, 21), activity.getTimestamp());
    assertEquals(0, activity.getUpdates().size());
    assertEquals((Long) 2L, activity.getUser().getId());
  }

  @Test
  public void allInsertActivities_NullPlate() throws Exception {
    List<Activity> activities = activityService.allInsertActivities((Plate) null);

    assertTrue(activities.isEmpty());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void allInsertActivities_AccessDenied_Anonymous() throws Exception {
    Plate plate = new Plate(26L);

    activityService.allInsertActivities(plate);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allInsertActivities_AccessDenied() throws Exception {
    Plate plate = new Plate(26L);

    activityService.allInsertActivities(plate);
  }

  @Test
  public void allUpdateWellActivities() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allUpdateWellActivities(plate);

    // Ban.
    Activity activity = activities.get(0);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertEquals("problem with wells", activity.getExplanation());
    assertEquals(LocalDateTime.of(2011, 11, 16, 13, 53, 16, 0), activity.getTimestamp());
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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void allUpdateWellActivities_AccessDenied_Anonymous() throws Exception {
    Plate plate = new Plate(26L);

    activityService.allUpdateWellActivities(plate);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allUpdateWellActivities_AccessDenied() throws Exception {
    Plate plate = new Plate(26L);

    activityService.allUpdateWellActivities(plate);
  }

  @Test
  public void allTreatmentActivities_Plate() throws Exception {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allTreatmentActivities(plate);

    // Transfer.
    assertTrue(find(activities, 5573L).isPresent());
    Activity activity = find(activities, 5573L).get();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 9L, activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 07, 34, 0), activity.getTimestamp());
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
    assertEquals(LocalDateTime.of(2011, 11, 16, 13, 31, 13, 0), activity.getTimestamp());
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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void allTreatmentActivities_AccessDenied_Anonymous() throws Exception {
    Plate plate = new Plate(26L);

    activityService.allTreatmentActivities(plate);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allTreatmentActivities_AccessDenied() throws Exception {
    Plate plate = new Plate(26L);

    activityService.allTreatmentActivities(plate);
  }

  @Test
  public void allMsAnalysisActivities_Plate() throws Exception {
    Plate plate = new Plate(115L);

    List<Activity> activities = activityService.allMsAnalysisActivities(plate);

    assertTrue(find(activities, 5829L).isPresent());
    Activity activity = find(activities, 5829L).get();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals((Long) 20L, activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(LocalDateTime.of(2014, 10, 15, 15, 53, 34), activity.getTimestamp());
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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void allMsAnalysisActivities_AccessDenied_Anonymous() throws Exception {
    Plate plate = new Plate(115L);

    activityService.allMsAnalysisActivities(plate);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allMsAnalysisActivities_AccessDenied() throws Exception {
    Plate plate = new Plate(115L);

    activityService.allMsAnalysisActivities(plate);
  }

  @Test
  public void description_Insert() {
    Submission submission = submissionRepository.findById(1L).orElse(null);
    Activity activity = repository.findById(5543L).orElse(null);

    String description = activityService.description(activity, locale);

    assertEquals(resources.message("activity", activity.getActionType().ordinal(),
        activity.getTableName(), submission.getExperiment(), activity.getRecordId()), description);
  }

  @Test
  public void description_Update() {
    Submission submission = submissionRepository.findById(163L).orElse(null);
    Activity activity = repository.findById(5936L).orElse(null);

    String description = activityService.description(activity, locale);

    String[] descriptionLines = description.split("\n", -1);
    assertEquals(resources.message("activity", activity.getActionType().ordinal(),
        activity.getTableName(), submission.getExperiment(), activity.getRecordId()),
        descriptionLines[0]);
    for (int i = 0; i < activity.getUpdates().size(); i++) {
      UpdateActivity update = activity.getUpdates().get(i);
      String name = null;
      if (update.getTableName().equals(Submission.TABLE_NAME)) {
        name = submissionRepository.findById(update.getRecordId()).map(su -> su.getExperiment())
            .orElse(null);
      } else if (update.getTableName().equals(Sample.TABLE_NAME)) {
        name = sampleRepository.findById(update.getRecordId()).map(su -> su.getName()).orElse(null);
      } else if (update.getTableName().equals(Plate.TABLE_NAME)) {
        name = plateRepository.findById(update.getRecordId()).map(su -> su.getName()).orElse(null);
      }
      assertEquals(
          resources.message("update", update.getActionType().ordinal(), update.getTableName(), name,
              update.getRecordId(), update.getColumn(), update.getOldValue(), update.getNewValue()),
          descriptionLines[i + 1]);
    }
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void description_AccessDenied_Anonymous() throws Exception {
    Activity activity = repository.findById(5543L).orElse(null);

    activityService.description(activity, locale);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void description_AccessDenied() throws Exception {
    Activity activity = repository.findById(5543L).orElse(null);

    activityService.description(activity, locale);
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
    LocalDateTime beforeInsert = LocalDateTime.now().minusMinutes(2);
    assertTrue(activity.getTimestamp().isAfter(beforeInsert)
        || activity.getTimestamp().equals(beforeInsert));
    LocalDateTime afterInsert = LocalDateTime.now().plusMinutes(2);
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
    LocalDateTime beforeInsert = LocalDateTime.now().minusMinutes(2);
    assertTrue(activity.getTimestamp().isAfter(beforeInsert)
        || activity.getTimestamp().equals(beforeInsert));
    LocalDateTime afterInsert = LocalDateTime.now().plusMinutes(2);
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
