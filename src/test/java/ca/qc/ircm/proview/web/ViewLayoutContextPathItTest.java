package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.web.UsersViewElement;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ViewLayout}.
 */
@TestBenchTestAnnotations
@ActiveProfiles({"integration-test", "context-path"})
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ViewLayoutContextPathItTest extends AbstractTestBenchTestCase {

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void exitSwitchUser() {
    open();
    $(ViewLayoutElement.class).waitForFirst().users().click();
    UsersViewElement usersView = $(UsersViewElement.class).waitForFirst();
    usersView.users().select(1);
    usersView.switchUser().click();
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement view = $(ViewLayoutElement.class).first();
    view.contact().click();
    view.exitSwitchUser().click();
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewAfterExitSwitchUser = $(ViewLayoutElement.class).first();
    assertFalse(optional(viewAfterExitSwitchUser::exitSwitchUser).isPresent());
    assertTrue(optional(viewAfterExitSwitchUser::users).isPresent());
  }

  @Test
  public void signout() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.signout().click();
    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void changeLanguage() {
    open();
    final Locale before = currentLocale();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.changeLanguage().click();
    $(ContactViewElement.class).waitForFirst();
    assertEquals(ENGLISH.equals(before) ? FRENCH : ENGLISH, currentLocale());
  }
}
