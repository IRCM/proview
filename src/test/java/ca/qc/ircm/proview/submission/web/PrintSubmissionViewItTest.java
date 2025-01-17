package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link PrintSubmissionView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class PrintSubmissionViewItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(PrintSubmissionView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(PrintSubmissionViewItTest.class);
  @Autowired
  private MessageSource messageSource;

  private void open() {
    openView(VIEW_NAME, "164");
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void url() {
    open();
    $(PrintSubmissionViewElement.class).waitForFirst();
    assertEquals(viewUrl(VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }

  @Test
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName =
        messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[] { applicationName }, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence_User() {
    open();
    PrintSubmissionViewElement view = $(PrintSubmissionViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissionsView()).isPresent());
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.secondHeader()).isPresent());
    assertTrue(optional(() -> view.printSubmission()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() {
    open();
    PrintSubmissionViewElement view = $(PrintSubmissionViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissionsView()).isPresent());
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.secondHeader()).isPresent());
    assertTrue(optional(() -> view.printSubmission()).isPresent());
  }

  @Test
  public void submissionsView() {
    open();
    PrintSubmissionViewElement view = $(PrintSubmissionViewElement.class).waitForFirst();

    view.submissionsView().click();

    $(SubmissionsViewElement.class).waitForFirst();
  }
}
