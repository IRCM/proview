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

package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.QPreference.preference;
import static ca.qc.ircm.proview.user.QUserPreference.userPreference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserPreferenceServiceTest {
  private static final String PREFERENCE_1 = "preference_1";
  private static final String PREFERENCE_2 = "preference_2";
  private UserPreferenceService userPreferenceService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Mock
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    userPreferenceService =
        new UserPreferenceService(entityManager, jpaQueryFactory, authorizationService);
    user = entityManager.find(User.class, 2L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private Object getValue(UserPreference preference) throws IOException, ClassNotFoundException {
    try (ObjectInputStream input =
        new ObjectInputStream(new ByteArrayInputStream(preference.getValue()))) {
      return input.readObject();
    }
  }

  @Test
  public void get() {
    assertEquals("value 1", userPreferenceService.get(this, PREFERENCE_1, "default value"));
    assertEquals((Integer) 208, userPreferenceService.get(this, PREFERENCE_2, 20));
  }

  @Test
  public void get_MissingUserPreference() {
    User user = entityManager.find(User.class, 10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    assertEquals("default value", userPreferenceService.get(this, PREFERENCE_1, "default value"));
    assertEquals((Integer) 20, userPreferenceService.get(this, PREFERENCE_2, 20));
  }

  @Test
  public void get_MissingPreference() {
    assertEquals("default value",
        userPreferenceService.get(this, "missing reference", "default value"));
  }

  @Test
  public void get_NullReferer() {
    assertEquals("default value", userPreferenceService.get(null, PREFERENCE_1, "default value"));
  }

  @Test
  public void get_NullName() {
    assertEquals("default value", userPreferenceService.get(this, null, "default value"));
  }

  @Test
  public void get_NoCurrentUser() {
    when(authorizationService.getCurrentUser()).thenReturn(null);

    assertEquals("default value", userPreferenceService.get(this, PREFERENCE_1, "default value"));
  }

  @Test
  public void get_NullDefaultValue() {
    assertNull(userPreferenceService.get(this, "missing reference", null));
  }

  @Test
  public void save_Insert_All() throws Throwable {
    String value = "test value 1";
    String name = "test new preference";
    User user = entityManager.find(User.class, 10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    userPreferenceService.save(this, name, value);
    entityManager.flush();

    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.referer.eq(this.getClass().getName()));
    query.where(userPreference.preference.name.eq(name));
    query.where(userPreference.user.eq(user));
    UserPreference userPreference = query.fetchOne();
    assertEquals(value, getValue(userPreference));
  }

  @Test
  public void save_Insert() throws Throwable {
    String value = "test value 1";
    User user = entityManager.find(User.class, 10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    userPreferenceService.save(this, PREFERENCE_1, value);
    entityManager.flush();

    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.referer.eq(this.getClass().getName()));
    query.where(userPreference.preference.name.eq(PREFERENCE_1));
    query.where(userPreference.user.eq(user));
    UserPreference userPreference = query.fetchOne();
    assertEquals(value, getValue(userPreference));
  }

  @Test
  public void save_Update() throws Throwable {
    String newValue = "new value 1";

    userPreferenceService.save(this, PREFERENCE_1, newValue);
    entityManager.flush();

    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.referer.eq(this.getClass().getName()));
    query.where(userPreference.preference.name.eq(PREFERENCE_1));
    query.where(userPreference.user.eq(user));
    UserPreference userPreference = query.fetchOne();
    assertEquals(newValue, getValue(userPreference));
  }

  @Test
  public void save_UpdateToNull() throws Throwable {
    String newValue = null;

    userPreferenceService.save(this, PREFERENCE_1, newValue);
    entityManager.flush();

    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.referer.eq(this.getClass().getName()));
    query.where(userPreference.preference.name.eq(PREFERENCE_1));
    query.where(userPreference.user.eq(user));
    UserPreference userPreference = query.fetchOne();
    assertEquals(newValue, getValue(userPreference));
  }

  @Test
  public void delete() {
    userPreferenceService.delete(this, PREFERENCE_1);
    entityManager.flush();

    JPAQuery<Preference> preferenceQuery = jpaQueryFactory.select(preference);
    preferenceQuery.from(preference);
    preferenceQuery.where(preference.referer.eq(this.getClass().getName()));
    preferenceQuery.where(preference.name.eq(PREFERENCE_1));
    assertEquals(1, preferenceQuery.fetchCount());
    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.referer.eq(this.getClass().getName()));
    query.where(userPreference.preference.name.eq(PREFERENCE_1));
    query.where(userPreference.user.eq(user));
    assertEquals(0, query.fetchCount());
  }

  @Test
  public void deleteAll() {
    userPreferenceService.deleteAll(this, PREFERENCE_1);
    entityManager.flush();

    JPAQuery<Preference> query = jpaQueryFactory.select(preference);
    query.from(preference);
    query.where(preference.referer.eq(this.getClass().getName()));
    query.where(preference.name.eq(PREFERENCE_1));
    assertEquals(0, query.fetchCount());
  }
}
