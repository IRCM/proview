package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;

/**
 * Integration tests for {@link UseForgotPasswordView}.
 */
@ServiceTestAnnotations
@WithAnonymousUser
public class UseForgotPasswordViewIT extends SpringBrowserlessTest {

  private static final String MESSAGES_PREFIX = messagePrefix(UseForgotPasswordView.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(UseForgotPasswordViewIT.class);
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  private final String password = "test_password";
  private final long id = 9;
  private final String confirm = "174407008";

  @Test
  public void save() {
    UseForgotPasswordView view = navigate(UseForgotPasswordView.class, id + SEPARATOR + confirm);

    test(view.password).setValue(password);
    test(view.confirmPassword).setValue(password);
    test(view.save).click();

    Notification notification = find(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED), test(notification).getText());
    ForgotPassword forgotPassword = repository.findById(id).orElseThrow();
    assertTrue(forgotPassword.isUsed());
    User user = userRepository.findById(10L).orElseThrow();
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    find(SigninView.class).single();
  }
}
