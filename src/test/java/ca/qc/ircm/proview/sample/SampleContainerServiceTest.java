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
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleContainerServiceTest {
  @Inject
  private SampleContainerService service;
  @MockBean
  private AuthorizationService authorizationService;

  @Test
  public void get_Id() throws Throwable {
    SampleContainer container = service.get(1L);

    verify(authorizationService).checkSampleReadPermission(container.getSample());
    assertEquals((Long) 1L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(SampleContainerType.TUBE, container.getType());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        container.getTimestamp());
  }

  @Test
  public void get_NullId() throws Throwable {
    SampleContainer container = service.get((Long) null);

    assertNull(container);
  }

  @Test
  public void last() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    SampleContainer container = service.last(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals((Long) 129L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(SampleContainerType.WELL, container.getType());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 15, 7, 34, 0).atZone(ZoneId.systemDefault()).toInstant(),
        container.getTimestamp());
  }

  @Test
  public void last_Null() throws Throwable {
    SampleContainer container = service.last(null);

    assertNull(container);
  }
}
