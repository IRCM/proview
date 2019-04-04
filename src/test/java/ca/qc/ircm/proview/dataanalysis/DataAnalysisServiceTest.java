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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DataAnalysisServiceTest extends AbstractServiceTestCase {
  @Inject
  private DataAnalysisService service;
  @Inject
  private SubmissionRepository submissionRepository;
  @MockBean
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
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
}
