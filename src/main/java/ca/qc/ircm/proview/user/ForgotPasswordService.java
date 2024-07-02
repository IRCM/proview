package ca.qc.ircm.proview.user;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.mail.EmailService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private TemplateEngine emailTemplateEngine;
  @Autowired
  private EmailService emailService;
  @Autowired
  private ApplicationConfiguration applicationConfiguration;

  protected ForgotPasswordService() {
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
  public Optional<ForgotPassword> get(final Long id, final String confirmNumber) {
    if (id == null || confirmNumber == null) {
      return Optional.empty();
    }

    ForgotPassword forgotPassword = repository.findById(id).orElse(null);
    if (forgotPassword != null && confirmNumber.equals(forgotPassword.getConfirmNumber())
        && !forgotPassword.isUsed()
        && forgotPassword.getRequestMoment().isAfter(LocalDateTime.now().minus(VALID_PERIOD))) {
      return Optional.of(forgotPassword);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Inserts a new forgot password request for user into the database.
   *
   * @param email
   *          user's email
   * @param webContext
   *          web context used to send email to user
   */
  public void insert(String email, ForgotPasswordWebContext webContext) {
    ForgotPassword forgotPassword = new ForgotPassword();

    // Set time.
    forgotPassword.setRequestMoment(LocalDateTime.now());

    // Generate random confirm number.
    forgotPassword.setConfirmNumber(RandomStringUtils.randomAlphanumeric(40));

    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null) {
      // Ignore request.
      return;
    }
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
  }

  private void sendMail(String emailAddress, ForgotPassword forgotPassword, Locale locale,
      ForgotPasswordWebContext webContext) throws MessagingException {
    // Prepare URL used to change password.
    final String url = applicationConfiguration
        .getUrl(webContext.getChangeForgottenPasswordUrl(forgotPassword, locale));

    // Prepare email content.
    MimeMessageHelper email = emailService.htmlEmail();
    AppResources resources = new AppResources(ForgotPasswordService.class, locale);
    String subject = resources.message("subject");
    email.setSubject(subject);
    email.addTo(emailAddress);
    Context context = new Context(locale);
    context.setVariable("url", url);
    String htmlTemplateLocation = "user/forgotpassword.html";
    String htmlEmail = emailTemplateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation = "user/forgotpassword.txt";
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
    if (LocalDateTime.now().isAfter(forgotPassword.getRequestMoment().plus(VALID_PERIOD))) {
      throw new IllegalArgumentException("ForgotPassword instance has expired.");
    }

    // Get User that changes his password.
    User user = forgotPassword.getUser();

    // Encrypt password.
    String hashedPassword = passwordEncoder.encode(newPassword);
    // Update password.
    user = userRepository.findById(user.getId()).orElse(null);
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
