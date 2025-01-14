package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.web.SigninView.DISABLED;
import static ca.qc.ircm.proview.web.SigninView.FAIL;
import static ca.qc.ircm.proview.web.SigninView.LOCKED;
import static ca.qc.ircm.proview.web.SigninView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.web.ForgotPasswordViewElement;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SigninView}.
 */
@TestBenchTestAnnotations
public class SigninViewItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(SigninView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Autowired
  private transient SecurityConfiguration configuration;
  @Autowired
  private MessageSource messageSource;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void title() throws Throwable {
    open();

    Locale locale = currentLocale();
    String applicationName =
        messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[] { applicationName }, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.getUsernameField()).isPresent());
    assertTrue(optional(() -> view.getPasswordField()).isPresent());
    assertTrue(optional(() -> view.getSubmitButton()).isPresent());
    assertTrue(optional(() -> view.getForgotPasswordButton()).isPresent());
  }

  @Test
  public void sign_Fail() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    view.getPasswordField().setValue("notright");
    view.getSubmitButton().click();
    assertEquals(messageSource.getMessage(MESSAGES_PREFIX + FAIL, null, currentLocale()),
        view.getErrorMessage());
    assertNotNull(getDriver().getCurrentUrl());
    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(VIEW_NAME) + "?"));
  }

  @Test
  public void sign_Disabled() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    view.getUsernameField().setValue("robert.stlouis@ircm.qc.ca");
    view.getPasswordField().setValue("password");
    view.getSubmitButton().click();
    assertEquals(messageSource.getMessage(MESSAGES_PREFIX + DISABLED, null, currentLocale()),
        view.getErrorMessage());
    assertNotNull(getDriver().getCurrentUrl());
    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(VIEW_NAME) + "?"));
  }

  @Test
  public void sign_Locked() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    for (int i = 0; i < 6; i++) {
      view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
      view.getPasswordField().setValue("notright");
      view.getSubmitButton().click();
    }
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + LOCKED,
            new Object[] { configuration.lockDuration().getSeconds() / 60 }, currentLocale()),
        view.getErrorMessage());
    assertNotNull(getDriver().getCurrentUrl());
    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(VIEW_NAME) + "?"));
  }

  @Test
  public void sign() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    view.getPasswordField().setValue("password");
    view.getSubmitButton().click();
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @Test
  public void forgotPassword() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    view.getForgotPasswordButton().click();
    $(ForgotPasswordViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void already_User() throws Throwable {
    open();
    $(SubmissionsViewElement.class).waitForFirst();
  }
}
