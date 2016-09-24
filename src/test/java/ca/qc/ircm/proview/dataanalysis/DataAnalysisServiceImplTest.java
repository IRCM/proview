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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DataAnalysisServiceImplTest {
  private DataAnalysisServiceImpl dataAnalysisServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private DataAnalysisActivityService dataAnalysisActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<DataAnalysis> dataAnalysisCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    dataAnalysisServiceImpl = new DataAnalysisServiceImpl(entityManager, queryFactory,
        dataAnalysisActivityService, activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get() {
    DataAnalysis dataAnalysis = dataAnalysisServiceImpl.get(3L);

    verify(authorizationService).checkDataAnalysisReadPermission(dataAnalysis);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals((Long) 1L, dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysis.getType());
  }

  @Test
  public void get_Null() {
    DataAnalysis dataAnalysis = dataAnalysisServiceImpl.get(null);

    assertNull(dataAnalysis);
  }

  @Test
  public void all() {
    SubmissionSample sample = new SubmissionSample(1L);

    List<DataAnalysis> dataAnalyses = dataAnalysisServiceImpl.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(1, dataAnalyses.size());
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals((Long) 1L, dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysis.getType());
  }

  @Test
  public void all_Null() {
    List<DataAnalysis> dataAnalyses = dataAnalysisServiceImpl.all(null);

    assertEquals(0, dataAnalyses.size());
  }

  @Test
  public void insert() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    DataAnalysis dataAnalysis = new DataAnalysis();
    dataAnalysis.setSample(sample);
    dataAnalysis.setProtein("85574");
    dataAnalysis.setPeptide("54, 62");
    dataAnalysis.setMaxWorkTime(2.3);
    dataAnalysis.setType(DataAnalysis.Type.PROTEIN_PEPTIDE);
    Collection<DataAnalysis> dataAnalyses = new LinkedList<DataAnalysis>();
    dataAnalyses.add(dataAnalysis);
    when(dataAnalysisActivityService.insert(any(DataAnalysis.class))).thenReturn(activity);

    dataAnalysisServiceImpl.insert(dataAnalyses);

    entityManager.flush();
    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(dataAnalysis.getId());
    dataAnalysis = dataAnalysisServiceImpl.get(dataAnalysis.getId());
    assertEquals(dataAnalysis.getId(), dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("85574", dataAnalysis.getProtein());
    assertEquals("54, 62", dataAnalysis.getPeptide());
    assertEquals((Double) 2.3, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysis.getType());
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    verify(dataAnalysisActivityService).insert(dataAnalysisCaptor.capture());
    verify(activityService).insert(activity);
    DataAnalysis dataAnalysisLog = dataAnalysisCaptor.getValue();
    assertEquals(dataAnalysis.getId(), dataAnalysisLog.getId());
    assertEquals(sample.getId(), dataAnalysisLog.getSample().getId());
    assertEquals("85574", dataAnalysisLog.getProtein());
    assertEquals("54, 62", dataAnalysisLog.getPeptide());
    assertEquals((Double) 2.3, dataAnalysisLog.getMaxWorkTime());
    assertEquals(null, dataAnalysisLog.getScore());
    assertEquals(null, dataAnalysisLog.getWorkTime());
    assertEquals(DataAnalysis.Status.TO_DO, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysisLog.getType());
  }

  @Test
  public void analyse() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 442L);
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    DataAnalysis dataAnalysis = dataAnalysisServiceImpl.get(4L);
    entityManager.detach(dataAnalysis);
    assertEquals((Long) 4L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysis.getType());
    dataAnalysis.setScore("123456, 3: 85%\n12456, 4: 83%\n58774: 68%");
    dataAnalysis.setWorkTime(3.50);
    dataAnalysis.setStatus(DataAnalysis.Status.ANALYSED);
    Collection<DataAnalysis> dataAnalyses = new LinkedList<DataAnalysis>();
    dataAnalyses.add(dataAnalysis);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    dataAnalysisServiceImpl.analyse(dataAnalyses);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    dataAnalysis = dataAnalysisServiceImpl.get(4L);
    assertEquals((Long) 4L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456, 3: 85%\n12456, 4: 83%\n58774: 68%", dataAnalysis.getScore());
    assertEquals((Double) 3.5, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysis.getType());
    sample = entityManager.find(SubmissionSample.class, 442L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    verify(dataAnalysisActivityService).update(dataAnalysisCaptor.capture(), isNull(String.class));
    verify(activityService).insert(activity);
    DataAnalysis dataAnalysisLog = dataAnalysisCaptor.getValue();
    assertEquals((Long) 4L, dataAnalysisLog.getId());
    assertEquals(sample.getId(), dataAnalysisLog.getSample().getId());
    assertEquals("123456, 58774", dataAnalysisLog.getProtein());
    assertEquals("3, 4", dataAnalysisLog.getPeptide());
    assertEquals((Double) 4.0, dataAnalysisLog.getMaxWorkTime());
    assertEquals("123456, 3: 85%\n12456, 4: 83%\n58774: 68%", dataAnalysisLog.getScore());
    assertEquals((Double) 3.5, dataAnalysisLog.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysisLog.getType());
    assertEquals(SampleStatus.ANALYSED, dataAnalysisLog.getSample().getStatus());
  }

  @Test
  public void update() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    DataAnalysis dataAnalysis = dataAnalysisServiceImpl.get(3L);
    entityManager.detach(dataAnalysis);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysis.getType());
    dataAnalysis.setScore("123456: ~80%");
    dataAnalysis.setStatus(DataAnalysis.Status.CANCELLED);
    dataAnalysis.setWorkTime(2.0);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    dataAnalysisServiceImpl.update(dataAnalysis, "unit_test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    dataAnalysis = dataAnalysisServiceImpl.get(3L);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: ~80%", dataAnalysis.getScore());
    assertEquals((Double) 2.0, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.CANCELLED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysis.getType());
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    verify(dataAnalysisActivityService).update(dataAnalysisCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    DataAnalysis dataAnalysisLog = dataAnalysisCaptor.getValue();
    assertEquals((Long) 3L, dataAnalysisLog.getId());
    assertEquals(sample.getId(), dataAnalysisLog.getSample().getId());
    assertEquals("123456", dataAnalysisLog.getProtein());
    assertEquals(null, dataAnalysisLog.getPeptide());
    assertEquals((Double) 2.0, dataAnalysisLog.getMaxWorkTime());
    assertEquals("123456: ~80%", dataAnalysisLog.getScore());
    assertEquals((Double) 2.0, dataAnalysisLog.getWorkTime());
    assertEquals(DataAnalysis.Status.CANCELLED, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysisLog.getType());
    assertEquals(SampleStatus.ANALYSED, dataAnalysisLog.getSample().getStatus());
  }

  @Test
  public void update_Status_Todo() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    DataAnalysis dataAnalysis = dataAnalysisServiceImpl.get(3L);
    entityManager.detach(dataAnalysis);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysis.getType());
    dataAnalysis.setScore(null);
    dataAnalysis.setStatus(DataAnalysis.Status.TO_DO);
    dataAnalysis.setWorkTime(null);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    dataAnalysisServiceImpl.update(dataAnalysis, "unit_test");

    entityManager.flush();
    dataAnalysis = dataAnalysisServiceImpl.get(3L);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysis.getType());
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    verify(dataAnalysisActivityService).update(dataAnalysisCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    DataAnalysis dataAnalysisLog = dataAnalysisCaptor.getValue();
    assertEquals((Long) 3L, dataAnalysisLog.getId());
    assertEquals(sample.getId(), dataAnalysisLog.getSample().getId());
    assertEquals("123456", dataAnalysisLog.getProtein());
    assertEquals(null, dataAnalysisLog.getPeptide());
    assertEquals((Double) 2.0, dataAnalysisLog.getMaxWorkTime());
    assertEquals(null, dataAnalysisLog.getScore());
    assertEquals(null, dataAnalysisLog.getWorkTime());
    assertEquals(DataAnalysis.Status.TO_DO, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN, dataAnalysisLog.getType());
    assertEquals(SampleStatus.DATA_ANALYSIS, dataAnalysisLog.getSample().getStatus());
  }

  @Test
  public void updateToAnalysed() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 442L);
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    DataAnalysis dataAnalysis = dataAnalysisServiceImpl.get(4L);
    entityManager.detach(dataAnalysis);
    assertEquals((Long) 4L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysis.getType());
    dataAnalysis.setScore("123456, 3: 85%\n12456, 4: 83%\n58774: 68%");
    dataAnalysis.setWorkTime(3.50);
    dataAnalysis.setStatus(DataAnalysis.Status.ANALYSED);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    dataAnalysisServiceImpl.update(dataAnalysis, "unit_test");

    entityManager.flush();
    dataAnalysis = dataAnalysisServiceImpl.get(4L);
    assertEquals((Long) 4L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456, 3: 85%\n12456, 4: 83%\n58774: 68%", dataAnalysis.getScore());
    assertEquals((Double) 3.50, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysis.getType());
    sample = entityManager.find(SubmissionSample.class, 442L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    verify(dataAnalysisActivityService).update(dataAnalysisCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    DataAnalysis dataAnalysisLog = dataAnalysisCaptor.getValue();
    assertEquals((Long) 4L, dataAnalysisLog.getId());
    assertEquals(sample.getId(), dataAnalysisLog.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456, 3: 85%\n12456, 4: 83%\n58774: 68%", dataAnalysis.getScore());
    assertEquals((Double) 3.50, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysis.Status.ANALYSED, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysis.Type.PROTEIN_PEPTIDE, dataAnalysis.getType());
    assertEquals(SampleStatus.ANALYSED, dataAnalysisLog.getSample().getStatus());
  }
}
