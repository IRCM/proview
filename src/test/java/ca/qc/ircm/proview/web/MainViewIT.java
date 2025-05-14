package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.MainView.VIEW_NAME;

import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.testbench.BrowserTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link MainView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class MainViewIT extends AbstractBrowserTestCase {

  private void open() {
    openView(VIEW_NAME);
  }

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void userRedirected() {
    open();

    $(SubmissionsViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void managerRedirected() {
    open();

    $(SubmissionsViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void adminRedirected() {
    open();

    $(SubmissionsViewElement.class).waitForFirst();
  }
}
