package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.mail.MailConfiguration;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordRepository;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Integration tests for {@link ForgotPasswordView}.
 */
@TestBenchTestAnnotations
public class ForgotPasswordViewIT extends AbstractTestBenchTestCase {

  @RegisterExtension
  static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);
  private static final String MESSAGES_PREFIX = messagePrefix(ForgotPasswordView.class);
  private static final String SERVICE_PREFIX = messagePrefix(ForgotPasswordService.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordViewIT.class);
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private MailConfiguration mailConfiguration;
  @Autowired
  private MessageSource messageSource;
  private final String email = "christopher.anderson@ircm.qc.ca";

  @DynamicPropertySource
  public static void springMailProperties(DynamicPropertyRegistry registry) {
    logger.info("Setting spring.mail.port to {}", ServerSetupTest.SMTP.getPort());
    registry.add("spring.mail.port", () -> String.valueOf(ServerSetupTest.SMTP.getPort()));
    registry.add("email.enabled", () -> "true");
  }

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName = messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null,
        locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() {
    open();
    ForgotPasswordViewElement view = $(ForgotPasswordViewElement.class).waitForFirst();
    assertTrue(optional(view::header).isPresent());
    assertTrue(optional(view::message).isPresent());
    assertTrue(optional(view::email).isPresent());
    assertTrue(optional(view::save).isPresent());
  }

  @Test
  public void save() throws MessagingException {
    open();
    ForgotPasswordViewElement view = $(ForgotPasswordViewElement.class).waitForFirst();
    view.email().setValue(email);
    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[]{email}, currentLocale()),
        notification.getText());
    List<ForgotPassword> forgotPasswords = repository.findByUserEmail(email);
    assertEquals(4, forgotPasswords.size());
    ForgotPassword forgotPassword = forgotPasswords.get(forgotPasswords.size() - 1);
    $(SigninViewElement.class).waitForFirst();
    MimeMessage[] messages = greenMail.getReceivedMessages();
    assertEquals(1, messages.length);
    MimeMessage message = messages[0];
    String subject = messageSource.getMessage(SERVICE_PREFIX + "subject", null, currentLocale());
    assertEquals(subject, message.getSubject());
    assertNotNull(message.getFrom());
    assertEquals(1, message.getFrom().length);
    assertEquals(new InternetAddress(mailConfiguration.from()), message.getFrom()[0]);
    assertNotNull(message.getRecipients(RecipientType.TO));
    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(new InternetAddress(email), message.getRecipients(RecipientType.TO)[0]);
    assertTrue(message.getRecipients(RecipientType.CC) == null
        || message.getRecipients(RecipientType.CC).length == 0);
    assertTrue(message.getRecipients(RecipientType.BCC) == null
        || message.getRecipients(RecipientType.BCC).length == 0);
    String body = GreenMailUtil.getBody(message);
    String url = viewUrl(UseForgotPasswordView.VIEW_NAME) + "/" + forgotPassword.getId() + "/"
        + forgotPassword.getConfirmNumber();
    assertTrue(body.contains(url), url + " not found in email " + body);
  }
}
