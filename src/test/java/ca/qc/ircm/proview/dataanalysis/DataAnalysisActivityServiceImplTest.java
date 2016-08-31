package ca.qc.ircm.proview.dataanalysis;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.sample.GelSample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DataAnalysisActivityServiceImplTest {
  private DataAnalysisActivityServiceImpl dataAnalysisActivityServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    dataAnalysisActivityServiceImpl =
        new DataAnalysisActivityServiceImpl(entityManager, authorizationService);
  }

  @Test
  public void insert() {
    SubmissionSample sample = new GelSample(1L);
    sample.setStatus(SubmissionSample.Status.DATA_ANALYSIS);
    DataAnalysis dataAnalysis = new DataAnalysis();
    dataAnalysis.setId(123456L);
    dataAnalysis.setSample(sample);
    dataAnalysis.setMaxWorkTime(5.0);
    dataAnalysis.setProtein("1, 2, 3");
    dataAnalysis.setType(DataAnalysis.Type.PROTEIN);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = dataAnalysisActivityServiceImpl.insert(dataAnalysis);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(dataAnalysis.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Set<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(sample.getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SubmissionSample.Status.ANALYSED.name());
    sampleStatusUpdate.setNewValue(SubmissionSample.Status.DATA_ANALYSIS.name());
    expecteds.add(sampleStatusUpdate);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update() {
    DataAnalysis dataAnalysis = entityManager.find(DataAnalysis.class, 4L);
    entityManager.detach(dataAnalysis);
    entityManager.detach(dataAnalysis.getSample());
    dataAnalysis.setScore("90.0");
    dataAnalysis.setStatus(DataAnalysis.Status.ANALYSED);
    dataAnalysis.setWorkTime(2.0);
    dataAnalysis.getSample().setStatus(SubmissionSample.Status.ANALYSED);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        dataAnalysisActivityServiceImpl.update(dataAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(dataAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Set<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(dataAnalysis.getSample().getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SubmissionSample.Status.DATA_ANALYSIS.name());
    sampleStatusUpdate.setNewValue(SubmissionSample.Status.ANALYSED.name());
    expecteds.add(sampleStatusUpdate);
    UpdateActivity scoreUpdate = new UpdateActivity();
    scoreUpdate.setActionType(ActionType.UPDATE);
    scoreUpdate.setTableName("dataanalysis");
    scoreUpdate.setRecordId(dataAnalysis.getId());
    scoreUpdate.setColumn("score");
    scoreUpdate.setOldValue(null);
    scoreUpdate.setNewValue("90.0");
    expecteds.add(scoreUpdate);
    UpdateActivity statusUpdate = new UpdateActivity();
    statusUpdate.setActionType(ActionType.UPDATE);
    statusUpdate.setTableName("dataanalysis");
    statusUpdate.setRecordId(dataAnalysis.getId());
    statusUpdate.setColumn("status");
    statusUpdate.setOldValue(DataAnalysis.Status.TO_DO.name());
    statusUpdate.setNewValue(DataAnalysis.Status.ANALYSED.name());
    expecteds.add(statusUpdate);
    UpdateActivity workTimeUpdate = new UpdateActivity();
    workTimeUpdate.setActionType(ActionType.UPDATE);
    workTimeUpdate.setTableName("dataanalysis");
    workTimeUpdate.setRecordId(dataAnalysis.getId());
    workTimeUpdate.setColumn("workTime");
    workTimeUpdate.setOldValue(null);
    workTimeUpdate.setNewValue("2.0");
    expecteds.add(workTimeUpdate);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_AlreadyAnalysedData() {
    DataAnalysis dataAnalysis = entityManager.find(DataAnalysis.class, 3L);
    entityManager.detach(dataAnalysis);
    entityManager.detach(dataAnalysis.getSample());
    dataAnalysis.setScore(null);
    dataAnalysis.setStatus(DataAnalysis.Status.TO_DO);
    dataAnalysis.setWorkTime(null);
    dataAnalysis.getSample().setStatus(SubmissionSample.Status.DATA_ANALYSIS);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        dataAnalysisActivityServiceImpl.update(dataAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(dataAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Set<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(dataAnalysis.getSample().getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SubmissionSample.Status.ANALYSED.name());
    sampleStatusUpdate.setNewValue(SubmissionSample.Status.DATA_ANALYSIS.name());
    expecteds.add(sampleStatusUpdate);
    UpdateActivity scoreUpdate = new UpdateActivity();
    scoreUpdate.setActionType(ActionType.UPDATE);
    scoreUpdate.setTableName("dataanalysis");
    scoreUpdate.setRecordId(dataAnalysis.getId());
    scoreUpdate.setColumn("score");
    scoreUpdate.setOldValue("123456: 95%");
    scoreUpdate.setNewValue(null);
    expecteds.add(scoreUpdate);
    UpdateActivity statusUpdate = new UpdateActivity();
    statusUpdate.setActionType(ActionType.UPDATE);
    statusUpdate.setTableName("dataanalysis");
    statusUpdate.setRecordId(dataAnalysis.getId());
    statusUpdate.setColumn("status");
    statusUpdate.setOldValue(DataAnalysis.Status.ANALYSED.name());
    statusUpdate.setNewValue(DataAnalysis.Status.TO_DO.name());
    expecteds.add(statusUpdate);
    UpdateActivity workTimeUpdate = new UpdateActivity();
    workTimeUpdate.setActionType(ActionType.UPDATE);
    workTimeUpdate.setTableName("dataanalysis");
    workTimeUpdate.setRecordId(dataAnalysis.getId());
    workTimeUpdate.setColumn("workTime");
    workTimeUpdate.setOldValue("1.75");
    workTimeUpdate.setNewValue(null);
    expecteds.add(workTimeUpdate);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    DataAnalysis dataAnalysis = entityManager.find(DataAnalysis.class, 4L);
    entityManager.detach(dataAnalysis);
    entityManager.detach(dataAnalysis.getSample());
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        dataAnalysisActivityServiceImpl.update(dataAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(false, optionalActivity.isPresent());
  }
}
