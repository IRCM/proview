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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserPreferenceServiceTest {
  private static final String PREFERENCE_1 = "preference_1";
  private static final String PREFERENCE_2 = "preference_2";
  @Inject
  private UserPreferenceService service;
  @Inject
  private UserPreferenceRepository repository;
  @Inject
  private PreferenceRepository preferenceRepository;
  @Inject
  private UserRepository userRepository;
  @MockBean
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    user = userRepository.findOne(2L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private String referer() {
    return this.getClass().getName();
  }

  private Object getValue(UserPreference preference) throws IOException, ClassNotFoundException {
    try (ObjectInputStream input =
        new ObjectInputStream(new ByteArrayInputStream(preference.getValue()))) {
      return input.readObject();
    }
  }

  private UserPreference find(User user, String referer, String name) {
    BooleanExpression predicate = userPreference.preference.referer.eq(referer)
        .and(userPreference.preference.name.eq(name)).and(userPreference.user.eq(user));
    return repository.findOne(predicate);
  }

  private Preference findPreference(String referer, String name) {
    BooleanExpression predicate = preference.referer.eq(referer).and(preference.name.eq(name));
    return preferenceRepository.findOne(predicate);
  }

  @Test
  public void get() {
    assertEquals("value 1", service.get(this, PREFERENCE_1, "default value"));
    assertEquals((Integer) 208, service.get(this, PREFERENCE_2, 20));
  }

  @Test
  public void get_MissingUserPreference() {
    User user = userRepository.findOne(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    assertEquals("default value", service.get(this, PREFERENCE_1, "default value"));
    assertEquals((Integer) 20, service.get(this, PREFERENCE_2, 20));
  }

  @Test
  public void get_MissingPreference() {
    assertEquals("default value",
        service.get(this, "missing reference", "default value"));
  }

  @Test
  public void get_NullReferer() {
    assertEquals("default value", service.get(null, PREFERENCE_1, "default value"));
  }

  @Test
  public void get_NullName() {
    assertEquals("default value", service.get(this, null, "default value"));
  }

  @Test
  public void get_NoCurrentUser() {
    when(authorizationService.getCurrentUser()).thenReturn(null);

    assertEquals("default value", service.get(this, PREFERENCE_1, "default value"));
  }

  @Test
  public void get_NullDefaultValue() {
    assertNull(service.get(this, "missing reference", null));
  }

  @Test
  public void save_Insert_All() throws Throwable {
    String value = "test value 1";
    String name = "test new preference";
    User user = userRepository.findOne(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    service.save(this, name, value);
    repository.flush();

    UserPreference userPreference = find(user, referer(), name);
    assertEquals(value, getValue(userPreference));
  }

  @Test
  public void save_Insert() throws Throwable {
    String value = "test value 1";
    User user = userRepository.findOne(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    service.save(this, PREFERENCE_1, value);
    repository.flush();

    UserPreference userPreference = find(user, referer(), PREFERENCE_1);
    assertEquals(value, getValue(userPreference));
  }

  @Test
  public void save_Update() throws Throwable {
    String newValue = "new value 1";

    service.save(this, PREFERENCE_1, newValue);
    repository.flush();

    UserPreference userPreference = find(user, referer(), PREFERENCE_1);
    assertEquals(newValue, getValue(userPreference));
  }

  @Test
  public void save_UpdateToNull() throws Throwable {
    String newValue = null;

    service.save(this, PREFERENCE_1, newValue);
    repository.flush();

    UserPreference userPreference = find(user, referer(), PREFERENCE_1);
    assertEquals(newValue, getValue(userPreference));
  }

  @Test
  public void delete() {
    service.delete(this, PREFERENCE_1);
    repository.flush();

    Preference preference = findPreference(referer(), PREFERENCE_1);
    assertNotNull(preference);
    UserPreference userPreference = find(user, referer(), PREFERENCE_1);
    assertNull(userPreference);
  }

  @Test
  public void deleteAll() {
    service.deleteAll(this, PREFERENCE_1);
    preferenceRepository.flush();

    Preference preference = findPreference(referer(), PREFERENCE_1);
    assertNull(preference);
  }
}
