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

package ca.qc.ircm.proview.tube;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TubeServiceTest {
  @Inject
  private TubeService service;
  @MockBean
  private AuthorizationService authorizationService;

  @Test
  public void get() throws Throwable {
    Tube tube = service.get(1L);

    verify(authorizationService).checkSampleReadPermission(tube.getSample());
    assertEquals((Long) 1L, tube.getId());
    assertEquals("FAM119A_band_01", tube.getName());
    assertEquals((Long) 1L, tube.getSample().getId());
    assertEquals(SampleContainerType.TUBE, tube.getType());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        tube.getTimestamp());
  }

  @Test
  public void get_Null() throws Throwable {
    Tube tube = service.get((Long) null);

    assertNull(tube);
  }

  @Test
  public void nameAvailable_True() throws Throwable {
    boolean available = service.nameAvailable("FAM119A_band_01");

    verify(authorizationService).checkAdminRole();
    assertFalse(available);
  }

  @Test
  public void nameAvailable_False() throws Throwable {
    boolean available = service.nameAvailable("unit_test");

    verify(authorizationService).checkAdminRole();
    assertTrue(available);
  }

  @Test
  public void nameAvailable_Null() throws Throwable {
    boolean available = service.nameAvailable(null);

    assertFalse(available);
  }

  @Test
  public void all() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    List<Tube> tubes = service.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(3, tubes.size());
    assertTrue(find(tubes, 1L).isPresent());
    assertTrue(find(tubes, 6L).isPresent());
    assertTrue(find(tubes, 7L).isPresent());
    assertFalse(find(tubes, 5L).isPresent());
  }

  @Test
  public void all_Null() throws Throwable {
    List<Tube> tubes = service.all(null);

    assertEquals(0, tubes.size());
  }
}
