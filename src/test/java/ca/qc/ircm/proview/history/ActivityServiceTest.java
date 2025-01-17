package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link ActivityService}.
 */
@ServiceTestAnnotations
@WithMockUser(authorities = { UserRole.ADMIN })
public class ActivityServiceTest extends AbstractServiceTestCase {
  private static final QActivity qactivity = QActivity.activity;
  private static final String MESSAGES_PREFIX = messagePrefix(ActivityService.class);
  @Autowired
  private ActivityService activityService;
  @Autowired
  private ActivityRepository repository;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Autowired
  private SampleRepository sampleRepository;
  @Autowired
  private PlateRepository plateRepository;
  @Autowired
  private MessageSource messageSource;
  private Locale locale = Locale.ENGLISH;

  @Test
  public void record_Digestion() {
    Activity activity = repository.findById(5639L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment digestion = (Treatment) object.get();
    assertEquals((Long) 195L, digestion.getId());
  }

  @Test
  public void record_Dilution() {
    Activity activity = repository.findById(5680L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment dilution = (Treatment) object.get();
    assertEquals((Long) 210L, dilution.getId());
  }

  @Test
  public void record_Enrichment() {
    Activity activity = repository.findById(5719L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment enrichment = (Treatment) object.get();
    assertEquals((Long) 225L, enrichment.getId());
  }

  @Test
  public void record_Fractionation() {
    Activity activity = repository.findById(5659L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment fractionation = (Treatment) object.get();
    assertEquals((Long) 203L, fractionation.getId());
  }

  @Test
  public void record_Acquisition() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Acquisition.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Acquisition);
    Acquisition acquisition = (Acquisition) object.get();
    assertEquals((Long) 1L, acquisition.getId());
  }

  @Test
  public void record_MsAnalysis() {
    Activity activity = repository.findById(5828L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof MsAnalysis);
    MsAnalysis msAnalysis = (MsAnalysis) object.get();
    assertEquals((Long) 19L, msAnalysis.getId());
  }

  @Test
  public void record_Plate() {
    Activity activity = repository.findById(5559L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Plate);
    Plate plate = (Plate) object.get();
    assertEquals((Long) 26L, plate.getId());
  }

  @Test
  public void record_Well() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(128L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Well);
    Well well = (Well) object.get();
    assertEquals((Long) 128L, well.getId());
  }

  @Test
  public void record_Control() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Sample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(444L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Control);
    Control control = (Control) object.get();
    assertEquals((Long) 444L, control.getId());
  }

  @Test
  public void record_Sample() {
    Activity activity = repository.findById(5635L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof SubmissionSample);
    SubmissionSample sample = (SubmissionSample) object.get();
    assertEquals((Long) 559L, sample.getId());
  }

