package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.Permission;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
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

  private final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository repository;
  private final LaboratoryRepository laboratoryRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticatedUser authenticatedUser;

  @Autowired
  protected UserService(UserRepository repository, LaboratoryRepository laboratoryRepository,
      PasswordEncoder passwordEncoder, AuthenticatedUser authenticatedUser) {
    this.repository = repository;
    this.laboratoryRepository = laboratoryRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Selects user from database.
   *
   * @param id database identifier of user
   * @return user
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<User> get(long id) {
    return repository.findById(id);
  }

  /**
   * Returns user with email.
   *
   * @param email email
   * @return user with email
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<User> get(String email) {
    return noSecurityGet(email);
  }

  private Optional<User> noSecurityGet(String email) {
    return repository.findByEmail(email);
  }

  /**
   * Returns true if a user exists with this email.
   *
   * @param email email
   * @return true if a user exists with this email
   */
  public boolean exists(String email) {
    return repository.findByEmail(email).isPresent();
  }

  /**
   * Returns all users.
   * <p>
   * Only admin users can search users without a laboratory.
   * </p>
   *
   * @return all users
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<User> all() {
    return repository.findAll();
  }

  /**
   * Returns all laboratory's users. Only managers can search users with a laboratory.
   *
   * @param laboratory laboratory
   * @return all laboratory's users
   */
  @PreAuthorize("hasPermission(#laboratory, 'write')")
  public List<User> all(Laboratory laboratory) {
    return repository.findAllByLaboratory(laboratory);
  }

  /**
   * Saves user in database.
   *
   * @param user     user
   * @param password user's new password, required if user does not exists
   */
  @PreAuthorize("hasPermission(#user, 'write')")
  public void save(User user, @Nullable String password) {
    if (user.getId() == 0) {
      register(user, Objects.requireNonNull(password));
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
    if (user.getLaboratory().getId() == 0 && !user.isManager()) {
      throw new IllegalArgumentException(
          "user must be a manager when a new laboratory is to be created");
    }
    setUserPassword(user, password);
    user.setActive(true);
    user.setRegisterTime(LocalDateTime.now());
    if (authenticatedUser.hasPermission(user.getLaboratory(), Permission.WRITE)) {
      Laboratory laboratory = user.getLaboratory();
      if (laboratory.getId() == 0) {
        laboratory.setDirector(user.getName());
      }
      laboratoryRepository.save(laboratory);
    }
    repository.saveAndFlush(user);

    logger.info("User {} of laboratory {} added to database", user, user.getLaboratory());
  }

  private void update(User user, @Nullable String newPassword) {
    if (user.getLaboratory().getId() == 0 && !user.isManager()) {
      throw new IllegalArgumentException(
          "user must be a manager when a new laboratory is to be created");
    }
    if (newPassword != null) {
      setUserPassword(user, newPassword);
    }

    boolean updateLaboratory = authenticatedUser.hasPermission(user.getLaboratory(),
        Permission.WRITE);
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
    repository.findAllByLaboratoryAndManagerTrue(laboratory).stream().mapToLong(User::getId).min()
        .ifPresent(id -> {
          User manager = repository.findById(id).orElseThrow();
          laboratory.setDirector(manager.getName());
        });
  }
}
