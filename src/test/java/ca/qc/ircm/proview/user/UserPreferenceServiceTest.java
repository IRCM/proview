package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.QPreference.preference;
import static ca.qc.ircm.proview.user.QUserPreference.userPreference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link UserPreferenceService}.
 */
@ServiceTestAnnotations
public class UserPreferenceServiceTest {
  private static final String PREFERENCE_1 = "preference_1";
  private static final String PREFERENCE_2 = "preference_2";
  @Autowired
  private UserPreferenceService service;
  @Autowired
  private UserPreferenceRepository repository;
  @Autowired
  private PreferenceRepository preferenceRepository;
  @Autowired
  private UserRepository userRepository;
  @MockitoBean
  private AuthenticatedUser authenticatedUser;
  private User user;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    user = userRepository.findById(2L).orElseThrow();
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
  }

  private String referer() {
    return this.getClass().getName();
  }

  private Object getValue(UserPreference preference) throws IOException, ClassNotFoundException {
    try (ObjectInputStream input =
        new ObjectInputStream(new ByteArrayInputStream(preference.getContent()))) {
      return input.readObject();
    }
  }

  private Optional<UserPreference> find(User user, String referer, String name) {
    BooleanExpression predicate = userPreference.preference.referer.eq(referer)
        .and(userPreference.preference.name.eq(name)).and(userPreference.user.eq(user));
    return repository.findOne(predicate);
  }

  private Optional<Preference> findPreference(String referer, String name) {
    BooleanExpression predicate = preference.referer.eq(referer).and(preference.name.eq(name));
    return preferenceRepository.findOne(predicate);
  }

  @Test
  public void get() {
    assertEquals("value 1", service.get(this, PREFERENCE_1).orElse("default value"));
    assertEquals((Integer) 208, service.get(this, PREFERENCE_2).orElse(20));
  }

  @Test
  public void get_MissingUserPreference() {
    when(authenticatedUser.getUser()).thenReturn(userRepository.findById(10L));

    assertEquals("default value", service.get(this, PREFERENCE_1).orElse("default value"));
    assertEquals((Integer) 20, service.get(this, PREFERENCE_2).orElse(20));
  }

  @Test
  public void get_MissingPreference() {
    assertEquals("default value", service.get(this, "missing reference").orElse("default value"));
  }

  @Test
  public void get_NoCurrentUser() {
    when(authenticatedUser.getUser()).thenReturn(Optional.empty());

    assertEquals("default value", service.get(this, PREFERENCE_1).orElse("default value"));
  }

  @Test
  public void get_NullDefaultValue() {
    assertNull(service.get(this, "missing reference").orElse(null));
  }

  @Test
  public void save_Insert_All() throws Throwable {
    String value = "test value 1";
    String name = "test new preference";
    user = userRepository.findById(10L).orElseThrow();
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    service.save(this, name, value);
    repository.flush();

    Optional<UserPreference> optionalUserPreference = find(user, referer(), name);
    assertTrue(optionalUserPreference.isPresent());
    assertEquals(value, getValue(optionalUserPreference.orElseThrow()));
  }

  @Test
  public void save_Insert() throws Throwable {
    String value = "test value 1";
    user = userRepository.findById(10L).orElseThrow();
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    service.save(this, PREFERENCE_1, value);
    repository.flush();

    Optional<UserPreference> optionalUserPreference = find(user, referer(), PREFERENCE_1);
    assertTrue(optionalUserPreference.isPresent());
    assertEquals(value, getValue(optionalUserPreference.orElseThrow()));
  }

  @Test
  public void save_Update() throws Throwable {
    String newValue = "new value 1";

    service.save(this, PREFERENCE_1, newValue);
    repository.flush();

    Optional<UserPreference> optionalUserPreference = find(user, referer(), PREFERENCE_1);
    assertTrue(optionalUserPreference.isPresent());
    assertEquals(newValue, getValue(optionalUserPreference.orElseThrow()));
  }

  @Test
  public void delete() {
    service.delete(this, PREFERENCE_1);
    repository.flush();

    Optional<Preference> optionalPreference = findPreference(referer(), PREFERENCE_1);
    assertTrue(optionalPreference.isPresent());
    Optional<UserPreference> optionalUserPreference = find(user, referer(), PREFERENCE_1);
    assertFalse(optionalUserPreference.isPresent());
  }

  @Test
  public void deleteAll() {
    service.deleteAll(this, PREFERENCE_1);
    preferenceRepository.flush();

    Optional<Preference> optionalPreference = findPreference(referer(), PREFERENCE_1);
    assertFalse(optionalPreference.isPresent());
  }
}
