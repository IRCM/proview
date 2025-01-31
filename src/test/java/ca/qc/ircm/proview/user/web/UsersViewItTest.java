package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.web.AccessDeniedViewElement;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import ca.qc.ircm.proview.web.ViewLayoutElement;
import java.util.Locale;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UsersView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewItTest extends AbstractTestBenchTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(UsersView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Autowired
  private MessageSource messageSource;
  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() {
    open();

    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  public void security_Admin() {
    open();

    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName =
        messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    assertTrue(optional(view::users).isPresent());
    assertFalse(optional(view::switchFailed).isPresent());
    assertTrue(optional(view::add).isPresent());
    assertTrue(optional(view::edit).isPresent());
    assertTrue(optional(view::switchUser).isPresent());
    assertTrue(optional(view::viewLaboratory).isPresent());
  }

  @Test
  public void edit() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);

    view.edit().click();

    assertTrue(view.dialog().isOpen());
  }

  @Test
  public void add() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();

    view.add().click();

    assertTrue(view.dialog().isOpen());
  }

  @Test
  public void switchUser() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(4);

    view.switchUser().click();

    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewLayout = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(viewLayout::exitSwitchUser).isPresent());
    assertFalse(optional(viewLayout::users).isPresent());
  }

  @Test
  @Disabled("Admins are allowed to switch to another admin right now")
  public void switchUser_Fail() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);

    view.switchUser().click();

    assertTrue(optional(view::switchFailed).isPresent());
  }

  @Test
  public void view_Laboratory() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    assertTrue(view.laboratoryDialog().isOpen());
  }
}
