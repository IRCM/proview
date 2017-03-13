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

package ca.qc.ircm.proview.msanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AcquisitionServiceTest {
  private AcquisitionService acquisitionService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    acquisitionService = new AcquisitionService(entityManager, authorizationService);
  }

  @Test
  public void get() {
    Acquisition acquisition = acquisitionService.get(1L);

    verify(authorizationService).checkRobotRole();
    assertEquals((Long) 1L, acquisition.getId());
    assertEquals((Long) 1L, acquisition.getSample().getId());
    assertEquals((Long) 1L, acquisition.getContainer().getId());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals("XL_20100614_02", acquisition.getSampleListName());
    assertEquals("XL_20100614_COU_09", acquisition.getAcquisitionFile());
    assertEquals((Integer) 1, acquisition.getPosition());
    assertEquals((Integer) 1, acquisition.getListIndex());
    assertEquals(null, acquisition.getComments());
  }

  @Test
  public void get_Null() {
    Acquisition acquisition = acquisitionService.get(null);

    assertNull(acquisition);
  }
}
