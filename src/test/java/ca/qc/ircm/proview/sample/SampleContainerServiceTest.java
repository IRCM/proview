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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link SampleContainerService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class SampleContainerServiceTest {
  private static final String READ = "read";
  @Autowired
  private SampleContainerService service;
  @MockBean
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void beforeTest() {
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  @Test
  public void get_Id() throws Throwable {
    SampleContainer container = service.get(1L).get();

    verify(permissionEvaluator).hasPermission(any(), eq(container.getSample()), eq(READ));
    assertEquals((Long) 1L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(SampleContainerType.TUBE, container.getType());
    assertEquals(LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0), container.getTimestamp());
  }

  @Test
  public void get_NullId() throws Throwable {
    assertFalse(service.get((Long) null).isPresent());
  }

  @Test
  public void last() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    SampleContainer container = service.last(sample).get();

    verify(permissionEvaluator).hasPermission(any(), eq(sample), eq(READ));
    assertEquals((Long) 129L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(SampleContainerType.WELL, container.getType());
    assertEquals(LocalDateTime.of(2011, 11, 16, 15, 7, 34, 0), container.getTimestamp());
  }

  @Test
  public void last_Null() throws Throwable {
    assertFalse(service.last(null).isPresent());
  }
}
