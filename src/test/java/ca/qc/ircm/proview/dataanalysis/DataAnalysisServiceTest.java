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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DataAnalysisServiceTest extends AbstractServiceTestCase {
  @Inject
  private DataAnalysisService service;
  @Inject
  private DataAnalysisRepository repository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private SubmissionSampleRepository submissionSampleRepository;
  @MockBean
  private DataAnalysisActivityService dataAnalysisActivityService;
  @MockBean
  private ActivityService activityService;
  @MockBean
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
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get() {
    DataAnalysis dataAnalysis = service.get(3L);

    verify(authorizationService).checkDataAnalysisReadPermission(dataAnalysis);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals((Long) 1L, dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysis.getType());
  }

  @Test
  public void get_Null() {
    DataAnalysis dataAnalysis = service.get(null);

    assertNull(dataAnalysis);
  }

  @Test
  public void all() {
    Submission submission = submissionRepository.findOne(1L);

    List<DataAnalysis> dataAnalyses = service.all(submission);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals(1, dataAnalyses.size());
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals((Long) 1L, dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysis.getType());
  }

  @Test
  public void all_Null() {
    List<DataAnalysis> dataAnalyses = service.all(null);

    assertEquals(0, dataAnalyses.size());
  }

  @Test
  public void insert() {
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    DataAnalysis dataAnalysis = new DataAnalysis();
    dataAnalysis.setSample(sample);
    dataAnalysis.setProtein("85574");
    dataAnalysis.setPeptide("54, 62");
    dataAnalysis.setMaxWorkTime(2.3);
    dataAnalysis.setType(DataAnalysisType.PROTEIN_PEPTIDE);
    Collection<DataAnalysis> dataAnalyses = new LinkedList<>();
    dataAnalyses.add(dataAnalysis);
    when(dataAnalysisActivityService.insert(any(DataAnalysis.class))).thenReturn(activity);

    service.insert(dataAnalyses);

    repository.flush();
    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(dataAnalysis.getId());
    dataAnalysis = service.get(dataAnalysis.getId());
    assertEquals(dataAnalysis.getId(), dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("85574", dataAnalysis.getProtein());
    assertEquals("54, 62", dataAnalysis.getPeptide());
    assertEquals((Double) 2.3, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN_PEPTIDE, dataAnalysis.getType());
    sample = submissionSampleRepository.findOne(1L);
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
    assertEquals(DataAnalysisStatus.TO_DO, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysisType.PROTEIN_PEPTIDE, dataAnalysisLog.getType());
  }

  @Test
  public void update() {
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    DataAnalysis dataAnalysis = service.get(3L);
    detach(dataAnalysis);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysis.getType());
    dataAnalysis.setScore("123456: ~80%");
    dataAnalysis.setStatus(DataAnalysisStatus.CANCELLED);
    dataAnalysis.setWorkTime(2.0);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    service.update(dataAnalysis, "unit_test");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    dataAnalysis = service.get(3L);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: ~80%", dataAnalysis.getScore());
    assertEquals((Double) 2.0, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.CANCELLED, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysis.getType());
    sample = submissionSampleRepository.findOne(1L);
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
    assertEquals(DataAnalysisStatus.CANCELLED, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysisLog.getType());
    assertEquals(SampleStatus.ANALYSED, dataAnalysisLog.getSample().getStatus());
  }

  @Test
  public void update_StatusTodo() {
    SubmissionSample sample = submissionSampleRepository.findOne(1L);
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    DataAnalysis dataAnalysis = service.get(3L);
    detach(dataAnalysis);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456: 95%", dataAnalysis.getScore());
    assertEquals((Double) 1.75, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysis.getType());
    dataAnalysis.setScore(null);
    dataAnalysis.setStatus(DataAnalysisStatus.TO_DO);
    dataAnalysis.setWorkTime(null);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    service.update(dataAnalysis, "unit_test");

    repository.flush();
    dataAnalysis = service.get(3L);
    assertEquals((Long) 3L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456", dataAnalysis.getProtein());
    assertEquals(null, dataAnalysis.getPeptide());
    assertEquals((Double) 2.0, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysis.getType());
    sample = submissionSampleRepository.findOne(1L);
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
    assertEquals(DataAnalysisStatus.TO_DO, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysisType.PROTEIN, dataAnalysisLog.getType());
    assertEquals(SampleStatus.DATA_ANALYSIS, dataAnalysisLog.getSample().getStatus());
  }

  @Test
  public void update_StatusAnalysed() {
    SubmissionSample sample = submissionSampleRepository.findOne(442L);
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    DataAnalysis dataAnalysis = service.get(4L);
    detach(dataAnalysis);
    assertEquals((Long) 4L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals(null, dataAnalysis.getScore());
    assertEquals(null, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.TO_DO, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN_PEPTIDE, dataAnalysis.getType());
    dataAnalysis.setScore("123456, 3: 85%\n12456, 4: 83%\n58774: 68%");
    dataAnalysis.setWorkTime(3.50);
    dataAnalysis.setStatus(DataAnalysisStatus.ANALYSED);
    when(dataAnalysisActivityService.update(any(DataAnalysis.class), any(String.class)))
        .thenReturn(optionalActivity);

    service.update(dataAnalysis, "unit_test");

    repository.flush();
    dataAnalysis = service.get(4L);
    assertEquals((Long) 4L, dataAnalysis.getId());
    assertEquals(sample.getId(), dataAnalysis.getSample().getId());
    assertEquals("123456, 58774", dataAnalysis.getProtein());
    assertEquals("3, 4", dataAnalysis.getPeptide());
    assertEquals((Double) 4.0, dataAnalysis.getMaxWorkTime());
    assertEquals("123456, 3: 85%\n12456, 4: 83%\n58774: 68%", dataAnalysis.getScore());
    assertEquals((Double) 3.50, dataAnalysis.getWorkTime());
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysis.getStatus());
    assertEquals(DataAnalysisType.PROTEIN_PEPTIDE, dataAnalysis.getType());
    sample = submissionSampleRepository.findOne(442L);
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
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysisLog.getStatus());
    assertEquals(DataAnalysisType.PROTEIN_PEPTIDE, dataAnalysis.getType());
    assertEquals(SampleStatus.ANALYSED, dataAnalysisLog.getSample().getStatus());
  }
}
