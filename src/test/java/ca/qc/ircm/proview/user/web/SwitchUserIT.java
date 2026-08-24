package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewComponent;
import ca.qc.ircm.proview.test.config.AbstractSeleniumTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ViewLayoutComponent;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UsersView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SwitchUserIT extends AbstractSeleniumTestCase {

  @Test
  public void switchUser() {
    openView(VIEW_NAME);
    UsersViewComponent view = waitUntil(UsersViewComponent.find());
    view.users().select(5);

    view.switchUser().click();

    waitUntil(SubmissionsViewComponent.find());
    ViewLayoutComponent layout = waitUntil(ViewLayoutComponent.find());
    assertTrue(optional(layout::exitSwitchUser).isPresent());
    assertFalse(optional(layout::users).isPresent());
  }

  @Test
  public void exitSwitchUser() {
    openView(SubmissionsView.VIEW_NAME);
    ViewLayoutComponent layout = waitUntil(ViewLayoutComponent.find());
    waitUntil(layout.openDrawer());
    layout.users().click();
    UsersViewComponent view = waitUntil(UsersViewComponent.find());
    view.users().select(2);
    view.switchUser().click();
    waitUntil(SubmissionsViewComponent.find());
    layout = waitUntil(ViewLayoutComponent.find());
    waitUntil(layout.openDrawer());
    layout.profile().click();
    openView(ExitSwitchUserView.VIEW_NAME);
    waitUntil(SubmissionsViewComponent.find());
    layout = waitUntil(ViewLayoutComponent.find());
    assertFalse(optional(layout::exitSwitchUser).isPresent());
    assertTrue(optional(layout::users).isPresent());
  }
}
