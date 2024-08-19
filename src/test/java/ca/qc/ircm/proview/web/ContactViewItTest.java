package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link ContactView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ContactViewItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(ContactView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Autowired
  private MessageSource messageSource;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    $(SigninViewElement.class).waitForFirst();
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
    ContactViewElement view = $(ContactViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.proteomicHeader()).isPresent());
    assertTrue(optional(() -> view.proteomicName()).isPresent());
    assertTrue(optional(() -> view.proteomicAddress()).isPresent());
    assertTrue(optional(() -> view.proteomicPhone()).isPresent());
    assertTrue(optional(() -> view.websiteHeader()).isPresent());
    assertTrue(optional(() -> view.websiteName()).isPresent());
    assertTrue(optional(() -> view.websiteAddress()).isPresent());
    assertTrue(optional(() -> view.websitePhone()).isPresent());
  }
}
