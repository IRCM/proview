package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordRepository;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Integration tests for {@link ForgotPasswordView}.
 */
@TestBenchTestAnnotations
public class ForgotPasswordViewItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(ForgotPasswordView.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordViewItTest.class);
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private MessageSource messageSource;
  private String email = "christopher.anderson@ircm.qc.ca";

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(ForgotPasswordView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    ForgotPasswordViewElement view = $(ForgotPasswordViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.message()).isPresent());
    assertTrue(optional(() -> view.email()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    open();
    ForgotPasswordViewElement view = $(ForgotPasswordViewElement.class).waitForFirst();
    view.email().setValue(email);
    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[] { email }, currentLocale()),
        notification.getText());
    List<ForgotPassword> forgotPasswords = repository.findByUserEmail(email);
    assertEquals(4, forgotPasswords.size());
    $(SigninViewElement.class).waitForFirst();
  }
}
