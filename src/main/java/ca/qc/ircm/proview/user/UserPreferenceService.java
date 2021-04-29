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

import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user preferences.
 */
@Service
@Transactional
public class UserPreferenceService {
  @Autowired
  private UserPreferenceRepository repository;
  @Autowired
  private PreferenceRepository preferenceRepository;
  @Autowired
  private AuthorizationService authorizationService;

  protected UserPreferenceService() {
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

  private UserPreference find(User user, String referer, String name) {
    BooleanExpression predicate = userPreference.preference.referer.eq(referer)
        .and(userPreference.preference.name.eq(name)).and(userPreference.user.eq(user));
    return repository.findOne(predicate).orElse(null);
  }

  private UserPreference find(User user, Preference preference) {
    BooleanExpression predicate =
        userPreference.user.eq(user).and(userPreference.preference.eq(preference));
    return repository.findOne(predicate).orElse(null);
  }

  private Preference findPreference(String referer, String name) {
    BooleanExpression predicate = preference.referer.eq(referer).and(preference.name.eq(name));
    return preferenceRepository.findOne(predicate).orElse(null);
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
   * @param defaultValue
   *          preference's default value, if none is available
   * @return current user's preference value
   */
  @SuppressWarnings("unchecked")
  public <T> T get(Object referer, String name, T defaultValue) {
    if (referer == null || name == null) {
      return defaultValue;
    }
    User user = authorizationService.getCurrentUser().orElse(null);
    if (user == null) {
      return defaultValue;
    }

    UserPreference userPreference = find(user, toString(referer), name);
    if (userPreference != null) {
      try {
        return (T) toObject(userPreference.getValue());
      } catch (ClassNotFoundException | IOException e) {
        return defaultValue;
      }
    } else {
      return defaultValue;
    }
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
    final User user = authorizationService.getCurrentUser().orElse(null);
    Preference preference = findPreference(toString(referer), name);
    if (preference == null) {
      preference = new Preference();
      preference.setReferer(refererName);
      preference.setName(name);
      preferenceRepository.save(preference);
    }
    UserPreference userPreference = find(user, preference);
    if (userPreference != null) {
      userPreference.setValue(toBytes(value));
    } else {
      userPreference = new UserPreference();
      userPreference.setPreference(preference);
      userPreference.setUser(user);
      userPreference.setValue(toBytes(value));
    }
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
    User user = authorizationService.getCurrentUser().orElse(null);
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
