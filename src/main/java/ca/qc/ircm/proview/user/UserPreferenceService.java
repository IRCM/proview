package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.QPreference.preference;
import static ca.qc.ircm.proview.user.QUserPreference.userPreference;

import ca.qc.ircm.proview.security.AuthenticatedUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user preferences.
 */
@Service
@Transactional
public class UserPreferenceService {
  private final UserPreferenceRepository repository;
  private final PreferenceRepository preferenceRepository;
  private final AuthenticatedUser authenticatedUser;

  @Autowired
  protected UserPreferenceService(UserPreferenceRepository repository,
      PreferenceRepository preferenceRepository, AuthenticatedUser authenticatedUser) {
    this.repository = repository;
    this.preferenceRepository = preferenceRepository;
    this.authenticatedUser = authenticatedUser;
  }

  private String toString(Object referer) {
    return referer.getClass().getName();
  }

  private byte[] toBytes(Object value) {
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      try (ObjectOutputStream objectOutput = new ObjectOutputStream(output)) {
        objectOutput.writeObject(value);
      }
      return output.toByteArray();
    } catch (IOException e) {
      throw new IllegalArgumentException("Value " + value + " cannot be serialized");
    }
  }

  private Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return objectInput.readObject();
    }
  }

  private Optional<UserPreference> find(User user, String referer, String name) {
    BooleanExpression predicate = userPreference.preference.referer.eq(referer)
        .and(userPreference.preference.name.eq(name)).and(userPreference.user.eq(user));
    return repository.findOne(predicate);
  }

  private Optional<UserPreference> find(User user, Preference preference) {
    BooleanExpression predicate =
        userPreference.user.eq(user).and(userPreference.preference.eq(preference));
    return repository.findOne(predicate);
  }

  private Optional<Preference> findPreference(String referer, String name) {
    BooleanExpression predicate = preference.referer.eq(referer).and(preference.name.eq(name));
    return preferenceRepository.findOne(predicate);
  }

  /**
   * Returns current user's preference value.
   *
   * @param <T>
   *          type of preference's value
   * @param referer
   *          object that gets / saves preference
   * @param name
   *          preference's name
   * @return current user's preference value
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(Object referer, String name) {
    User user = authenticatedUser.getUser().orElse(null);
    if (user == null) {
      return Optional.empty();
    }

    return find(user, toString(referer), name).map(up -> {
      try {
        return (T) toObject(up.getContent());
      } catch (ClassNotFoundException | IOException e) {
        return null;
      }
    });
  }

  /**
   * Save current user's preference in database.
   *
   * @param referer
   *          object that gets / saves preference
   * @param name
   *          preference's name
   * @param value
   *          preference's value
   */
  public void save(Object referer, String name, Serializable value) {
    final String refererName = referer.getClass().getName();
    final User user = authenticatedUser.getUser().orElseThrow();
    Preference preference = findPreference(toString(referer), name).orElseGet(() -> {
      Preference newPreference = new Preference();
      newPreference.setReferer(refererName);
      newPreference.setName(name);
      preferenceRepository.save(newPreference);
      return newPreference;
    });
    UserPreference userPreference = find(user, preference).orElseGet(() -> {
      UserPreference newUserPreference = new UserPreference();
      newUserPreference.setPreference(preference);
      newUserPreference.setUser(user);
      return newUserPreference;
    });
    userPreference.setContent(toBytes(value));
    repository.save(userPreference);
  }

  /**
   * Deletes current user's preference.
   *
   * @param referer
   *          object that gets / saves preference
   * @param name
   *          preference's name
   */
  public void delete(Object referer, String name) {
    User user = authenticatedUser.getUser().orElseThrow();
    repository.deleteByUserAndPreferenceRefererAndPreferenceName(user, toString(referer), name);
  }

  /**
   * Deletes preference for all users.
   *
   * @param referer
   *          object that gets / saves preference
   * @param name
   *          preference's name
   */
  public void deleteAll(Object referer, String name) {
    preferenceRepository.deleteByRefererAndName(toString(referer), name);
  }
}
