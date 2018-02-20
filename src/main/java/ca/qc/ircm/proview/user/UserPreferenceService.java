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
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for user preferences.
 */
@Service
@Transactional
public class UserPreferenceService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected UserPreferenceService() {
  }

  protected UserPreferenceService(EntityManager entityManager, JPAQueryFactory jpaQueryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.jpaQueryFactory = jpaQueryFactory;
    this.authorizationService = authorizationService;
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
    User user = authorizationService.getCurrentUser();
    if (user == null) {
      return defaultValue;
    }

    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.referer.eq(referer.getClass().getName()));
    query.where(userPreference.preference.name.eq(name));
    query.where(userPreference.user.eq(user));
    UserPreference preference = query.fetchOne();
    if (preference != null) {
      try {
        return (T) toObject(preference.getValue());
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
    final User user = authorizationService.getCurrentUser();
    JPAQuery<Preference> preferenceQuery = jpaQueryFactory.select(preference);
    preferenceQuery.from(preference);
    preferenceQuery.where(preference.referer.eq(refererName));
    preferenceQuery.where(preference.name.eq(name));
    Preference preference = preferenceQuery.fetchOne();
    if (preference == null) {
      preference = new Preference();
      preference.setReferer(refererName);
      preference.setName(name);
      entityManager.persist(preference);
    }
    JPAQuery<UserPreference> query = jpaQueryFactory.select(userPreference);
    query.from(userPreference);
    query.where(userPreference.preference.eq(preference));
    query.where(userPreference.user.eq(user));
    UserPreference userPreference = query.fetchOne();
    if (userPreference != null) {
      userPreference.setValue(toBytes(value));
      entityManager.merge(userPreference);
    } else {
      userPreference = new UserPreference();
      userPreference.setPreference(preference);
      userPreference.setUser(user);
      userPreference.setValue(toBytes(value));
      entityManager.persist(userPreference);
    }
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
    User user = authorizationService.getCurrentUser();
    JPADeleteClause query = jpaQueryFactory.delete(userPreference);
    query.where(userPreference.preference.referer.eq(referer.getClass().getName()));
    query.where(userPreference.preference.name.eq(name));
    query.where(userPreference.user.eq(user));
    query.execute();
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
    JPADeleteClause query = jpaQueryFactory.delete(preference);
    query.where(preference.referer.eq(referer.getClass().getName()));
    query.where(preference.name.eq(name));
    query.execute();
  }
}
