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

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.text.MessageResource;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
  private EmailService emailService;
  @Inject
  private TemplateEngine emailTemplateEngine;
  @Inject
  private ApplicationConfiguration applicationConfiguration;
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
   * Returns true if email parameter is the email of a non-admin laboratory manager, false
   * otherwise.
   *
   * @param email
   *          email
   * @return true if email parameter is the email of a non-admin laboratory manager, false otherwise
   */
  public boolean isManager(String email) {
    if (email == null) {
      return false;
    }

    BooleanExpression predicate = user.valid.eq(true).and(user.active.eq(true))
        .and(user.admin.eq(false)).and(user.email.eq(email)).and(user.manager.eq(true));
    return repository.count(predicate) > 0;
  }

  /**
   * Returns true if any user is invalid, false otherwise.
   *
   * @return true if any user is invalid, false otherwise
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public boolean hasInvalid() {
    return repository.countByValidFalse() > 0;
  }

  /**
   * Returns true if laboratory contains an invalid user, false otherwise.
   *
   * @param laboratory
   *          laboratory
   * @return true if laboratory contains an invalid user, false otherwise
   */
  @PreAuthorize("hasPermission(#laboratory, 'write')")
  public boolean hasInvalid(Laboratory laboratory) {
    if (laboratory == null) {
      throw new IllegalArgumentException("laboratory parameter cannot be null");
    }
    return repository.countByValidFalseAndLaboratory(laboratory) > 0;
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
    return Lists.newArrayList(repository.findAll(predicate));
  }

  /**
   * Register a new user in a laboratory.
   *
   * @param user
   *          user to register
   * @param password
   *          user's password
   * @param manager
   *          user's manager
   * @param webContext
   *          web context used to send email to managers or admin users
   */
  public void register(User user, String password, User manager,
      RegisterUserWebContext webContext) {
    user.setRegisterTime(Instant.now());
    if (user.isAdmin()) {
      registerAdmin(user, password);
    } else if (manager == null) {
      registerNewLaboratory(user, password, webContext);
    } else {
      registerNewUser(user, password, manager, webContext);
    }
  }

  private void setUserPassword(User user, String password) {
    String hashedPassword = passwordEncoder.encode(password);
    user.setHashedPassword(hashedPassword);
    user.setSalt(null);
    user.setPasswordVersion(null);
  }

  private void registerNewUser(User user, String password, User manager,
      RegisterUserWebContext webContext) {
    manager = noSecurityGet(manager.getEmail());
    if (!manager.isValid()) {
      throw new IllegalArgumentException("Cannot add user to a laboratory with an invalid manager");
    }

    setUserPassword(user, password);
    user.setLaboratory(manager.getLaboratory());
    repository.saveAndFlush(user);

    // Send email to manager to inform him that a new user has registered.
    try {
      this.sendEmailForNewUser(user, manager, webContext);
    } catch (MessagingException e) {
      logger.warn(e.getMessage(), e);
    }

    logger.info("User {} of laboratory {} added to database", user, user.getLaboratory());
  }

  private void sendEmailForNewUser(final User user, final User manager,
      final RegisterUserWebContext webContext) throws MessagingException {
    // Get manager's prefered locale.
    Locale locale = Locale.CANADA;
    if (manager.getLocale() != null) {
      locale = manager.getLocale();
    }

    // Prepare URL used to validate user.
    final String url = applicationConfiguration.getUrl(webContext.getValidateUserUrl(locale));

    // Prepare email content.
    MimeMessageHelper email = emailService.htmlEmail();
    email.addTo(manager.getEmail());
    MessageResource resources =
        new MessageResource(UserService.class.getName() + "_RegisterEmail", locale);
    String subject = resources.message("email.subject");
    email.setSubject(subject);
    Context context = new Context(locale);
    context.setVariable("user", user);
    context.setVariable("newLaboratory", false);
    context.setVariable("url", url);
    String htmlTemplateLocation =
        "/" + UserService.class.getName().replace(".", "/") + "_RegisterEmail.html";
    String htmlEmail = emailTemplateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        "/" + UserService.class.getName().replace(".", "/") + "_RegisterEmail.txt";
    String textEmail = emailTemplateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);

    emailService.send(email);
  }

  private void registerNewLaboratory(User manager, String password,
      RegisterUserWebContext webContext) {
    setUserPassword(manager, password);
    manager.setManager(true);
    Laboratory laboratory = manager.getLaboratory();
    laboratory.setDirector(manager.getName());
    laboratoryRepository.save(laboratory);
    repository.saveAndFlush(manager);

    // Send email to admin users to inform them that a new laboratory has registered.
    try {
      this.sendEmailForNewLaboratory(laboratory, manager, webContext);
    } catch (Throwable e) {
      logger.warn(e.getMessage(), e);
    }

    logger.info("Laboratory {} with manager {} added to database", laboratory, manager);
  }

  private List<User> adminUsers() {
    BooleanExpression predicate = user.admin.eq(true).and(user.valid.eq(true))
        .and(user.active.eq(true)).and(user.id.ne(ROBOT_ID));
    return Lists.newArrayList(repository.findAll(predicate));
  }

  private void sendEmailForNewLaboratory(final Laboratory laboratory, final User manager,
      final RegisterUserWebContext webContext) throws MessagingException {
    List<User> adminUsers = adminUsers();

    for (final User adminUser : adminUsers) {
      // Get adminUser's prefered locale.
      Locale locale = Locale.CANADA;
      if (adminUser.getLocale() != null) {
        locale = adminUser.getLocale();
      }
      // Prepare URL used to validate laboratory.
      final String url = applicationConfiguration.getUrl(webContext.getValidateUserUrl(locale));
      // Prepare email content.
      MimeMessageHelper email = emailService.htmlEmail();
      MessageResource resources =
          new MessageResource(UserService.class.getName() + "_RegisterEmail", locale);
      String subject = resources.message("newLaboratory.email.subject");
      email.setSubject(subject);
      email.addTo(adminUser.getEmail());
      Context context = new Context(locale);
      context.setVariable("user", manager);
      context.setVariable("newLaboratory", true);
      context.setVariable("url", url);
      String htmlTemplateLocation =
          UserService.class.getName().replace(".", "/") + "_RegisterEmail.html";
      String htmlEmail = emailTemplateEngine.process(htmlTemplateLocation, context);
      String textTemplateLocation =
          UserService.class.getName().replace(".", "/") + "_RegisterEmail.txt";
      String textEmail = emailTemplateEngine.process(textTemplateLocation, context);
      email.setText(textEmail, htmlEmail);

      emailService.send(email);
    }
  }

  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  protected void registerAdmin(User user, String password) {
    if (!user.isAdmin()) {
      throw new IllegalArgumentException("Laboratory must be admin, use register instead.");
    }

    final User manager = authorizationService.getCurrentUser();
    setUserPassword(user, password);
    user.setValid(true);
    user.setActive(true);
    user.setLaboratory(manager.getLaboratory());
    repository.saveAndFlush(user);

    logger.info("Admin user {} added to database", user);
  }

  /**
   * Updates a User.
   *
   * @param user
   *          signed user with new information
   * @param newPassword
   *          new password or null if password does not change
   * @throws UnauthorizedException
   *           user must match signed user
   */
  @PreAuthorize("hasPermission(#user, 'write')")
  public void update(User user, String newPassword) {
    if (!user.isValid() && user.isManager()) {
      User before = repository.findById(user.getId()).orElse(null);
      if (!before.isManager()) {
        throw new InvalidUserException();
      }
    }

    if (newPassword != null) {
      setUserPassword(user, newPassword);
    }
    if (user.isManager()) {
      user.setActive(true);
    }

    repository.save(user);

    if (repository.findAllByLaboratoryAndManagerTrue(user.getLaboratory()).isEmpty()) {
      throw new UnmanagedLaboratoryException();
    }

    if (authorizationService.hasRole(UserRole.MANAGER)) {
      authorizationService.hasPermission(user.getLaboratory(), BasePermission.WRITE);
      updateDirectorName(user.getLaboratory(), user);
      laboratoryRepository.save(user.getLaboratory());
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

  /**
   * Approve that user is valid.
   *
   * @param user
   *          user to validate
   * @param webContext
   *          web context used to send email to user
   */
  @PreAuthorize("hasPermission(#user.laboratory, 'write')")
  public void validate(User user, HomeWebContext webContext) {
    user = repository.findById(user.getId()).orElse(null);

    user.setValid(true);
    user.setActive(true);

    try {
      sendEmailForUserValidation(user, webContext);
    } catch (MessagingException e) {
      logger.warn(e.getMessage(), e);
    }

    logger.info("User {} have been validated", user);
  }

  private void sendEmailForUserValidation(final User user, final HomeWebContext webContext)
      throws MessagingException {
    // Get user's prefered locale.
    Locale locale = Locale.CANADA;
    if (user.getLocale() != null) {
      locale = user.getLocale();
    }

    final String url = applicationConfiguration.getUrl(webContext.getHomeUrl(locale));

    // Prepare email content.
    MimeMessageHelper email = emailService.htmlEmail();
    email.addTo(user.getEmail());
    MessageResource messageResource =
        new MessageResource(UserService.class.getName() + "_ValidateEmail", locale);
    String subject = messageResource.message("email.subject");
    email.setSubject(subject);
    Context context = new Context(user.getLocale());
    context.setVariable("user", user);
    context.setVariable("url", url);
    String htmlTemplateLocation =
        "/" + UserService.class.getName().replace(".", "/") + "_ValidateEmail.html";
    String htmlEmail = emailTemplateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        "/" + UserService.class.getName().replace(".", "/") + "_ValidateEmail.txt";
    String textEmail = emailTemplateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);

    emailService.send(email);
  }

  /**
   * Allows user to use program.
   *
   * @param user
   *          user
   */
  @PreAuthorize("hasPermission(#user.laboratory, 'write')")
  public void activate(User user) {
    user = repository.findById(user.getId()).orElse(null);

    user.setActive(true);
    repository.save(user);

    logger.info("User {} was activated", user);
  }

  /**
   * Prevents user to use program.
   *
   * @param user
   *          user
   */
  @PreAuthorize("hasPermission(#user.laboratory, 'write')")
  public void deactivate(User user) {
    if (user.getId() == ROBOT_ID) {
      throw new IllegalArgumentException("Robot cannot be deactivated");
    }

    user = repository.findById(user.getId()).orElse(null);
    user.setActive(false);
    repository.save(user);

    logger.info("User {} was deactivate", user);
  }

  /**
   * Deletes invalid user from database.
   *
   * @param user
   *          user to delete
   */
  @PreAuthorize("hasPermission(#user.laboratory, 'write')")
  public void delete(User user) {
    user = repository.findById(user.getId()).orElse(null);

    if (user.isValid()) {
      throw new DeleteValidUserException(user);
    }

    repository.delete(user);
    if (user.isManager()) {
      laboratoryRepository.delete(user.getLaboratory());
    }

    logger.info("User {} have been removed", user);
  }
}
