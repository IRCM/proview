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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleServiceTest {
  @Inject
  private SampleService service;
  @MockBean
  private AuthorizationService authorizationService;

  @Test
  public void get_SubmissionSample_Gel() throws Throwable {
    Sample sample = service.get(1L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof SubmissionSample);
    SubmissionSample gelSample = (SubmissionSample) sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals((Long) 1L, gelSample.getOriginalContainer().getId());
    assertEquals(SampleType.GEL, gelSample.getType());
    assertEquals(Sample.Category.SUBMISSION, gelSample.getCategory());
    assertEquals(SampleStatus.ANALYSED, gelSample.getStatus());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
  }

  @Test
  public void get_SubmissionSample() throws Throwable {
    Sample sample = service.get(442L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof SubmissionSample);
    SubmissionSample eluateSample = (SubmissionSample) sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("CAP_20111013_01", eluateSample.getName());
    assertEquals((Long) 2L, eluateSample.getOriginalContainer().getId());
    assertEquals(SampleType.SOLUTION, eluateSample.getType());
    assertEquals(Sample.Category.SUBMISSION, eluateSample.getCategory());
    assertEquals(SampleStatus.DATA_ANALYSIS, eluateSample.getStatus());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals("1.5 μg", eluateSample.getQuantity());
    assertEquals("50 μl", eluateSample.getVolume());
  }

  @Test
  public void get_Control() throws Throwable {
    Sample sample = service.get(444L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof Control);
    Control control = (Control) sample;
    assertEquals((Long) 444L, control.getId());
    assertEquals("control_01", control.getName());
    assertEquals((Long) 4L, control.getOriginalContainer().getId());
    assertEquals(SampleType.GEL, control.getType());
    assertEquals(Sample.Category.CONTROL, control.getCategory());
    assertEquals(ControlType.NEGATIVE_CONTROL, control.getControlType());
    assertEquals(null, control.getVolume());
    assertEquals(null, control.getQuantity());
  }

  @Test
  public void get_Null() throws Throwable {
    Sample sample = service.get(null);

    assertNull(sample);
  }
}
