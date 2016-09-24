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
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ContaminantServiceImplTest {
  private ContaminantServiceImpl contaminantServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;
  @Captor
  private ArgumentCaptor<Sample> sampleCaptor;

  @Before
  public void beforeTest() {
    contaminantServiceImpl =
        new ContaminantServiceImpl(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get() {
    Contaminant contaminant = contaminantServiceImpl.get(2L);

    verify(authorizationService).checkSampleReadPermission(sampleCaptor.capture());
    assertEquals((Long) 445L, sampleCaptor.getValue().getId());
    assertEquals((Long) 2L, contaminant.getId());
    assertEquals("keratin1", contaminant.getName());
    assertEquals("1.5 μg", contaminant.getQuantity());
    assertEquals(null, contaminant.getComments());
  }

  @Test
  public void get_Null() {
    Contaminant contaminant = contaminantServiceImpl.get(null);

    assertNull(contaminant);
  }
}
