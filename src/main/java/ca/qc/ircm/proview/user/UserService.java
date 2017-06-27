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

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.cache.CacheFlusher;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.HashedPassword;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User service class.
 */
@Service
@Transactional
public class UserService {
  private static final long ROBOT_ID = 1L;
  private final Logger logger = LoggerFactory.getLogger(UserService.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private EmailService emailService;
  @Inject
  private TemplateEngine templateEngine;
  @Inject
  private CacheFlusher cacheFlusher;
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  @Inject
  private AuthorizationService authorizationService;

  protected UserService() {
  }

  protected UserService(EntityManager entityManager, AuthenticationService authenticationService,
      TemplateEngine templateEngine, EmailService emailService, CacheFlusher cacheFlusher,
      ApplicationConfiguration applicationConfiguration,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authenticationService = authenticationService;
    this.templateEngine = templateEngine;
    this.emailService = emailService;
    this.cacheFlusher = cacheFlusher;
    this.applicationConfiguration = applicationConfiguration;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects user from database.
   *
   * @param id
   *          database identifier of user
   * @return user
   */
  public User get(Long id) {
    if (id == null) {
      return null;
    }

    User user = entityManager.find(User.class, id);
    authorizationService.checkUserReadPermission(user);
    return user;
  }

  /**
   * Returns user with email.
   *
   * @param email
   *          email
   * @return user with email
   */
  public User get(String email) {
    if (email == null) {
      return null;
    }

    User ret = noSecurityGet(email);
    authorizationService.checkUserReadPermission(ret);
    return ret;
  }

  private User noSecurityGet(String email) {
    if (email == null) {
      return null;
    }

    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.email.eq(email));
    User ret = query.fetchOne();
    return ret;
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

    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.email.eq(email));
    return query.fetchOne() != null;
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

    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.from(laboratory);
    query.where(user.valid.eq(true));
    query.where(user.active.eq(true));
    query.where(user.admin.eq(false));
    query.where(user.email.eq(email));
    query.where(laboratory.managers.contains(user));
    return query.fetchOne() != null;
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
   * @param parameters
   *          parameters
   * @return all users that match parameters
   */
  public List<User> all(UserFilter parameters) {
    if (parameters == null) {
      parameters = new UserFilterBuilder();
    }
    if (parameters.getLaboratory() == null) {
      authorizationService.checkAdminRole();
    } else {
      authorizationService.checkLaboratoryManagerPermission(parameters.getLaboratory());
    }

    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.id.ne(ROBOT_ID));
    if (parameters.isActive()) {
      query.where(user.active.eq(true));
    }
    if (parameters.isInvalid()) {
      query.where(user.valid.eq(false));
    }
    if (parameters.isValid()) {
      query.where(user.valid.eq(true));
    }
    if (parameters.isNonAdmin()) {
      query.where(user.admin.eq(false));
    }
    if (parameters.getLaboratory() != null) {
      query.where(user.laboratory.eq(parameters.getLaboratory()));
    }
    return query.fetch();
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
    if (user.isAdmin()) {
      registerAdmin(user, password);
    } else if (manager == null) {
      registerNewLaboratory(user, password, webContext);
    } else {
      registerNewUser(user, password, manager, webContext);
    }
  }

  private void setUserPassword(User user, String password) {
    HashedPassword hashedPassword = authenticationService.hashPassword(password);
    user.setHashedPassword(hashedPassword.getPassword());
    user.setSalt(hashedPassword.getSalt());
    user.setPasswordVersion(hashedPassword.getPasswordVersion());
  }

  private void registerNewUser(User user, String password, User manager,
      RegisterUserWebContext webContext) {
    manager = noSecurityGet(manager.getEmail());
    if (!manager.isValid()) {
      throw new IllegalArgumentException("Cannot add user to a laboratory with an invalid manager");
    }

    setUserPassword(user, password);
    user.setLaboratory(manager.getLaboratory());
    entityManager.persist(user);

    entityManager.flush();
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
    String htmlEmail = templateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        "/" + UserService.class.getName().replace(".", "/") + "_RegisterEmail.txt";
    String textEmail = templateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);

