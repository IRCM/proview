package ca.qc.ircm.proview.submission;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionActivityServiceImplTest {
  private SubmissionActivityServiceImpl submissionActivityServiceImpl;
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
    submissionActivityServiceImpl =
        new SubmissionActivityServiceImpl(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    Submission submission = new Submission();
    submission.setId(123456L);
    submission.setSubmissionDate(Instant.now());

    Activity activity = submissionActivityServiceImpl.insert(submission);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Submission oldSubmission = entityManager.find(Submission.class, 1L);
    entityManager.detach(oldSubmission);
    Submission newSubmission = entityManager.find(Submission.class, 1L);
    entityManager.detach(newSubmission);
    final User oldUser = new User(3L);
    final Laboratory oldLaboratory = new Laboratory(2L);
    User newUser = new User(4L);
    Laboratory newLaboratory = new Laboratory(1L);
    newSubmission.setLaboratory(newLaboratory);
    newSubmission.setUser(newUser);
    newSubmission.setSubmissionDate(Instant.now());

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(newSubmission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(newSubmission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity userActivity = new UpdateActivity();
    userActivity.setActionType(ActionType.UPDATE);
    userActivity.setTableName("submission");
    userActivity.setRecordId(newSubmission.getId());
    userActivity.setColumn("userId");
    userActivity.setOldValue(String.valueOf(oldUser.getId()));
    userActivity.setNewValue(String.valueOf(newUser.getId()));
    expectedUpdateActivities.add(userActivity);
    UpdateActivity laboratoryActivity = new UpdateActivity();
    laboratoryActivity.setActionType(ActionType.UPDATE);
    laboratoryActivity.setTableName("submission");
    laboratoryActivity.setRecordId(newSubmission.getId());
    laboratoryActivity.setColumn("laboratoryId");
    laboratoryActivity.setOldValue(String.valueOf(oldLaboratory.getId()));
    laboratoryActivity.setNewValue(String.valueOf(newLaboratory.getId()));
    expectedUpdateActivities.add(laboratoryActivity);
    UpdateActivity submissionDateActivity = new UpdateActivity();
    submissionDateActivity.setActionType(ActionType.UPDATE);
    submissionDateActivity.setTableName("submission");
    submissionDateActivity.setRecordId(newSubmission.getId());
    submissionDateActivity.setColumn("submissionDate");
    DateTimeFormatter instantFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    submissionDateActivity.setOldValue(instantFormatter.format(
        LocalDateTime.ofInstant(oldSubmission.getSubmissionDate(), ZoneId.systemDefault())));
    submissionDateActivity.setNewValue(instantFormatter.format(
        LocalDateTime.ofInstant(newSubmission.getSubmissionDate(), ZoneId.systemDefault())));
    expectedUpdateActivities.add(submissionDateActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    Submission submission = entityManager.find(Submission.class, 1L);
    entityManager.detach(submission);

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(submission, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
