package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.Permission;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private AuthenticatedUser authenticatedUser;

  protected UserService() {
  }

  /**
   * Selects user from database.
   *
   * @param id
   *          database identifier of user
   * @return user
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<User> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Returns user with email.
   *
   * @param email
   *          email
   * @return user with email
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<User> get(String email) {
    if (email == null) {
      return Optional.empty();
    }

    return noSecurityGet(email);
  }

  private Optional<User> noSecurityGet(String email) {
    if (email == null) {
      return Optional.empty();
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

    return repository.findByEmail(email).isPresent();
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
      register(user, password);
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

  private void register(User user, String password) {
    if (user.getLaboratory().getId() == null && !user.isManager()) {
      throw new IllegalArgumentException(
          "user must be a manager when a new laboratory is to be created");
    }
    setUserPassword(user, password);
    user.setActive(true);
    user.setRegisterTime(LocalDateTime.now());
    if (authenticatedUser.hasPermission(user.getLaboratory(), Permission.WRITE)) {
      Laboratory laboratory = user.getLaboratory();
      if (laboratory.getId() == null) {
        laboratory.setDirector(user.getName());
      }
      laboratoryRepository.save(laboratory);
    }
    repository.saveAndFlush(user);

    logger.info("User {} of laboratory {} added to database", user, user.getLaboratory());
  }

  private void update(User user, String newPassword) {
    if (user.getLaboratory().getId() == null && !user.isManager()) {
      throw new IllegalArgumentException(
          "user must be a manager when a new laboratory is to be created");
    }
    if (newPassword != null) {
      setUserPassword(user, newPassword);
    }

    boolean updateLaboratory =
        authenticatedUser.hasPermission(user.getLaboratory(), Permission.WRITE);
    if (updateLaboratory) {
      laboratoryRepository.save(user.getLaboratory());
    }
    repository.save(user);
    if (updateLaboratory) {
      Laboratory laboratory = user.getLaboratory();
      updateDirectorName(laboratory, user);
      laboratoryRepository.save(laboratory);
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
