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

package ca.qc.ircm.proview.dataanalysis;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DataAnalysisActivityServiceTest {
  private DataAnalysisActivityService dataAnalysisActivityService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    dataAnalysisActivityService =
        new DataAnalysisActivityService(entityManager, authorizationService);
  }

  @Test
  public void insert() {
    SubmissionSample sample = new SubmissionSample(1L);
    sample.setStatus(SampleStatus.DATA_ANALYSIS);
    DataAnalysis dataAnalysis = new DataAnalysis();
    dataAnalysis.setId(123456L);
    dataAnalysis.setSample(sample);
    dataAnalysis.setMaxWorkTime(5.0);
    dataAnalysis.setProtein("1, 2, 3");
    dataAnalysis.setType(DataAnalysisType.PROTEIN);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = dataAnalysisActivityService.insert(dataAnalysis);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(dataAnalysis.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Set<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(sample.getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SampleStatus.ANALYSED.name());
    sampleStatusUpdate.setNewValue(SampleStatus.DATA_ANALYSIS.name());
    expecteds.add(sampleStatusUpdate);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update() {
    DataAnalysis dataAnalysis = entityManager.find(DataAnalysis.class, 4L);
    entityManager.detach(dataAnalysis);
    entityManager.detach(dataAnalysis.getSample());
    dataAnalysis.setScore("90.0");
    dataAnalysis.setStatus(DataAnalysisStatus.ANALYSED);
    dataAnalysis.setWorkTime(2.0);
    dataAnalysis.getSample().setStatus(SampleStatus.ANALYSED);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        dataAnalysisActivityService.update(dataAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(dataAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Set<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(dataAnalysis.getSample().getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SampleStatus.DATA_ANALYSIS.name());
    sampleStatusUpdate.setNewValue(SampleStatus.ANALYSED.name());
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
    statusUpdate.setOldValue(DataAnalysisStatus.TO_DO.name());
    statusUpdate.setNewValue(DataAnalysisStatus.ANALYSED.name());
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
    dataAnalysis.setStatus(DataAnalysisStatus.TO_DO);
    dataAnalysis.setWorkTime(null);
    dataAnalysis.getSample().setStatus(SampleStatus.DATA_ANALYSIS);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        dataAnalysisActivityService.update(dataAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("dataanalysis", activity.getTableName());
    assertEquals(dataAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Set<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity sampleStatusUpdate = new UpdateActivity();
    sampleStatusUpdate.setActionType(ActionType.UPDATE);
    sampleStatusUpdate.setTableName("sample");
    sampleStatusUpdate.setRecordId(dataAnalysis.getSample().getId());
    sampleStatusUpdate.setColumn("status");
    sampleStatusUpdate.setOldValue(SampleStatus.ANALYSED.name());
    sampleStatusUpdate.setNewValue(SampleStatus.DATA_ANALYSIS.name());
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
    statusUpdate.setOldValue(DataAnalysisStatus.ANALYSED.name());
    statusUpdate.setNewValue(DataAnalysisStatus.TO_DO.name());
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
        dataAnalysisActivityService.update(dataAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(false, optionalActivity.isPresent());
  }
}