    emailService.send(email);
  }

  private void registerNewLaboratory(User manager, String password,
      RegisterUserWebContext webContext) {
    setUserPassword(manager, password);
    Laboratory laboratory = manager.getLaboratory();
    laboratory.setManagers(new ArrayList<User>());
    laboratory.getManagers().add(manager);
    entityManager.persist(laboratory);

    // Send email to admin users to inform them that a new laboratory has registered.
    entityManager.flush();
    try {
      this.sendEmailForNewLaboratory(laboratory, manager, webContext);
    } catch (Throwable e) {
      logger.warn(e.getMessage(), e);
    }

    logger.info("Laboratory {} with manager {} added to database", laboratory, manager);
  }

  private List<User> adminUsers() {
    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.admin.eq(true));
    query.where(user.valid.eq(true));
    query.where(user.active.eq(true));
    query.where(user.id.ne(ROBOT_ID));
    return query.fetch();
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
      String htmlEmail = templateEngine.process(htmlTemplateLocation, context);
      String textTemplateLocation =
          UserService.class.getName().replace(".", "/") + "_RegisterEmail.txt";
      String textEmail = templateEngine.process(textTemplateLocation, context);
      email.setText(textEmail, htmlEmail);

      emailService.send(email);
    }
  }

  private void registerAdmin(User user, String password) {
    if (!user.isAdmin()) {
      throw new IllegalArgumentException("Laboratory must be admin, use register instead.");
    }
    authorizationService.checkAdminRole();

    final User manager = authorizationService.getCurrentUser();
    setUserPassword(user, password);
    user.setValid(true);
    user.setActive(true);
    user.setLaboratory(manager.getLaboratory());
    entityManager.persist(user);

    cacheFlusher.flushShiroCache();

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
  public void update(User user, String newPassword) {
    authorizationService.checkUserWritePermission(user);

    if (newPassword != null) {
      authorizationService.checkUserWritePasswordPermission(user);
      setUserPassword(user, newPassword);
    }

    entityManager.merge(user);

    if (authorizationService.hasManagerRole()) {
      authorizationService.checkLaboratoryManagerPermission(user.getLaboratory());
      entityManager.merge(user.getLaboratory());
    }

    logger.info("User {} updated", user);
  }

  /**
   * Approve that users are valid.
   *
   * @param users
   *          users to validate
   * @param webContext
   *          web context used to send email to user
   */
  public void validate(Collection<User> users, HomeWebContext webContext) {
    for (User user : users) {
      user = entityManager.merge(user);
      entityManager.refresh(user);
      authorizationService.checkLaboratoryManagerPermission(user.getLaboratory());

      user.setValid(true);
      user.setActive(true);
    }

    cacheFlusher.flushShiroCache();

    for (User user : users) {
      try {
        sendEmailForUserValidation(user, webContext);
      } catch (MessagingException e) {
        logger.warn(e.getMessage(), e);
      }
    }

    logger.info("Users {} have been validated", users);
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
    String htmlEmail = templateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        "/" + UserService.class.getName().replace(".", "/") + "_ValidateEmail.txt";
    String textEmail = templateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);

    emailService.send(email);
  }

  /**
   * Allow users to use program.
   *
   * @param users
   *          users
   */
  public void activate(Collection<User> users) {
    for (User user : users) {
      user = entityManager.merge(user);
      entityManager.refresh(user);
      authorizationService.checkLaboratoryManagerPermission(user.getLaboratory());

      user.setActive(true);
    }

    cacheFlusher.flushShiroCache();

    logger.info("Users {} were activated", users);
  }

  /**
   * Block users from using program.
   *
   * @param users
   *          users
   * @throws DeactivateManagerException
   *           managers cannot be deactivated
   */
  public void deactivate(Collection<User> users) throws DeactivateManagerException {
    for (User user : users) {
      authorizationService.checkLaboratoryManagerPermission(user.getLaboratory());
      if (authorizationService.hasManagerRole(user)) {
        throw new DeactivateManagerException(user);
      }

      user = entityManager.merge(user);
      entityManager.refresh(user);
    }

    for (User user : users) {
      user = entityManager.merge(user);
      entityManager.refresh(user);
      user.setActive(false);
    }

    cacheFlusher.flushShiroCache();

    logger.info("Users {} were deactivate", users);
  }

  /**
   * Deletes invalid users from database.
   *
   * @param users
   *          users to delete
   */
  public void delete(Collection<User> users) {
    for (User user : users) {
      user = entityManager.merge(user);
      entityManager.refresh(user);
      authorizationService.checkLaboratoryManagerPermission(user.getLaboratory());

      if (user.isValid()) {
        throw new DeleteValidUserException(user);
      }

      boolean manager = user.getLaboratory().getManagers().contains(user);
      entityManager.remove(user);
      if (manager) {
        entityManager.remove(user.getLaboratory());
      }
    }

    logger.info("Users {} have been removed", users);
  }
}
