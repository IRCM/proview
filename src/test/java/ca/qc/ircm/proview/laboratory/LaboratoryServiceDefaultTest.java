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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.DatabaseRule;
import ca.qc.ircm.proview.test.config.RollBack;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.user.InvalidUserException;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserNotMemberOfLaboratoryException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.persistence.EntityManager;

@RollBack
public class LaboratoryServiceDefaultTest {
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(LaboratoryServiceDefaultTest.class);
  private LaboratoryServiceDefault laboratoryServiceDefault;
  @ClassRule
  public static DatabaseRule databaseRule = new DatabaseRule();
  @Mock
  private CacheFlusher cacheFlusher;
  @Mock
  private AuthorizationService authorizationService;
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(databaseRule);
  private EntityManager entityManager;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    laboratoryServiceDefault = new LaboratoryServiceDefault(databaseRule.getEntityManager(),
        cacheFlusher, authorizationService);
    entityManager = databaseRule.getEntityManager();
  }

  @Test
  public void get() throws Exception {
    Laboratory laboratory = laboratoryServiceDefault.get(3L);

    verify(authorizationService).checkLaboratoryReadPermission(laboratory);
    assertEquals((Long) 3L, laboratory.getId());
    assertEquals("IRCM", laboratory.getOrganization());
    assertEquals("Chromatin and Genomic Expression", laboratory.getName());
    assertEquals(1, laboratory.getManagers().size());
    assertEquals((Long) 7L, laboratory.getManagers().get(0).getId());
  }

  @Test
  public void get_Null() throws Exception {
    Laboratory laboratory = laboratoryServiceDefault.get(null);

    assertNull(laboratory);
  }

  @Test
  public void update() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    // Update laboratory.
    laboratory.setOrganization("test_organization_2");
    laboratory.setName("test_group_2");

    laboratoryServiceDefault.update(laboratory);

    entityManager.flush();
    verify(authorizationService).checkLaboratoryManagerPermission(laboratory);
    Laboratory testLaboratory = entityManager.find(Laboratory.class, laboratory.getId());
    assertEquals(laboratory.getOrganization(), testLaboratory.getOrganization());
    assertEquals(laboratory.getName(), testLaboratory.getName());
  }

  @Test
  public void addManagerProteomic() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 1L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 6L);

    laboratoryServiceDefault.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = entityManager.find(Laboratory.class, 1L);
    List<User> testManagers = laboratory.getManagers();
    assertEquals(true, testManagers.contains(user));
  }

  @Test
  public void addManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 8L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    laboratoryServiceDefault.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    verify(cacheFlusher).flushShiroCache();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertEquals(true, testManagers.contains(user));
  }

  @Test
  public void addManager_InactivatedUser() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 5L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));
    assertEquals(false, user.isActive());

    laboratoryServiceDefault.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    User testUser = entityManager.find(User.class, 5L);
    assertEquals(true, testManagers.contains(user));
    assertEquals(true, testUser.isActive());
  }

  @Test
  public void addManager_AlreadyManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 3L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(true, managers.contains(user));

    laboratoryServiceDefault.addManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertEquals(true, testManagers.contains(user));
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void addManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    laboratoryServiceDefault.addManager(laboratory, user);
  }

  @Test(expected = InvalidUserException.class)
  public void addManager_Invalid() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 4L);
    entityManager.detach(user);

    laboratoryServiceDefault.addManager(laboratory, user);
  }

  @Test
  public void removeManagerProteomic() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 1L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    laboratoryServiceDefault.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    laboratory = entityManager.find(Laboratory.class, 1L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertEquals(false, testManagers.contains(user));
  }

  @Test
  public void removeManager_UnmanagedLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 3L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 7L);
    entityManager.detach(user);

    try {
      laboratoryServiceDefault.removeManager(laboratory, user);
      fail("Expected UnmanagedLaboratoryException");
    } catch (UnmanagedLaboratoryException e) {
      // Ignore.
    }
  }

  @Test
  public void removeManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 9L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(true, managers.contains(user));

    laboratoryServiceDefault.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    laboratory = entityManager.find(Laboratory.class, 4L);
    verify(cacheFlusher).flushShiroCache();
    List<User> testManagers = laboratory.getManagers();
    assertEquals(false, testManagers.contains(user));
  }

  @Test
  public void removeManager_AlreadyNotManager() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 8L);
    entityManager.detach(user);
    List<User> managers = laboratory.getManagers();
    assertEquals(false, managers.contains(user));

    laboratoryServiceDefault.removeManager(laboratory, user);

    entityManager.flush();
    verify(authorizationService).checkProteomicRole();
    laboratory = entityManager.find(Laboratory.class, 2L);
    List<User> testManagers = laboratory.getManagers();
    assertEquals(false, testManagers.contains(user));
  }

  @Test(expected = UserNotMemberOfLaboratoryException.class)
  public void removeManager_WrongLaboratory() throws Exception {
    Laboratory laboratory = entityManager.find(Laboratory.class, 2L);
    entityManager.detach(laboratory);
    User user = entityManager.find(User.class, 2L);
    entityManager.detach(user);

    laboratoryServiceDefault.removeManager(laboratory, user);
  }
}
