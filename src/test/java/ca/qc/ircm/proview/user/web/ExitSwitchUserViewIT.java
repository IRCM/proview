package ca.qc.ircm.proview.user.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.security.web.AccessDeniedViewElement;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import ca.qc.ircm.proview.web.ViewLayoutElement;
import com.vaadin.testbench.BrowserTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link ExitSwitchUserView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class ExitSwitchUserViewIT extends AbstractBrowserTestCase {

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    openView(ExitSwitchUserView.VIEW_NAME);

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() {
    openView(ExitSwitchUserView.VIEW_NAME);

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() {
    openView(ExitSwitchUserView.VIEW_NAME);

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void security_Admin() {
    openView(ExitSwitchUserView.VIEW_NAME);

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void exitSwitchUser() {
    openView(SubmissionsView.VIEW_NAME);
    $(ViewLayoutElement.class).waitForFirst().users().click();
    UsersViewElement usersView = $(UsersViewElement.class).waitForFirst();
    usersView.users().select(2);
    usersView.switchUser().click();
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.profile().click();
    openView(ExitSwitchUserView.VIEW_NAME);
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewReload = $(ViewLayoutElement.class).waitForFirst();
    assertFalse(optional(viewReload::exitSwitchUser).isPresent());
    assertTrue(optional(viewReload::users).isPresent());
  }
}