  @Test
  public void record_SampleContainer() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof SampleContainer);
    SampleContainer container = (SampleContainer) object.get();
    assertEquals((Long) 1L, container.getId());
  }

  @Test
  public void record_SubmissionSample() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Sample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof SubmissionSample);
    SubmissionSample sample = (SubmissionSample) object.get();
    assertEquals((Long) 1L, sample.getId());
  }

  @Test
  public void record_Solubilisation() {
    Activity activity = repository.findById(5763L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment solubilisation = (Treatment) object.get();
    assertEquals((Long) 236L, solubilisation.getId());
  }

  @Test
  public void record_StandardAddition() {
    Activity activity = repository.findById(5796L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment standardAddition = (Treatment) object.get();
    assertEquals((Long) 248L, standardAddition.getId());
  }

  @Test
  public void record_Submission() {
    Activity activity = repository.findById(5543L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Submission);
    Submission submission = (Submission) object.get();
    assertEquals((Long) 1L, submission.getId());
  }

  @Test
  public void record_SubmissionFile() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SubmissionFile.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof SubmissionFile);
    SubmissionFile file = (SubmissionFile) object.get();
    assertEquals((Long) 1L, file.getId());
  }

  @Test
  public void record_Protocol() {
    Activity activity = repository.findById(5545L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Protocol);
    Protocol protocol = (Protocol) object.get();
    assertEquals((Long) 1L, protocol.getId());
  }

  @Test
  public void record_TreatedSample() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(TreatedSample.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof TreatedSample);
    TreatedSample ts = (TreatedSample) object.get();
    assertEquals((Long) 1L, ts.getId());
  }

  @Test
  public void record_Treatment() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Treatment.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment treatment = (Treatment) object.get();
    assertEquals((Long) 1L, treatment.getId());
  }

  @Test
  public void record_Transfer() {
    Activity activity = repository.findById(5657L).orElseThrow();

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Treatment);
    Treatment transfer = (Treatment) object.get();
    assertEquals((Long) 201L, transfer.getId());
  }

  @Test
  public void record_Tube() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(SampleContainer.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Tube);
    Tube tube = (Tube) object.get();
    assertEquals((Long) 1L, tube.getId());
  }

  @Test
  public void record_ForgotPassword() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(ForgotPassword.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(7L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof ForgotPassword);
    ForgotPassword forgotPassword = (ForgotPassword) object.get();
    assertEquals((Long) 7L, forgotPassword.getId());
  }

  @Test
  public void record_Address() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Address.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Address);
    Address address = (Address) object.get();
    assertEquals((Long) 1L, address.getId());
  }

  @Test
  public void record_Laboratory() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(Laboratory.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(2L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof Laboratory);
    Laboratory laboratory = (Laboratory) object.get();
    assertEquals((Long) 2L, laboratory.getId());
  }

  @Test
  public void record_PhoneNumber() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(PhoneNumber.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof PhoneNumber);
    PhoneNumber phoneNumber = (PhoneNumber) object.get();
    assertEquals((Long) 1L, phoneNumber.getId());
  }

  @Test
  public void record_User() {
    Activity activity = mock(Activity.class);
    when(activity.getTableName()).thenReturn(User.TABLE_NAME);
    when(activity.getRecordId()).thenReturn(1L);

    Optional<Object> object = activityService.record(activity);

    assertTrue(object.orElseThrow() instanceof User);
    User user = (User) object.get();
    assertEquals((Long) 1L, user.getId());
  }

  @Test
  @WithAnonymousUser
  public void record_AccessDenied_Anonymous() {
    Activity activity = repository.findById(5639L).orElseThrow();

    assertThrows(AccessDeniedException.class, () -> activityService.record(activity));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void record_AccessDenied() {
    Activity activity = repository.findById(5639L).orElseThrow();

    assertThrows(AccessDeniedException.class, () -> activityService.record(activity));
  }

  @Test
  public void all_Submission() {
    Submission submission = submissionRepository.findById(1L).orElseThrow();

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
  public void all_Submission_147() {
    Submission submission = submissionRepository.findById(147L).orElseThrow();

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
  @WithAnonymousUser
  public void all_AccessDenied_Anonymous() {
    Submission submission = submissionRepository.findById(1L).orElseThrow();

    assertThrows(AccessDeniedException.class, () -> activityService.all(submission));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void all_AccessDenied() {
    Submission submission = submissionRepository.findById(1L).orElseThrow();

    assertThrows(AccessDeniedException.class, () -> activityService.all(submission));
  }

  @Test
  public void allInsertActivities_Plate() {
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
  @WithAnonymousUser
  public void allInsertActivities_AccessDenied_Anonymous() {
    Plate plate = new Plate(26L);

    assertThrows(AccessDeniedException.class, () -> activityService.allInsertActivities(plate));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allInsertActivities_AccessDenied() {
    Plate plate = new Plate(26L);

    assertThrows(AccessDeniedException.class, () -> activityService.allInsertActivities(plate));
  }

  @Test
  public void allUpdateWellActivities() {
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
  @WithAnonymousUser
  public void allUpdateWellActivities_AccessDenied_Anonymous() {
    Plate plate = new Plate(26L);

    assertThrows(AccessDeniedException.class, () -> activityService.allUpdateWellActivities(plate));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allUpdateWellActivities_AccessDenied() {
    Plate plate = new Plate(26L);

    assertThrows(AccessDeniedException.class, () -> activityService.allUpdateWellActivities(plate));
  }

  @Test
  public void allTreatmentActivities_Plate() {
    Plate plate = new Plate(26L);

    List<Activity> activities = activityService.allTreatmentActivities(plate);

    // Transfer.
    assertTrue(find(activities, 5573L).isPresent());
    Activity activity = find(activities, 5573L).get();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals((Long) 9L, activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 7, 34, 0), activity.getTimestamp());
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
  @WithAnonymousUser
  public void allTreatmentActivities_AccessDenied_Anonymous() {
    Plate plate = new Plate(26L);

    assertThrows(AccessDeniedException.class, () -> activityService.allTreatmentActivities(plate));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allTreatmentActivities_AccessDenied() {
    Plate plate = new Plate(26L);

    assertThrows(AccessDeniedException.class, () -> activityService.allTreatmentActivities(plate));
  }

  @Test
  public void allMsAnalysisActivities_Plate() {
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
  @WithAnonymousUser
  public void allMsAnalysisActivities_AccessDenied_Anonymous() {
    Plate plate = new Plate(115L);

    assertThrows(AccessDeniedException.class, () -> activityService.allMsAnalysisActivities(plate));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void allMsAnalysisActivities_AccessDenied() {
    Plate plate = new Plate(115L);

    assertThrows(AccessDeniedException.class, () -> activityService.allMsAnalysisActivities(plate));
  }

  @Test
  public void description_Insert() {
    Submission submission = submissionRepository.findById(1L).orElseThrow();
    Activity activity = repository.findById(5543L).orElseThrow();

    Optional<String> description = activityService.description(activity, locale);

    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + "activity",
            new Object[] { activity.getActionType().ordinal(), activity.getTableName(),
                submission.getExperiment(), activity.getRecordId() },
            locale),
        description.orElse(""));
  }

  @Test
  public void description_Update() {
    Submission submission = submissionRepository.findById(163L).orElseThrow();
    Activity activity = repository.findById(5936L).orElseThrow();

    Optional<String> description = activityService.description(activity, locale);

    String[] descriptionLines = description.orElse("").split("\n", -1);
    assertEquals(
        messageSource.getMessage(
            MESSAGES_PREFIX + "activity", new Object[] { activity.getActionType().ordinal(),
                activity.getTableName(), submission.getExperiment(), activity.getRecordId() },
            locale),
        descriptionLines[0]);
    for (int i = 0; i < activity.getUpdates().size(); i++) {
      UpdateActivity update = activity.getUpdates().get(i);
      String name = switch (update.getTableName()) {
        case Submission.TABLE_NAME -> submissionRepository.findById(update.getRecordId())
            .map(Submission::getExperiment).orElseThrow();
        case Sample.TABLE_NAME ->
          sampleRepository.findById(update.getRecordId()).map(Sample::getName).orElseThrow();
        case Plate.TABLE_NAME ->
          plateRepository.findById(update.getRecordId()).map(Plate::getName).orElseThrow();
        default -> null;
      };
      assertEquals(messageSource.getMessage(MESSAGES_PREFIX + "update",
          new Object[] { update.getActionType().ordinal(), update.getTableName(), name,
              update.getRecordId(), update.getColumn(), update.getOldValue(),
              update.getNewValue() },
          locale), descriptionLines[i + 1]);
    }
  }

  @Test
  @WithAnonymousUser
  public void description_AccessDenied_Anonymous() {
    Activity activity = repository.findById(5543L).orElseThrow();

    assertThrows(AccessDeniedException.class, () -> activityService.description(activity, locale));
  }

  @Test
  @WithMockUser(authorities = { UserRole.MANAGER, UserRole.USER })
  public void description_AccessDenied() {
    Activity activity = repository.findById(5543L).orElseThrow();

    assertThrows(AccessDeniedException.class, () -> activityService.description(activity, locale));
  }

  @Test
  public void insertLogWithoutUpdates() {
    User user = new User(4L);
    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(45L);
    activity.setUser(user);
    activity.setTableName("sample");
    activity.setExplanation("unit_test");
    activity.setUpdates(new ArrayList<>());

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
  public void insertLogWithUpdates() {
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
