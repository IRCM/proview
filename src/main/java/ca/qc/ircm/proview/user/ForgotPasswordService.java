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

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.utils.MessageResource;
import java.time.Instant;
import java.time.Period;
import java.util.Locale;
import java.util.Random;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for forgot password.
 */
@Service
@Transactional
public class ForgotPasswordService {
  /**
   * Period for which {@link ForgotPassword} instances are valid.
   */
  public static final Period VALID_PERIOD = Period.ofDays(2);
  private final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);
  @Inject
  private ForgotPasswordRepository repository;
  @Inject
  private UserRepository userRepository;
  @Inject
  private PasswordEncoder passwordEncoder;
  @Inject
  private TemplateEngine emailTemplateEngine;
  @Inject
  private EmailService emailService;
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  private Random random;

  protected ForgotPasswordService() {
    random = new Random();
  }

  /**
   * Returns ForgotPassword having this id.
   *
   * @param id
   *          Database identifier of ForgotPassword.
   * @param confirmNumber
   *          The confirm number of ForgotPassword.
   * @return ForgotPassword having this id.
   */
  public ForgotPassword get(final Long id, final Integer confirmNumber) {
    if (id == null || confirmNumber == null) {
      return null;
    }

    ForgotPassword forgotPassword = repository.findOne(id);
    if (confirmNumber.equals(forgotPassword.getConfirmNumber()) && !forgotPassword.isUsed()
        && forgotPassword.getRequestMoment().isAfter(Instant.now().minus(VALID_PERIOD))) {
      return forgotPassword;
    } else {
      return null;
    }
  }

  /**
   * Inserts a new forgot password request for user into the database.
   *
   * @param email
   *          user's email
   * @param webContext
   *          web context used to send email to user
   * @return forgot password request created for user
   */
  public ForgotPassword insert(String email, ForgotPasswordWebContext webContext) {
    ForgotPassword forgotPassword = new ForgotPassword();

    // Set time.
    forgotPassword.setRequestMoment(Instant.now());

    // Generate random confirm number.
    int rand = random.nextInt(Integer.MAX_VALUE);
    forgotPassword.setConfirmNumber(rand);

    User user = userRepository.findByEmail(email);
    if (user.getId() == User.ROBOT_ID) {
      throw new AccessDeniedException("Cannot change password for robot");
    }
    forgotPassword.setUser(user);
    repository.saveAndFlush(forgotPassword);
    try {
      this.sendMail(email, forgotPassword, user.getLocale(), webContext);
    } catch (Throwable e) {
      logger.error("Could not send email to user " + email + " that forgot his password", e);
    }

    logger.info("Forgot password request {} added to database", forgotPassword);

    return forgotPassword;
  }

  private void sendMail(String emailAddress, ForgotPassword forgotPassword, Locale locale,
      ForgotPasswordWebContext webContext) throws MessagingException {
    // Prepare URL used to change password.
    final String url = applicationConfiguration
        .getUrl(webContext.getChangeForgottenPasswordUrl(forgotPassword, locale));

    // Prepare email content.
    MimeMessageHelper email = emailService.htmlEmail();
    MessageResource resources =
        new MessageResource(ForgotPasswordService.class.getName() + "_Email", locale);
    String subject = resources.message("email.subject");
    email.setSubject(subject);
    email.addTo(emailAddress);
    Context context = new Context(locale);
    context.setVariable("url", url);
    String htmlTemplateLocation =
        ForgotPasswordService.class.getName().replace(".", "/") + "_Email.html";
    String htmlEmail = emailTemplateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        ForgotPasswordService.class.getName().replace(".", "/") + "_Email.txt";
    String textEmail = emailTemplateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);

    emailService.send(email);
  }

  /**
   * Updated password of user of the ForgotPassword request. ForgotPassword instance must still be
   * in it's valid period before this method is called or an {@link IllegalArgumentException} will
   * be raised.
   *
   * @param forgotPassword
   *          The ForgotPassword request.
   * @param newPassword
   *          The new password of User.
   * @throws IllegalArgumentException
   *           if forgotPassword has expired
   */
  public synchronized void updatePassword(ForgotPassword forgotPassword, String newPassword) {
    if (Instant.now().isAfter(forgotPassword.getRequestMoment().plus(VALID_PERIOD))) {
      throw new IllegalArgumentException("ForgotPassword instance has expired.");
    }

    // Get User that changes his password.
    User user = forgotPassword.getUser();

    // Encrypt password.
    String hashedPassword = passwordEncoder.encode(newPassword);
    // Update password.
    user = userRepository.findOne(user.getId());
    user.setHashedPassword(hashedPassword);
    user.setSalt(null);
    user.setPasswordVersion(null);
    userRepository.save(user);

    // Tag ForgotPassword has being used.
    forgotPassword.setUsed(true);
    repository.save(forgotPassword);

    logger.info("Forgot password request {} was used", forgotPassword);
  }
}
