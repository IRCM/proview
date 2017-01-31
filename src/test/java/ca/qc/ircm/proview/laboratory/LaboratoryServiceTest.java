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

package ca.qc.ircm.proview.laboratory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.InvalidUserException;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserNotMemberOfLaboratoryException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class LaboratoryServiceTest {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(LaboratoryServiceTest.class);
  private LaboratoryService laboratoryService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private CacheFlusher cacheFlusher;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    laboratoryService =
        new LaboratoryService(entityManager, cacheFlusher, authorizationService);
  }

  private <D extends Data> Optional<D> find(Collection<D> datas, long id) {
    return datas.stream().filter(d -> d.getId() == id).findAny();
  }

  @Test
  public void get() throws Exception {
    Laboratory laboratory = laboratoryService.get(3L);

    verify(authorizationService).checkLaboratoryReadPermission(laboratory);
    assertEquals((Long) 3L, laboratory.getId());
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Chromatin and Genomic Expression", laboratory.getName());
    assertEquals(1, laboratory.getManagers().size());
    assertEquals((Long) 6L, laboratory.getManagers().get(0).getId());
  }

  @Test
  public void get_Null() throws Exception {
    Laboratory laboratory = laboratoryService.get(null);

    assertNull(laboratory);
  }

  @Test
  public void update() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    // Update laboratory.
    laboratory.setOrganization("test_organization_2");
    laboratory.setName("test_group_2");

    laboratoryService.update(laboratory);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    Laboratory testLaboratory = entityManager.find(Laboratory.class, laboratory.getId());
    assertEquals(laboratory.getOrganization(), testLaboratory.getOrganization());
    assertEquals(laboratory.getName(), testLaboratory.getName());
  }

  @Test
  public void addManagerAdmin() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 1L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 5L);

    laboratoryService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = entityManager.find(Laboratory.class, 1L);
    List<User> testManagers = laboratory.getManagers();
    assertEquals(true, testManagers.contains(user));
  }

  @Test
  public void addManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    laboratoryService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void addManager_InactivatedUser() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 12L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));
    assertEquals(false, user.isActive());

    laboratoryService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    User testUser = entityManager.find(User.class, 12L);
    assertTrue(find(testManagers, user.getId()).isPresent());
    assertEquals(true, testUser.isActive());
  }

  @Test
  public void addManager_AlreadyManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertTrue(find(managers, user.getId()).isPresent());

    laboratoryService.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertTrue(find(testManagers, user.getId()).isPresent());
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void addManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    laboratoryService.addManager(laboratory, user);
  }

  @Test(expected = InvalidUserException.class)
  public void addManager_Invalid() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);

    laboratoryService.addManager(laboratory, user);
  }

  @Test
  public void removeManagerAdmin() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 1L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    laboratoryService.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 1L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void removeManager_UnmanagedLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 3L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 6L);
    entityManager.detach(user);

    try {
      laboratoryService.removeManager(laboratory, user);
      fail("Expected UnmanagedLaboratoryException");
    } catch (UnmanagedLaboratoryException e) {
      // Ignore.
    }
  }

  @Test
  public void removeManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 27L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertTrue(find(managers, user.getId()).isPresent());

    laboratoryService.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test
  public void removeManager_AlreadyNotManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 10L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    laboratoryService.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertFalse(find(testManagers, user.getId()).isPresent());
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void removeManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    laboratoryService.removeManager(laboratory, user);
  }
}
