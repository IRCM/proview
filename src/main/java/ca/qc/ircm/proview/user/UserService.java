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

import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.security.AuthorizationService;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User service class.
 */
@Service
@Transactional
public class UserService {
  private static final long ROBOT_ID = 1L;
  private final Logger logger = LoggerFactory.getLogger(UserService.class);
  @Inject
  private UserRepository repository;
  @Inject
  private LaboratoryRepository laboratoryRepository;
  @Inject
  private PasswordEncoder passwordEncoder;
  @Inject
  private AuthorizationService authorizationService;

  protected UserService() {
  }

  /**
   * Selects user from database.
   *
   * @param id
   *          database identifier of user
   * @return user
   */
  @PostAuthorize("returnObject == null || hasPermission(returnObject, 'read')")
  public User get(Long id) {
    if (id == null) {
      return null;
    }

    User user = repository.findById(id).orElse(null);
    return user;
  }

  /**
   * Returns user with email.
   *
   * @param email
   *          email
   * @return user with email
   */
  @PostAuthorize("returnObject == null || hasPermission(returnObject, 'read')")
  public User get(String email) {
    if (email == null) {
      return null;
    }

    User ret = noSecurityGet(email);
    return ret;
  }

  private User noSecurityGet(String email) {
    if (email == null) {
      return null;
    }

    return repository.findByEmail(email);
  }

  /**
   * Returns true if a user exists with this email.
   *
   * @param email
   *          email
   * @return true if a user exists with this email
   */
  public boolean exists(String email) {
    if (email == null) {
      return false;
    }

    return repository.findByEmail(email) != null;
  }

  /**
   * Returns all users that match parameters.
   * <p>
   * Only admin users can search users without a laboratory.
   * </p>
   * <p>
   * Only managers can search users with a laboratory.
   * </p>
   *
   * @param filter
   *          parameters
   * @return all users that match parameters
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<User> all(UserFilter filter) {
    if (filter == null) {
      filter = new UserFilter();
    }

    BooleanExpression predicate = user.id.ne(ROBOT_ID);
    predicate = predicate.and(filter.predicate());
    return Lists.newArrayList(repository.findAll(predicate));
  }

  /**
   * Returns all laboratory's users that match parameters. Only managers can search users with a
   * laboratory.
   *
   * @param filter
   *          parameters
   * @param laboratory
   *          laboratory
   * @return all laboratory's users that match parameters
   */
  @PreAuthorize("hasPermission(#laboratory, 'write')")
  public List<User> all(UserFilter filter, Laboratory laboratory) {
    if (filter == null) {
      filter = new UserFilter();
    } else if (laboratory == null) {
      return new ArrayList<>();
    }

    BooleanExpression predicate = user.id.ne(ROBOT_ID);
    predicate = predicate.and(filter.predicate());
    predicate = predicate.and(user.laboratory.eq(laboratory));
    return Lists.newArrayList(repository.findAll(predicate));
  }

  /**
   * Saves user in database.
   *
   * @param user
   *          user
   * @param password
   *          user's new password, required if user does not exists
   */
  @PreAuthorize("hasPermission(#user, 'write')")
  public void save(User user, String password) {
    if (user.getId() == null) {
      user.setRegisterTime(Instant.now());
      if (user.isAdmin()) {
        registerAdmin(user, password);
      } else if (user.getLaboratory().getId() == null) {
        registerNewLaboratory(user, password);
      } else {
        registerNewUser(user, password);
      }
    } else {
      update(user, password);
    }
  }

  private void setUserPassword(User user, String password) {
    String hashedPassword = passwordEncoder.encode(password);
    user.setHashedPassword(hashedPassword);
    user.setSalt(null);
    user.setPasswordVersion(null);
  }

  private void registerNewUser(User user, String password) {
    setUserPassword(user, password);
    user.setActive(true);
    repository.saveAndFlush(user);

    logger.info("User {} of laboratory {} added to database", user, user.getLaboratory());
  }

  private void registerNewLaboratory(User manager, String password) {
    setUserPassword(manager, password);
    manager.setManager(true);
    manager.setActive(true);
    Laboratory laboratory = manager.getLaboratory();
    laboratory.setDirector(manager.getName());
    laboratoryRepository.save(laboratory);
    repository.saveAndFlush(manager);

    logger.info("Laboratory {} with manager {} added to database", laboratory, manager);
  }

  private void registerAdmin(User user, String password) {
    if (!user.isAdmin()) {
      throw new IllegalArgumentException("Laboratory must be admin, use register instead.");
    }

    final User manager = authorizationService.getCurrentUser();
    setUserPassword(user, password);
    user.setActive(true);
    user.setLaboratory(manager.getLaboratory());
    repository.saveAndFlush(user);

    logger.info("Admin user {} added to database", user);
  }

  private void update(User user, String newPassword) {
    if (newPassword != null) {
      setUserPassword(user, newPassword);
    }

    boolean updateLaboratory = authorizationService.hasAnyRole(UserRole.MANAGER, UserRole.ADMIN);
    if (updateLaboratory) {
      laboratoryRepository.save(user.getLaboratory());
    }
    repository.save(user);
    if (updateLaboratory) {
      updateDirectorName(user.getLaboratory(), user);
      laboratoryRepository.save(user.getLaboratory());
    }

    if (repository.findAllByLaboratoryAndManagerTrue(user.getLaboratory()).isEmpty()) {
      throw new UnmanagedLaboratoryException();
    }

    logger.info("User {} updated", user);
  }

  private void updateDirectorName(Laboratory laboratory, User possibleDirector) {
    repository.findAllByLaboratoryAndManagerTrue(laboratory).stream()
        .mapToLong(manager -> manager.getId()).min().ifPresent(id -> {
          User manager = repository.findById(id).orElse(null);
          laboratory.setDirector(manager.getName());
        });
    ;
  }
}
