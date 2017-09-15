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

package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleServiceTest {
  private SampleService sampleService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    sampleService = new SampleService(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get_SubmissionSample_Gel() throws Throwable {
    Sample sample = sampleService.get(1L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof SubmissionSample);
    SubmissionSample gelSample = (SubmissionSample) sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals((Long) 1L, gelSample.getOriginalContainer().getId());
    assertEquals(SampleSupport.GEL, gelSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, gelSample.getType());
    assertEquals(SampleStatus.ANALYSED, gelSample.getStatus());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
  }

  @Test
  public void get_SubmissionSample() throws Throwable {
    Sample sample = sampleService.get(442L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof SubmissionSample);
    SubmissionSample eluateSample = (SubmissionSample) sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("CAP_20111013_01", eluateSample.getName());
    assertEquals((Long) 2L, eluateSample.getOriginalContainer().getId());
    assertEquals(SampleSupport.SOLUTION, eluateSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, eluateSample.getType());
    assertEquals(SampleStatus.DATA_ANALYSIS, eluateSample.getStatus());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals("1.5 μg", eluateSample.getQuantity());
    assertEquals((Double) 50.0, eluateSample.getVolume());
  }

  @Test
  public void get_Control() throws Throwable {
    Sample sample = sampleService.get(444L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof Control);
    Control control = (Control) sample;
    assertEquals((Long) 444L, control.getId());
    assertEquals("control_01", control.getName());
    assertEquals((Long) 4L, control.getOriginalContainer().getId());
    assertEquals(SampleSupport.GEL, control.getSupport());
    assertEquals(Sample.Type.CONTROL, control.getType());
    assertEquals(ControlType.NEGATIVE_CONTROL, control.getControlType());
    assertEquals(null, control.getVolume());
    assertEquals(null, control.getQuantity());
  }

  @Test
  public void get_Null() throws Throwable {
    Sample sample = sampleService.get(null);

    assertNull(sample);
  }

  @Test
  public void linkedToResults_True() throws Throwable {
    Sample sample = entityManager.find(Sample.class, 442L);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean linkedToResults = sampleService.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(true, linkedToResults);
  }

  @Test
  public void linkedToResults_False() throws Throwable {
    Sample sample = entityManager.find(Sample.class, 447L);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean linkedToResults = sampleService.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(false, linkedToResults);
  }

  @Test
  public void linkedToResults_WithHidden() throws Throwable {
    Sample sample = new SubmissionSample(445L);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean linkedToResults = sampleService.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(true, linkedToResults);
  }

  @Test
  public void linkedToResults_NoHidden() throws Throwable {
    Sample sample = new SubmissionSample(445L);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean linkedToResults = sampleService.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(false, linkedToResults);
  }

  @Test
  public void linkedToResults_Null() throws Throwable {
    boolean linkedToResults = sampleService.linkedToResults(null);

    assertEquals(false, linkedToResults);
  }
}
