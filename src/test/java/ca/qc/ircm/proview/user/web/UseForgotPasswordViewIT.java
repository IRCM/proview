package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SEPARATOR;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.testbench.BrowserTest;
import jakarta.persistence.EntityManager;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Integration tests for {@link UseForgotPasswordView}.
 */
@TestBenchTestAnnotations
public class UseForgotPasswordViewIT extends AbstractBrowserTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(UseForgotPasswordView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(UseForgotPasswordViewIT.class);
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private MessageSource messageSource;
  private final String password = "test_password";
  private final long id = 9;
  private final String confirm = "174407008";

  private void open() {
    openView(VIEW_NAME, id + SEPARATOR + confirm);
  }

  @BrowserTest
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName = messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null,
        locale);
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    UseForgotPasswordViewElement view = $(UseForgotPasswordViewElement.class).waitForFirst();
    assertTrue(optional(view::header).isPresent());
    assertTrue(optional(view::message).isPresent());
    assertTrue(optional(view::password).isPresent());
    assertTrue(optional(view::confirmPassword).isPresent());
    assertTrue(optional(view::save).isPresent());
  }

  @BrowserTest
  public void save() {
    open();
    UseForgotPasswordViewElement view = $(UseForgotPasswordViewElement.class).waitForFirst();

    view.password().setValue(password);
    view.confirmPassword().setValue(password);
    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, null, currentLocale()),
        notification.getText());
    ForgotPassword forgotPassword = repository.findById(id).orElseThrow();
    entityManager.refresh(forgotPassword);
    assertTrue(forgotPassword.isUsed());
    User user = userRepository.findById(10L).orElseThrow();
    entityManager.refresh(user);
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    $(SigninViewElement.class).waitForFirst();
  }
}
