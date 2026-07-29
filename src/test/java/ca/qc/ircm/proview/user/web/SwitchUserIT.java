package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ViewLayoutElement;
import com.vaadin.testbench.BrowserTest;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UsersView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SwitchUserIT extends AbstractBrowserTestCase {

  @BrowserTest
  public void switchUser() {
    openView(VIEW_NAME);
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(5);

    view.switchUser().click();

    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewLayout = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(viewLayout::exitSwitchUser).isPresent());
    assertFalse(optional(viewLayout::users).isPresent());
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
