package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;

import ca.qc.ircm.proview.test.config.AbstractSeleniumTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ViewLayout}.
 */
@TestBenchTestAnnotations
@ActiveProfiles({"integration-test", "context-path"})
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SignoutContextPathIT extends AbstractSeleniumTestCase {

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void signout() {
    open();
    ViewLayoutComponent view = waitUntil(ViewLayoutComponent.find());
    openView(SignoutView.VIEW_NAME);
    waitUntil(SigninViewComponent.find());
  }

  @Test
  public void signout_sidenav() {
    open();
    ViewLayoutComponent view = waitUntil(ViewLayoutComponent.find());
    waitUntil(view.openDrawer());
    view.signout().click();
    waitUntil(SigninViewComponent.find());
  }
}
